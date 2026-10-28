// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Drives the provider against an in-process stub server ({@code com.sun.net.httpserver}) that
 * records the request and answers with a canned chat-completions response: the real HTTP path, no
 * network. {@code OpenAiCompatibleLiveTest} runs the same provider against a real server.
 */
public class OpenAiCompatibleAiGenerationProviderTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final AiPromptSupport PROMPT_SUPPORT =
            new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Test.java",
            AiMdHeaderCodec.HEADER_VERSION_1_0,
            "00000000",
            "2026-10-28T00:00:00Z",
            "2026-10-28T00:00:00Z",
            "0.1.0-SNAPSHOT",
            "0.0.0",
            AiMdHeaderCodec.NODE_TYPE_FILE);

    private static final AiGenerationRequest REQUEST = new AiGenerationRequest(
            CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), "class Test {}", HEADER);

    private static final Function<String, @Nullable String> NO_ENVIRONMENT = name -> null;

    /** A chat-completions answer as OpenRouter and llama-server send it. */
    private static String completion(final String content, final String finishReason) {
        return "{\"id\":\"gen-1\",\"object\":\"chat.completion\",\"model\":\"stub\",\"choices\":[{\"index\":0,"
                + "\"message\":{\"role\":\"assistant\",\"content\":" + quote(content) + "},\"finish_reason\":\""
                + finishReason + "\"}],\"usage\":{\"prompt_tokens\":42,\"completion_tokens\":7,\"total_tokens\":49,"
                + "\"prompt_tokens_details\":{\"cached_tokens\":40}}}";
    }

    private static String quote(final String text) {
        try {
            return MAPPER.writeValueAsString(text);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private HttpServer server;
    private int status;
    private String answer;
    private String requestMethod;
    private String requestPath;
    private String requestBody;
    private Map<String, java.util.List<String>> requestHeaders;

    @BeforeEach
    public void startServer() throws IOException {
        status = 200;
        answer = completion("A tiny test class.", "stop");
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            requestMethod = exchange.getRequestMethod();
            requestPath = exchange.getRequestURI().getPath();
            requestHeaders = exchange.getRequestHeaders();
            try (InputStream in = exchange.getRequestBody()) {
                final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                final byte[] chunk = new byte[4096];
                int n;
                while ((n = in.read(chunk)) != -1) {
                    buffer.write(chunk, 0, n);
                }
                requestBody = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
            }
            final byte[] bytes = answer.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/";
    }

    private OpenAiCompatibleAiGenerationProvider provider(
            final LlamaCppJniConfig.Builder builder, final Function<String, @Nullable String> environment) {
        return new OpenAiCompatibleAiGenerationProvider(
                builder.baseUrl(baseUrl()).build(), PROMPT_SUPPORT, environment);
    }

    private @Nullable String header(final String name) {
        final java.util.List<String> values = requestHeaders.get(name);
        return values == null ? null : values.get(0);
    }

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_postsTheChatRequestAndReturnsTheAnswer() throws Exception {
        // act
        final String text = provider(LlamaCppJniConfig.builder("").remoteModel("stub/model"), NO_ENVIRONMENT)
                .generate(REQUEST);

        // assert
        assertThat(text, is("A tiny test class."));
        assertThat(requestMethod, is("POST"));
        assertThat(requestPath, is("/v1/chat/completions"));
        assertThat(header("Content-type"), is("application/json; charset=utf-8"));
        assertThat(header("Accept"), is("application/json"));
        assertThat(header("X-title"), is(OpenAiCompatibleAiGenerationProvider.APPLICATION_TITLE));
        assertThat(header("Content-length"), is(String.valueOf(requestBody.getBytes(StandardCharsets.UTF_8).length)));
        final JsonNode body = MAPPER.readTree(requestBody);
        assertThat(body.get("model").asText(), is("stub/model"));
        assertThat(body.get("messages").size(), is(2));
    }

    @Test
    public void generate_keySet_sendsItAsBearer() throws Exception {
        provider(
                        LlamaCppJniConfig.builder("").apiKeyEnv("OPENROUTER_API_KEY"),
                        name -> "OPENROUTER_API_KEY".equals(name) ? "sk-stub" : null)
                .generate(REQUEST);
        assertThat(header("Authorization"), is("Bearer sk-stub"));
    }

    @Test
    public void generate_noKey_sendsNoAuthorization() throws Exception {
        provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST);
        assertThat(header("Authorization"), is(nullValue()));
    }

    @Test
    public void generate_thinkingBlock_isStrippedLikeTheJniProviderDoes() throws Exception {
        answer = completion(
                AiCompletionParser.THINKING_BLOCK_START_MARKER + "hmm" + AiCompletionParser.THINKING_BLOCK_END_MARKER
                        + "\nThe answer.",
                "stop");
        assertThat(provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST), is("The answer."));
    }

    @Test
    public void generate_errorStatus_throwsWithStatusAndBody() {
        // arrange
        status = 401;
        answer = "{\"error\":{\"message\":\"No auth credentials found\",\"code\":401}}";

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.getMessage(), containsString("/v1/chat/completions answered HTTP 401"));
        assertThat(thrown.getMessage(), containsString("No auth credentials found"));
    }

    @Test
    public void generate_errorStatusWithoutBody_throwsWithTheStatus() {
        status = 503;
        answer = "";
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));
        assertThat(thrown.getMessage(), containsString("answered HTTP 503: "));
    }

    @Test
    public void generate_truncatedAnswer_warnsLikeTheJniProvider() throws Exception {
        // arrange
        answer = completion("cut off", "length");
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(LlamaCppJniProviderSupport.class);
        logger.setLevel(Level.WARN);
        logger.addAppender(appender);
        try {
            // act
            provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST);

            // assert
            assertThat(appender.list.size(), is(1));
            assertThat(appender.list.get(0).getFormattedMessage(), containsString("Test.java"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generateWithTimings">
    @Test
    public void generateWithTimings_reportsTheServersUsage() throws Exception {
        // act
        final AiGenerationTimings timings =
                provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generateWithTimings(REQUEST);

        // assert
        assertThat(timings.text(), is("A tiny test class."));
        assertThat(timings.promptTokens(), is(42));
        assertThat(timings.cachedPromptTokens(), is(40));
        assertThat(timings.predictedTokens(), is(7));
        assertThat(timings.prefillTokensPerSecond(), is(0.0d));
        assertThat(timings.decodeTokensPerSecond(), is(greaterThan(0.0d)));
    }
    // </editor-fold>

    @Test
    public void close_isANoOp() throws Exception {
        final OpenAiCompatibleAiGenerationProvider provider = new OpenAiCompatibleAiGenerationProvider(
                LlamaCppJniConfig.builder("").build(), PROMPT_SUPPORT);
        provider.close();
        assertThat(provider.toString(), containsString("OpenAiCompatibleAiGenerationProvider"));
    }
}
