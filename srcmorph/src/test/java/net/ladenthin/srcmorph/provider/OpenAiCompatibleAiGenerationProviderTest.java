// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
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

    /** When set, the server answers with these Server-Sent Events instead of {@link #answer}. */
    private java.util.@Nullable List<String> events;

    /** An event stream of the same answer {@link #completion} gives, split into two tokens. */
    private static java.util.List<String> streamed(final String first, final String second, final String finishReason) {
        return java.util.Arrays.asList(
                ": ping\n\n",
                "data: {\"id\":\"gen-1\",\"object\":\"chat.completion.chunk\",\"choices\":[{\"index\":0,\"delta\":"
                        + "{\"role\":\"assistant\",\"content\":" + quote(first) + "},\"finish_reason\":null}]}\n\n",
                "data: {\"id\":\"gen-1\",\"object\":\"chat.completion.chunk\",\"choices\":[{\"index\":0,\"delta\":"
                        + "{\"content\":" + quote(second) + "},\"finish_reason\":\"" + finishReason + "\"}]}\n\n",
                "data: {\"id\":\"gen-1\",\"object\":\"chat.completion.chunk\",\"choices\":[],\"usage\":"
                        + "{\"prompt_tokens\":42,\"completion_tokens\":7,\"total_tokens\":49,"
                        + "\"prompt_tokens_details\":{\"cached_tokens\":40}}}\n\n",
                "data: [DONE]\n\n");
    }

    @BeforeEach
    public void startServer() throws IOException {
        status = 200;
        answer = completion("A tiny test class.", "stop");
        events = null;
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
            if (events != null) {
                // an event stream as a server sends it: chunked, one flushed write per event
                exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
                exchange.sendResponseHeaders(status, 0);
                try (OutputStream out = exchange.getResponseBody()) {
                    for (final String event : events) {
                        out.write(event.getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    }
                }
                return;
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

    // <editor-fold defaultstate="collapsed" desc="stream">
    @Test
    public void generate_stream_requestsAndAssemblesTheEventStream() throws Exception {
        // arrange
        events = streamed("A tiny ", "test class.", "stop");

        // act
        final String text = provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                .generate(REQUEST);

        // assert
        assertThat(text, is("A tiny test class."));
        assertThat(header("Accept"), is("text/event-stream"));
        final JsonNode body = MAPPER.readTree(requestBody);
        assertThat(body.get("stream").asBoolean(), is(true));
        assertThat(body.get("stream_options").get("include_usage").asBoolean(), is(true));
    }

    @Test
    public void generateWithTimings_stream_reportsTheUsageChunk() throws Exception {
        events = streamed("A tiny ", "test class.", "stop");
        final AiGenerationTimings timings = provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                .generateWithTimings(REQUEST);
        assertThat(timings.text(), is("A tiny test class."));
        assertThat(timings.promptTokens(), is(42));
        assertThat(timings.cachedPromptTokens(), is(40));
        assertThat(timings.predictedTokens(), is(7));
    }

    @Test
    public void generate_streamCutOff_warnsLikeTheJniProvider() throws Exception {
        // arrange
        events = streamed("cut ", "off", "length");
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(LlamaCppJniProviderSupport.class);
        logger.setLevel(Level.WARN);
        logger.addAppender(appender);
        try {
            // act
            provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT).generate(REQUEST);

            // assert
            assertThat(appender.list.size(), is(1));
            assertThat(appender.list.get(0).getFormattedMessage(), containsString("Test.java"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    public void generate_streamWithoutDone_fails() {
        events = java.util.Arrays.asList(
                "data: {\"choices\":[{\"index\":0,\"delta\":{\"content\":\"half\"},\"finish_reason\":null}]}\n\n");
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                        .generate(REQUEST));
        assertThat(thrown.getMessage(), startsWith("The stream ended without 'data: [DONE]' after "));
    }

    @Test
    public void generate_streamErrorStatus_throwsWithStatusAndBody() {
        // a server refuses before the stream starts with a plain JSON error, as the gateway's 429 does
        status = 429;
        answer = "{\"error\":{\"message\":\"Too many concurrent requests\"}}";
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                        .generate(REQUEST));
        assertThat(thrown.getMessage(), containsString("answered HTTP 429"));
        assertThat(thrown.getMessage(), containsString("Too many concurrent requests"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listModels">
    @Test
    public void listModels_getsTheModelListWithoutABodyAndParsesIt() throws Exception {
        // arrange
        answer = ModelListFixtures.COPILOT_GATEWAY;

        // act
        final java.util.List<RemoteModel> models = provider(
                        LlamaCppJniConfig.builder("").apiKeyEnv("COPILOT_GATEWAY_API_KEY").stream(true),
                        name -> "COPILOT_GATEWAY_API_KEY".equals(name) ? "gw-key" : null)
                .listModels();

        // assert
        assertThat(models.size(), is(3));
        assertThat(models.get(2).id(), is("mai-code-1.1-flash"));
        assertThat(requestMethod, is("GET"));
        assertThat(requestPath, is("/v1/models"));
        assertThat(requestBody, is(""));
        assertThat(header("Accept"), is("application/json"));
        assertThat(header("Content-type"), is(nullValue()));
        assertThat(header("X-title"), is(OpenAiCompatibleAiGenerationProvider.APPLICATION_TITLE));
        assertThat(header("Authorization"), is("Bearer gw-key"));
    }

    @Test
    public void listModels_errorStatus_throwsWithStatusAndBody() {
        status = 401;
        answer = "{\"error\":\"Unauthorized\"}";
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).listModels());
        assertThat(thrown.getMessage(), containsString("/v1/models answered HTTP 401: {\"error\":\"Unauthorized\"}"));
    }

    @Test
    public void listModels_keyForAnotherHostOverPlainHttp_isRefusedBeforeConnecting() {
        final OpenAiCompatibleAiGenerationProvider provider = new OpenAiCompatibleAiGenerationProvider(
                LlamaCppJniConfig.builder("")
                        .baseUrl("http://192.0.2.1:3030/v1")
                        .apiKeyEnv("K")
                        .build(),
                PROMPT_SUPPORT,
                name -> "key");
        final IOException thrown = assertThrows(IOException.class, provider::listModels);
        assertThat(thrown.getMessage(), startsWith("Refusing to send the API key from K in cleartext to 192.0.2.1"));
        assertThat(requestPath, is(nullValue()));
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
