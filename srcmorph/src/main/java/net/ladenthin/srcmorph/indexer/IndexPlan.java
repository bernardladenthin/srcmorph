// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.ladenthin.srcmorph.config.OversizeStrategy;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.indexer.generation.GenerationTimeEstimator;
import net.ladenthin.srcmorph.indexer.generation.MapReduceMath;
import org.jspecify.annotations.Nullable;

/**
 * The routing plan for one {@code generate} run: which files each AI model will index (with which
 * prompt), which files are skipped, and which matched no rule.
 *
 * <p>Built in a planning pass <em>before</em> any inference so the user sees the full mapping up front
 * (see {@link #renderMarkdown}) and a misconfiguration fails fast. Routed files are grouped by
 * {@code aiDefinitionKey} (the model id, which carries the full parameter set) so the executor can load
 * each model exactly once.</p>
 */
public final class IndexPlan {

    /** A single planned file together with the rule that routed it and its rough time estimate. */
    public static final class Route {
        private final Path file;
        private final RoutingRule rule;
        private final long estimatedSeconds;
        private final boolean windowChecked;
        private final long sourceChars;
        private final long availableSourceChars;
        private final long sourceTokens;

        /**
         * Creates a route.
         *
         * @param file                 the source file
         * @param rule                 the rule that selected it
         * @param estimatedSeconds     the rough estimated generation time in seconds
         * @param windowChecked        whether the context-window fit was computed for this entry
         * @param sourceChars          the source length in characters fed to the model (when checked)
         * @param availableSourceChars the source-character budget of the routed model+prompt (when checked)
         * @param sourceTokens         the estimated tokens of the source (when checked)
         */
        public Route(
                final Path file,
                final RoutingRule rule,
                final long estimatedSeconds,
                final boolean windowChecked,
                final long sourceChars,
                final long availableSourceChars,
                final long sourceTokens) {
            this.file = file;
            this.rule = rule;
            this.estimatedSeconds = estimatedSeconds;
            this.windowChecked = windowChecked;
            this.sourceChars = sourceChars;
            this.availableSourceChars = availableSourceChars;
            this.sourceTokens = sourceTokens;
        }

        /**
         * Returns the estimated tokens of the source (meaningful only when {@link #windowChecked()} is
         * {@code true}): its characters over the routed model's {@code charsPerToken}.
         *
         * @return the estimated source tokens
         */
        public long sourceTokens() {
            return sourceTokens;
        }

        /**
         * Returns the source file.
         *
         * @return the source file
         */
        public Path file() {
            return file;
        }

        /**
         * Returns the rule that routed the file.
         *
         * @return the routing rule
         */
        public RoutingRule rule() {
            return rule;
        }

        /**
         * Returns the rough estimated generation time in seconds.
         *
         * @return the estimated seconds
         */
        public long estimatedSeconds() {
            return estimatedSeconds;
        }

        /**
         * Returns whether the context-window fit was computed for this entry.
         *
         * @return {@code true} when window data is available
         */
        public boolean windowChecked() {
            return windowChecked;
        }

        /**
         * Returns the source length in characters fed to the model (meaningful only when
         * {@link #windowChecked()} is {@code true}).
         *
         * @return the source character count
         */
        public long sourceChars() {
            return sourceChars;
        }

        /**
         * Returns the source-character budget of the routed model+prompt (meaningful only when
         * {@link #windowChecked()} is {@code true}).
         *
         * @return the available source-character budget
         */
        public long availableSourceChars() {
            return availableSourceChars;
        }

        /**
         * Returns whether this file exceeds the routed model's context window and would be trimmed.
         *
         * @return {@code true} when checked and the source exceeds the window budget
         */
        public boolean exceedsWindow() {
            return windowChecked && sourceChars > availableSourceChars;
        }
    }

    /** What a model header names as the basis of a time without a calibration of the model. */
    static final String BUILT_IN_BASIS = "based on GPT-6 Luna";

    /** What a model header names as the basis of a time from the model's own calibration. */
    static final String CALIBRATED_BASIS = "from the model's calibration";

