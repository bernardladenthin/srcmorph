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
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.ToString;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import net.ladenthin.srcmorph.support.ReplaceWithLibrary;
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

    /** Path appended to {@code baseUrl}: the OpenAI model list. */
    static final String MODELS_PATH = "/models";

    /**
     * Where a model list entry names its token limit, as JSON pointers; the first positive one wins:
     * {@code context_length} (OpenRouter), {@code max_input_tokens} (the GitHub Copilot API Gateway),
     * {@code context_window} and {@code max_model_len} (other OpenAI-compatible servers, e.g. vLLM),
     * OpenRouter's top provider, then llama-server's running context and, failing that, its training
     * context.
     */
    private static final String[] CONTEXT_POINTERS = {
        "/context_length",
        "/max_input_tokens",
        "/context_window",
        "/max_model_len",
        "/top_provider/context_length",
        "/meta/n_ctx",
        "/meta/n_ctx_train"
    };

    /** The field of an entry that only aliases another listed model (the Copilot gateway's Cursor aliases). */
    private static final String ALIAS_FIELD = "aliased_model_id";

    /** OpenRouter states prices per token; the model list states them per this many tokens. */
    private static final int PRICE_SCALE_TOKENS = 6;

    /** How many characters of an error body go into the exception message. */
    static final int ERROR_BODY_EXCERPT = 500;

    /** {@code 400 Bad Request}: refused as malformed, or, with the wording, as too long for the context. */
    static final int HTTP_BAD_REQUEST = 400;

    /** {@code 408 Request Timeout}. */
    static final int HTTP_REQUEST_TIMEOUT = 408;

    /** {@code 413 Payload Too Large}: OpenRouter's answer to a prompt longer than the model's context. */
    static final int HTTP_PAYLOAD_TOO_LARGE = 413;

    /** {@code 429 Too Many Requests}. */
    static final int HTTP_TOO_MANY_REQUESTS = 429;

    /** First server-error status. */
    static final int HTTP_SERVER_ERROR_FIRST = 500;

    /** One past the last server-error status. */
    static final int HTTP_SERVER_ERROR_END = 600;

    /** How servers word a refused prompt that is longer than the context, in any case. */
    private static final Pattern CONTEXT_OVERFLOW_WORDING = Pattern.compile(
            "context[ _]length|maximum context|context window|too many tokens|prompt is too long"
                    + "|reduce the length|exceeds? the (?:available )?context|input token count"
                    + "|maximum number of tokens",
            Pattern.CASE_INSENSITIVE);

    /** The backoff before the first retry when the server sends no {@code Retry-After}. */
    static final long RETRY_BASE_DELAY_MILLIS = 1_000L;

    /** The longest backoff between two attempts. */
    static final long MAX_RETRY_BACKOFF_MILLIS = 60_000L;

    /** The longest {@code Retry-After} honoured; a server asking for more waits this long. */
    static final long MAX_RETRY_AFTER_MILLIS = 300_000L;

    /** Up to this fraction of the backoff is added as jitter. */
    static final double RETRY_JITTER_FRACTION = 0.25d;

    /**
     * A 429 that no wait cures: the account's quota or credit is used up (OpenAI's {@code insufficient_quota},
     * OpenRouter's credits), or Gemini's daily quota. Gemini words a per-minute limit as "exceeded your
     * current quota" too, which a wait does cure, so that wording counts only with a per-day quota id.
     */
    private static final Pattern QUOTA_EXHAUSTED = Pattern.compile(
            "insufficient_quota|insufficient credits|exceeded your current quota(?=[\\s\\S]*PerDay)",
            Pattern.CASE_INSENSITIVE);

    /** A 5xx that sending again cannot change: not implemented, HTTP version not supported. */
    private static final int HTTP_NOT_IMPLEMENTED = 501;

    /** See {@link #HTTP_NOT_IMPLEMENTED}. */
    private static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    /** Milliseconds per second. */
    private static final long MILLIS_PER_SECOND = 1_000L;

    /** Lowest HTTP status that counts as success. */
    private static final int HTTP_OK_FIRST = 200;

    /** First HTTP status above the success range. */
    private static final int HTTP_OK_END = 300;

    /** The plain-HTTP scheme prefix a {@code baseUrl} may start with (a local server). */
    static final String HTTP_SCHEME = "http://";

    /** The HTTPS scheme prefix a {@code baseUrl} may start with. */
    static final String HTTPS_SCHEME = "https://";

    /** Host name of the loopback interface, in any case. */
    private static final Pattern LOOPBACK_HOST_NAME = Pattern.compile("localhost", Pattern.CASE_INSENSITIVE);

    /**
     * An IPv4 loopback literal ({@code 127.0.0.0/8}). A literal, not a prefix: {@code 127.evil.com}
     * is a host name that resolves anywhere.
     */
    private static final Pattern IPV4_LOOPBACK = Pattern.compile("127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    /** A bracketed IPv6 literal as {@link URL#getHost()} returns it: hex digits and colons only. */
    private static final Pattern IPV6_LITERAL = Pattern.compile("\\[[0-9A-Fa-f:]{2,39}]");

    /** Appended to an error body cut at {@link #ERROR_BODY_EXCERPT}. */
    private static final String ELLIPSIS = "...";

    /** The {@code Accept} header of a request for one JSON answer. */
    static final String ACCEPT_JSON = "application/json";

    /** The {@code Accept} header of a request for a streamed answer. */
    static final String ACCEPT_EVENT_STREAM = "text/event-stream";

    /**
     * The jllama class that assembles a stream like {@link #assembleStream(String)} does. It is in
     * java-llama.cpp {@code main} but in no release yet; srcmorph carries its own copy until then (see
     * {@link ReplaceWithLibrary} and {@code TODO.md}).
     */
    static final String JLLAMA_STREAM_ASSEMBLER = "net.ladenthin.llama.json.ChatStreamAssembler";

    /** The release expected to carry {@link #JLLAMA_STREAM_ASSEMBLER}. */
    static final String JLLAMA_STREAM_ASSEMBLER_RELEASE =
            "net.ladenthin:llama after 6.0.0 (bernardladenthin/java-llama.cpp#595)";

    /** The field prefix of a Server-Sent Events line that carries data; every other line is skipped. */
    @ReplaceWithLibrary(value = JLLAMA_STREAM_ASSEMBLER, since = JLLAMA_STREAM_ASSEMBLER_RELEASE)
    static final String SSE_DATA_PREFIX = "data:";

    /** The data of the event that ends an OpenAI stream. */
    @ReplaceWithLibrary(value = JLLAMA_STREAM_ASSEMBLER, since = JLLAMA_STREAM_ASSEMBLER_RELEASE)
    static final String SSE_DONE = "[DONE]";

    /** The finish reason of an assembled answer whose stream named none. */
    @ReplaceWithLibrary(value = JLLAMA_STREAM_ASSEMBLER, since = JLLAMA_STREAM_ASSEMBLER_RELEASE)
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
    @ReplaceWithLibrary(value = JLLAMA_STREAM_ASSEMBLER, since = JLLAMA_STREAM_ASSEMBLER_RELEASE)
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
        return baseUrl() + CHAT_COMPLETIONS_PATH;
    }

    /**
     * The URL of the model list: {@code baseUrl}, as for {@link #endpoint()}, plus {@link #MODELS_PATH}.
     *
     * @return the models URL
     * @throws IllegalArgumentException if {@code baseUrl} is not an {@code http(s)} URL
     */
    String modelsEndpoint() {
        return baseUrl() + MODELS_PATH;
    }

    /**
     * Names the server a request limit counts for: the trimmed {@code baseUrl} without trailing
     * slashes, and the name of the key variable (two keys can have two limits on one server).
     *
     * @return the server key
     */
    String serverKey() {
        return withoutTrailingSlashes(config.baseUrl().trim()) + " "
                + config.apiKeyEnv().trim();
    }

    private String baseUrl() {
        final String base = config.baseUrl().trim();
        if (!base.startsWith(HTTP_SCHEME) && !base.startsWith(HTTPS_SCHEME)) {
            throw new IllegalArgumentException("baseUrl is not an http(s) URL: '" + base + "'");
        }
        return withoutTrailingSlashes(base);
    }

    private static String withoutTrailingSlashes(final String url) {
        String base = url;
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }

    /**
     * Reads the answer of {@code GET <baseUrl>/models}: the {@code data} array of an OpenAI model
     * list, one {@link RemoteModel} per entry with an {@code id}, sorted by id.
     *
     * <p>The token limit is the first positive of the {@link #CONTEXT_POINTERS}; the price is
     * OpenRouter's {@code pricing.prompt} / {@code pricing.completion}, per token there and per million
     * tokens here, and unknown when absent or negative (OpenRouter's router models state {@code -1}).
     * An entry that only aliases another ({@code aliased_model_id}) and a repeated id are left out.</p>
     *
     * @param json the response body
     * @return the models, sorted by id
     * @throws IOException if the body is not JSON or has no {@code data} array
     */
    static List<RemoteModel> parseModels(final String json) throws IOException {
        final JsonNode data = MAPPER.readTree(json).path("data");
        if (!data.isArray()) {
            throw new IOException("The answer is not an OpenAI model list (no 'data' array): " + excerpt(json));
        }
        final Map<String, RemoteModel> models = new LinkedHashMap<>();
        for (final JsonNode entry : data) {
            final JsonNode id = entry.path("id");
            if (!id.isTextual() || entry.hasNonNull(ALIAS_FIELD) || models.containsKey(id.asText())) {
                continue;
            }
            final JsonNode pricing = entry.path("pricing");
            models.put(
                    id.asText(),
                    new RemoteModel(
                            id.asText(),
                            contextLength(entry),
                            pricePerMillion(pricing.path("prompt")),
                            pricePerMillion(pricing.path("completion"))));
        }
        final List<RemoteModel> sorted = new ArrayList<>(models.values());
        sorted.sort(Comparator.comparing(RemoteModel::id));
        return sorted;
    }

    private static int contextLength(final JsonNode entry) {
        for (final String pointer : CONTEXT_POINTERS) {
            final int value = entry.at(pointer).asInt();
            if (value > 0) {
                return value;
            }
        }
        return RemoteModel.UNKNOWN_CONTEXT_LENGTH;
    }

    private static @Nullable BigDecimal pricePerMillion(final JsonNode perToken) {
        if (!perToken.isTextual() && !perToken.isNumber()) {
            return null;
        }
        final BigDecimal price;
        try {
            price = new BigDecimal(perToken.asText().trim());
        } catch (final NumberFormatException e) {
            return null;
        }
        if (price.signum() < 0) {
            return null;
        }
        return price.movePointRight(PRICE_SCALE_TOKENS).stripTrailingZeros();
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
        final String key = environment.apply(config.apiKeyEnv().trim());
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
     * @return {@code true} for {@code localhost} in any case, {@code 127.x.x.x} and the IPv6 loopback in
     *         any of its written forms ({@code [::1]}, {@code [0:0:0:0:0:0:0:1]})
     */
    static boolean isLoopbackHost(final String host) {
        if (LOOPBACK_HOST_NAME.matcher(host).matches()
                || IPV4_LOOPBACK.matcher(host).matches()) {
            return true;
        }
        if (!IPV6_LITERAL.matcher(host).matches()) {
            return false;
        }
        try {
            // an IPv6 literal (hex digits and colons only): getByName checks its format, it looks nothing up
            return InetAddress.getByName(host.substring(1, host.length() - 1)).isLoopbackAddress();
        } catch (final UnknownHostException e) {
            return false;
        }
    }

    /**
     * What would fail the first request of a model, found before any request is made: an
     * {@code apiKeyEnv} whose variable is not set, or a key that would go in cleartext to another
     * machine (a plain {@code http} {@code baseUrl} that is not loopback).
     *
     * @param baseUrl     the model's {@code baseUrl}, an {@code http(s)} URL
     * @param apiKeyEnv   the model's {@code apiKeyEnv}
     * @param environment looks up an environment variable by name, {@code null} when unset
     * @return the problem, or {@code null} when there is none
     */
    static @Nullable String keyProblem(
            final String baseUrl, final String apiKeyEnv, final Function<String, @Nullable String> environment) {
        final String name = apiKeyEnv.trim();
        if (name.isEmpty()) {
            return null;
        }
        final String key = environment.apply(name);
        if (key == null || key.trim().isEmpty()) {
            return "its apiKeyEnv names " + name + ", which is not set in this process's environment";
        }
        final String base = baseUrl.trim();
        if (!base.startsWith(HTTP_SCHEME)) {
            return null;
        }
        final String host;
        try {
            host = new URL(base).getHost();
        } catch (final MalformedURLException e) {
            return "its baseUrl is not a URL: '" + base + "'";
        }
        return isLoopbackHost(host)
                ? null
                : "its key from " + name + " would go in cleartext to " + host
                        + "; use an https baseUrl, a loopback server, or an empty apiKeyEnv";
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
     * Whether a request that failed with this status may succeed when sent again: a timeout of the
     * server ({@code 408}), a rate limit ({@code 429}, unless its body says the quota or credit is used
     * up) or a server error ({@code 5xx}, but not {@code 501} and {@code 505}, which no retry changes).
     *
     * @param status the HTTP status
     * @param body   the error body
     * @return {@code true} for 408, a 429 of a rate limit, and 500 to 599 except 501 and 505
     */
    static boolean isRetryable(final int status, final String body) {
        if (status == HTTP_TOO_MANY_REQUESTS) {
            return !QUOTA_EXHAUSTED.matcher(body).find();
        }
        return status == HTTP_REQUEST_TIMEOUT
                || (status >= HTTP_SERVER_ERROR_FIRST
                        && status < HTTP_SERVER_ERROR_END
                        && status != HTTP_NOT_IMPLEMENTED
                        && status != HTTP_VERSION_NOT_SUPPORTED);
    }

    /**
     * Whether a failed answer says the prompt is longer than the model's context: a {@code 413}, or a
     * {@code 400} whose body says so in one of the wordings servers use.
     *
     * @param status the HTTP status
     * @param body   the error body
     * @return {@code true} for a context overflow
     */
    static boolean isContextOverflow(final int status, final String body) {
        if (status == HTTP_PAYLOAD_TOO_LARGE) {
            return true;
        }
        return status == HTTP_BAD_REQUEST
                && CONTEXT_OVERFLOW_WORDING.matcher(body).find();
    }

    /**
     * How long to wait before retry number {@code retry}: the server's {@code Retry-After} in seconds
     * when it sends one (at most {@link #MAX_RETRY_AFTER_MILLIS}), otherwise an exponential backoff from
     * {@link #RETRY_BASE_DELAY_MILLIS}, at most {@link #MAX_RETRY_BACKOFF_MILLIS}, plus up to a quarter
     * of it as jitter so that clients that failed together do not retry together.
     *
     * @param retryAfter the {@code Retry-After} header, or {@code null}; seconds of any length are capped,
     *                   a date instead of seconds is ignored
     * @param retry      the retry about to be made, from {@code 1}
     * @param jitter     a random number in {@code [0, 1)}
     * @return the delay in milliseconds
     */
    static long retryDelayMillis(
            final @Nullable String retryAfter, final int retry, final double jitter, final long nowMillis) {
        final String value = retryAfter == null ? "" : retryAfter.trim();
        long asked = retryAfterMillis(value);
        if (asked < 0) {
            asked = retryAtMillis(value, nowMillis);
        }
        if (asked >= 0) {
            return asked;
        }
        final long backoff = Math.min(MAX_RETRY_BACKOFF_MILLIS, RETRY_BASE_DELAY_MILLIS << Math.min(retry - 1, 30));
        return backoff + (long) (backoff * RETRY_JITTER_FRACTION * jitter);
    }

    /**
     * The wait until a {@code Retry-After} given as an HTTP date (RFC 9110 allows a date as well as seconds),
     * at most {@link #MAX_RETRY_AFTER_MILLIS}; a date in the past waits nothing.
     *
     * @param date      the trimmed header value
     * @param nowMillis the current time
     * @return the milliseconds, or {@code -1} when the value is no HTTP date
     */
    private static long retryAtMillis(final String date, final long nowMillis) {
        try {
            final long at = ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .toInstant()
                    .toEpochMilli();
            return Math.min(MAX_RETRY_AFTER_MILLIS, Math.max(0L, at - nowMillis));
        } catch (final DateTimeParseException e) {
            return -1;
        }
    }

    /**
     * The {@code Retry-After} seconds as milliseconds, at most {@link #MAX_RETRY_AFTER_MILLIS}, read digit
     * by digit so that no length overflows.
     *
     * @param seconds the trimmed header value
     * @return the milliseconds, or {@code -1} when the value is not a number of seconds
     */
    private static long retryAfterMillis(final String seconds) {
        if (seconds.isEmpty()) {
            return -1;
        }
        long millis = 0;
        for (int i = 0; i < seconds.length(); i++) {
            final char c = seconds.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
            millis = Math.min(MAX_RETRY_AFTER_MILLIS, millis * 10 + (c - '0') * MILLIS_PER_SECOND);
        }
        return millis;
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
        return endpoint + " answered HTTP " + status + ": " + excerpt(body);
    }

    /**
     * The message of the exception a 2xx answer without a choice fails with: endpoint, file and the start
     * of the body, which shows whether it was a proxy's HTML page, an error object or something else.
     *
     * @param endpoint   the URL that was called
     * @param sourceFile the file the answer was for
     * @param body       the response body
     * @return the message
     */
    static String noChoiceMessage(final String endpoint, final Path sourceFile, final String body) {
        return endpoint + " answered without a completion for " + sourceFile + ": " + excerpt(body);
    }

    private static String excerpt(final String body) {
        return body.length() > ERROR_BODY_EXCERPT ? body.substring(0, ERROR_BODY_EXCERPT) + ELLIPSIS : body;
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
