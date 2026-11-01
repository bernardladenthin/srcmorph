// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.ToString;
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
 * AI-generated summary fields.
 *
 * <p>Extracted from what was {@code AggregatePackagesMojo.execute()} in the
 * {@code llamacpp-ai-index-maven-plugin} module.</p>
 */
@ToString
public final class AggregatePackagesEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregatePackagesEngine.class);

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
     * Aggregates every package beneath the configured output directory.
     *
     * @return the number of package index files written; {@code 0} when the output directory does not yet
     *         exist
     * @throws SrcMorphException if the prompt/model definitions are misconfigured, or, after every package
     *                           was tried, when a package got an incomplete answer and was not written
     * @throws IOException       if the output tree cannot be read or written
     */
    public int execute() throws SrcMorphException, IOException {
        final Path basePath =
                config.getBaseDirectory().toPath().toAbsolutePath().normalize();
        final Path outputPath =
                config.getOutputDirectory().toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = EngineSupport.resolveSubtrees(basePath, config.getSubtrees());

        LOGGER.info("Starting AI package aggregation");
        LOGGER.info("Base directory  : {}", basePath);
        LOGGER.info("Output directory: {}", outputPath);
        LOGGER.info("Subtrees        : {}", resolvedSubtrees);
        LOGGER.info("Force           : {}", config.isForce());
        LOGGER.info("Provider        : {}", config.getGenerationProvider());
        LOGGER.info("LlamaCpp Temperature: {}", config.getLlamaTemperature());
        LOGGER.info("LlamaCpp Max Output Tokens: {}", config.getLlamaMaxOutputTokens());

        if (!outputPath.toFile().exists()) {
            LOGGER.info("AI output directory does not exist, skipping package aggregation: {}", outputPath);
            return 0;
        }

        final AiPromptSupport promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getFieldGenerations());
        final AiModelDefinitionSupport modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory());
        final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();

        final AiIndexTally aggregated;
        try (AiGenerationProvider provider = providerFactory.create(
                config.getGenerationProvider(),
                EngineSupport.resolveLlamaCppJniConfig(config, modelDefinitionSupport),
                promptSupport)) {
            final PackageIndexer packageIndexer = new PackageIndexer(
                    basePath,
                    outputPath,
                    config.getPluginVersion(),
                    config.getAiVersion(),
                    resolvedSubtrees,
                    config.isForce(),
                    provider,
                    config.getFieldGenerations(),
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
            throw new SrcMorphException(aggregated.failed() + " package(s) got an incomplete answer and were not"
                    + " written; the WARN lines name them, and the next run tries them again");
        }
        LOGGER.info("AI package aggregation finished.");
        return aggregated.written();
    }
}
