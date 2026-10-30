// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfig;
import net.ladenthin.srcmorph.provider.ModelListFixtures;
import net.ladenthin.srcmorph.provider.RemoteModel;
import org.junit.jupiter.api.Test;

public class ListModelsEngineTest {

    private static final String GATEWAY = "http://127.0.0.1:3030/v1";

    private static final List<RemoteModel> MODELS = Arrays.asList(
            new RemoteModel("auto", 871793, null, null),
            new RemoteModel("GROK-4.7", 424794, null, null),
            new RemoteModel("grok-4.6", 424794, null, null),
            new RemoteModel("mai-code-1.1-flash", 127790, null, null));

    /** The configurations the lister was asked with, in order. */
    private final List<LlamaCppJniConfig> asked = new ArrayList<>();

    private List<RemoteModel> record(final LlamaCppJniConfig config) {
        asked.add(config);
        return MODELS;
    }

    private static AiModelDefinition definition(
            final String key, final String provider, final String baseUrl, final String apiKeyEnv) {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(key);
        definition.setProvider(provider);
        definition.setBaseUrl(baseUrl);
        definition.setApiKeyEnv(apiKeyEnv);
        return definition;
    }

    private static SrcMorphConfiguration configuration(
            final String runProvider, final AiModelDefinition... definitions) {
        final SrcMorphConfiguration configuration = new SrcMorphConfiguration();
        configuration.setGenerationProvider(runProvider);
        configuration.setAiDefinitions(new ArrayList<>(Arrays.asList(definitions)));
        return configuration;
    }

    private ModelListReport execute(final SrcMorphConfiguration configuration, final String filter)
            throws SrcMorphException {
        return new ListModelsEngine(configuration, filter, (config, prompts) -> record(config)).execute();
    }

    // <editor-fold defaultstate="collapsed" desc="which servers">
    @Test
    public void execute_asksEveryOpenAiCompatibleServerOnce_perBaseUrlAndKey() throws Exception {
        // arrange
        final AiModelDefinition local = definition("local", "llamacpp-jni", GATEWAY, "");
        local.setModelPath("model.gguf");
        final SrcMorphConfiguration configuration = configuration(
                "llamacpp-jni",
                local,
                definition("copilot", "openai-compatible", GATEWAY, "COPILOT_GATEWAY_API_KEY"),
                definition("copilot-again", "openai-compatible", GATEWAY + " ", " COPILOT_GATEWAY_API_KEY"),
                definition("copilot-other-key", "openai-compatible", GATEWAY, "OTHER_KEY"),
                definition("run-provider", "", "https://openrouter.ai/api/v1", "OPENROUTER_API_KEY"));

        // act
        final ModelListReport report = execute(configuration, "");

        // assert
        assertThat(asked.size(), is(2));
        assertThat(asked.get(0).baseUrl(), is(GATEWAY));
        assertThat(asked.get(0).apiKeyEnv(), is("COPILOT_GATEWAY_API_KEY"));
        assertThat(asked.get(1).apiKeyEnv(), is("OTHER_KEY"));
        assertThat(report.listings().size(), is(2));
        assertThat(report.listings().get(0).key(), is("copilot"));
        assertThat(report.listings().get(1).key(), is("copilot-other-key"));
    }

    @Test
    public void execute_aDefinitionWithoutProvider_takesTheRunsProvider() throws Exception {
        final ModelListReport report = execute(
                configuration(
                        "openai-compatible",
                        definition("openrouter", "", "https://openrouter.ai/api/v1", "OPENROUTER_API_KEY")),
                "");

        assertThat(asked.size(), is(1));
        assertThat(report.listings().get(0).baseUrl(), is("https://openrouter.ai/api/v1"));
    }

