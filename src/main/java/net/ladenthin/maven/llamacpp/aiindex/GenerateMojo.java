// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.ToString;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code ai-index:generate}: indexes source files and fills in their
 * AI-generated summary and keyword fields.
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "generate", threadSafe = true)
@ToString(callSuper = true)
public class GenerateMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link GenerateMojo}. */
    public GenerateMojo() {
        // no-op
    }

    /**
     * Default file extension used when no explicit {@code fileExtensions} parameter
     * is configured. Only files whose names end with this extension are indexed.
     */
    private static final String DEFAULT_FILE_EXTENSION = ".java";

    @Parameter(defaultValue = "${project.version}", readonly = true)
    private String pluginVersion;

    @Parameter(property = "aiIndex.aiVersion", defaultValue = "0.0.0")
    private String aiVersion;

    @Parameter(property = "aiIndex.fileExtensions")
    private List<String> fileExtensions;

    /** llama.cpp context window size; smaller default suits the fast generate pass. */
    @Parameter(property = "aiIndex.llama.contextSize", defaultValue = "2048")
    private int llamaContextSize;

    /** CPU threads for llama.cpp inference during the generate pass. */
    @Parameter(property = "aiIndex.llama.threads", defaultValue = "2")
    private int llamaThreads;

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

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
            getLog().info("AI index generation skipped.");
            return;
        }

        final Path basePath = baseDirectory.toPath().toAbsolutePath().normalize();
        final Path outputPath = outputDirectory.toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = resolveSubtrees(basePath);
        final List<String> resolvedExtensions = resolveFileExtensions();

        logExecutionParameters(
                "Starting AI index generation", basePath, outputPath, resolvedSubtrees, resolvedExtensions);

        try {
            final AiPromptSupport promptSupport = buildPromptSupport();
            final AiModelDefinitionSupport modelDefinitionSupport = buildAiModelDefinitionSupport();
            final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();

            try (AiGenerationProvider provider =
                    providerFactory.create(generationProvider, buildLlamaCppJniConfig(), promptSupport)) {

                final SourceFileIndexer fileIndexer = new SourceFileIndexer(
                        getLog(),
                        basePath,
                        outputPath,
                        resolvedExtensions,
                        pluginVersion,
                        aiVersion,
                        resolvedSubtrees,
                        force,
                        provider,
                        fieldGenerations,
                        promptSupport,
                        modelDefinitionSupport);

                int count = 0;

                for (Path subtree : resolvedSubtrees.isEmpty()
                        ? compatibilityHelper.listOf(basePath.resolve("src/main/java"))
                        : resolvedSubtrees) {

                    if (!subtree.toFile().exists()) {
                        getLog().warn("Skipping missing subtree: " + subtree);
                        continue;
                    }

                    count += fileIndexer.indexSourceRoot(subtree);
                }

                getLog().info("Generated AI files: " + count);
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate AI index files", e);
        }

        getLog().info("AI index generation finished.");
    }

    private List<String> resolveFileExtensions() {
        if (fileExtensions == null || fileExtensions.isEmpty()) {
            return compatibilityHelper.listOf(DEFAULT_FILE_EXTENSION);
        }
        return fileExtensions;
    }
}