    private final Map<String, List<Route>> routesByModel = new LinkedHashMap<>();
    private final List<Path> skipped = new ArrayList<>();
    private final List<Path> unmatched = new ArrayList<>();
    private final Map<Path, Integer> chunkRoomRefused = new LinkedHashMap<>();
    private final Map<Path, Integer> chunkRoomEstimated = new LinkedHashMap<>();
    private final Set<String> calibratedModels = new HashSet<>();
    private final List<Path> orphans = new ArrayList<>();

    /** Creates an empty plan. */
    public IndexPlan() {
        // no-op
    }

    /**
     * Records that a model's times come from its own {@code calibration}; the times of every other model
     * are the built-in estimate, based on GPT-6 Luna. The plan names the basis in the model's header.
     *
     * @param aiDefinitionKey the model id
     */
    public void markCalibrated(final String aiDefinitionKey) {
        calibratedModels.add(aiDefinitionKey);
    }

    /**
     * Records a routed file under its model id.
     *
     * @param aiDefinitionKey  the model id (rule's {@code aiDefinitionKey})
     * @param file             the source file
     * @param rule             the routing rule
     * @param estimatedSeconds the rough estimated generation time in seconds
     */
    public void addRoute(
            final String aiDefinitionKey, final Path file, final RoutingRule rule, final long estimatedSeconds) {
        routesByModel
                .computeIfAbsent(aiDefinitionKey, key -> new ArrayList<>())
                .add(new Route(file, rule, estimatedSeconds, false, 0L, 0L, 0L));
    }

    /**
     * Records a routed file under its model id together with its context-window fit.
     *
     * @param aiDefinitionKey      the model id (rule's {@code aiDefinitionKey})
     * @param file                 the source file
     * @param rule                 the routing rule
     * @param estimatedSeconds     the rough estimated generation time in seconds
     * @param sourceChars          the source length in characters fed to the model
     * @param availableSourceChars the source-character budget of the routed model+prompt
     */
    public void addRoute(
            final String aiDefinitionKey,
            final Path file,
            final RoutingRule rule,
            final long estimatedSeconds,
            final long sourceChars,
            final long availableSourceChars) {
        addRoute(
                aiDefinitionKey,
                file,
                rule,
                estimatedSeconds,
                sourceChars,
                availableSourceChars,
                estimatedTokens(sourceChars, GenerationTimeEstimator.ESTIMATION_CHARS_PER_TOKEN));
    }

    /**
     * Records a routed file under its model id together with its context-window fit and its tokens.
     *
     * @param aiDefinitionKey      the model id (rule's {@code aiDefinitionKey})
     * @param file                 the source file
     * @param rule                 the routing rule
     * @param estimatedSeconds     the rough estimated generation time in seconds
     * @param sourceChars          the source length in characters fed to the model
     * @param availableSourceChars the source-character budget of the routed model+prompt
     * @param sourceTokens         the estimated tokens of the source
     */
    public void addRoute(
            final String aiDefinitionKey,
            final Path file,
            final RoutingRule rule,
            final long estimatedSeconds,
            final long sourceChars,
            final long availableSourceChars,
            final long sourceTokens) {
        routesByModel
                .computeIfAbsent(aiDefinitionKey, key -> new ArrayList<>())
                .add(new Route(file, rule, estimatedSeconds, true, sourceChars, availableSourceChars, sourceTokens));
    }

    /**
     * Returns the estimated tokens of a source: its characters over the characters per token, rounded up.
     *
     * @param sourceChars   the characters
     * @param charsPerToken the characters per token, above 0
     * @return the tokens
     */
    public static long estimatedTokens(final long sourceChars, final double charsPerToken) {
        return (long) Math.ceil(sourceChars / charsPerToken);
    }

    /**
     * Records a skipped (ignored) file.
     *
     * @param file the source file
     */
    public void addSkipped(final Path file) {
        skipped.add(file);
    }