    @Test
    public void execute_noOpenAiCompatibleDefinition_fails() {
        final AiModelDefinition local = definition("local", "llamacpp-jni", GATEWAY, "");
        local.setModelPath("model.gguf");
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> execute(configuration("openai-compatible", local), ""));
        assertThat(
                thrown.getMessage(),
                is("None of the 1 aiDefinitions has the provider 'openai-compatible'; ListModels lists the models"
                        + " of such a definition's baseUrl."));
        assertThat(asked.isEmpty(), is(true));
    }

    @Test
    public void execute_noDefinitionsAtAll_fails() {
        final SrcMorphConfiguration configuration = new SrcMorphConfiguration();
        configuration.setGenerationProvider("openai-compatible");
        configuration.setAiDefinitions(null);
        assertThrows(SrcMorphException.class, () -> execute(configuration, ""));
    }

    @Test
    public void execute_aDefinitionWithoutKey_failsAsAConfigurationError() {
        final AiModelDefinition keyless = new AiModelDefinition();
        keyless.setProvider("openai-compatible");
        assertThrows(SrcMorphException.class, () -> execute(configuration("openai-compatible", keyless), ""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="filter">
    @Test
    public void execute_filter_keepsTheIdsThatContainIt_ignoringCaseAndSurroundingSpace() throws Exception {
        final ModelListReport report =
                execute(configuration("openai-compatible", definition("copilot", "", GATEWAY, "")), " Grok ");

        final ModelListReport.Listing listing = report.listings().get(0);
        assertThat(listing.listed(), is(4));
        assertThat(listing.models(), is(Arrays.asList(MODELS.get(1), MODELS.get(2))));
    }

    @Test
    public void execute_blankFilter_keepsEveryModel() throws Exception {
        final ModelListReport report =
                execute(configuration("openai-compatible", definition("copilot", "", GATEWAY, "")), "");

        assertThat(report.listings().get(0).models(), is(MODELS));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="failures">
    @Test
    public void execute_aServerThatCannotBeAsked_failsNamingTheDefinitionAndTheServer() {
        final IOException cause = new IOException("http://127.0.0.1:3030/v1/models answered HTTP 401: nope");
        final ListModelsEngine engine = new ListModelsEngine(
                configuration("openai-compatible", definition("copilot", "", GATEWAY, "")), "", (config, prompts) -> {
                    throw cause;
                });

        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        assertThat(
                thrown.getMessage(),
                is("Listing the models of aiDefinition 'copilot' at http://127.0.0.1:3030/v1 failed: "
                        + "http://127.0.0.1:3030/v1/models answered HTTP 401: nope"));
        assertThat(thrown.getCause(), is(sameInstance(cause)));
    }

    @Test
    public void execute_aBaseUrlThatIsNotHttp_failsAsWell() {
        final ListModelsEngine engine = new ListModelsEngine(
                configuration("openai-compatible", definition("copilot", "", "file:///etc", "")),
                "",
                (config, prompts) -> {
                    throw new IllegalArgumentException("baseUrl is not an http(s) URL: 'file:///etc'");
                });

        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        assertThat(
                thrown.getMessage(),
                is("Listing the models of aiDefinition 'copilot' at file:///etc failed: baseUrl is not an http(s)"
                        + " URL: 'file:///etc'"));
    }

    @Test
    public void constructor_nullArguments_throw() {
        final SrcMorphConfiguration configuration = new SrcMorphConfiguration();
        assertThrows(NullPointerException.class, () -> new ListModelsEngine(null, ""));
        assertThrows(NullPointerException.class, () -> new ListModelsEngine(configuration, null));
        assertThrows(NullPointerException.class, () -> new ListModelsEngine(configuration, "", null));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="over HTTP">
    /**
     * The public constructor asks the server for real: {@code GET /v1/models} on an in-process stub
     * that answers like jllama's server.
     *
     * @throws Exception if the stub cannot start or the run fails
     */
    @Test
    public void execute_publicConstructor_asksTheServerOverHttp() throws Exception {
        final List<String> paths = Collections.synchronizedList(new ArrayList<>());
        final HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            paths.add(
                    exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            final byte[] bytes = ModelListFixtures.JLLAMA.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        server.start();
        try {
            final String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1";

            final ModelListReport report = new ListModelsEngine(
                            configuration("openai-compatible", definition("jllama", "", baseUrl, "")), "")
                    .execute();

            assertThat(paths, is(Collections.singletonList("GET /v1/models")));
            assertThat(
                    report.listings().get(0).models(),
                    is(Collections.singletonList(new RemoteModel("smollm2", 0, null, null))));
        } finally {
            server.stop(0);
        }
    }
    // </editor-fold>
}
