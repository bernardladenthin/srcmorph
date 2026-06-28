// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.mojo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationSelector;
import net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport;
import net.ladenthin.maven.llamacpp.aiindex.indexer.AiFieldGenerationSupport;
import net.ladenthin.maven.llamacpp.aiindex.indexer.AiIndexPlan;
import net.ladenthin.maven.llamacpp.aiindex.indexer.SourceFileIndexer;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory;
import net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper;
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

    /**
     * Phase switch for the <strong>file</strong> phase (the {@code generate} goal): when {@code true},
     * only this phase is skipped. The global {@link #skip} still skips every phase.
     */
    @Parameter(property = "aiIndex.file.skip", defaultValue = "false")
    private boolean skipFile;

    @Parameter(defaultValue = "${project.version}", readonly = true)
    private String pluginVersion;

    @Parameter(property = "aiIndex.aiVersion", defaultValue = "0.0.0")
    private String aiVersion;

    @Parameter(property = "aiIndex.fileExtensions")
    private List<String> fileExtensions;

    /**
     * Glob patterns for source files to skip, matched against each file's path relative to the
     * project base directory with {@code /} separators (e.g. {@code **}{@code /package-info.java},
     * {@code **}{@code /generated/**}). Lets the index stay focused by excluding trivial or generated
     * sources. Empty by default — nothing is excluded.
     *
     * @see net.ladenthin.maven.llamacpp.aiindex.support.AiSourceExcludeFilter
     */
    @Parameter(property = "aiIndex.excludes")
    private List<String> excludes;

    /**
     * Exclusive lower file-size bound in bytes: source files whose size is {@code <= this} are skipped.
     * {@code 0} (default) disables the lower bound. Together with {@link #maxFileSizeBytes} this lets one
     * {@code generate} execution target a size band, so several executions (each with its own model,
     * context size and prompt) can tier a project by file size while the source-checksum skip indexes
     * every file exactly once. Use non-overlapping bands ({@code band2.min == band1.max}); make the last
     * band unbounded ({@code maxFileSizeBytes=0}) so files above all bands still get indexed.
     */
    @Parameter(property = "aiIndex.file.minSizeBytes", defaultValue = "0")
    private long minFileSizeBytes;

    /**
     * Inclusive upper file-size bound in bytes: source files whose size is {@code > this} are skipped.
     * {@code 0} (default) disables the upper bound (unlimited). See {@link #minFileSizeBytes} for the
     * size-tiering pattern.
     */
    @Parameter(property = "aiIndex.file.maxSizeBytes", defaultValue = "0")
    private long maxFileSizeBytes;

    /** llama.cpp context window size; smaller default suits the fast generate pass. */
    @Parameter(property = "aiIndex.llama.contextSize", defaultValue = "2048")
    private int llamaContextSize;

    /** CPU threads for llama.cpp inference during the generate pass. */
    @Parameter(property = "aiIndex.llama.threads", defaultValue = "2")
    private int llamaThreads;

    /**
     * When {@code true}, only build and log the routing plan (which model indexes which files with which
     * prompt) and then stop — no model is loaded and nothing is generated. Useful to verify routing
     * before a long run.
     */
    @Parameter(property = "aiIndex.planOnly", defaultValue = "false")
    private boolean planOnly;

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
    protected boolean isPhaseSkipped() {
        return skipFile;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI index generation skipped.");
            return;
        }

        final Path basePath = baseDirectory.toPath().toAbsolutePath().normalize();
        final Path outputPath = outputDirectory.toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = resolveSubtrees(basePath);
        final List<String> resolvedExtensions = resolveFileExtensions();

        logExecutionParameters(
                "Starting AI index generation", basePath, outputPath, resolvedSubtrees, resolvedExtensions);

        if (fieldGenerations == null || fieldGenerations.isEmpty()) {
            throw new MojoExecutionException("No <fieldGenerations> configured for the generate goal.");
        }

        final AiPromptSupport promptSupport = buildPromptSupport();
        final AiModelDefinitionSupport modelDefinitionSupport = buildAiModelDefinitionSupport();
        final AiFieldGenerationSelector selector = new AiFieldGenerationSelector();
        // Fail fast on a bad rule set (e.g. >1 fallback, a route rule missing prompt/model).
        selector.validate(fieldGenerations);

        final SourceFileIndexer fileIndexer = new SourceFileIndexer(
                getLog(),
                basePath,
                outputPath,
                resolvedExtensions,
                pluginVersion,
                aiVersion,
                resolvedSubtrees,
                excludes,
                minFileSizeBytes,
                maxFileSizeBytes,
                force);

        try {
            // 1. Collect candidate files across the configured subtrees.
            final List<Path> candidates = new ArrayList<>();
            for (final Path subtree : resolvedSubtrees.isEmpty()
                    ? compatibilityHelper.listOf(basePath.resolve("src/main/java"))
                    : resolvedSubtrees) {
                if (!subtree.toFile().exists()) {
                    getLog().warn("Skipping missing subtree: " + subtree);
                    continue;
                }
                candidates.addAll(fileIndexer.collectCandidates(subtree));
            }

            // 2. Plan the run: which model + prompt each file gets (or skip / unmatched).
            final AiIndexPlan plan = fileIndexer.classify(candidates, fieldGenerations);
            getLog().info(plan.renderTree(basePath));

            // 3. A file that matched no rule and no fallback is a fatal misconfiguration.
            if (!plan.unmatched().isEmpty()) {
                throw new MojoExecutionException(plan.unmatched().size()
                        + " source file(s) matched no rule and no fallback is configured; "
                        + "add a <fallback> rule or a matching rule (see the plan above).");
            }

            if (planOnly) {
                getLog().info("planOnly=true: stopping after the plan; no model loaded, nothing generated.");
                return;
            }

            // 4. Execute model group by model group: load each model once, index its files, close.
            final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();
            final AiPromptPreparationSupport promptPreparationSupport = new AiPromptPreparationSupport(promptSupport);
            int wrote = 0;
            int unchanged = 0;
            for (final Map.Entry<String, List<AiIndexPlan.Entry>> group :
                    plan.routesByModel().entrySet()) {
                final String aiDefinitionKey = group.getKey();
                getLog().info("Loading model '" + aiDefinitionKey + "' for "
                        + group.getValue().size() + " file(s)");
                try (AiGenerationProvider provider = providerFactory.create(
                        generationProvider, buildLlamaCppJniConfig(aiDefinitionKey), promptSupport)) {
                    final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                            getLog(), provider, promptPreparationSupport, modelDefinitionSupport);
                    for (final AiIndexPlan.Entry entry : group.getValue()) {
                        if (fileIndexer.indexFile(entry.file(), entry.rule(), support)) {
                            wrote++;
                        } else {
                            unchanged++;
                        }
                    }
                }
            }

            getLog().info("Generated AI files: " + wrote + " written, " + unchanged + " unchanged, "
                    + plan.skipped().size() + " skipped");

        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to generate AI index files under " + outputPath + " from base " + basePath, e);
        }

        getLog().info("AI index generation finished.");
    }

    private List<String> resolveFileExtensions() {
        final List<String> configured = fileExtensions;
        if (configured == null || configured.isEmpty()) {
            return compatibilityHelper.listOf(DEFAULT_FILE_EXTENSION);
        }
        return configured;
    }
}
