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
import net.ladenthin.srcmorph.config.AiFactDefinition;
import net.ladenthin.srcmorph.config.AiFactDefinitionSupport;
import net.ladenthin.srcmorph.config.AiFieldGenerationSelector;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.indexer.AiFieldGenerationSupport;
import net.ladenthin.srcmorph.indexer.AiIndexPlan;
import net.ladenthin.srcmorph.indexer.SourceFileIndexer;
import net.ladenthin.srcmorph.prompt.AiPromptPreparationSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import net.ladenthin.srcmorph.support.AiGenerationTimeEstimator;
import net.ladenthin.srcmorph.support.AiProgressBar;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
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

    /** Nanoseconds per second, for converting the measured run elapsed time to whole seconds. */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

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
     * @see net.ladenthin.srcmorph.support.AiSourceExcludeFilter
     */
    @Parameter(property = "aiIndex.excludes")
    private List<String> excludes;

    /**
     * Reusable, named {@code <factDefinitions>} groups referenced from a rule's {@code <factsKey>}, so a
     * fact set (e.g. {@code java-facts}, {@code sql-facts}) is defined once and shared across rules
     * instead of repeated inline. Resolved onto each rule's {@code facts} before indexing.
     *
     * @see net.ladenthin.srcmorph.config.AiFactDefinitionSupport
     */
    @Parameter
    private List<AiFactDefinition> factDefinitions;

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
        final AiPromptPreparationSupport promptPreparationSupport = new AiPromptPreparationSupport(promptSupport);
        // Resolve each rule's factsKey to its shared factDefinitions group (copies the counters onto the
        // rule's facts) BEFORE validation, so the resolved fact patterns are validated too.
        resolveSharedFacts();

        final AiFieldGenerationSelector selector = new AiFieldGenerationSelector();
        // Fail fast on a bad rule set (e.g. >1 fallback, a route rule missing prompt/model).
        selector.validate(fieldGenerations);

        final SourceFileIndexer fileIndexer = new SourceFileIndexer(
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

            // 2. Plan the run: which model + prompt each file gets (or skip / unmatched), and whether
            //    each file fits its routed model's context window (computed up front, same threshold the
            //    run uses to trim — see AiInputWindowCalculator).
            final AiIndexPlan plan = fileIndexer.classify(
                    candidates, fieldGenerations, modelDefinitionSupport, promptPreparationSupport);
            getLog().info("AI index plan (Markdown):\n" + plan.renderMarkdown(basePath));

            // 3. A file that matched no rule and no fallback is a fatal misconfiguration.
            if (!plan.unmatched().isEmpty()) {
                throw new MojoExecutionException(plan.unmatched().size()
                        + " source file(s) matched no rule and no fallback is configured; "
                        + "add a <fallback> rule or a matching rule (see the plan above).");
            }

            // 3b. A file larger than its routed model's window would lose content if trimmed. By default
            //     (onOversize=fail) this is a hard failure: the fix is user configuration, never an
            //     automatic model choice — route oversized files to a larger-context model, or set the
            //     rule's onOversize (sample/mapReduce/deterministic) to handle them at run time. Only the
            //     fail entries abort here; the handled ones are processed during generation.
            final int overWindowFailCount = plan.windowFailCount();
            if (overWindowFailCount > 0) {
                throw new MojoExecutionException(overWindowFailCount
                        + " source file(s) exceed their routed model's context window with onOversize=fail "
                        + "(see the 'Over window' section in the plan above). Route them to a model with a "
                        + "large enough context window, or set onOversize=sample|mapReduce|deterministic on "
                        + "the rule. The build does not pick a model for you; this is configuration only.");
            }

            if (planOnly) {
                getLog().info("planOnly=true: stopping after the plan; no model loaded, nothing generated.");
                return;
            }

            // 4. Execute model group by model group: load each model once, index its files, close.
            //    Progress is the running sum of each finished file's PLAN estimate over the grand total
            //    (no re-estimation), logged as a bar + percent after every file, with the estimated time
            //    left and the actual wall-clock elapsed for comparison.
            final AiGenerationProviderFactory providerFactory = new AiGenerationProviderFactory();
            final AiGenerationTimeEstimator estimator = new AiGenerationTimeEstimator();
            final long totalEstimatedSeconds = plan.totalEstimatedSeconds();
            final int totalFiles = plan.routedCount();
            final long runStartNanos = System.nanoTime();
            long doneEstimatedSeconds = 0;
            int doneFiles = 0;
            int wrote = 0;
            int unchanged = 0;
            for (final Map.Entry<String, List<AiIndexPlan.Entry>> group :
                    plan.routesByModel().entrySet()) {
                final String aiDefinitionKey = group.getKey();
                getLog().info("Loading model '" + aiDefinitionKey + "' for "
                        + group.getValue().size() + " file(s)");
                try (AiGenerationProvider provider = providerFactory.create(
                        generationProvider, buildLlamaCppJniConfig(aiDefinitionKey), promptSupport)) {
                    final AiFieldGenerationSupport support =
                            new AiFieldGenerationSupport(provider, promptPreparationSupport, modelDefinitionSupport);
                    for (final AiIndexPlan.Entry entry : group.getValue()) {
                        if (fileIndexer.indexFile(entry.file(), entry.rule(), support)) {
                            wrote++;
                        } else {
                            unchanged++;
                        }
                        doneEstimatedSeconds += entry.estimatedSeconds();
                        doneFiles++;
                        final long elapsedSeconds = (System.nanoTime() - runStartNanos) / NANOS_PER_SECOND;
                        final long remainingSeconds = Math.max(0L, totalEstimatedSeconds - doneEstimatedSeconds);
                        getLog().info(AiProgressBar.render(doneEstimatedSeconds, totalEstimatedSeconds)
                                + " " + doneFiles + "/" + totalFiles + " files - est. "
                                + estimator.formatDuration(doneEstimatedSeconds) + "/"
                                + estimator.formatDuration(totalEstimatedSeconds) + " done, "
                                + estimator.formatDuration(remainingSeconds) + " left (estimate) | "
                                + estimator.formatDuration(elapsedSeconds) + " elapsed (actual)");
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

    /**
     * Resolves each rule's {@code factsKey} to its shared {@code <factDefinitions>} group, copying the
     * counters onto the rule's {@code facts}. Translates a misconfiguration (unknown key, or a definition
     * with a null key) into a {@link MojoExecutionException} so Maven reports a user configuration error.
     *
     * @throws MojoExecutionException if a {@code factsKey} matches no group or a definition has a null key
     */
    private void resolveSharedFacts() throws MojoExecutionException {
        try {
            new AiFactDefinitionSupport(factDefinitions).resolveFactsKeys(fieldGenerations);
        } catch (final IllegalArgumentException | NullPointerException e) {
            throw new MojoExecutionException("Invalid factDefinitions/factsKey configuration: " + e.getMessage(), e);
        }
    }
}
