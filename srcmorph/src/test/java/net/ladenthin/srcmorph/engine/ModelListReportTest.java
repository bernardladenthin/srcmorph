// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.provider.RemoteModel;
import org.junit.jupiter.api.Test;

public class ModelListReportTest {

    private static AiGenerationConfig gatewayPattern() {
        final AiGenerationConfig pattern = new AiGenerationConfig();
        pattern.setBaseUrl(" http://127.0.0.1:3030/v1 ");
        pattern.setApiKeyEnv("COPILOT_GATEWAY_API_KEY");
        pattern.setStream(true);
        pattern.setContextSize(200000);
        pattern.setMaxOutputTokens(2048);
        pattern.setTemperature(0.2f);
        return pattern;
    }

    private static AiGenerationConfig openRouterPattern() {
        final AiGenerationConfig pattern = new AiGenerationConfig();
        pattern.setApiKeyEnv("OPENROUTER_API_KEY");
        pattern.setMaxOutputTokens(4096);
        pattern.setTemperature(0.2f);
        pattern.setReasoningEffort("none");
        return pattern;
    }

    private static final List<RemoteModel> GATEWAY_MODELS = Arrays.asList(
            new RemoteModel("auto", 871793, null, null), new RemoteModel("claude-haiku-4.5", 127790, null, null));

    private static final RemoteModel GROK =
            new RemoteModel("x-ai/grok-4.7", 500000, new BigDecimal("2"), new BigDecimal("6"));

    // <editor-fold defaultstate="collapsed" desc="render">
    @Test
    public void render_gateway_tableAsCommentsThenOneEntryPerModel() {
        final ModelListReport report = new ModelListReport(
                "",
                Collections.singletonList(ModelListReport.Listing.of("copilot", gatewayPattern(), 3, GATEWAY_MODELS)));

        assertThat(
                report.render(),
                is("# aiDefinition 'copilot': http://127.0.0.1:3030/v1, 3 models\n"
                        + "#   model              context   USD/M in  USD/M out\n"
                        + "#   auto                871793          -          -\n"
                        + "#   claude-haiku-4.5    127790          -          -\n"
                        + "#\n"
                        + "aiDefinitions:\n"
                        + "  - key: \"copilot-auto\"\n"
                        + "    provider: \"openai-compatible\"\n"
                        + "    baseUrl: \"http://127.0.0.1:3030/v1\"\n"
                        + "    remoteModel: \"auto\"\n"
                        + "    apiKeyEnv: \"COPILOT_GATEWAY_API_KEY\"\n"
                        + "    stream: true\n"
                        + "    contextSize: 200000\n"
                        + "    maxOutputTokens: 2048\n"
                        + "    temperature: 0.2\n"
                        + "  - key: \"copilot-claude-haiku-4.5\"\n"
                        + "    provider: \"openai-compatible\"\n"
                        + "    baseUrl: \"http://127.0.0.1:3030/v1\"\n"
                        + "    remoteModel: \"claude-haiku-4.5\"\n"
                        + "    apiKeyEnv: \"COPILOT_GATEWAY_API_KEY\"\n"
                        + "    stream: true\n"
                        + "    contextSize: 127790\n"
                        + "    maxOutputTokens: 2048\n"
                        + "    temperature: 0.2\n"));
    }

    @Test
    public void render_filteredOpenRouter_namesTheFilterThePriceAndAChangedReasoningEffort() {
        final ModelListReport report = new ModelListReport(
                " grok ",
                Collections.singletonList(ModelListReport.Listing.of(
                        "openrouter", openRouterPattern(), 2, Collections.singletonList(GROK))));

        assertThat(
                report.render(),
                is("# aiDefinition 'openrouter': https://openrouter.ai/api/v1, 2 models, 1 matching 'grok'\n"
                        + "#   model           context   USD/M in  USD/M out\n"
                        + "#   x-ai/grok-4.7    500000          2          6\n"
                        + "#\n"
                        + "aiDefinitions:\n"
                        + "  - key: \"openrouter-x-ai/grok-4.7\"\n"
                        + "    provider: \"openai-compatible\"\n"
                        + "    baseUrl: \"https://openrouter.ai/api/v1\"\n"
                        + "    remoteModel: \"x-ai/grok-4.7\"\n"
                        + "    apiKeyEnv: \"OPENROUTER_API_KEY\"\n"
                        + "    contextSize: 32768\n"
                        + "    maxOutputTokens: 4096\n"
                        + "    temperature: 0.2\n"
                        + "    reasoningEffort: \"none\"\n"));
    }

