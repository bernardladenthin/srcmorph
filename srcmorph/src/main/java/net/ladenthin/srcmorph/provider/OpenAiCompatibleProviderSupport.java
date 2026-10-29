// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.ToString;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Everything {@link OpenAiCompatibleAiGenerationProvider} does that needs no network: the request
 * body, the endpoint, the API key lookup, the status check, the error message and the timings.
 *
 * <p>Split from the provider for the same reason {@link LlamaCppJniProviderSupport} is split from
 * the JNI provider: this class is on the 100% PIT gate, the provider's socket handling is not
 * (removing a timeout or a {@code disconnect()} call changes nothing a test can observe), and the
 * stub-server tests cover the provider instead.</p>
 */
@ToString
final class OpenAiCompatibleProviderSupport {

    /** Path appended to {@code baseUrl}: the OpenAI chat-completions endpoint. */
    static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    /** How many characters of an error body go into the exception message. */
    static final int ERROR_BODY_EXCERPT = 500;

    /** Lowest HTTP status that counts as success. */
    private static final int HTTP_OK_FIRST = 200;

    /** First HTTP status above the success range. */
    private static final int HTTP_OK_END = 300;

    /** The plain-HTTP scheme prefix a {@code baseUrl} may start with (a local server). */
    static final String HTTP_SCHEME = "http://";

    /** The HTTPS scheme prefix a {@code baseUrl} may start with. */
    static final String HTTPS_SCHEME = "https://";

    /** Host name of the loopback interface. */
    private static final String LOOPBACK_HOST_NAME = "localhost";

