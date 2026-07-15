// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.mojo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.indexer.AiCalibrationMeasurement;
import net.ladenthin.srcmorph.indexer.AiCalibrationRunner;
import net.ladenthin.srcmorph.prompt.AiPromptPreparationSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code ai-index:calibrate}: a preflight + timing pass that sits between {@code planOnly} (no
 * model loaded) and a real {@code generate} run.
 *
 * <p>For each distinct model a run would load (the {@code aiDefinitionKey}s referenced by
 * {@code <fieldGenerations>}) it loads the model once (catching a bad path / OOM / wrong native early),
 * runs a couple of representative generations, reads the model's own measured prefill/decode throughput,
 * and prints a paste-ready {@code <calibration>} block. Paste that onto the matching {@code <aiDefinition>}
 * so the plan's time estimate reflects <em>this</em> machine instead of the built-in reference-CPU
 * coefficients. The numbers are per machine (GPU/CPU, quant, context), so re-run on each host.</p>
 */
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "calibrate", threadSafe = true)
public class CalibrateMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link CalibrateMojo}. */
    public CalibrateMojo() {
        // no-op
    }

    /** llama.cpp context window size (unused by calibrate, which uses each model's own config). */
    @Parameter(property = "aiIndex.llama.contextSize", defaultValue = "2048")
    private int llamaContextSize;

    /** CPU threads (unused by calibrate, which uses each model's own config). */
    @Parameter(property = "aiIndex.llama.threads", defaultValue = "2")
    private int llamaThreads;

    private final AiCalibrationRunner calibrationRunner = new AiCalibrationRunner();

    @Override
    protected int getLlamaContextSize() {
        return llamaContextSize;
    }

    @Override
    protected int getLlamaThreads() {
        return llamaThreads;
    }

    @Override
    protected boolean isPhaseSkipped() {
        // calibrate is a manual diagnostic goal, not one of the file/package/project phases; only the
        // global aiIndex.skip disables it.
        return false;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI index calibration skipped.");
            return;
        }
        if (fieldGenerations == null || fieldGenerations.isEmpty()) {
            throw new MojoExecutionException("No <fieldGenerations> configured; nothing to calibrate.");
        }

        final AiPromptSupport promptSupport = buildPromptSupport();
        final AiModelDefinitionSupport modelDefinitionSupport = buildAiModelDefinitionSupport();
        final AiPromptPreparationSupport promptPreparationSupport = new AiPromptPreparationSupport(promptSupport);
        final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();

        // Distinct routed models, each mapped to a representative prompt key (skip rules have none).
        final Map<String, String> modelToPrompt = new LinkedHashMap<>();
        for (final AiFieldGenerationConfig rule : fieldGenerations) {
            if (rule == null || rule.getAiDefinitionKey() == null || rule.getPromptKey() == null) {
                continue;
            }
            modelToPrompt.putIfAbsent(rule.getAiDefinitionKey(), rule.getPromptKey());
        }
        if (modelToPrompt.isEmpty()) {
            throw new MojoExecutionException("No routable (model + prompt) rule found to calibrate.");
        }

        getLog().info("AI index calibration: " + modelToPrompt.size() + " model(s). Provider: " + generationProvider);
        final StringBuilder pasteBlocks = new StringBuilder();
        for (final Map.Entry<String, String> entry : modelToPrompt.entrySet()) {
            calibrateModel(
                    entry.getKey(),
                    entry.getValue(),
                    modelDefinitionSupport,
                    promptSupport,
                    promptPreparationSupport,
                    providerFactory,
                    pasteBlocks);
        }

        getLog().info("");
        getLog().info("Paste each <calibration> onto its matching <aiDefinition> (numbers are per machine).");
        getLog().info("These are the model's own measured prefill/decode throughput (from the engine's "
                + "per-call timings); only the mock/no-timings path falls back to a wall-clock estimate.");
        for (final String line : pasteBlocks.toString().split("\n", -1)) {
            getLog().info(line);
        }
    }

    /**
     * Loads one model, measures it via {@link AiCalibrationRunner}, logs the result, and appends a
     * paste-ready {@code <calibration>} block for it.
     *
     * @param modelKey                 the aiDefinitionKey to calibrate
     * @param promptKey                a representative prompt key routed to this model
     * @param modelDefinitionSupport   model lookup
     * @param promptSupport            prompt lookup (for the provider)
     * @param promptPreparationSupport prompt preparation (for the window calculation)
     * @param providerFactory          provider factory
     * @param pasteBlocks              accumulator for the printable {@code <calibration>} blocks
     * @throws MojoExecutionException if the model fails to load or generate
     */
    private void calibrateModel(
            final String modelKey,
            final String promptKey,
            final AiModelDefinitionSupport modelDefinitionSupport,
            final AiPromptSupport promptSupport,
            final AiPromptPreparationSupport promptPreparationSupport,
            final AiGenerationProviderFactory providerFactory,
            final StringBuilder pasteBlocks)
            throws MojoExecutionException {
        final AiGenerationConfig config = modelDefinitionSupport.getConfig(modelKey);
        final long windowChars = calibrationRunner.windowChars(config, promptKey, promptPreparationSupport);

        getLog().info("");
        getLog().info("Model '" + modelKey + "': loading (window ~" + windowChars + " source chars)...");
        try (AiGenerationProvider provider =
                providerFactory.create(generationProvider, buildLlamaCppJniConfig(modelKey), promptSupport)) {
            final AiCalibrationMeasurement m =
                    calibrationRunner.measure(provider, config, promptKey, promptPreparationSupport);

            getLog().info(String.format(
                    Locale.ROOT,
                    "Model '%s': loaded+first-gen ~%.1fs | prefill %.0f tok/s | decode %.0f tok/s | ~%.2f chars/token",
                    modelKey,
                    m.loadSeconds(),
                    m.prefillTokensPerSecond(),
                    m.decodeTokensPerSecond(),
                    m.charsPerToken()));
            if (m.prefillTokensPerSecond() > 0 && m.midPrefillTokensPerSecond() > 0) {
                getLog().info(String.format(
                        Locale.ROOT,
                        "  (mid-window prefill %.0f tok/s vs near-window %.0f tok/s - larger gap => more curvature)",
                        m.midPrefillTokensPerSecond(),
                        m.prefillTokensPerSecond()));
            }
            appendPasteBlock(pasteBlocks, modelKey, m);
        } catch (final IOException e) {
            throw new MojoExecutionException("Calibration failed for model '" + modelKey + "': " + e.getMessage(), e);
        }
    }

    /**
     * Appends a copy-pasteable {@code <calibration>} block (with a comment naming the model) to the buffer.
     *
     * @param out      the buffer
     * @param modelKey the model key (for the comment)
     * @param m        the measurement
     */
    private static void appendPasteBlock(
            final StringBuilder out, final String modelKey, final AiCalibrationMeasurement m) {
        out.append("<!-- calibration for aiDefinition '").append(modelKey).append("' (this machine) -->\n");
        out.append("<calibration>\n");
        out.append(String.format(
                Locale.ROOT,
                "    <prefillTokensPerSecond>%.1f</prefillTokensPerSecond>%n",
                m.prefillTokensPerSecond()));
        out.append(String.format(
                Locale.ROOT, "    <decodeTokensPerSecond>%.1f</decodeTokensPerSecond>%n", m.decodeTokensPerSecond()));
        out.append(String.format(Locale.ROOT, "    <charsPerToken>%.2f</charsPerToken>%n", m.charsPerToken()));
        out.append("</calibration>\n");
    }
}
