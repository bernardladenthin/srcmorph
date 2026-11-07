// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.provider.ProviderConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CalibrateEngineTest {

    /** A second model a rule can name next to the default one. */
    private static final String OTHER_MODEL_KEY = "other";

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock
     * provider, the engine always builds a
     * {@link net.ladenthin.srcmorph.provider.ProviderConfig} value object, which requires a
     * non-null model path.
     */
    private static ModelDefinition mockModelDefinition(final String key) {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey(key);
        definition.setModelPath("mock.gguf");
        definition.setCharsPerToken(0);
        return definition;
    }

    /**
     * Per-test output directory. {@code execute()} writes the machine-readable report into
     * {@code outputDirectory}, whose default is the source-tree path {@code src/site/ai} -- without
     * this every calibration test would leave two files behind in the checkout.
     */
    @TempDir
    Path outputDirectory;

    private SrcMorphConfiguration baseConfig() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setOutputDirectory(outputDirectory.toFile());
        config.setGenerationProvider("mock");
        config.setPromptDefinitions(CommonTestFixtures.createFilePromptDefinitions());
        config.setAiDefinitions(Arrays.asList(
                mockModelDefinition(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT),
                mockModelDefinition(OTHER_MODEL_KEY)));
        return config;
    }

    /** The one-rule set of a run: the file-body rule on the default model, as its fallback. */
    private static List<RoutingRule> fallbackRules() {
        final RoutingRule rule = CommonTestFixtures.createFileFieldGeneration();
        rule.setFallback(true);
        return Collections.singletonList(rule);
    }

    /** A rule for {@code .java} files on the given model, or a skip rule for them. */
    private static RoutingRule javaRule(final String aiDefinitionKey, final boolean skip) {
        final RoutingRule rule = CommonTestFixtures.createFileFieldGeneration();
        rule.setAiDefinitionKey(aiDefinitionKey);
        rule.setSkip(skip);
        final RuleCondition condition = new RuleCondition();
        condition.setExtensions(Collections.singletonList(".java"));
        rule.setCondition(condition);
        return rule;
    }

    // <editor-fold defaultstate="collapsed" desc="execute">
    @Test
    public void execute_missingFieldGenerations_throwsSrcMorphException() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(null);
        final CalibrateEngine engine = new CalibrateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("nothing to calibrate"));
    }

    @Test
    public void execute_onlyASkipRule_throwsNoRoutableRule() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(Collections.singletonList(javaRule(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT, true)));
        final CalibrateEngine engine = new CalibrateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("No routable"));
    }

    @Test
    public void execute_fileRules_calibratesTheRoutedModelAndRendersXml() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(fallbackRules());

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        assertThat(report.measurements().size(), is(1));
        final CalibrationReport.ModelMeasurement measurement =
                report.measurements().get(0);
        assertThat(measurement.modelKey(), is(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        // The mock provider reports fixed synthetic throughput; the engine surfaces it unchanged.
        assertThat(measurement.measurement().prefillTokensPerSecond(), is(1000.0d));
        assertThat(measurement.measurement().decodeTokensPerSecond(), is(100.0d));

        final String block = report.renderPasteBlock();
        assertThat(
                block,
                containsString(
                        "calibration of the aiDefinitions entry \"" + CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        assertThat(block, containsString("  prefillTokensPerSecond: 1000.0\n"));
    }

    @Test
    public void execute_multipleRulesRoutedToTheSameModel_dedupesTheMeasurement() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final RoutingRule fallback = fallbackRules().get(0);
        config.setRules(Arrays.asList(javaRule(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT, false), fallback));

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        assertThat(report.measurements().size(), is(1));
    }

    /** Two models routed by two rules are two measurements; the skip-rule test below differs in one flag. */
    @Test
    public void execute_twoRoutedModels_calibratesBoth() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(
                Arrays.asList(javaRule(OTHER_MODEL_KEY, false), fallbackRules().get(0)));

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        assertThat(report.measurements().size(), is(2));
        assertThat(report.measurements().get(0).modelKey(), is(OTHER_MODEL_KEY));
    }

    /** A skip rule sends no file to a model, so the model it happens to name is not loaded and measured. */
    @Test
    public void execute_aSkipRuleNamingAModel_doesNotCalibrateIt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(
                Arrays.asList(javaRule(OTHER_MODEL_KEY, true), fallbackRules().get(0)));

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        assertThat(report.measurements().size(), is(1));
        assertThat(report.measurements().get(0).modelKey(), is(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
    }

    /** Nor does a skip rule's model need a definition: a run would never look it up. */
    @Test
    public void execute_aSkipRuleNamingAnUndefinedModel_calibratesTheRoutedModel() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(
                Arrays.asList(javaRule("no-such-model", true), fallbackRules().get(0)));

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        assertThat(report.measurements().size(), is(1));
    }

    /** The rule set is validated as a run validates it: two fallbacks are refused. */
    @Test
    public void execute_twoFallbackRules_failsAsInvalidRules() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final RoutingRule second = fallbackRules().get(0);
        second.setAiDefinitionKey(OTHER_MODEL_KEY);
        config.setRules(Arrays.asList(fallbackRules().get(0), second));
        final CalibrateEngine engine = new CalibrateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("Invalid rules"));
        assertThat(e.getMessage(), containsString("At most one fallback"));
    }

    /** A routed model nobody defined is a configuration error naming the rule and the key. */
    @Test
    public void execute_aRuleNamingAnUndefinedModel_failsNamingTheKey() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final List<RoutingRule> rules = fallbackRules();
        rules.get(0).setAiDefinitionKey("no-such-model");
        config.setRules(rules);
        final CalibrateEngine engine = new CalibrateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("names the model 'no-such-model', which no aiDefinition defines"));
    }

    /** A prompt without a template fails before a model loads, as a configuration error. */
    @Test
    public void execute_aPromptWithoutATemplate_failsAsInvalidRules() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final List<RoutingRule> rules = fallbackRules();
        rules.get(0).setPromptKey("no-such-prompt");
        config.setRules(rules);
        final CalibrateEngine engine = new CalibrateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("Invalid rules"));
        assertThat(e.getMessage(), containsString("no-such-prompt"));
    }

    /**
     * A provider that fails with an unchecked exception (the JNI binding throws
     * {@code IllegalStateException} for a model it cannot load) fails the calibration naming the model,
     * as an {@code IOException} does.
     */
    @Test
    public void execute_aProviderFailingUnchecked_failsNamingTheModel() {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(fallbackRules());
        final IllegalStateException nativeFailure = new IllegalStateException("could not load the model");
        final GenerationProviderFactory failing = new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog prompts) {
                return request -> {
                    throw nativeFailure;
                };
            }
        };
        final CalibrateEngine engine = new CalibrateEngine(config, failing);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(
                e.getMessage(),
                containsString("Calibration failed for model '" + CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT + "'"));
        assertThat(e.getMessage(), containsString("could not load the model"));
        assertThat(e.getCause(), is(instanceOf(IllegalStateException.class)));
    }

    /**
     * The point of the feature: a calibration run's numbers must survive as something diffable and
     * committable, not only as log lines a human has to re-read. Asserted end to end through the real
     * engine -- rendering is covered separately in {@link CalibrationReportTest}, but only this proves
     * the files are actually written, to the documented names, with the documented content.
     */
    @Test
    public void execute_fileRules_writesTheReportAsJsonAndYamlIntoTheOutputDirectory() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(fallbackRules());

        // act
        final CalibrationReport report = new CalibrateEngine(config).execute();

        // assert
        final Path json = outputDirectory.resolve(CalibrationReport.JSON_FILE_NAME);
        final Path yaml = outputDirectory.resolve(CalibrationReport.YAML_FILE_NAME);
        assertThat(Files.exists(json), is(true));
        assertThat(Files.exists(yaml), is(true));

        // Byte-for-byte what the report renders -- so the file cannot drift from the API.
        assertThat(new String(Files.readAllBytes(json), StandardCharsets.UTF_8), is(report.renderJson()));
        assertThat(new String(Files.readAllBytes(yaml), StandardCharsets.UTF_8), is(report.renderYaml()));
        assertThat(
                new String(Files.readAllBytes(json), StandardCharsets.UTF_8),
                containsString("\"prefillTokensPerSecond\": 1000.0"));
    }

    /** A missing output directory is created rather than being a reason to fail the run. */
    @Test
    public void execute_outputDirectoryDoesNotExist_createsTheOutputDirectory() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path nested = outputDirectory.resolve("does/not/exist/yet");
        config.setOutputDirectory(nested.toFile());
        config.setRules(fallbackRules());

        // act
        new CalibrateEngine(config).execute();

        // assert
        assertThat(Files.exists(nested.resolve(CalibrationReport.JSON_FILE_NAME)), is(true));
    }
    // </editor-fold>
}