    /**
     * An IPv4 loopback literal ({@code 127.0.0.0/8}). A literal, not a prefix: {@code 127.evil.com}
     * is a host name that resolves anywhere.
     */
    private static final Pattern IPV4_LOOPBACK = Pattern.compile("127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    /** The IPv6 loopback address as {@link URL#getHost()} returns it, in brackets. */
    private static final String IPV6_LOOPBACK = "[::1]";

    /** Appended to an error body cut at {@link #ERROR_BODY_EXCERPT}. */
    private static final String ELLIPSIS = "...";

    /** The {@code Accept} header of a request for one JSON answer. */
    static final String ACCEPT_JSON = "application/json";

    /** The {@code Accept} header of a request for a streamed answer. */
    static final String ACCEPT_EVENT_STREAM = "text/event-stream";

    /** The field prefix of a Server-Sent Events line that carries data; every other line is skipped. */
    static final String SSE_DATA_PREFIX = "data:";

    /** The data of the event that ends an OpenAI stream. */
    static final String SSE_DONE = "[DONE]";

    /** The finish reason of an assembled answer whose stream named none. */
    private static final String NO_FINISH_REASON = "";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final LlamaCppJniConfig config;

    @ToString.Exclude
    private final AiPromptSupport promptSupport;

    @ToString.Exclude
    private final Function<String, @Nullable String> environment;

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates the support for one model.
     *
     * @param config        the model configuration (its {@code remoteModel}, {@code baseUrl},
     *                      {@code apiKeyEnv} and sampling knobs)
     * @param promptSupport the prompt definitions
     * @param environment   looks up an environment variable by name, {@code null} when unset
     */
    OpenAiCompatibleProviderSupport(
            final LlamaCppJniConfig config,
            final AiPromptSupport promptSupport,
            final Function<String, @Nullable String> environment) {
        this.config = Objects.requireNonNull(config, "config");
        this.promptSupport = Objects.requireNonNull(promptSupport, "promptSupport");
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    /**
     * The JSON body of one chat-completions request: the same two messages the {@code llamacpp-jni}
     * provider sends, plus the portable sampling knobs. {@code top_k} and {@code min_p} go out only
     * when positive, {@code seed} only when set ({@code >= 0}), {@code stop} only when non-empty,
     * {@code model} and {@code reasoning.effort} only when non-blank. {@code stream} is the model's
     * switch; a streamed request also asks for the usage chunk ({@code stream_options.include_usage}),
     * which carries the token counts the timings report.
     *
     * @param request the file to describe
     * @return the request body
     * @throws IOException if the body cannot be serialised
     */
    String requestBody(final AiGenerationRequest request) throws IOException {
        final ObjectNode body = MAPPER.createObjectNode();
        final String remoteModel = config.remoteModel();
        if (!compatibilityHelper.isBlank(remoteModel)) {
            body.put("model", remoteModel);
        }
        final ArrayNode messages = body.putArray("messages");
        messages.addObject().put("role", "system").put("content", promptSupport.systemPrompt(request.promptKey()));
        messages.addObject()
                .put("role", "user")
                .put("content", promptSupport.userMessage(request.sourceFile(), request.sourceText()));
        body.put("max_tokens", config.maxOutputTokens());
        body.put("temperature", config.temperature());
        body.put("top_p", config.topP());
        if (config.topK() > 0) {
            body.put("top_k", config.topK());
        }
        if (config.minP() > 0.0f) {
            body.put("min_p", config.minP());
        }
        body.put("repetition_penalty", config.repeatPenalty());
        final int seed = config.seed();
        if (seed >= 0) {
            body.put("seed", seed);
        }
        final List<String> stopStrings = config.stopStrings();
        if (!stopStrings.isEmpty()) {
            final ArrayNode stop = body.putArray("stop");
            for (final String s : stopStrings) {
                stop.add(s);
            }
        }
        final String reasoningEffort = config.reasoningEffort();
        if (!compatibilityHelper.isBlank(reasoningEffort)) {
            body.putObject("reasoning").put("effort", reasoningEffort);
        }
        body.put("stream", config.stream());
        if (config.stream()) {
            body.putObject("stream_options").put("include_usage", true);
        }
        return MAPPER.writeValueAsString(body);
    }

    /**
     * The {@code Accept} header of the request: an event stream when the model streams, JSON otherwise.
     *
     * @return {@link #ACCEPT_EVENT_STREAM} or {@link #ACCEPT_JSON}
     */
    String accept() {
        return config.stream() ? ACCEPT_EVENT_STREAM : ACCEPT_JSON;
    }

    /**
     * The chat-completion JSON of a successful answer: the body itself, or, when the model streams,
     * the body's events assembled by {@link #assembleStream(String)}.
     *
     * @param responseBody the body of a 2xx answer
     * @return a chat-completion JSON for {@code ChatResponseParser}
     * @throws IOException if a streamed body is not a complete OpenAI stream
     */
    String completionJson(final String responseBody) throws IOException {
        return config.stream() ? assembleStream(responseBody) : responseBody;
    }

    /**
     * Assembles an OpenAI chat-completions event stream into the chat-completion JSON a non-streaming
     * request would have been answered with, as far as srcmorph reads it: the first choice's message
     * content (the {@code delta.content} of every chunk, in order), its last finish reason, and the
     * usage object of the usage chunk.
     *
     * <p>Only {@code data:} lines count; blank lines, comments (a server's {@code : ping} heartbeat
     * during a long prefill) and other fields are skipped, and nothing after {@code data: [DONE]} is
     * read. A stream that ends without {@code [DONE]} fails instead of passing for a short answer, and
     * so does a chunk that carries an {@code error} (a provider failing in the middle of the stream).</p>
     *
     * @param sse the whole response body
     * @return the assembled chat-completion JSON
     * @throws IOException if a data line is not JSON, a chunk carries an error, or {@code [DONE]} is missing
     */
    static String assembleStream(final String sse) throws IOException {
        final StringBuilder content = new StringBuilder();
        String finishReason = NO_FINISH_REASON;
        @Nullable JsonNode usage = null;
        boolean done = false;
        // readLine ends a line at LF, CRLF or CR, the three line ends an event stream may use
        final BufferedReader lines = new BufferedReader(new StringReader(sse));
        for (String line = lines.readLine(); line != null; line = lines.readLine()) {
            if (!line.startsWith(SSE_DATA_PREFIX)) {
                continue;
            }
            final String data = line.substring(SSE_DATA_PREFIX.length()).trim();
            if (SSE_DONE.equals(data)) {
                done = true;
                break;
            }
            final JsonNode chunk = MAPPER.readTree(data);
            if (chunk.hasNonNull("error")) {
                throw new IOException("The stream reported an error: " + chunk.get("error"));
            }
            for (final JsonNode choice : chunk.path("choices")) {
                final JsonNode delta = choice.path("delta").path("content");
                if (delta.isTextual()) {
                    content.append(delta.asText());
                }
                final JsonNode reason = choice.path("finish_reason");
                if (reason.isTextual()) {
                    finishReason = reason.asText();
                }
            }
            final JsonNode chunkUsage = chunk.path("usage");
            if (chunkUsage.isObject()) {
                usage = chunkUsage;
            }
        }
        if (!done) {
            throw new IOException(
                    "The stream ended without 'data: " + SSE_DONE + "' after " + sse.length() + " characters");
        }
        final ObjectNode completion = MAPPER.createObjectNode();
        final ObjectNode choice = completion.putArray("choices").addObject();
        choice.putObject("message").put("role", "assistant").put("content", content.toString());
        choice.put("finish_reason", finishReason);
        if (usage != null) {
            completion.set("usage", usage);
        }
        return MAPPER.writeValueAsString(completion);
    }

    /**
     * The endpoint the requests go to: {@code baseUrl}, trimmed and without trailing slashes, plus
     * {@link #CHAT_COMPLETIONS_PATH}.
     *
     * @return the chat-completions URL
     * @throws IllegalArgumentException if {@code baseUrl} is not an {@code http(s)} URL, so no other
     *                                  scheme (e.g. {@code file:}) ever reaches the connection
     */
    String endpoint() {
        String base = config.baseUrl().trim();
        if (!base.startsWith(HTTP_SCHEME) && !base.startsWith(HTTPS_SCHEME)) {
            throw new IllegalArgumentException("baseUrl is not an http(s) URL: '" + base + "'");
        }
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + CHAT_COMPLETIONS_PATH;
    }

    /**
     * The API key from the environment variable {@code apiKeyEnv} names.
     *
     * @return the trimmed key, or {@code null} when no variable is named, or it is unset or blank
     */
    @Nullable
    String apiKey() {
        if (compatibilityHelper.isBlank(config.apiKeyEnv())) {
            return null;
        }
        final String key = environment.apply(config.apiKeyEnv());
        if (key == null || compatibilityHelper.isBlank(key)) {
            return null;
        }
        return key.trim();
    }

    /**
     * The API key to send to {@code endpoint}: {@link #apiKey()}, but never in cleartext to another
     * machine. A key goes out over {@code https}, or over plain {@code http} to a loopback host only
     * (a local {@code llama-server} or {@code NativeServer}); anything else fails before a byte is
     * sent. Without that, a model that sets a plain-{@code http} {@code baseUrl} would send the key its
     * {@code apiKeyEnv} names readable to every hop. Which host may receive a key at all is the
     * model's own decision: no {@code apiKeyEnv}, no key ({@code AiGenerationConfig.DEFAULT_API_KEY_ENV}
     * is empty).
     *
     * @param endpoint the URL the request goes to (from {@link #endpoint()})
     * @return the key, or {@code null} when none is configured
     * @throws IOException if a key is configured and {@code endpoint} is plain {@code http} to a host
     *                     that is not loopback, or if {@code endpoint} cannot be parsed
     */
    @Nullable
    String apiKeyFor(final String endpoint) throws IOException {
        final String key = apiKey();
        if (key == null || !endpoint.startsWith(HTTP_SCHEME)) {
            return key;
        }
        final String host = new URL(endpoint).getHost();
        if (!isLoopbackHost(host)) {
            throw new IOException("Refusing to send the API key from " + config.apiKeyEnv()
                    + " in cleartext to " + host + ": use an https baseUrl, a loopback server, or an empty"
                    + " apiKeyEnv for a server that needs no key");
        }
        return key;
    }

    /**
     * Whether a host names the local machine's loopback interface.
     *
     * @param host the host part of a URL, as {@link URL#getHost()} returns it
     * @return {@code true} for {@code localhost} (exactly, lower case), {@code 127.x.x.x} and {@code [::1]}
     */
    static boolean isLoopbackHost(final String host) {
        return LOOPBACK_HOST_NAME.equals(host) || IPV4_LOOPBACK.matcher(host).matches() || IPV6_LOOPBACK.equals(host);
    }

    /**
     * Whether an HTTP status is a success (2xx).
     *
     * @param status the status
     * @return {@code true} for 200 to 299
     */
    static boolean isSuccess(final int status) {
        return status >= HTTP_OK_FIRST && status < HTTP_OK_END;
    }

    /**
     * The message of the exception a non-2xx answer fails with: endpoint, status and the start of
     * the body (a server's error JSON says what was wrong, e.g. an unknown model or a missing key).
     *
     * @param endpoint the URL that was called
     * @param status   the HTTP status
     * @param body     the error body, possibly empty
     * @return the message
     */
    static String errorMessage(final String endpoint, final int status, final String body) {
        final String excerpt =
                body.length() > ERROR_BODY_EXCERPT ? body.substring(0, ERROR_BODY_EXCERPT) + ELLIPSIS : body;
        return endpoint + " answered HTTP " + status + ": " + excerpt;
    }

    /**
     * The timings of one call. A remote server reports token counts in {@code usage} but no
     * throughput, so the decode rate is the generated tokens over the wall-clock seconds of the whole
     * call (prefill and network included), and the prefill rate is zero, which callers treat as
     * unknown.
     *
     * @param text     the parsed completion
     * @param response the chat response
     * @param seconds  wall-clock seconds of the call
     * @return the timings
     */
    static AiGenerationTimings timings(final String text, final ChatResponse response, final double seconds) {
        final Usage usage = response.getUsage();
        final int completionTokens = (int) usage.getCompletionTokens();
        final double decodeRate = seconds > 0.0d ? completionTokens / seconds : 0.0d;
        return new AiGenerationTimings(
                text, (int) usage.getPromptTokens(), (int) usage.getCachedTokens(), 0.0d, completionTokens, decodeRate);
    }
}