    /**
     * Records a file that matched no rule and no fallback (a fatal misconfiguration).
     *
     * @param file the source file
     */
    public void addUnmatched(final Path file) {
        unmatched.add(file);
    }

    /**
     * Records an orphaned summary: a file summary below the output directory that no routed file of this
     * plan is summarized into.
     *
     * @param summary the {@code .ai.md} file
     */
    public void addOrphan(final Path summary) {
        orphans.add(summary);
    }

    /**
     * Returns the orphaned summaries, in the order they were recorded.
     *
     * @return the orphaned summaries
     */
    public List<Path> orphans() {
        return orphans;
    }

    /**
     * Returns the routed files grouped by model id, in first-seen order.
     *
     * @return an order-preserving map of model id to its planned entries
     */
    public Map<String, List<Route>> routesByModel() {
        return routesByModel;
    }

    /**
     * Returns the skipped files.
     *
     * @return the skipped files
     */
    public List<Path> skipped() {
        return skipped;
    }

    /**
     * Returns the files that matched no rule and no fallback.
     *
     * @return the unmatched files
     */
    public List<Path> unmatched() {
        return unmatched;
    }

    /**
     * Returns the total number of routed files across all models.
     *
     * @return the routed file count
     */
    public int routedCount() {
        int total = 0;
        for (final List<Route> entries : routesByModel.values()) {
            total += entries.size();
        }
        return total;
    }

