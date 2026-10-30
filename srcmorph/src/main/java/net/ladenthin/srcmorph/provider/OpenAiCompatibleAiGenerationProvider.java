// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.ToString;
import net.ladenthin.llama.json.ChatResponseParser;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.jspecify.annotations.Nullable;

/**
 * {@link AiGenerationProvider} over an OpenAI-compatible chat-completions API: OpenRouter (the
 * default {@code baseUrl}), a llama.cpp {@code llama-server}, jllama's {@code NativeServer}, or any
 * other server that answers {@code POST <baseUrl>/chat/completions}.
 *
 * <p>The request carries the same two messages the {@code llamacpp-jni} provider sends, and the
 * response is parsed by the same {@link ChatResponseParser} and {@link AiCompletionParser}, so a
 * remote model fills the {@code .ai.md} body exactly as a local one does. Only the portable sampling
 * knobs go out (see {@link OpenAiCompatibleProviderSupport#requestBody}); threads, GPU layers, KV
 * cache, DRY sampling and prompt caching belong to a local llama.cpp context and are not sent.</p>
 *
 * <p>The API key is read from the environment variable {@code apiKeyEnv} names and sent as
 * {@code Authorization: Bearer}; it never sits in a configuration file. No variable, or an empty
 * one, sends no key, which a local server without {@code --api-key} accepts. An answer outside 2xx
 * fails the call with the status and the start of the body; there is no automatic retry.</p>
 *
 * <p>With {@code stream} set, the answer is requested as Server-Sent Events, read to the end and
 * assembled into the same chat response (see {@link OpenAiCompatibleProviderSupport#assembleStream});
 * the read timeout then applies between two events instead of to the whole answer.</p>
 *
 * <p>With {@code requestsPerMinute} set, every request first waits until fewer than that many requests
 * to the same server started in the last minute (see {@link RequestRateLimiter}).</p>
 *
 * <p>{@link #listModels()} asks the same server which models it serves ({@code GET <baseUrl>/models}),
 * with the same key and the same refusal to send it in cleartext to another machine.</p>
 */
@ToString
public final class OpenAiCompatibleAiGenerationProvider implements AiGenerationProvider {

    /** Connect timeout, in milliseconds. */
    public static final int CONNECT_TIMEOUT_MILLIS = 30_000;

    /** Read timeout, in milliseconds: a long file on a slow model can take minutes to answer. */
    public static final int READ_TIMEOUT_MILLIS = 600_000;

    /** The {@code X-Title} header, which OpenRouter shows as the application of these requests. */
    public static final String APPLICATION_TITLE = "srcmorph";

    /** Size of the buffer a response is read with. */
    private static final int READ_BUFFER_SIZE = 8192;

    private final OpenAiCompatibleProviderSupport support;

    /** Holds requests back to {@code requestsPerMinute}; {@code null} without a limit. */
    private final @Nullable RequestRateLimiter limiter;

    @ToString.Exclude
    private final LlamaCppJniProviderSupport truncationSupport;

    private final AiCompletionParser completionParser = new AiCompletionParser();
    private final ChatResponseParser chatResponseParser = new ChatResponseParser();

    /**
     * Creates a provider that reads the API key from the process environment.
     *
     * @param config        the model configuration (its {@code remoteModel}, {@code baseUrl},
     *                      {@code apiKeyEnv} and sampling knobs)
     * @param promptSupport the prompt definitions
     */
    public OpenAiCompatibleAiGenerationProvider(final LlamaCppJniConfig config, final AiPromptSupport promptSupport) {
        this(config, promptSupport, System::getenv);
    }

