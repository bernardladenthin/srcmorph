// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import lombok.ToString;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.indexer.IndexOutcome;
import net.ladenthin.srcmorph.indexer.ProjectIndexer;
import net.ladenthin.srcmorph.indexer.ProjectOverview;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 3 orchestration: aggregates every per-package {@code package.ai.md} into a single project-level
 * {@code project.ai.md} — the top of the three-level index.
 *
 * <p>Extracted from what was {@code AggregateProjectMojo.execute()} in the
 * {@code llamacpp-ai-index-maven-plugin} module. By default this is fully deterministic (a per-package
 * lead/link table of contents, no model call); when
 * {@link SrcMorphConfiguration#getRules()} holds a rule (at most one), it is the
 * <em>optional</em> AI overview generation (one extra call synthesising a short project overview
 * paragraph from the per-package leads) — see {@link ProjectIndexer}.</p>
 */
@ToString
public final class AggregateProjectEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateProjectEngine.class);

    /** Title used for the project index when {@link SrcMorphConfiguration#getProjectName()} is absent or blank. */
    private static final String DEFAULT_PROJECT_TITLE = "project";

    /** The goal, named in a configuration error. */
    private static final String PHASE = "aggregate-project";

    /** The configuration file's key for the phase's rule, named in a configuration error. */
    private static final String CLI_KEY = "projectRules";

    private final SrcMorphConfiguration config;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates a new {@link AggregateProjectEngine} for the given run configuration.
     *
     * @param config the run configuration
     */
    public AggregateProjectEngine(final SrcMorphConfiguration config) {
        this.config = config;
    }

    /**
     * Aggregates the project index beneath the configured output directory.
     *
     * @return {@code 1} if the project index was written, {@code 0} otherwise (no package index files
     *         yet, the output directory does not exist, the existing file is up to date, or the run is
     *         {@link SrcMorphConfiguration#isPlanOnly() planOnly})
     * @throws SrcMorphException if more than one rule is given, the overview's prompt/model definitions are
     *                           misconfigured, or the
     *                           overview's answer was incomplete and the project index was not written
     * @throws IOException       if the output tree cannot be read or the project index cannot be written
     */
    public int execute() throws SrcMorphException, IOException {
        if (config.isPlanOnly()) {
            // planOnly plans the file phase and loads no model; this phase would write, so it does not run
            LOGGER.info("planOnly: the project phase does not run.");
            return 0;
        }
        final Path outputPath =
                config.getOutputDirectory().toPath().toAbsolutePath().normalize();

        LOGGER.info("Starting AI project index aggregation");
        LOGGER.info("Output directory: {}", outputPath);
        LOGGER.info("Force           : {}", config.isForce());
        LOGGER.info("Regenerate on   : {}", config.getRegenerateOn());

        // the rule is checked before anything else, so a wrong one fails on a fresh tree too
        final RoutingRule overview = EngineSupport.optionalPhaseRule(PHASE, CLI_KEY, config.getRules());
        if (overview != null) {
            EngineSupport.resolveFactsKeys(config, Collections.singletonList(overview));
        }
        if (!outputPath.toFile().exists()) {
            LOGGER.info("AI output directory does not exist, skipping project index: {}", outputPath);
            return 0;
        }

        final String projectName = config.getProjectName();
        final String title =
                projectName == null || compatibilityHelper.isBlank(projectName) ? DEFAULT_PROJECT_TITLE : projectName;

        final IndexOutcome outcome;
        if (overview != null) {
            outcome = aggregateWithOverview(outputPath, title, overview);
        } else {
            LOGGER.info("Project overview generation: disabled (no rule configured)");
            final ProjectIndexer indexer = new ProjectIndexer(
                    title,
                    config.getGeneratorVersion(),
                    config.getAiVersion(),
                    config.isForce(),
                    config.getRegenerateOn());
            outcome = indexer.aggregate(outputPath);
        }
        if (outcome == IndexOutcome.FAILED) {
            throw new SrcMorphException("The project overview for " + outputPath + " got an incomplete answer"
                    + " and the project index was not written; the WARN line says why, and the next run tries it"
                    + " again");
        }
        final int written = outcome == IndexOutcome.WRITTEN ? 1 : 0;
        LOGGER.info("Project index files written: {}", written);

        LOGGER.info("AI project index aggregation finished.");
        return written;
    }

    /**
     * Checks the phase as a run would, without loading a model or writing anything: at most one rule,
     * and with one its profile files and prompts and its model (a local model's file and GGUF header, a
     * remote model's {@code baseUrl} and {@code apiKeyEnv}).
     *
     * @throws SrcMorphException naming the first problem found
     */
    public void check() throws SrcMorphException {
        final RoutingRule overview = EngineSupport.optionalPhaseRule(PHASE, CLI_KEY, config.getRules());
        if (overview == null) {
            LOGGER.info("Check passed: the project index has no overview rule and calls no model.");
            return;
        }
        EngineSupport.resolveFactsKeys(config, Collections.singletonList(overview));
        EngineSupport.requirePrompts(
                EngineSupport.buildPromptSupport(
                        config.getPromptDefinitions(), config.getProfileDirectory(), config.getRules()),
                Collections.singletonList(overview));
        EngineSupport.validateRoutedModelPaths(
                config.getGenerationProvider(),
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory()),
                Collections.singletonList(overview));
        LOGGER.info(
                "Check passed: the overview rule (prompt '{}', model '{}'), its profile files and its model are in"
                        + " order.",
                overview.requirePromptKey(),
                overview.requireAiDefinitionKey());
    }

    /**
     * Aggregates the project index with the optional AI overview paragraph enabled, generated by the
     * given rule. A provider is created (and closed) only on this path, so the deterministic
     * default never instantiates a model.
     *
     * @param outputPath the resolved output directory holding the {@code .ai.md} tree
     * @param title      the resolved project title
     * @param overview   the overview rule (prompt + model)
     * @return what the project index pass did
     * @throws SrcMorphException if the provider or model definitions are misconfigured
     * @throws IOException       if the output tree cannot be read or written
     */
    private IndexOutcome aggregateWithOverview(final Path outputPath, final String title, final RoutingRule overview)
            throws SrcMorphException, IOException {
        final PromptCatalog promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getRules());
        final ModelRegistry modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory());
        EngineSupport.validateRoutedModelPaths(
                config.getGenerationProvider(), modelDefinitionSupport, Collections.singletonList(overview));
        LOGGER.info(
                "Project overview generation: enabled (prompt '{}', model '{}')",
                overview.requirePromptKey(),
                overview.requireAiDefinitionKey());
        final GenerationProviderFactory providerFactory = new GenerationProviderFactory();
        try (GenerationProvider provider = providerFactory.create(
                config.getGenerationProvider(),
                EngineSupport.resolveProviderConfig(modelDefinitionSupport, overview.requireAiDefinitionKey()),
                promptSupport)) {
            final ProjectIndexer indexer = new ProjectIndexer(
                    title,
                    config.getGeneratorVersion(),
                    config.getAiVersion(),
                    config.isForce(),
                    config.getRegenerateOn(),
                    new ProjectOverview(provider, overview, promptSupport, modelDefinitionSupport));
            return indexer.aggregate(outputPath);
        }
    }
}