    /**
     * Returns the number of routed files whose source exceeds the routed model's context window
     * (would be trimmed). Zero when no window check was performed.
     *
     * @return the count of over-window files
     */
    public int windowExceededCount() {
        int total = 0;
        for (final List<Route> entries : routesByModel.values()) {
            for (final Route entry : entries) {
                if (entry.exceedsWindow()) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Records that an over-window file's rule chunks it ({@code mapReduce} or {@code sampleChunks}) in a
     * window that leaves a chunk fewer than {@link MapReduceMath#MIN_CHUNK_CHARS} characters, on a model
     * whose provider does not count its prompt (the run sizes its window by the same estimate): the run
     * would refuse it, so the plan fails it.
     *
     * @param file the source file, routed with {@code addRoute} as well
     * @param room the characters a chunk would get
     */
    public void refuseChunkRoom(final Path file, final int room) {
        chunkRoomRefused.put(file, room);
    }

    /**
     * Records that an over-window file's chunks get fewer than {@link MapReduceMath#MIN_CHUNK_CHARS}
     * characters by the estimate, on a model whose provider counts the prompt's tokens: the run sizes the
     * window by that count, so the file may fit or get room after all, and the plan only warns.
     *
     * @param file the source file, routed with {@code addRoute} as well
     * @param room the characters a chunk would get by the estimate
     */
    public void estimateChunkRoom(final Path file, final int room) {
        chunkRoomEstimated.put(file, room);
    }

    /**
     * Returns the number of over-window files whose chunks the window leaves too little room
     * ({@link #refuseChunkRoom}): they fail the build as the {@code onOversize=fail} ones do.
     *
     * @return the count
     */
    public int chunkRoomFailCount() {
        return chunkRoomRefused.size();
    }

    /**
     * Returns the number of over-window files whose rule's {@code onOversize} strategy is
     * {@link OversizeStrategy#FAIL} — i.e. the ones that abort the build. Over-window files with a
     * {@code sample}/{@code mapReduce}/{@code sampleChunks}/{@code deterministic} strategy are handled at
     * run time and do not count here.
     *
     * @return the count of over-window files that fail the build
     */
    public int windowFailCount() {
        int total = 0;
        for (final List<Route> entries : routesByModel.values()) {
            for (final Route entry : entries) {
                if (entry.exceedsWindow() && strategyOf(entry) == OversizeStrategy.FAIL) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Returns the oversize strategy configured on an entry's rule.
     *
     * @param entry the plan entry
     * @return the parsed oversize strategy
     */
    private static OversizeStrategy strategyOf(final Route entry) {
        return entry.rule().oversizeStrategy();
    }

    /**
     * Returns the summed estimated seconds for one model's entries.
     *
     * @param entries the model's planned entries
     * @return the total estimated seconds
     */
    private long modelSeconds(final List<Route> entries) {
        long total = 0;
        for (final Route entry : entries) {
            total += entry.estimatedSeconds();
        }
        return total;
    }

    /**
     * Returns the grand total of estimated seconds across all routed files.
     *
     * @return the total estimated seconds
     */
    public long totalEstimatedSeconds() {
        long total = 0;
        for (final List<Route> entries : routesByModel.values()) {
            total += modelSeconds(entries);
        }
        return total;
    }

    /**
     * Renders the plan as a Markdown document for the build log: a summary, one section + table per
     * model (file → prompt → rough time estimate) with a per-model subtotal, a grand total, the
     * skipped / unmatched lists and the orphaned summaries. Markdown so it can be copied straight out of
     * the console. Paths are relative to {@code baseDir}; the time column is a rough estimate (see
     * {@code GenerationTimeEstimator}).
     *
     * @param baseDir directory to relativize file paths against for readability
     * @return the Markdown document
     */
    public String renderMarkdown(final Path baseDir) {
        final GenerationTimeEstimator estimator = new GenerationTimeEstimator();
        final StringBuilder sb = new StringBuilder();
        sb.append("## AI index plan\n\n");
        sb.append("**Total:** ")
                .append(routedCount())
                .append(" file(s) across ")
                .append(routesByModel.size())
                .append(" model(s), est. ")
                .append(estimator.formatDuration(totalEstimatedSeconds()))
                .append(" - ")
                .append(skipped.size())
                .append(" skipped, ")
                .append(unmatched.size())
                .append(" unmatched, ")
                .append(windowExceededCount())
                .append(" over window (")
                .append(windowFailCount())
                .append(" will fail) _(time is a rough estimate)_\n");
        for (final Map.Entry<String, List<Route>> group : routesByModel.entrySet()) {
            final List<Route> entries = group.getValue();
            sb.append("\n### Model `")
                    .append(group.getKey())
                    .append("` - ")
                    .append(entries.size())
                    .append(" file(s), ~")
                    .append(tokens(entries))
                    .append(" source tokens (~")
                    .append(sentTokens(entries))
                    .append(" sent), est. ")
                    .append(estimator.formatDuration(modelSeconds(entries)))
                    .append(" (")
                    .append(calibratedModels.contains(group.getKey()) ? CALIBRATED_BASIS : BUILT_IN_BASIS)
                    .append(")\n\n| File | Rule | Prompt | Window | Tokens | Est. |\n|---|---|---|---|---|---|\n");
            for (final Route entry : entries) {
                sb.append("| ")
                        .append(relativize(baseDir, entry.file()))
                        .append(" | ")
                        .append(ruleId(entry.rule()))
                        .append(" | ")
                        .append(entry.rule().getPromptKey())
                        .append(" | ")
                        .append(windowCell(entry))
                        .append(" | ")
                        .append(entry.windowChecked() ? "~" + entry.sourceTokens() : "-")
                        .append(" | ")
                        .append(estimator.formatDuration(entry.estimatedSeconds()))
                        .append(" |\n");
            }
        }
        appendTokensByDirectory(sb, baseDir);
        if (!skipped.isEmpty()) {
            sb.append("\n### Skipped (").append(skipped.size()).append(")\n\n");
            for (final Path file : skipped) {
                sb.append("- ").append(relativize(baseDir, file)).append("\n");
            }
        }
        if (!unmatched.isEmpty()) {
            sb.append("\n### (!) Unmatched - no rule and no fallback (")
                    .append(unmatched.size())
                    .append(")\n\n");
            for (final Path file : unmatched) {
                sb.append("- ").append(relativize(baseDir, file)).append("\n");
            }
        }
        appendWindowExceededSection(sb, baseDir);
        if (!orphans.isEmpty()) {
            sb.append("\n### Orphaned summaries (")
                    .append(orphans.size())
                    .append(")\n\nFile summaries no file of this plan is summarized into (a deleted, renamed or")
                    .append(" excluded source); the package and project phases still read them, and")
                    .append(" pruneOrphans: true deletes them.\n\n");
            for (final Path file : orphans) {
                sb.append("- ").append(relativize(baseDir, file)).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns the estimated source tokens of the window-checked entries.
     *
     * @param entries the entries
     * @return the summed tokens
     */
    private static long tokens(final List<Route> entries) {
        long sum = 0;
        for (final Route entry : entries) {
            if (entry.windowChecked()) {
                sum += entry.sourceTokens();
            }
        }
        return sum;
    }

    /**
     * Returns the source tokens the model is sent of the window-checked entries, by each one's oversize
     * strategy ({@link #sentTokens(Route)}).
     *
     * @param entries the entries
     * @return the tokens sent
     */
    private static long sentTokens(final List<Route> entries) {
        long sum = 0;
        for (final Route entry : entries) {
            sum += sentTokens(entry);
        }
        return sum;
    }

    /**
     * Returns the source tokens a routed file sends the model: all of a file that fits the window, and of
     * one over it what its oversize strategy reads, none under {@code deterministic}, one window under
     * {@code sample} and {@code fail}, every chunk under {@code mapReduce} and {@code maxChunks} windows
     * under {@code sampleChunks}; {@code 0} for a file without a window check.
     *
     * @param entry the routed file
     * @return the tokens sent, the overlap of the chunks and the prompts left out
     */
    static long sentTokens(final Route entry) {
        if (!entry.windowChecked()) {
            return 0L;
        }
        if (!entry.exceedsWindow()) {
            return entry.sourceTokens();
        }
        // a window holds as many tokens as its characters do at the file's own density
        final long windowTokens = entry.sourceTokens() * entry.availableSourceChars() / entry.sourceChars();
        switch (strategyOf(entry)) {
            case DETERMINISTIC:
                return 0L;
            case MAP_REDUCE:
                return entry.sourceTokens();
            case SAMPLE_CHUNKS:
                return Math.min(
                        entry.sourceTokens(), windowTokens * entry.rule().getMaxChunks());
            default:
                return windowTokens;
        }
    }

    /**
     * Appends the estimated source tokens per directory of the routed, window-checked files, by path:
     * where a run's tokens (and cost) go.
     *
     * @param sb      the buffer to append to
     * @param baseDir the base directory for relativizing paths
     */
    private void appendTokensByDirectory(final StringBuilder sb, final Path baseDir) {
        final Map<String, Integer> files = new TreeMap<>();
        final Map<String, Long> tokens = new TreeMap<>();
        for (final List<Route> entries : routesByModel.values()) {
            for (final Route entry : entries) {
                if (!entry.windowChecked()) {
                    continue;
                }
                final Path parent = entry.file().getParent();
                final String relative = parent == null ? "" : relativize(baseDir, parent);
                final String directory = relative.isEmpty() ? "." : relative;
                files.merge(directory, 1, Integer::sum);
                tokens.merge(directory, entry.sourceTokens(), Long::sum);
            }
        }
        if (files.isEmpty()) {
            return;
        }
        sb.append("\n### Source tokens by directory\n\n| Directory | Files | Tokens |\n|---|---|---|\n");
        for (final Map.Entry<String, Integer> directory : files.entrySet()) {
            sb.append("| ")
                    .append(directory.getKey())
                    .append(" | ")
                    .append(directory.getValue())
                    .append(" | ~")
                    .append(tokens.get(directory.getKey()))
                    .append(" |\n");
        }
    }

    /**
     * Appends the section listing files whose source exceeds the routed model's context window (would be
     * trimmed): each file with its model, source chars and the window budget, so the user can route it to
     * a larger-window model.
     *
     * @param sb      the buffer to append to
     * @param baseDir the base directory for relativizing paths
     */
    private void appendWindowExceededSection(final StringBuilder sb, final Path baseDir) {
        final int count = windowExceededCount();
        if (count == 0) {
            return;
        }
        sb.append("\n### (!) Over window - source exceeds the model's context window, would be trimmed (")
                .append(count)
                .append(")\n\n");
        for (final Map.Entry<String, List<Route>> group : routesByModel.entrySet()) {
            for (final Route entry : group.getValue()) {
                if (entry.exceedsWindow()) {
                    final OversizeStrategy strategy = strategyOf(entry);
                    final Integer room = chunkRoomRefused.get(entry.file());
                    final Integer estimatedRoom = chunkRoomEstimated.get(entry.file());
                    sb.append("- ")
                            .append(relativize(baseDir, entry.file()))
                            .append(" -> model `")
                            .append(group.getKey())
                            .append("` (source ")
                            .append(entry.sourceChars())
                            .append(" chars > window ")
                            .append(entry.availableSourceChars())
                            .append(" chars) onOversize=")
                            .append(strategy.configValue())
                            .append(outcome(strategy, room, estimatedRoom))
                            .append('\n');
                }
            }
        }
        if (windowFailCount() > 0) {
            sb.append("\nThe onOversize=fail entries fail the build. Either route them to a model with a large")
                    .append(" enough context window, or set onOversize on the rule")
                    .append(" (sample/mapReduce/sampleChunks/deterministic)")
                    .append(" to handle oversized files (config only; the build never picks a model for you).\n");
        }
        if (chunkRoomFailCount() > 0) {
            sb.append("\nThe entries with too little room for a chunk fail the build: give their model a larger")
                    .append(" contextSize or a smaller maxOutputTokens, or shorten the map and reduce prompts.\n");
        }
    }

    /**
     * Returns what the run does with an over-window file: fail it, or handle it by its strategy.
     *
     * @param strategy the rule's oversize strategy
     * @param room          the characters a chunk would get when the window leaves too few, {@code null}
     *                      otherwise
     * @param estimatedRoom the same by the estimate on a model that counts its prompt, {@code null} otherwise
     * @return the text after the file's line
     */
    private static String outcome(
            final OversizeStrategy strategy, final @Nullable Integer room, final @Nullable Integer estimatedRoom) {
        if (room != null) {
            return " -> FAILS BUILD: " + room + " chars for a chunk, fewer than " + MapReduceMath.MIN_CHUNK_CHARS;
        }
        if (estimatedRoom != null) {
            return " -> handled; WARNING: " + estimatedRoom + " chars for a chunk by the estimate, fewer than "
                    + MapReduceMath.MIN_CHUNK_CHARS + " (the run counts the prompt's tokens and refuses the file"
                    + " only if the count leaves too little room as well)";
        }
        return strategy == OversizeStrategy.FAIL ? " -> FAILS BUILD" : " -> handled";
    }

    /**
     * Returns the per-entry "Window" cell: {@code "-"} when not checked, {@code "ok"} when it fits, or a
     * {@code "(!) X>Y"} marker (source chars vs budget) when the source exceeds the window.
     *
     * @param entry the plan entry
     * @return the window cell text
     */
    private String windowCell(final Route entry) {
        if (!entry.windowChecked()) {
            return "-";
        }
        if (entry.exceedsWindow()) {
            return "(!) " + entry.sourceChars() + ">" + entry.availableSourceChars() + " "
                    + strategyOf(entry).configValue();
        }
        return "ok";
    }

    /**
     * Returns the rule's id for display, or {@code "-"} when the rule has no id set.
     *
     * @param rule the routing rule
     * @return the rule id, or {@code "-"}
     */
    private String ruleId(final RoutingRule rule) {
        final String id = rule.getId();
        return id != null ? id : "-";
    }

    /**
     * Relativizes {@code file} against {@code baseDir} with {@code /} separators, falling back to the
     * absolute path when relativization is not possible.
     *
     * @param baseDir the base directory
     * @param file    the file
     * @return a display path
     */
    private String relativize(final Path baseDir, final Path file) {
        try {
            return baseDir.relativize(file).toString().replace('\\', '/');
        } catch (final IllegalArgumentException e) {
            return file.toString().replace('\\', '/');
        }
    }
}
