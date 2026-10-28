// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import net.ladenthin.llama.server.NativeServer;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.NativeLlamaAvailability;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs {@link OpenAiCompatibleAiGenerationProvider} against real servers, one test per kind:
 *
 * <ul>
 *   <li><b>jllama's {@code NativeServer}</b>, started in-process on the committed SmolLM2 GGUF. Runs
 *       wherever the model-backed tests run (see {@link NativeLlamaAvailability}); no network.</li>
 *   <li><b>A server that is already running</b>, named by {@value #BASE_URL_PROPERTY} (plus
 *       {@value #MODEL_PROPERTY} and {@value #API_KEY_ENV_PROPERTY} if it needs them); skipped
 *       without it. CI points it at a {@code llama-server} container.</li>
 *   <li><b>OpenRouter</b>, when {@value #OPENROUTER_MODEL_PROPERTY} names a model and
 *       {@code OPENROUTER_API_KEY} holds a key; skipped otherwise, so a plain {@code mvn test} never
 *       spends money. CI sets the property and passes the repository secret; a pull request from a
 *       fork or from Dependabot gets no secret and skips.</li>
 * </ul>
 *
 * <p>The {@code test} job of {@code publish.yml} checks the Surefire report afterwards, so none of
 * these turns into a silent skip there.</p>
 */
public class OpenAiCompatibleLiveTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiCompatibleLiveTest.class);

    /** System property naming a running server's base URL; that test is skipped without it. */
    public static final String BASE_URL_PROPERTY = "net.ladenthin.srcmorph.openai.baseUrl";

    /** System property naming the model to request from that server; empty for a single-model one. */
    public static final String MODEL_PROPERTY = "net.ladenthin.srcmorph.openai.model";

    /** System property naming the environment variable that holds that server's key; none by default. */
    public static final String API_KEY_ENV_PROPERTY = "net.ladenthin.srcmorph.openai.apiKeyEnv";

    /** System property naming the OpenRouter model to test; the OpenRouter test is skipped without it. */
    public static final String OPENROUTER_MODEL_PROPERTY = "net.ladenthin.srcmorph.openai.openRouterModel";

    /** Environment variable holding the OpenRouter key, named explicitly as a model must. */
    private static final String OPENROUTER_KEY_ENV = "OPENROUTER_API_KEY";

    /** Output budget: enough for a reasoning model's low-effort thinking plus a short summary. */
    private static final int MAX_OUTPUT_TOKENS = 1024;

    /** Context of the in-process server: the prompt plus {@link #MAX_OUTPUT_TOKENS}, with room. */
    private static final int NATIVE_SERVER_CONTEXT = 8192;

    /** How long the in-process server may take to load the model and answer {@code /health}. */
    private static final long NATIVE_SERVER_READY_MILLIS = TimeUnit.SECONDS.toMillis(120);

    /** Pause between two {@code /health} polls. */
    private static final long HEALTH_POLL_MILLIS = 200;

    private static final String SOURCE = "package com.example;\n\n"
            + "public final class Greeter {\n"
            + "    public String greet(final String name) {\n"
            + "        return \"Hello, \" + name + \"!\";\n"
            + "    }\n"
            + "}\n";

    @Test
    public void generateWithTimings_nativeServer_describesTheFile() throws Exception {
        // arrange
        NativeLlamaAvailability.assumeAvailable();
        final int port = freePort();
        try (NativeServer server = new NativeServer(
                        "-m",
                        NativeLlamaAvailability.modelPath(),
                        "--host",
                        "127.0.0.1",
                        "--port",
                        String.valueOf(port),
                        "-c",
                        String.valueOf(NATIVE_SERVER_CONTEXT),
                        "-t",
                        String.valueOf(NativeLlamaAvailability.THREADS))
                .start()) {
            final String baseUrl = "http://127.0.0.1:" + port + "/v1";
            awaitHealthy("http://127.0.0.1:" + port + "/health", server);

            // act + assert
            describeGreeter(baseUrl, "", "");
        }
    }

    @Test
    public void generateWithTimings_runningServer_describesTheFile() throws Exception {
        final String baseUrl = System.getProperty(BASE_URL_PROPERTY, "");
        Assumptions.assumeTrue(!baseUrl.trim().isEmpty(), "-D" + BASE_URL_PROPERTY + " is not set");
        describeGreeter(baseUrl, System.getProperty(MODEL_PROPERTY, ""), System.getProperty(API_KEY_ENV_PROPERTY, ""));
    }

    @Test
    public void generateWithTimings_openRouter_describesTheFile() throws Exception {
        final String model = System.getProperty(OPENROUTER_MODEL_PROPERTY, "");
        Assumptions.assumeTrue(!model.trim().isEmpty(), "-D" + OPENROUTER_MODEL_PROPERTY + " is not set");
        final String key = System.getenv(OPENROUTER_KEY_ENV);
        Assumptions.assumeTrue(
                key != null && !key.trim().isEmpty(),
                OPENROUTER_KEY_ENV + " is not set (a pull request from a fork or from Dependabot gets no secret)");
        describeGreeter(AiGenerationConfig.DEFAULT_BASE_URL, model, OPENROUTER_KEY_ENV);
    }

    /**
     * Asks the server to describe a small class, through the factory as an engine would, and checks
     * that text and token counts come back. The text is logged so a human can judge it.
     */
    private static void describeGreeter(final String baseUrl, final String model, final String apiKeyEnv)
            throws IOException {
        // arrange
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .provider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE)
                .baseUrl(baseUrl)
                .remoteModel(model)
                .apiKeyEnv(apiKeyEnv)
                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .build();
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiMdHeader header = new AiMdHeader(
                "Greeter.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "00000000",
                "2026-10-28T00:00:00Z",
                "2026-10-28T00:00:00Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiGenerationRequest request = new AiGenerationRequest(
                CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Greeter.java"), SOURCE, header);

        // act
        final AiGenerationTimings timings;
        try (AiGenerationProvider provider = new AiGenerationProviderFactory()
                .create(AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI, config, promptSupport)) {
            timings = provider.generateWithTimings(request);
        }

        // assert
        LOGGER.info(
                "{} answered with {} prompt / {} generated tokens:\n{}",
                baseUrl,
                timings.promptTokens(),
                timings.predictedTokens(),
                timings.text());
        assertThat(timings.text().trim().isEmpty(), is(false));
        assertThat(timings.promptTokens(), is(greaterThan(0)));
        assertThat(timings.predictedTokens(), is(greaterThan(0)));
    }

    private static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** Polls {@code /health} until the server answers 200, failing early when its worker exits. */
    private static void awaitHealthy(final String healthUrl, final NativeServer server) throws Exception {
        final long deadline = System.currentTimeMillis() + NATIVE_SERVER_READY_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            if (!server.isRunning()) {
                throw new AssertionError("NativeServer stopped before it became healthy");
            }
            if (healthy(healthUrl)) {
                return;
            }
            TimeUnit.MILLISECONDS.sleep(HEALTH_POLL_MILLIS);
        }
        throw new AssertionError("NativeServer was not healthy within " + NATIVE_SERVER_READY_MILLIS + " ms");
    }

    private static boolean healthy(final String healthUrl) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(healthUrl).openConnection();
            try {
                connection.setConnectTimeout((int) HEALTH_POLL_MILLIS);
                connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(5));
                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } finally {
                connection.disconnect();
            }
        } catch (final IOException e) {
            // Not listening yet.
            return false;
        }
    }
}
