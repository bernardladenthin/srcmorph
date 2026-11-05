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

    /** Statuses answered before {@link #status}, one per request; empty after they are used up. */
    private final java.util.Deque<Integer> firstStatuses = new java.util.ArrayDeque<>();

    /** The {@code Retry-After} header sent with a status of {@link #firstStatuses}, or {@code null}. */
    private @Nullable String retryAfter;

    /** How many requests the server received. */
    private int requests;

    /** Milliseconds the server waits before it answers at all. */
    private int stallBeforeAnswer;

    /** Milliseconds the server waits after the first part of its answer. */
    private int stallInAnswer;

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
            requests++;
            if (stallBeforeAnswer > 0) {
                sleep(stallBeforeAnswer);
            }
            if (stallInAnswer > 0) {
                // the headers and the first bytes go out, then the answer stops
                exchange.sendResponseHeaders(status, 0);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write("{\"choices\":".getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    sleep(stallInAnswer);
                } catch (final IOException e) {
                    // the client gave up and closed the connection
                }
                return;
            }
            final Integer first = firstStatuses.poll();
            if (first != null) {
                if (retryAfter != null) {
                    exchange.getResponseHeaders().set("Retry-After", retryAfter);
                }
                final byte[] error = "{\"error\":\"busy\"}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(first, error.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(error);
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

    private static void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** A provider whose reads wait {@link #SHORT_READ_TIMEOUT} at most. */
    private OpenAiCompatibleAiGenerationProvider impatientProvider(final LlamaCppJniConfig.Builder builder) {
        return new OpenAiCompatibleAiGenerationProvider(
                builder.baseUrl(baseUrl()).build(), PROMPT_SUPPORT, NO_ENVIRONMENT, waits::add, SHORT_READ_TIMEOUT);
    }

    /** A read timeout a test can wait for. */
    private static final int SHORT_READ_TIMEOUT = 200;

    /** Longer than {@link #SHORT_READ_TIMEOUT}: the server stalls past it. */
    private static final int STALL = 1_500;

    // <editor-fold defaultstate="collapsed" desc="timeouts">
    @Test
    public void generate_noAnswerWithinTheReadTimeout_isNotSentAgain() {
        // arrange: the request was sent and may be billed; the server does not answer in time
        stallBeforeAnswer = STALL;

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> impatientProvider(LlamaCppJniConfig.builder("").maxRetries(3))
                        .generate(REQUEST));

        // assert
        assertThat(thrown.getMessage(), containsString("the request is not sent again"));
        assertThat(thrown instanceof java.io.InterruptedIOException, is(false));
        assertThat(requests, is(1));
        assertThat(waits.isEmpty(), is(true));
    }

    @Test
    public void generate_anAnswerThatStopsWhileRead_isNotSentAgain() {
        // arrange: the answer starts, then stalls
        stallInAnswer = STALL;

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> impatientProvider(LlamaCppJniConfig.builder("").maxRetries(3))
                        .generate(REQUEST));

        // assert
        assertThat(thrown.getMessage(), containsString("stopped answering"));
        assertThat(thrown instanceof java.io.InterruptedIOException, is(false));
        assertThat(requests, is(1));
        // no wait for a second attempt: the one-threaded stub could not count one in time
        assertThat(waits.isEmpty(), is(true));
    }

    @Test
    public void listModels_aTimeout_isRetried_andEndsAsAPlainIOException() {
        // arrange: a GET may be sent again; every attempt times out
        stallBeforeAnswer = STALL;

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> impatientProvider(LlamaCppJniConfig.builder("").maxRetries(1))
                        .listModels());

        // assert: two attempts, one wait, and no InterruptedIOException that would end a whole run (the
        // one-threaded stub is still asleep in the first exchange when the second attempt gives up, so the
        // attempts are counted by the provider's message, not by the server)
        assertThat(thrown.getMessage(), containsString("timed out 2 times"));
        assertThat(thrown instanceof java.io.InterruptedIOException, is(false));
        assertThat(waits.size(), is(1));
    }
    // </editor-fold>

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/";
    }

    /** The waits a provider made between attempts; a test waits for nothing. */
    private final java.util.List<Long> waits = new java.util.ArrayList<>();

    private OpenAiCompatibleAiGenerationProvider provider(
            final LlamaCppJniConfig.Builder builder, final Function<String, @Nullable String> environment) {
        return new OpenAiCompatibleAiGenerationProvider(
                builder.baseUrl(baseUrl()).build(), PROMPT_SUPPORT, environment, waits::add);
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
        status = 403;
        answer = "{\"error\":{\"message\":\"No auth credentials found\",\"code\":403}}";

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.getMessage(), containsString("/v1/chat/completions answered HTTP 403"));
        assertThat(thrown.getMessage(), containsString("No auth credentials found"));
    }

    @Test
    public void generate_unauthorized_namesTheKey() {
        // arrange: a POST goes in streaming mode, where a 401's body cannot be read
        status = 401;
        answer = "{\"error\":{\"message\":\"No auth credentials found\",\"code\":401}}";

        // act
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.getMessage(), containsString("/v1/chat/completions answered HTTP 401"));
        assertThat(thrown.getMessage(), containsString("the key was refused, or none was sent"));
        assertThat(requests, is(1));
    }

    @Test
    public void generate_rateLimitedTwice_retriesAfterTheServersRetryAfterAndSucceeds() throws Exception {
        firstStatuses.add(429);
        firstStatuses.add(503);
        retryAfter = "2";

        final String text =
                provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST);

        assertThat(text, is("A tiny test class."));
        assertThat(requests, is(3));
        assertThat(waits, is(java.util.Arrays.asList(2_000L, 2_000L)));
    }

    @Test
    public void generate_serverErrorBeyondMaxRetries_failsWithTheLastAnswer() {
        firstStatuses.add(502);
        firstStatuses.add(502);

        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder("").maxRetries(1), NO_ENVIRONMENT)
                        .generate(REQUEST));

        assertThat(thrown.getMessage(), containsString("answered HTTP 502"));
        assertThat(requests, is(2));
        assertThat(waits.size(), is(1));
    }

    @Test
    public void generate_clientError_isNotRetried() {
        status = 401;
        answer = "{\"error\":\"no key\"}";

        assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        assertThat(requests, is(1));
        assertThat(waits.isEmpty(), is(true));
    }

    @Test
    public void generate_promptLongerThanTheContext_isAContextOverflow() {
        status = 400;
        answer = "{\"error\":{\"message\":\"This endpoint's maximum context length is 131072 tokens\"}}";

        final AiContextOverflowException thrown = assertThrows(
                AiContextOverflowException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        assertThat(thrown.getMessage(), containsString("answered HTTP 400"));
        assertThat(requests, is(1));
    }

    @Test
    public void generate_errorStatusWithoutBody_throwsWithTheStatus() {
        status = 503;
        answer = "";
        final IOException thrown = assertThrows(
                IOException.class,
                () -> provider(LlamaCppJniConfig.builder("").maxRetries(0), NO_ENVIRONMENT)
                        .generate(REQUEST));
        assertThat(thrown.getMessage(), containsString("answered HTTP 503: "));
    }

    @Test
    public void generate_truncatedAnswer_isRefusedWithWhatWasWritten() {
        // arrange
        answer = completion("cut off", "length");

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.TRUNCATED));
        assertThat(thrown.getMessage(), containsString("Test.java"));
        assertThat(thrown.partialText(), is("cut off"));
    }

    /**
     * The TypeScript measurement of 2026-11-05: an OpenRouter generation that failed half-way came
     * back as a 2xx with 694 tokens, finish reason {@code error} and a text ending mid-sentence, and was
     * written as a finished summary.
     */
    @Test
    public void generate_answerTheProviderStoppedWithAnError_isRefusedWithWhatWasWritten() {
        // arrange
        answer = completion("- test paging (", "error");

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.ABORTED));
        assertThat(thrown.getMessage(), containsString("Test.java ended with finish reason 'error'"));
        assertThat(thrown.partialText(), is("- test paging ("));
    }

    @Test
    public void generate_streamTheProviderStoppedWithAnError_isRefused() {
        // arrange
        events = streamed("- test ", "paging (", "error");

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                        .generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.ABORTED));
        assertThat(thrown.partialText(), is("- test paging ("));
    }

    @Test
    public void generate_emptyAnswer_isRefused() {
        // arrange
        answer = completion("", "stop");

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.EMPTY));
        assertThat(thrown.partialText(), is(""));
    }

    @Test
    public void generate_successWithoutAChoice_isRefusedWithTheBody() {
        // arrange: a 2xx answer carrying an error object instead of a completion
        answer = "{\"error\":{\"message\":\"upstream overloaded\"}}";

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT).generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.NO_ANSWER));
        assertThat(thrown.getMessage(), containsString("answered without a completion for "));
        assertThat(thrown.getMessage(), containsString("upstream overloaded"));
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

    /** A calibration answer cut at the budget is warned about under this provider's logger, not llama.cpp's. */
    @Test
    public void generateWithTimings_anAnswerCutAtTheBudget_warnsUnderTheRemoteProvidersLogger() throws Exception {
        // arrange
        answer = completion("cut off", "length");
        final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
                org.slf4j.LoggerFactory.getLogger(OpenAiCompatibleAiGenerationProvider.class);
        final ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender =
                new ch.qos.logback.core.read.ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            // act
            provider(LlamaCppJniConfig.builder("").maxOutputTokens(64), NO_ENVIRONMENT)
                    .generateWithTimings(REQUEST);

            // assert
            assertThat(appender.list.size(), is(1));
            assertThat(
                    appender.list.get(0).getFormattedMessage(),
                    startsWith("Generated text for Test.java was cut off at the 64-token output budget"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
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
    public void generate_streamCutOff_isRefusedWithWhatWasWritten() {
        // arrange
        events = streamed("cut ", "off", "length");

        // act
        final AiIncompleteAnswerException thrown = assertThrows(
                AiIncompleteAnswerException.class,
                () -> provider(LlamaCppJniConfig.builder("").stream(true), NO_ENVIRONMENT)
                        .generate(REQUEST));

        // assert
        assertThat(thrown.reason(), is(AiIncompleteAnswerException.Reason.TRUNCATED));
        assertThat(thrown.partialText(), is("cut off"));
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
    public void requestsPerMinute_countsEveryRequestInTheServersSharedWindow() throws Exception {
        // arrange
        answer = ModelListFixtures.JLLAMA;
        final OpenAiCompatibleAiGenerationProvider provider =
                provider(LlamaCppJniConfig.builder("").requestsPerMinute(5), NO_ENVIRONMENT);
        final RequestRateLimiter shared = RequestRateLimiter.shared(
                "http://127.0.0.1:" + server.getAddress().getPort() + "/v1 ", 5);

        // act
        provider.listModels();
        answer = completion("A tiny test class.", "stop");
        provider.generate(REQUEST);

        // assert
        assertThat(shared.startedInWindow(), is(2));
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

    @Test
    public void lastUsage_isTheUsageOfTheLastAnswer() throws Exception {
        final OpenAiCompatibleAiGenerationProvider provider = provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT);
        assertThat(provider.lastUsage(), is(AiGenerationUsage.NONE));

        provider.generate(REQUEST);

        assertThat(provider.lastUsage(), is(new AiGenerationUsage(1, 42, 7, 40, "stop")));
    }

    @Test
    public void lastUsage_ofACutOffAnswer_isKeptAlthoughTheCallThrows() throws Exception {
        answer = completion("half an answ", "length");
        final OpenAiCompatibleAiGenerationProvider provider = provider(LlamaCppJniConfig.builder(""), NO_ENVIRONMENT);

        org.junit.jupiter.api.Assertions.assertThrows(java.io.IOException.class, () -> provider.generate(REQUEST));

        assertThat(provider.lastUsage().finishReason(), is("length"));
        assertThat(provider.lastUsage().completionTokens(), is(7L));
    }
}