    @Test
    public void render_noKeyNoLimitNoStream_leavesThoseFieldsOut() {
        final AiGenerationConfig pattern = new AiGenerationConfig();
        pattern.setBaseUrl("http://localhost:8080/v1");
        pattern.setApiKeyEnv("  ");

        final String rendered = new ModelListReport(
                        "  ",
                        Collections.singletonList(ModelListReport.Listing.of(
                                "local",
                                pattern,
                                1,
                                Collections.singletonList(new RemoteModel("local", 0, null, null)))))
                .render();

        assertThat(
                rendered,
                is("# aiDefinition 'local': http://localhost:8080/v1, 1 models\n"
                        + "#   model   context   USD/M in  USD/M out\n"
                        + "#   local         -          -          -\n"
                        + "#\n"
                        + "aiDefinitions:\n"
                        + "  - key: \"local-local\"\n"
                        + "    provider: \"openai-compatible\"\n"
                        + "    baseUrl: \"http://localhost:8080/v1\"\n"
                        + "    remoteModel: \"local\"\n"
                        + "    contextSize: 32768\n"
                        + "    maxOutputTokens: 128\n"
                        + "    temperature: 0.15\n"));
    }

    @Test
    public void render_nothingMatches_isAnEmptyListAfterEveryServersHeading() {
        final ModelListReport report = new ModelListReport(
                "zzz",
                Arrays.asList(
                        ModelListReport.Listing.of("copilot", gatewayPattern(), 3, Collections.emptyList()),
                        ModelListReport.Listing.of("openrouter", openRouterPattern(), 2, Collections.emptyList())));

        assertThat(
                report.render(),
                is("# aiDefinition 'copilot': http://127.0.0.1:3030/v1, 3 models, 0 matching 'zzz'\n"
                        + "#\n"
                        + "# aiDefinition 'openrouter': https://openrouter.ai/api/v1, 2 models, 0 matching 'zzz'\n"
                        + "#\n"
                        + "aiDefinitions: []\n"));
    }

    @Test
    public void render_twoServers_oneListWithTheEntriesOfBoth() {
        final String rendered = new ModelListReport(
                        "",
                        Arrays.asList(
                                ModelListReport.Listing.of(
                                        "copilot",
                                        gatewayPattern(),
                                        1,
                                        Collections.singletonList(GATEWAY_MODELS.get(0))),
                                ModelListReport.Listing.of(
                                        "openrouter", openRouterPattern(), 1, Collections.singletonList(GROK))))
                .render();

        assertThat(rendered.indexOf("aiDefinitions:"), is(rendered.lastIndexOf("aiDefinitions:")));
        assertThat(rendered.indexOf("# aiDefinition 'openrouter'") < rendered.indexOf("aiDefinitions:"), is(true));
        assertThat(
                rendered.indexOf("  - key: \"copilot-auto\"") < rendered.indexOf("  - key: \"openrouter-x-ai"),
                is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="contextSize / quote">
    @Test
    public void contextSize_isLoweredOnlyToASmallerKnownLimit() {
        assertThat(ModelListReport.contextSize(100, RemoteModel.UNKNOWN_CONTEXT_LENGTH), is(100));
        assertThat(ModelListReport.contextSize(100, 99), is(99));
        assertThat(ModelListReport.contextSize(100, 100), is(100));
        assertThat(ModelListReport.contextSize(100, 101), is(100));
        assertThat(ModelListReport.contextSize(100, 1), is(1));
    }

    @Test
    public void quote_escapesBackslashAndDoubleQuote() {
        assertThat(ModelListReport.quote("a\"b\\c"), is("\"a\\\"b\\\\c\""));
        assertThat(ModelListReport.quote(""), is("\"\""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listings">
    @Test
    public void listings_areACopyAndUnmodifiable() {
        final List<ModelListReport.Listing> given = new ArrayList<>();
        given.add(ModelListReport.Listing.of("copilot", gatewayPattern(), 3, GATEWAY_MODELS));
        final ModelListReport report = new ModelListReport("", given);
        given.clear();

        assertThat(report.listings().size(), is(1));
        assertThrows(
                UnsupportedOperationException.class, () -> report.listings().clear());
    }

    @Test
    public void listing_copiesThePatternAtCreation() {
        final AiGenerationConfig pattern = gatewayPattern();
        final List<RemoteModel> models = new ArrayList<>(GATEWAY_MODELS);
        final ModelListReport.Listing listing = ModelListReport.Listing.of("copilot", pattern, 3, models);
        pattern.setBaseUrl("http://changed");
        models.clear();

        assertThat(listing.key(), is("copilot"));
        assertThat(listing.baseUrl(), is("http://127.0.0.1:3030/v1"));
        assertThat(listing.apiKeyEnv(), is("COPILOT_GATEWAY_API_KEY"));
        assertThat(listing.stream(), is(true));
        assertThat(listing.contextSize(), is(200000));
        assertThat(listing.maxOutputTokens(), is(2048));
        assertThat(listing.temperature(), is(0.2f));
        assertThat(listing.reasoningEffort(), is(AiGenerationConfig.DEFAULT_REASONING_EFFORT));
        assertThat(listing.listed(), is(3));
        assertThat(listing.models(), is(GATEWAY_MODELS));
        assertThrows(UnsupportedOperationException.class, () -> listing.models().clear());
    }

    @Test
    public void constructors_nullArguments_throw() {
        assertThrows(NullPointerException.class, () -> new ModelListReport(null, Collections.emptyList()));
        assertThrows(
                NullPointerException.class,
                () -> ModelListReport.Listing.of(null, gatewayPattern(), 0, Collections.emptyList()));
    }
    // </editor-fold>
}
