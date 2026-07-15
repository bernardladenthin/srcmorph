// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Path;
import net.ladenthin.srcmorph.cli.Main;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import org.junit.jupiter.api.Test;

/**
 * Round-trips the ad-hoc {@code test-fixtures/minimal-generate.{json,yaml}} pair (kept private to
 * this module — the public, documented {@code examples/} fixture set is a later migration step)
 * through both {@link Main#fromJson(String)} and {@link Main#fromYaml(String)} and asserts the
 * resulting {@link CConfiguration}/{@link SrcMorphConfiguration} object graphs are equivalent.
 *
 * <p>Locks in the shared-config-object contract described in {@link SrcMorphConfiguration}'s own
 * Javadoc: the same field names bind identically whether the source is Maven XML (plexus, not
 * exercised here), JSON, or YAML.</p>
 */
public class ConfigBindingTest {

    private static final Path MINIMAL_GENERATE_JSON = Path.of("src/test/resources/test-fixtures/minimal-generate.json");
    private static final Path MINIMAL_GENERATE_YAML = Path.of("src/test/resources/test-fixtures/minimal-generate.yaml");

    @Test
    public void jsonFixture_bindsExpectedObjectGraph() throws IOException {
        final CConfiguration configuration = Main.fromJson(Main.readString(MINIMAL_GENERATE_JSON));

        assertConfigurationMatchesFixture(configuration);
    }

    @Test
    public void yamlFixture_bindsExpectedObjectGraph() throws IOException {
        final CConfiguration configuration = Main.fromYaml(Main.readString(MINIMAL_GENERATE_YAML));

        assertConfigurationMatchesFixture(configuration);
    }

    @Test
    public void jsonAndYamlFixtures_bindToEquivalentConfigurations() throws IOException {
        final CConfiguration fromJson = Main.fromJson(Main.readString(MINIMAL_GENERATE_JSON));
        final CConfiguration fromYaml = Main.fromYaml(Main.readString(MINIMAL_GENERATE_YAML));

        assertThat(fromJson.command, is(equalTo(fromYaml.command)));
        assertThat(fromJson.srcMorph.getGenerationProvider(), is(equalTo(fromYaml.srcMorph.getGenerationProvider())));
        assertThat(
                fromJson.srcMorph.getPromptDefinitions().get(0).getKey(),
                is(equalTo(fromYaml.srcMorph.getPromptDefinitions().get(0).getKey())));
        assertThat(
                fromJson.srcMorph.getAiDefinitions().get(0).getKey(),
                is(equalTo(fromYaml.srcMorph.getAiDefinitions().get(0).getKey())));
        assertThat(
                fromJson.srcMorph.getFieldGenerations().get(0).getPromptKey(),
                is(equalTo(fromYaml.srcMorph.getFieldGenerations().get(0).getPromptKey())));
    }

    private static void assertConfigurationMatchesFixture(final CConfiguration configuration) {
        assertThat(configuration.command, is(equalTo(CCommand.GenerateFileIndex)));

        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(srcMorph.getGenerationProvider(), is(equalTo("mock")));

        assertThat(srcMorph.getPromptDefinitions().size(), is(equalTo(1)));
        final AiPromptDefinition prompt = srcMorph.getPromptDefinitions().get(0);
        assertThat(prompt.getKey(), is(equalTo("file-body")));
        assertThat(prompt.getTemplate(), is(equalTo("Summarize this file:\n%s")));

        assertThat(srcMorph.getAiDefinitions().size(), is(equalTo(1)));
        final AiModelDefinition model = srcMorph.getAiDefinitions().get(0);
        assertThat(model.getKey(), is(equalTo("mock-model")));
        assertThat(model.getModelPath(), is(equalTo("unused-with-mock-provider.gguf")));
        assertThat(model.getContextSize(), is(equalTo(2048)));
        assertThat(model.getMaxOutputTokens(), is(equalTo(64)));
        assertThat(model.getThreads(), is(equalTo(2)));

        assertThat(srcMorph.getFieldGenerations().size(), is(equalTo(1)));
        final AiFieldGenerationConfig rule = srcMorph.getFieldGenerations().get(0);
        assertThat(rule.getPromptKey(), is(equalTo("file-body")));
        assertThat(rule.getAiDefinitionKey(), is(equalTo("mock-model")));
        assertThat(rule.isFallback(), is(true));
    }
}
