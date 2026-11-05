// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.facts.FactExtractor;
import net.ladenthin.srcmorph.indexer.HeaderHashes;
import net.ladenthin.srcmorph.indexer.IndexOutcome;
import net.ladenthin.srcmorph.indexer.IndexPlan;
import net.ladenthin.srcmorph.indexer.RecordedHashes;
import net.ladenthin.srcmorph.indexer.SourceFileIndexer;
import net.ladenthin.srcmorph.indexer.generation.GenerationTimeEstimator;
import net.ladenthin.srcmorph.indexer.generation.SummaryGenerator;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.provider.GenerationUsage;
import net.ladenthin.srcmorph.routing.RuleSelector;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 1 orchestration: indexes source files and fills in their AI-generated summary fields.
 *
 * <p>Extracted from what was {@code GenerateMojo.execute()} in the {@code llamacpp-ai-index-maven-plugin}
 * module. Plan-then-execute and rule-routed: {@link #execute()} first plans the whole run (which model +
 * prompt each file gets, or skip/unmatched, and whether each file fits its routed model's context
 * window), fails fast on an unmatched file or a hard oversize failure, stops after the plan when
 * {@link SrcMorphConfiguration#isPlanOnly()}, and otherwise loads each distinct routed model exactly once
 * (one provider resident at a time) and indexes that group's files.</p>
 */
@ToString
public final class GenerateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateEngine.class);

    /**
     * Default file extension used when {@link SrcMorphConfiguration#getFileExtensions()} is unset. Only
     * files whose names end with this extension are indexed.
     */
    private static final String DEFAULT_FILE_EXTENSION = ".java";

    /** Default source subtree used when no {@link SrcMorphConfiguration#getSubtrees()} is configured. */
    private static final String DEFAULT_SOURCE_SUBTREE = "src/main/java";

    /** Nanoseconds per second, for converting the measured run elapsed time to whole seconds. */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    /** What a kept file of map-reduce answers appends to the source path ({@code partsDirectory}). */
    static final String PARTS_SUFFIX = ".parts.md";

    private final SrcMorphConfiguration config;
    private final GenerationProviderFactory providerFactory;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates a new {@link GenerateEngine} for the given run configuration.
     *
     * @param config the run configuration
     */
    public GenerateEngine(final SrcMorphConfiguration config) {
        this(config, new GenerationProviderFactory());
    }

    /**
     * Creates a new {@link GenerateEngine} whose providers come from {@code providerFactory}.
     *
     * @param config          the run configuration
     * @param providerFactory the factory of each model group's provider
     */
    GenerateEngine(final SrcMorphConfiguration config, final GenerationProviderFactory providerFactory) {
        this.config = config;
        this.providerFactory = providerFactory;
    }

    /**
     * Runs the full plan-then-execute Phase 1 pipeline.
     *
     * @return the outcome of the run (a plan-only result when {@link SrcMorphConfiguration#isPlanOnly()})
     * @throws SrcMorphException if the run is misconfigured (no {@code fieldGenerations}, an unmatched
     *                            file with no fallback, a hard oversize failure, or an invalid rule set)
     * @throws IOException       if the source tree cannot be walked or a target file cannot be written
     */
    public GenerateResult execute() throws SrcMorphException, IOException {
        return run(false);
    }

    /**
     * Checks the configuration as a run would, without loading a model or writing anything: the plan
     * (every file routed, none over a window with {@code onOversize: fail}, no two into one summary), the
     * profile files of every rule, and every routed model (a local model's file and its GGUF header, a
     * remote model's {@code baseUrl} and {@code apiKeyEnv}). {@link SrcMorphConfiguration#isPlanOnly()}
     * does not apply.
     *
     * @return a plan-only result
     * @throws SrcMorphException naming the first problem found
     * @throws IOException       if the source tree cannot be walked
     */
    public GenerateResult check() throws SrcMorphException, IOException {
        return run(true);
    }

    private GenerateResult run(final boolean checkOnly) throws SrcMorphException, IOException {
        final Path basePath = EngineSupport.basePath(config);
        final Path outputPath =
                config.getOutputDirectory().toPath().toAbsolutePath().normalize();
        final List<Path> resolvedSubtrees = EngineSupport.resolveSubtrees(basePath, config.getSubtrees());
        final List<String> resolvedExtensions = resolveFileExtensions();

        logRun(basePath, outputPath, resolvedSubtrees, resolvedExtensions);

        final List<RoutingRule> configured = config.getFieldGenerations();
        if (configured == null || configured.isEmpty()) {
            throw new SrcMorphException("No fieldGenerations configured (srcMorph.fieldGenerations of the"
                    + " configuration file); nothing to generate.");
        }
        // the run resolves the facts keys into copies: the caller's rules stay as given, so check() and a
        // second execute() of the same configuration read them as the first did
        final List<RoutingRule> fieldGenerations = copies(configured);

        final PromptCatalog promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getFieldGenerations());
        final ModelRegistry modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(config.getAiDefinitions(), config.getModelDirectory());
        final PromptPreparer promptPreparationSupport = new PromptPreparer(promptSupport);
        // Resolve each rule's factsKey to its shared factDefinitions group (copies the counters onto the
        // rule's facts) BEFORE validation, so the resolved fact patterns are validated too.
        EngineSupport.resolveFactsKeys(config, fieldGenerations);

        final RuleSelector selector = new RuleSelector();
        // Fail fast on a bad rule set (e.g. >1 fallback, a route rule missing prompt/model), as a
        // configuration error the surfaces report as such.
        try {
            selector.validate(fieldGenerations);
        } catch (final IllegalArgumentException e) {
            throw new SrcMorphException("Invalid fieldGenerations: " + e.getMessage(), e);
        }
        EngineSupport.requireDefinedModels(modelDefinitionSupport, fieldGenerations);
        requirePrompts(promptSupport, fieldGenerations);

        final SourceFileIndexer fileIndexer =
                newFileIndexer(basePath, outputPath, resolvedSubtrees, resolvedExtensions);

        // 1. Collect candidate files across the configured subtrees.
        final List<Path> candidates = new ArrayList<>();
        for (final Path subtree : resolvedSubtrees.isEmpty()
                ? compatibilityHelper.listOf(basePath.resolve(DEFAULT_SOURCE_SUBTREE))
                : resolvedSubtrees) {
            if (!subtree.toFile().exists()) {
                LOGGER.warn("Skipping missing subtree: {}", subtree);
                continue;
            }
            candidates.addAll(fileIndexer.collectCandidates(subtree));
        }

        // 2. Plan the run: which model + prompt each file gets (or skip / unmatched), and whether
        //    each file fits its routed model's context window (computed up front, same threshold the
        //    run uses to trim — see InputWindowCalculator).
        final IndexPlan plan =
                fileIndexer.classify(candidates, fieldGenerations, modelDefinitionSupport, promptPreparationSupport);
        LOGGER.info("AI index plan (Markdown):\n{}", plan.renderMarkdown(basePath));

        // 3. A file that matched no rule and no fallback is a fatal misconfiguration.
        if (!plan.unmatched().isEmpty()) {
            throw new SrcMorphException(plan.unmatched().size()
                    + " source file(s) matched no rule and no fallback is configured; "
                    + "add a rule with fallback: true or a matching rule (see the plan above).");
        }

        // 3b. A file larger than its routed model's window would lose content if trimmed. By default
        //     (onOversize=fail) this is a hard failure: the fix is user configuration, never an
        //     automatic model choice — route oversized files to a larger-context model, or set the
        //     rule's onOversize (sample/mapReduce/sampleChunks/deterministic) to handle them at run time.
        //     Only the fail entries abort here; the handled ones are processed during generation.
        final int overWindowFailCount = plan.windowFailCount();
        if (overWindowFailCount > 0) {
            throw new SrcMorphException(overWindowFailCount
                    + " source file(s) exceed their routed model's context window with onOversize=fail "
                    + "(see the 'Over window' section in the plan above). Route them to a model with a "
                    + "large enough context window, or set onOversize=sample|mapReduce|sampleChunks|deterministic"
                    + " on the rule. The build does not pick a model for you; this is configuration only.");
        }

        // 3c. Two source files summarized into the same .ai.md (src/main/java/Foo.java and
        //     main/java/Foo.java) would overwrite each other; a prompt longer than its model's window
        //     leaves no room for any source. Both are configuration errors, found before the run.
        checkTargetsAndWindows(plan, fileIndexer);

        // Fail fast on a typo'd modelPath before the model-group loop below, not an hour into it.
        //
        // Deliberately skipped by planOnly: planOnly is documented to "stop before loading any model",
        // and the workflow it exists for is configuring routing on a machine where the GGUFs are not
        // present and running for real elsewhere. Stat'ing the file is not loading it, but failing the
        // plan on a missing model would break that workflow all the same -- and Plan is the CLI's
        // default command. check() is the plan with this check, for the machine that runs.
        final boolean planOnly = !checkOnly && config.isPlanOnly();
        if (!planOnly) {
            EngineSupport.validateRoutedModelPaths(
                    config.getGenerationProvider(), modelDefinitionSupport, fieldGenerations);
        }
        if (planOnly || checkOnly) {
            LOGGER.info(
                    checkOnly
                            ? "Check passed: the plan, the profile files and the routed models are in order; no"
                                    + " model loaded, nothing written."
                            : "planOnly=true: stopping after the plan; no model loaded, nothing generated.");
            return GenerateResult.planned();
        }

        // 4. Execute model group by model group: load each model once, index its files, close.
        //    Progress is the running sum of each finished file's PLAN estimate over the grand total
        //    (no re-estimation), logged as a bar + percent after every file, with the estimated time
        //    left and the actual wall-clock elapsed for comparison.
        final GenerationTimeEstimator estimator = new GenerationTimeEstimator();
        final long totalEstimatedSeconds = plan.totalEstimatedSeconds();
        final int totalFiles = plan.routedCount();
        final long runStartNanos = System.nanoTime();
        final RunRecord runRecord =
                new RunRecord(config.getGeneratorVersion(), plan.skipped().size());
        long doneEstimatedSeconds = 0;
        int doneFiles = 0;
        int wrote = 0;
        int unchanged = 0;
        int failed = 0;
        // the record is written however the run ends, and a failed write never hides why it ended
        try {
            for (final Map.Entry<String, List<IndexPlan.Route>> group :
                    plan.routesByModel().entrySet()) {
                final String aiDefinitionKey = group.getKey();
                LOGGER.info(
                        "Loading model '{}' for {} file(s)",
                        aiDefinitionKey,
                        group.getValue().size());
                try (GenerationProvider provider = providerFactory.create(
                        config.getGenerationProvider(),
                        EngineSupport.resolveProviderConfig(modelDefinitionSupport, aiDefinitionKey),
                        promptSupport)) {
                    final SummaryGenerator support =
                            new SummaryGenerator(provider, promptPreparationSupport, modelDefinitionSupport);
                    for (final IndexPlan.Route entry : group.getValue()) {
                        IndexOutcome outcome;
                        final long fileStartNanos = System.nanoTime();
                        try {
                            outcome = fileIndexer.indexFile(
                                    entry.file(),
                                    entry.rule(),
                                    support,
                                    HeaderHashes.prompt(promptSupport, entry.rule()),
                                    FactExtractor.factsHash(entry.rule().getFacts()),
                                    HeaderHashes.model(
                                            aiDefinitionKey, modelDefinitionSupport.definition(aiDefinitionKey)));
                        } catch (final InterruptedIOException e) {
                            // an interrupt ends the run, it is not one file's failure; a timeout is one (a
                            // SocketTimeoutException is an InterruptedIOException as well)
                            if (Thread.currentThread().isInterrupted()) {
                                throw e;
                            }
                            LOGGER.error("Summarizing {} failed: {}", entry.file(), e.toString());
                            LOGGER.debug("Summarizing {} failed", entry.file(), e);
                            outcome = IndexOutcome.FAILED;
                        } catch (final IOException | RuntimeException e) {
                            // one failed request (after its retries) does not end the run, nor does an unchecked
                            // error of a provider (the llama.cpp binding throws only those)
                            LOGGER.error("Summarizing {} failed: {}", entry.file(), e.toString());
                            LOGGER.debug("Summarizing {} failed", entry.file(), e);
                            outcome = IndexOutcome.FAILED;
                        }
                        // a local model loads on the first file that needs it: its own time, not the file's
                        final long loadNanos = provider.takeLoadNanos();
                        runRecord.addModelLoad(aiDefinitionKey, loadNanos);
                        runRecord.add(recordEntry(
                                basePath,
                                entry,
                                aiDefinitionKey,
                                outcome,
                                support.takeUsage(),
                                Math.max(0L, System.nanoTime() - fileStartNanos - loadNanos),
                                recordedHeader(fileIndexer, entry, outcome, promptSupport)));
                        keepParts(basePath, entry.file(), support.parts());
                        if (outcome == IndexOutcome.WRITTEN) {
                            wrote++;
                        } else if (outcome == IndexOutcome.UNCHANGED) {
                            unchanged++;
                        } else {
                            failed++;
                        }
                        doneEstimatedSeconds += entry.estimatedSeconds();
                        doneFiles++;
                        final long elapsedSeconds = (System.nanoTime() - runStartNanos) / NANOS_PER_SECOND;
                        final long remainingSeconds = Math.max(0L, totalEstimatedSeconds - doneEstimatedSeconds);
                        LOGGER.info(
                                "{} {}/{} files - est. {}/{} done, {} left (estimate) | {} elapsed (actual)",
                                ProgressBar.render(doneEstimatedSeconds, totalEstimatedSeconds),
                                doneFiles,
                                totalFiles,
                                estimator.formatDuration(doneEstimatedSeconds),
                                estimator.formatDuration(totalEstimatedSeconds),
                                estimator.formatDuration(remainingSeconds),
                                estimator.formatDuration(elapsedSeconds));
                    }
                }
            }
        } catch (final IOException | RuntimeException | Error e) {
            // the record of the files done so far; a failed write is added to the abort, not put in its place
            final IOException writeFailure = writeRecord(runRecord);
            if (writeFailure != null) {
                e.addSuppressed(writeFailure);
            }
            throw e;
        }
        final IOException writeFailure = writeRecord(runRecord);
        // files that were not written are the run's failure; a failed record write only when there is none
        if (writeFailure != null && failed == 0) {
            throw writeFailure;
        }

        final int skippedCount = plan.skipped().size();
        LOGGER.info(
                "Generated AI files: {} written, {} unchanged, {} skipped, {} failed",
                wrote,
                unchanged,
                skippedCount,
                failed);
        if (failed > 0) {
            throw new SrcMorphException(failed + " file(s) were not written (an incomplete answer or a failed"
                    + " request); the WARN and ERROR lines name them, and the next run tries them again");
        }
        LOGGER.info("AI index generation finished.");

        return new GenerateResult(false, wrote, unchanged, skippedCount);
    }

    /**
     * Keeps a file's map-reduce answers when {@code partsDirectory} is set: in
     * {@code <partsDirectory>/<path below the base directory>.parts.md}. A failed write is logged and
     * does not fail the file, whose summary is written already.
     *
     * @param basePath   the base directory
     * @param sourceFile the file
     * @param parts      its answers ({@link SummaryGenerator#parts()}), {@code ""} for none
     */
    private void keepParts(final Path basePath, final Path sourceFile, final String parts) {
        final File directory = config.getPartsDirectory();
        if (directory == null || parts.isEmpty()) {
            return;
        }
        final String relative = basePath.relativize(sourceFile).toString().replace('\\', '/');
        final Path target = directory.toPath().resolve(relative + PARTS_SUFFIX);
        try {
            final Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(
                    target, ("# Map-reduce answers of " + relative + "\n\n" + parts).getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Kept the map-reduce answers of {}: {}", relative, target);
        } catch (final IOException e) {
            LOGGER.warn("Keeping the map-reduce answers of {} in {} failed: {}", relative, target, e.toString());
        }
    }

    /**
     * Writes the run record when {@code runRecordFile} is set.
     *
     * @param runRecord the record of the files done so far
     * @return the write's failure, logged already, or {@code null}
     */
    private @Nullable IOException writeRecord(final RunRecord runRecord) {
        final File runRecordFile = config.getRunRecordFile();
        if (runRecordFile == null) {
            return null;
        }
        try {
            runRecord.write(runRecordFile.toPath());
            LOGGER.info("Run record written: {}", runRecordFile);
            return null;
        } catch (final IOException e) {
            LOGGER.error("Writing the run record {} failed: {}", runRecordFile, e.toString());
            return e;
        }
    }

    /**
     * Returns the run record's entry of one routed file.
     *
     * @param basePath      the base directory, the file is recorded relative to it
     * @param entry         the file's plan entry
     * @param model         the model key of its group
     * @param outcome       what happened to it
     * @param usage         the tokens and calls it cost
     * @param nanos         its wall time
     * @param hashes        its prompt hash (key) and facts hash (value)
     * @return the entry
     */
    private static RunRecord.FileRecord recordEntry(
            final Path basePath,
            final IndexPlan.Route entry,
            final String model,
            final IndexOutcome outcome,
            final GenerationUsage usage,
            final long nanos,
            final RecordedHashes hashes) {
        final RoutingRule rule = entry.rule();
        final String id = rule.getId();
        return new RunRecord.FileRecord(
                basePath.relativize(entry.file()).toString().replace('\\', '/'),
                id != null ? id : rule.requirePromptKey(),
                rule.requirePromptKey(),
                model,
                outcome,
                usage,
                nanos,
                hashes.promptHash(),
                hashes.factsHash());
    }

    /**
     * Returns a copy of every rule, so a run changes none of the caller's.
     *
     * @param rules the configured rules
     * @return the copies, in order
     */
    private static List<RoutingRule> copies(final List<RoutingRule> rules) {
        final List<RoutingRule> copies = new ArrayList<>(rules.size());
        for (final RoutingRule rule : rules) {
            copies.add(rule.copy());
        }
        return copies;
    }

    /**
     * Checks that every prompt a routing rule names, its map and reduce prompts too, has a template, before
     * a model loads: a key without one would otherwise fail every file of the rule once the model is up.
     *
     * @param promptSupport the prompts
     * @param rules         the rules
     * @throws SrcMorphException naming the key without a template
     */
    private static void requirePrompts(final PromptCatalog promptSupport, final List<RoutingRule> rules)
            throws SrcMorphException {
        for (final RoutingRule rule : rules) {
            if (!rule.isSkip()) {
                try {
                    HeaderHashes.prompt(promptSupport, rule);
                } catch (final IllegalArgumentException e) {
                    throw new SrcMorphException("Invalid fieldGenerations: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Returns the prompt and facts hashes of a file's record entry: of the summary as it stands, so an
     * unchanged file shows the prompt that wrote it (from its header; empty for a summary written before
     * the header recorded it), a written or failed one the current prompt.
     *
     * @param fileIndexer   the indexer that knows the summary's path
     * @param entry         the plan entry
     * @param outcome       the file's outcome
     * @param promptSupport the prompts
     * @return the prompt and facts hashes
     * @throws IOException if an unchanged file's summary cannot be read
     */
    private static RecordedHashes recordedHeader(
            final SourceFileIndexer fileIndexer,
            final IndexPlan.Route entry,
            final IndexOutcome outcome,
            final PromptCatalog promptSupport)
            throws IOException {
        if (outcome == IndexOutcome.UNCHANGED) {
            return fileIndexer.recordedHashes(entry.file());
        }
        return new RecordedHashes(
                HeaderHashes.prompt(promptSupport, entry.rule()),
                FactExtractor.factsHash(entry.rule().getFacts()));
    }

    /**
     * Fails the run when two routed files would be summarized into the same {@code .ai.md}, or when a
     * rule's prompt leaves its model's window no room for the source.
     *
     * @param plan        the routing plan
     * @param fileIndexer the indexer that names each file's summary
     * @throws SrcMorphException naming the files, or the model and the prompt
     */
    private static void checkTargetsAndWindows(final IndexPlan plan, final SourceFileIndexer fileIndexer)
            throws SrcMorphException {
        final Map<Path, Path> sourceByTarget = new HashMap<>();
        for (final List<IndexPlan.Route> entries : plan.routesByModel().values()) {
            for (final IndexPlan.Route entry : entries) {
                final Path other = sourceByTarget.put(fileIndexer.targetFile(entry.file()), entry.file());
                if (other != null) {
                    throw new SrcMorphException(entry.file() + " and " + other + " would both be summarized into "
                            + fileIndexer.targetFile(entry.file()) + "; exclude one of them or narrow the subtrees");
                }
                if (entry.windowChecked() && entry.availableSourceChars() <= 0) {
                    throw new SrcMorphException("The prompt '" + entry.rule().getPromptKey()
                            + "' leaves no room for the source in the window of model '"
                            + entry.rule().getAiDefinitionKey() + "' (" + entry.file()
                            + "); raise its contextSize or lower its maxOutputTokens");
                }
            }
        }
    }

    /**
     * Logs what the run reads and how.
     *
     * @param basePath           the base directory
     * @param outputPath         the output directory
     * @param resolvedSubtrees   the subtrees
     * @param resolvedExtensions the file extensions
     */
    private void logRun(
            final Path basePath,
            final Path outputPath,
            final List<Path> resolvedSubtrees,
            final List<String> resolvedExtensions) {
        LOGGER.info("Starting AI index generation");
        LOGGER.info("Base directory  : {}", basePath);
        LOGGER.info("Output directory: {}", outputPath);
        LOGGER.info("Subtrees        : {}", resolvedSubtrees);
        if (!resolvedExtensions.isEmpty()) {
            LOGGER.info("Extensions      : {}", resolvedExtensions);
        }
        final List<String> fileNames = config.getFileNames();
        if (fileNames != null && !fileNames.isEmpty()) {
            LOGGER.info("File names      : {}", fileNames);
        }
        LOGGER.info("Force           : {}", config.isForce());
        LOGGER.info("Regenerate on   : {}", config.getRegenerateOn());
        LOGGER.info("Provider        : {}", config.getGenerationProvider());
    }

    /**
     * Builds the file indexer of the run.
     *
     * @param basePath           the base directory
     * @param outputPath         the output directory
     * @param resolvedSubtrees   the subtrees
     * @param resolvedExtensions the file extensions
     * @return the indexer
     * @throws SrcMorphException if an exclude or a file name is not a valid glob, an interpreter is blank
     *                           or a path, or {@code contentHeadChars} is below 256
     */
    private SourceFileIndexer newFileIndexer(
            final Path basePath,
            final Path outputPath,
            final List<Path> resolvedSubtrees,
            final List<String> resolvedExtensions)
            throws SrcMorphException {
        try {
            return new SourceFileIndexer(
                    basePath,
                    outputPath,
                    resolvedExtensions,
                    config.getFileNames(),
                    config.getFileInterpreters(),
                    config.getGeneratorVersion(),
                    config.getAiVersion(),
                    resolvedSubtrees,
                    config.getExcludes(),
                    config.getMinFileSizeBytes(),
                    config.getMaxFileSizeBytes(),
                    config.isForce(),
                    config.getRegenerateOn(),
                    config.getContentHeadChars());
        } catch (final IllegalArgumentException e) {
            throw new SrcMorphException(
                    "Invalid excludes, fileNames, fileInterpreters or contentHeadChars: " + e.getMessage(), e);
        }
    }

    private List<String> resolveFileExtensions() {
        final List<String> configured = config.getFileExtensions();
        if (configured == null || configured.isEmpty()) {
            return compatibilityHelper.listOf(DEFAULT_FILE_EXTENSION);
        }
        return configured;
    }
}
