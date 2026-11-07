// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.indexer.CalibrationMeasurement;
import net.ladenthin.srcmorph.indexer.CalibrationRunner;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.routing.RuleSelector;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preflight + per-machine timing pass that sits between {@code planOnly} (no model loaded) and a real
 * {@link GenerateEngine} run.
 *
 * <p>Extracted from what was {@code CalibrateMojo.execute()} in the {@code llamacpp-ai-index-maven-plugin}
 * module. For each distinct model a {@link GenerateEngine} run would load (the {@code aiDefinitionKey}s
 * referenced by {@link SrcMorphConfiguration#getRules()}), loads the model once (catching a
 * bad path / OOM / wrong native early), runs a couple of representative generations via
 * {@link CalibrationRunner}, and reads the model's own measured prefill/decode throughput (an
 * {@code openai-compatible} server reports none: its numbers are a wall-clock estimate). The caller
 * pastes {@link CalibrationReport#renderPasteBlock()} into the model's entry under {@code aiDefinitions} so the plan's time
 * estimate reflects <em>this</em> machine instead of the built-in estimate, which is based on GPT-6 Luna.</p>
 */
@ToString
public final class CalibrateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalibrateEngine.class);

    private final SrcMorphConfiguration config;
    private final GenerationProviderFactory providerFactory;
    private final CalibrationRunner calibrationRunner = new CalibrationRunner();

    /**
     * Creates a new {@link CalibrateEngine} for the given run configuration.
     *
     * @param config the run configuration
     */
    public CalibrateEngine(final SrcMorphConfiguration config) {
        this(config, new GenerationProviderFactory());
    }

    /**
     * Creates a new {@link CalibrateEngine} whose models are built by the given factory (a test hands
     * in one whose provider fails).
     *
     * @param config          the run configuration
     * @param providerFactory builds each calibrated model's provider
     */
    CalibrateEngine(final SrcMorphConfiguration config, final GenerationProviderFactory providerFactory) {
        this.config = config;
        this.providerFactory = providerFactory;
    }

    /**
     * Calibrates every distinct model the configured routing rules route a file to; a skip rule routes
     * none, so its model is not loaded. The rules are validated as a run validates them first (the rule
     * set, every routed model defined, every prompt with a template, every model file).
     *
     * @return the calibration report, one measurement per distinct routed model
     * @throws SrcMorphException if no routable rule is configured, the rules are invalid, or a model fails
     *                            to load or generate (any {@link IOException} or {@link RuntimeException}
     *                            of the provider, a native one too, is caught per model and rewrapped with
     *                            the failing model's key)
     */
    public CalibrationReport execute() throws SrcMorphException {
        final List<? extends @Nullable RoutingRule> rules = config.getRules();
        if (rules == null || rules.isEmpty()) {
            throw new SrcMorphException(
                    "No rules configured (srcMorph.rules of the" + " configuration file); nothing to calibrate.");
        }

        final PromptCatalog promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getRules());
        final ModelRegistry modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory());
        final PromptPreparer promptPreparationSupport = new PromptPreparer(promptSupport);

        // the checks of a run, so a calibration fails on what the run would fail on, as a configuration error
        try {
            new RuleSelector().validate(rules);
        } catch (final IllegalArgumentException e) {
            throw new SrcMorphException("Invalid rules: " + e.getMessage(), e);
        }
        final List<RoutingRule> routed = new ArrayList<>(rules.size());
        for (final @Nullable RoutingRule rule : rules) {
            if (rule != null && !rule.isSkip()) {
                routed.add(rule);
            }
        }
        EngineSupport.requireDefinedModels(modelDefinitionSupport, routed);
        EngineSupport.requirePrompts(promptSupport, routed);

        // Distinct routed models, each mapped to a representative prompt key; a valid route rule has both.
        final Map<String, String> modelToPrompt = new LinkedHashMap<>();
        for (final RoutingRule rule : routed) {
            modelToPrompt.putIfAbsent(rule.requireAiDefinitionKey(), rule.requirePromptKey());
        }
        if (modelToPrompt.isEmpty()) {
            throw new SrcMorphException("No routable (model + prompt) rule found to calibrate.");
        }

        // Same fail-fast check as the generate goal: calibrate loads every routed model in turn.
        EngineSupport.validateRoutedModelPaths(config.getGenerationProvider(), modelDefinitionSupport, rules);

        LOGGER.info(
                "AI index calibration: {} model(s). Provider: {}",
                modelToPrompt.size(),
                config.getGenerationProvider());
        final List<CalibrationReport.ModelMeasurement> measurements = new ArrayList<>(modelToPrompt.size());
        for (final Map.Entry<String, String> entry : modelToPrompt.entrySet()) {
            measurements.add(calibrateModel(
                    entry.getKey(), entry.getValue(), modelDefinitionSupport, promptSupport, promptPreparationSupport));
        }

        final CalibrationReport report = new CalibrationReport(measurements);
        writeMachineReadableReport(report);
        return report;
    }

    /**
     * Writes the report next to the log, as JSON and YAML, into the configured output directory.
     *
     * <p>Without this the numbers a calibration run produces exist only as log lines: they cannot be
     * diffed across runs, committed as a baseline, or fed back into {@code aiDefinitions} by anything
     * other than a human re-reading the console &#x2014; which is most of the point of measuring
     * them.</p>
     *
     * @param report the report to write
     * @throws SrcMorphException if the directory cannot be created or a file cannot be written
     */
    private void writeMachineReadableReport(final CalibrationReport report) throws SrcMorphException {
        final Path directory = config.getOutputDirectory().toPath();
        final Path json = directory.resolve(CalibrationReport.JSON_FILE_NAME);
        final Path yaml = directory.resolve(CalibrationReport.YAML_FILE_NAME);
        try {
            Files.createDirectories(directory);
            writeUtf8(json, report.renderJson());
            writeUtf8(yaml, report.renderYaml());
        } catch (final IOException e) {
            throw new SrcMorphException("Failed to write the calibration report to " + directory + ": " + e, e);
        }
        LOGGER.info("");
        LOGGER.info("Machine-readable calibration report written to:");
        LOGGER.info("  {}", json);
        LOGGER.info("  {}", yaml);
    }

    /**
     * Writes UTF-8 text to a file, replacing any previous content.
     *
     * @param target the file
     * @param text   the content
     * @throws IOException if the write fails
     */
    private static void writeUtf8(final Path target, final String text) throws IOException {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(target), StandardCharsets.UTF_8)) {
            writer.write(text);
        }
    }

    /**
     * Loads one model, measures it via {@link CalibrationRunner}, and logs the result.
     *
     * @param modelKey                 the aiDefinitionKey to calibrate
     * @param promptKey                a representative prompt key routed to this model
     * @param modelDefinitionSupport   model lookup
     * @param promptSupport            prompt lookup (for the provider)
     * @param promptPreparationSupport prompt preparation (for the window calculation)
     * @return the model's key paired with its measurement
     * @throws SrcMorphException if the model fails to load or generate: an {@link IOException} and a
     *                           {@link RuntimeException} (the JNI binding's {@code IllegalStateException}
     *                           of a model that does not load, say) alike, naming the model
     */
    private CalibrationReport.ModelMeasurement calibrateModel(
            final String modelKey,
            final String promptKey,
            final ModelRegistry modelDefinitionSupport,
            final PromptCatalog promptSupport,
            final PromptPreparer promptPreparationSupport)
            throws SrcMorphException {
        try {
            return measureModel(modelKey, promptKey, modelDefinitionSupport, promptSupport, promptPreparationSupport);
        } catch (final IOException | RuntimeException e) {
            throw new SrcMorphException("Calibration failed for model '" + modelKey + "': " + e, e);
        }
    }

    /**
     * Loads one model, measures it and logs the result; {@link #calibrateModel} names the model in a failure.
     *
     * @param modelKey                 the aiDefinitionKey to calibrate
     * @param promptKey                a representative prompt key routed to this model
     * @param modelDefinitionSupport   model lookup
     * @param promptSupport            prompt lookup (for the provider)
     * @param promptPreparationSupport prompt preparation (for the window calculation)
     * @return the model's key paired with its measurement
     * @throws IOException if the provider fails
     */
    private CalibrationReport.ModelMeasurement measureModel(
            final String modelKey,
            final String promptKey,
            final ModelRegistry modelDefinitionSupport,
            final PromptCatalog promptSupport,
            final PromptPreparer promptPreparationSupport)
            throws IOException {
        final ModelDefinition modelConfig = modelDefinitionSupport.definition(modelKey);
        final long windowChars = calibrationRunner.windowChars(modelConfig, promptKey, promptPreparationSupport);

        LOGGER.info("");
        LOGGER.info("Model '{}': loading (window ~{} source chars)...", modelKey, windowChars);
        try (GenerationProvider provider = providerFactory.create(
                config.getGenerationProvider(),
                EngineSupport.resolveProviderConfig(modelDefinitionSupport, modelKey),
                promptSupport)) {
            final CalibrationMeasurement m =
                    calibrationRunner.measure(provider, modelConfig, promptKey, promptPreparationSupport);

            LOGGER.info(String.format(
                    Locale.ROOT,
                    "Model '%s': loaded+first-gen ~%.1fs | prefill %.0f tok/s | decode %.0f tok/s | ~%.2f chars/token",
                    modelKey,
                    m.loadSeconds(),
                    m.prefillTokensPerSecond(),
                    m.decodeTokensPerSecond(),
                    m.charsPerToken()));
            if (m.prefillTokensPerSecond() > 0 && m.midPrefillTokensPerSecond() > 0) {
                LOGGER.info(String.format(
                        Locale.ROOT,
                        "  (mid-window prefill %.0f tok/s vs near-window %.0f tok/s - larger gap => more curvature)",
                        m.midPrefillTokensPerSecond(),
                        m.prefillTokensPerSecond()));
            }
            logPromptCache(modelKey, modelConfig, m.cachedPromptTokens());
            return new CalibrationReport.ModelMeasurement(modelKey, m);
        }
    }

    /**
     * Logs the prompt tokens the model reused, as its provider reuses them.
     *
     * <p>For a local model it is the only observable the prefix-reuse settings produce; reporting it is
     * what turns {@code cachePrompt}/{@code cacheReuse}/{@code swaFull} from an article of faith into a
     * measurement, so none reused is a warning about them. An {@code openai-compatible} server caches a
     * prompt (or not) on its own: those settings do not reach it, and none reused names no setting.</p>
     *
     * @param modelKey           the calibrated model's key
     * @param modelConfig        its definition (its own {@code provider} overrides the run's)
     * @param cachedPromptTokens the prompt tokens the near-window request reused
     */
    private void logPromptCache(
            final String modelKey, final ModelDefinition modelConfig, final int cachedPromptTokens) {
        final boolean remote = GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE.equals(
                GenerationProviderFactory.effectiveProvider(config.getGenerationProvider(), modelConfig.getProvider()));
        if (remote) {
            if (cachedPromptTokens > 0) {
                LOGGER.info(
                        "  (prompt cache: the server reports {} prompt token(s) of model '{}' read from its cache)",
                        cachedPromptTokens,
                        modelKey);
            } else {
                LOGGER.info(
                        "  (prompt cache: the server reports no cached prompt tokens for model '{}'; a remote"
                                + " server caches on its own, cachePrompt/cacheReuse/swaFull do not reach it)",
                        modelKey);
            }
        } else if (cachedPromptTokens > 0) {
            LOGGER.info(
                    "  (prompt cache: {} prompt token(s) reused from the KV cache - prefix reuse is working)",
                    cachedPromptTokens);
        } else {
            LOGGER.warn("  (prompt cache: no prompt tokens were reused on this model; "
                    + "cachePrompt/cacheReuse/swaFull are costing KV memory without paying for it)");
        }
    }
}