    /**
     * Creates a provider with an explicit environment lookup (tests).
     *
     * @param config        the model configuration
     * @param promptSupport the prompt definitions
     * @param environment   looks up an environment variable by name, {@code null} when unset
     */
    OpenAiCompatibleAiGenerationProvider(
            final LlamaCppJniConfig config,
            final AiPromptSupport promptSupport,
            final Function<String, @Nullable String> environment) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(promptSupport, "promptSupport");
        this.support = new OpenAiCompatibleProviderSupport(config, promptSupport, environment);
        this.truncationSupport = new LlamaCppJniProviderSupport(config, promptSupport);
        this.limiter = config.requestsPerMinute() > 0
                ? RequestRateLimiter.shared(support.serverKey(), config.requestsPerMinute())
                : null;
    }

    @Override
    public String generate(final AiGenerationRequest request) throws IOException {
        final ChatResponse response = chatResponse(request);
        truncationSupport.warnOnTruncatedAnswer(request, response);
        return completionParser.parseCompletion(response.getFirstContent());
    }

    @Override
    public AiGenerationTimings generateWithTimings(final AiGenerationRequest request) throws IOException {
        final long start = System.nanoTime();
        final ChatResponse response = chatResponse(request);
        final double seconds = (System.nanoTime() - start) / (double) TimeUnit.SECONDS.toNanos(1);
        truncationSupport.warnOnTruncatedAnswer(request, response);
        final String text = completionParser.parseCompletion(response.getFirstContent());
        return OpenAiCompatibleProviderSupport.timings(text, response, seconds);
    }

    /**
     * The models the server lists under {@code GET <baseUrl>/models}, sorted by id (see
     * {@link OpenAiCompatibleProviderSupport#parseModels(String)}).
     *
     * @return the listed models
     * @throws IOException if the server cannot be reached, answers outside 2xx, the key must not go to
     *                     it, or the answer is not an OpenAI model list
     */
    public List<RemoteModel> listModels() throws IOException {
        return OpenAiCompatibleProviderSupport.parseModels(
                exchange(support.modelsEndpoint(), OpenAiCompatibleProviderSupport.ACCEPT_JSON, null));
    }

    private ChatResponse chatResponse(final AiGenerationRequest request) throws IOException {
        return chatResponseParser.parseResponse(
                support.completionJson(exchange(support.endpoint(), support.accept(), support.requestBody(request))));
    }

    /**
     * One request: a {@code POST} of {@code body}, or a {@code GET} when it is {@code null}.
     *
     * @param endpoint the URL
     * @param accept   the {@code Accept} header
     * @param body     the JSON body, {@code null} for a {@code GET}
     * @return the response body of a 2xx answer
     * @throws IOException if the request fails or the answer is outside 2xx
     */
    private String exchange(final String endpoint, final String accept, final @Nullable String body)
            throws IOException {
        // Resolved before the connection is opened: a key that must not go to this endpoint fails here.
        final String key = support.apiKeyFor(endpoint);
        if (limiter != null) {
            limiter.acquire();
        }
        final HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        try {
            connection.setRequestMethod(body == null ? "GET" : "POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);
            connection.setRequestProperty("Accept", accept);
            connection.setRequestProperty("X-Title", APPLICATION_TITLE);
            if (key != null) {
                connection.setRequestProperty("Authorization", "Bearer " + key);
            }
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                // No setFixedLengthStreamingMode: in streaming mode HttpURLConnection treats a 401 as an
                // authentication challenge it cannot retry and drops the error body, which is exactly the
                // body that says why (a missing or wrong key). Buffered, it still sends a Content-Length.
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }
            final int status = connection.getResponseCode();
            if (!OpenAiCompatibleProviderSupport.isSuccess(status)) {
                final InputStream error = connection.getErrorStream();
                final String text = error == null ? "" : read(error);
                throw new IOException(OpenAiCompatibleProviderSupport.errorMessage(endpoint, status, text));
            }
            return read(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

    private static String read(final InputStream in) throws IOException {
        try (InputStream stream = in) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final byte[] chunk = new byte[READ_BUFFER_SIZE];
            int n;
            while ((n = stream.read(chunk)) != -1) {
                buffer.write(chunk, 0, n);
            }
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
