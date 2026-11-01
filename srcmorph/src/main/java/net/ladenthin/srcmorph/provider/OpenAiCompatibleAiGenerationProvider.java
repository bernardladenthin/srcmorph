// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import lombok.ToString;
import net.ladenthin.llama.json.ChatResponseParser;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * fails the call with the status and the start of the body. A {@code 408}, {@code 429}, {@code 5xx}
 * or a read timeout is sent again up to {@code maxRetries} times, after the server's
 * {@code Retry-After} or an exponential backoff with jitter; a prompt the server refuses as longer
 * than the model's context fails with {@link AiContextOverflowException}, so the caller can shrink
 * it.</p>
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

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiCompatibleAiGenerationProvider.class);

    /** Waits the given milliseconds between two attempts of a request. */
    @FunctionalInterface
    interface RetryWaiter {
        /**
         * Waits.
         *
         * @param millis how long
         * @throws InterruptedIOException if the thread is interrupted while it waits
         */
        void await(long millis) throws InterruptedIOException;
    }

    /**
     * The waiter of a real run: parks the thread until the delay is over, again after a spurious wake-up
     * (srcmorph does not sleep threads, see the {@code noThreadSleep} architecture rule).
     */
    private static final RetryWaiter PARKING_WAITER = millis -> {
        final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millis);
        for (long left = deadline - System.nanoTime(); left > 0; left = deadline - System.nanoTime()) {
            LockSupport.parkNanos(left);
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException("interrupted while waiting to retry a request");
            }
        }
    };

    private final OpenAiCompatibleProviderSupport support;

    /** Holds requests back to {@code requestsPerMinute}; {@code null} without a limit. */
    private final @Nullable RequestRateLimiter limiter;

    @ToString.Exclude
    private final LlamaCppJniProviderSupport truncationSupport;

    @ToString.Exclude
    private final RetryWaiter waiter;

    /** The usage of the last {@link #generate} call. */
    private AiGenerationUsage lastUsage = AiGenerationUsage.NONE;

    /** How often a request that failed with a retryable status or a timeout is sent again. */
    private final int maxRetries;

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
        this(config, promptSupport, environment, PARKING_WAITER);
    }

    /**
     * Creates a provider with an explicit environment lookup and retry waiter (tests).
     *
     * @param config        the model configuration
     * @param promptSupport the prompt definitions
     * @param environment   looks up an environment variable by name, {@code null} when unset
     * @param waiter        waits between two attempts of a request
     */
    OpenAiCompatibleAiGenerationProvider(
            final LlamaCppJniConfig config,
            final AiPromptSupport promptSupport,
            final Function<String, @Nullable String> environment,
            final RetryWaiter waiter) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(promptSupport, "promptSupport");
        this.support = new OpenAiCompatibleProviderSupport(config, promptSupport, environment);
        this.truncationSupport = new LlamaCppJniProviderSupport(config, promptSupport);
        this.waiter = Objects.requireNonNull(waiter, "waiter");
        this.maxRetries = Math.max(0, config.maxRetries());
        this.limiter = config.requestsPerMinute() > 0
                ? RequestRateLimiter.shared(support.serverKey(), config.requestsPerMinute())
                : null;
    }

    @Override
    public String generate(final AiGenerationRequest request) throws IOException {
        lastUsage = AiGenerationUsage.NONE;
        final ChatResponse response = chatResponse(request);
        lastUsage = AiGenerationUsage.of(response);
        final String text = completionParser.parseCompletion(response.getFirstContent());
        truncationSupport.requireCompleteAnswer(request, response, text);
        return text;
    }

    @Override
    public AiGenerationUsage lastUsage() {
        return lastUsage;
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

    /**
     * What would fail the first request of a model, found before any request is made: an
     * {@code apiKeyEnv} whose variable is not set in the process environment, or a key that would go in
     * cleartext to another machine.
     *
     * @param baseUrl   the model's {@code baseUrl}, an {@code http(s)} URL
     * @param apiKeyEnv the model's {@code apiKeyEnv}
     * @return the problem, or {@code null} when there is none
     */
    public static @Nullable String keyProblem(final String baseUrl, final String apiKeyEnv) {
        return OpenAiCompatibleProviderSupport.keyProblem(baseUrl, apiKeyEnv, System::getenv);
    }

    private ChatResponse chatResponse(final AiGenerationRequest request) throws IOException {
        final String json =
                support.completionJson(exchange(support.endpoint(), support.accept(), support.requestBody(request)));
        final ChatResponse response = chatResponseParser.parseResponse(json);
        if (response.getChoices().isEmpty()) {
            // A 2xx answer without a choice: an HTML page of a proxy, an {"error": ...} object, or no
            // completion at all. The body says which.
            throw new AiIncompleteAnswerException(
                    AiIncompleteAnswerException.Reason.NO_ANSWER,
                    OpenAiCompatibleProviderSupport.noChoiceMessage(support.endpoint(), request.sourceFile(), json));
        }
        return response;
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
        for (int retry = 1; ; retry++) {
            final @Nullable String retryAfter;
            final IOException failure;
            try {
                return exchangeOnce(endpoint, accept, body);
            } catch (final RetryableAnswerException e) {
                retryAfter = e.retryAfter;
                failure = e;
            } catch (final SocketTimeoutException e) {
                retryAfter = null;
                failure = e;
            }
            if (retry > maxRetries) {
                throw failure;
            }
            final long delay = OpenAiCompatibleProviderSupport.retryDelayMillis(
                    retryAfter, retry, ThreadLocalRandom.current().nextDouble());
            LOGGER.warn(
                    "{} failed ({}); retry {}/{} in {} ms",
                    endpoint,
                    String.valueOf(failure.getMessage()),
                    retry,
                    maxRetries,
                    delay);
            waiter.await(delay);
        }
    }

    /** A failed answer that may succeed when sent again, with the server's {@code Retry-After}. */
    private static final class RetryableAnswerException extends IOException {

        private static final long serialVersionUID = 1L;

        private final @Nullable String retryAfter;

        RetryableAnswerException(final String message, final @Nullable String retryAfter) {
            super(message);
            this.retryAfter = retryAfter;
        }
    }

    /**
     * One attempt of a request.
     *
     * @param endpoint the URL
     * @param accept   the {@code Accept} header
     * @param body     the JSON body, {@code null} for a {@code GET}
     * @return the response body of a 2xx answer
     * @throws AiContextOverflowException if the server refuses the prompt as longer than the context
     * @throws IOException                if the request fails or the answer is outside 2xx
     */
    private String exchangeOnce(final String endpoint, final String accept, final @Nullable String body)
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
                final String message = OpenAiCompatibleProviderSupport.errorMessage(endpoint, status, text);
                if (OpenAiCompatibleProviderSupport.isContextOverflow(status, text)) {
                    throw new AiContextOverflowException(message);
                }
                if (OpenAiCompatibleProviderSupport.isRetryable(status)) {
                    throw new RetryableAnswerException(message, connection.getHeaderField("Retry-After"));
                }
                throw new IOException(message);
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
