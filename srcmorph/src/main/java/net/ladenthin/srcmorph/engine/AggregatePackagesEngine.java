// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.indexer.AiIndexTally;
import net.ladenthin.srcmorph.indexer.PackageIndexer;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 2 orchestration: aggregates per-package {@code .ai.md} index files and fills in their
 * AI-generated summary fields, with the phase's one rule: its prompt and model
 * ({@link SrcMorphConfiguration#getFieldGenerations()} holds exactly that rule).
 *
 * <p>Extracted from what was {@code AggregatePackagesMojo.execute()} in the
 * {@code llamacpp-ai-index-maven-plugin} module.</p>
 */
@ToString
public final class AggregatePackagesEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregatePackagesEngine.class);

    /** The goal, named in a configuration error. */
    private static final String PHASE = "aggregate-packages";

    /** The configuration file's key for the phase's rule, named in a configuration error. */
    private static final String CLI_KEY = "packageFieldGenerations";

    private final SrcMorphConfiguration config;

    /**
     * Creates a new {@link AggregatePackagesEngine} for the given run configuration.
     *
     * @param config the run configuration
     */
    public AggregatePackagesEngine(final SrcMorphConfiguration config) {
        this.config = config;
    }

    /**
     * Checks the phase as a run would, without loading a model or writing anything: its one rule, the
     * rule's profile files and its model (a local model's file and GGUF header, a remote model's
     * {@code baseUrl} and {@code apiKeyEnv}). Unlike a run it does not need the output tree yet.
     *
     * @throws SrcMorphException naming the first problem found
     */
    public void check() throws SrcMorphException {
        final AiFieldGenerationConfig rule =
                EngineSupport.requiredPhaseRule(PHASE, CLI_KEY, config.getFieldGenerations());
        EngineSupport.resolveFactsKeys(config, Collections.singletonList(rule));
        EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getFieldGenerations());
        EngineSupport.validateRoutedModelPaths(
                config.getGenerationProvider(),
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory()),
                Collections.singletonList(rule));
        LOGGER.info(
                "Check passed: the package rule (prompt '{}', model '{}'), its profile files and its model are in"
                        + " order.",
                rule.requirePromptKey(),
                rule.requireAiDefinitionKey());
    }

    /**
     * Aggregates every package beneath the configured output directory.
     *
     * @return the number of package index files written; {@code 0} when the output directory does not yet
     *         exist or the run is {@link SrcMorphConfiguration#isPlanOnly() planOnly}
     * @throws SrcMorphException if the phase has not exactly one rule, the prompt/model definitions are
     *                           misconfigured, or, after every package
     *                           was tried, when a package got an incomplete answer and was not written
     * @throws IOException       if the output tree cannot be read or written
     */
    public int execute() throws SrcMorphException, IOException {
        if (config.isPlanOnly()) {
            // planOnly plans the file phase and loads no model; this phase would write, so it does not run
            LOGGER.info("planOnly: the package phase does not run.");
            return 0;
        }
        final Path basePath = EngineSupport.basePath(config);
        final Path outputPath =
                config.getOutputDirectory().toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = EngineSupport.resolveSubtrees(basePath, config.getSubtrees());

        LOGGER.info("Starting AI package aggregation");
        LOGGER.info("Base directory  : {}", basePath);
        LOGGER.info("Output directory: {}", outputPath);
        LOGGER.info("Subtrees        : {}", resolvedSubtrees);
        LOGGER.info("Force           : {}", config.isForce());
        LOGGER.info("Regenerate on   : {}", config.getRegenerateOn());
        LOGGER.info("Provider        : {}", config.getGenerationProvider());

        // the rule is checked before anything else, so a wrong one fails on a fresh tree too
        final AiFieldGenerationConfig rule =
                EngineSupport.requiredPhaseRule(PHASE, CLI_KEY, config.getFieldGenerations());
        EngineSupport.resolveFactsKeys(config, Collections.singletonList(rule));
        if (!outputPath.toFile().exists()) {
            LOGGER.info("AI output directory does not exist, skipping package aggregation: {}", outputPath);
            return 0;
        }

        final AiPromptSupport promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getFieldGenerations());
        final AiModelDefinitionSupport modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory());
        EngineSupport.validateRoutedModelPaths(
                config.getGenerationProvider(), modelDefinitionSupport, Collections.singletonList(rule));
        LOGGER.info(
                "Package rule    : prompt '{}', model '{}'", rule.requirePromptKey(), rule.requireAiDefinitionKey());
        final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();

        final AiIndexTally aggregated;
        try (AiGenerationProvider provider = providerFactory.create(
                config.getGenerationProvider(),
                EngineSupport.resolveProviderConfig(modelDefinitionSupport, rule.requireAiDefinitionKey()),
                promptSupport)) {
            final PackageIndexer packageIndexer = new PackageIndexer(
                    basePath,
                    outputPath,
                    config.getPluginVersion(),
                    config.getAiVersion(),
                    resolvedSubtrees,
                    config.isForce(),
                    config.getRegenerateOn(),
                    provider,
                    rule,
                    promptSupport,
                    modelDefinitionSupport);

            aggregated = packageIndexer.aggregate(outputPath);
        }

        LOGGER.info(
                "Aggregated packages: {} written, {} unchanged, {} failed",
                aggregated.written(),
                aggregated.unchanged(),
                aggregated.failed());
        if (aggregated.failed() > 0) {
            throw new SrcMorphException(aggregated.failed() + " package(s) were not written (an incomplete answer"
                    + " or a failed request); the WARN and ERROR lines name them, and the next run tries them again");
        }
        LOGGER.info("AI package aggregation finished.");
        return aggregated.written();
    }
}
