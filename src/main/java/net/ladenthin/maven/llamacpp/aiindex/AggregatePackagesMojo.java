// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code ai-index:aggregate-packages}: aggregates per-package
 * {@code .ai.md} index files and fills in their AI-generated summary and keyword fields.
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "aggregate-packages", threadSafe = true)
public class AggregatePackagesMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link AggregatePackagesMojo}. */
    public AggregatePackagesMojo() {
        // no-op
    }

    @Parameter(defaultValue = "${project.version}", readonly = true)
    private String pluginVersion;

    @Parameter(property = "aiIndex.aiVersion", defaultValue = "0.0.0")
    private String aiVersion;

    /** llama.cpp context window size; smaller default suits the fast aggregate pass. */
    @Parameter(property = "aiIndex.llama.contextSize", defaultValue = "2048")
    private int llamaContextSize;

    /** CPU threads for llama.cpp inference during package aggregation. */
    @Parameter(property = "aiIndex.llama.threads", defaultValue = "2")
    private int llamaThreads;

    @Override
    protected int getLlamaContextSize() {
        return llamaContextSize;
    }

    @Override
    protected int getLlamaThreads() {
        return llamaThreads;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("AI package aggregation skipped.");
            return;
        }

        final Path basePath = baseDirectory.toPath().toAbsolutePath().normalize();
        final Path outputPath = outputDirectory.toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = resolveSubtrees(basePath);

        logExecutionParameters(
                "Starting AI package aggregation", basePath, outputPath, resolvedSubtrees, Collections.emptyList());

        if (!outputPath.toFile().exists()) {
            getLog().info("AI output directory does not exist, skipping package aggregation: " + outputPath);
            return;
        }

        try {
            final AiPromptSupport promptSupport = buildPromptSupport();
            final AiModelDefinitionSupport modelDefinitionSupport = buildAiModelDefinitionSupport();
            final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();

            try (AiGenerationProvider provider =
                    providerFactory.create(generationProvider, buildLlamaCppJniConfig(), promptSupport)) {
                final PackageIndexer packageIndexer = new PackageIndexer(
                        getLog(),
                        basePath,
                        outputPath,
                        pluginVersion,
                        aiVersion,
                        resolvedSubtrees,
                        force,
                        provider,
                        fieldGenerations,
                        promptSupport,
                        modelDefinitionSupport);

                final int aggregated = packageIndexer.aggregate(outputPath);
                getLog().info("Aggregated packages: " + aggregated);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to aggregate package AI index files", e);
        }

        getLog().info("AI package aggregation finished.");
    }
}
