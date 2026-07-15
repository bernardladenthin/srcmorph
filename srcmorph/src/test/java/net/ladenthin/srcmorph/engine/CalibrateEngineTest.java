// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import org.junit.jupiter.api.Test;

public class CalibrateEngineTest {

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock
     * provider, the engine always builds a
     * {@link net.ladenthin.srcmorph.provider.LlamaCppJniConfig} value object, which requires a
     * non-null model path.
     */
    private static AiModelDefinition mockModelDefinition() {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        definition.setModelPath("mock.gguf");
        definition.setCharsPerToken(0);
        return definition;
    }

    private SrcMorphConfiguration baseConfig() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setGenerationProvider("mock");
        config.setPromptDefinitions(CommonTestFixtures.createFilePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        return config;
    }

    @Test
    public void execute_missingFieldGenerationsThrowsSrcMorphException() {
        final SrcMorphConfiguration config = baseConfig();
        config.setFieldGenerations(null);

        final SrcMorphException e = assertThrows(SrcMorphException.class, () -> new CalibrateEngine(config).execute());
        assertThat(e.getMessage(), containsString("nothing to calibrate"));
    }

    @Test
    public void execute_noRoutableRuleThrowsSrcMorphException() {
        final SrcMorphConfiguration config = baseConfig();
        // A rule with neither promptKey nor aiDefinitionKey set is not routable.
        config.setFieldGenerations(Collections.singletonList(new AiFieldGenerationConfig()));

        final SrcMorphException e = assertThrows(SrcMorphException.class, () -> new CalibrateEngine(config).execute());
        assertThat(e.getMessage(), containsString("No routable"));
    }

    @Test
    public void execute_calibratesTheRoutedModelAndRendersXml() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        config.setFieldGenerations(CommonTestFixtures.createFileFieldGenerations());

        final CalibrationReport report = new CalibrateEngine(config).execute();

        assertThat(report.measurements().size(), is(1));
        final CalibrationReport.ModelMeasurement measurement =
                report.measurements().get(0);
        assertThat(measurement.modelKey(), is(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        // The mock provider reports fixed synthetic throughput; the engine surfaces it unchanged.
        assertThat(measurement.measurement().prefillTokensPerSecond(), is(1000.0d));
        assertThat(measurement.measurement().decodeTokensPerSecond(), is(100.0d));

        final String xml = report.renderXml();
        assertThat(
                xml, containsString("calibration for aiDefinition '" + CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        assertThat(xml, containsString("<prefillTokensPerSecond>1000.0</prefillTokensPerSecond>"));
    }

    @Test
    public void execute_dedupesMultipleRulesRoutedToTheSameModel() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        final AiFieldGenerationConfig ruleA = new AiFieldGenerationConfig();
        ruleA.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        ruleA.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final AiFieldGenerationConfig ruleB = new AiFieldGenerationConfig();
        ruleB.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        ruleB.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        config.setFieldGenerations(java.util.Arrays.asList(ruleA, ruleB));

        final CalibrationReport report = new CalibrateEngine(config).execute();

        assertThat(report.measurements().size(), is(1));
    }
}
