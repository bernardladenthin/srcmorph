// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig;
import net.ladenthin.maven.llamacpp.aiindex.support.AiGenerationTimeEstimator;

/**
 * The routing plan for one {@code generate} run: which files each AI model will index (with which
 * prompt), which files are skipped, and which matched no rule.
 *
 * <p>Built in a planning pass <em>before</em> any inference so the user sees the full mapping up front
 * (see {@link #renderMarkdown}) and a misconfiguration fails fast. Routed files are grouped by
 * {@code aiDefinitionKey} (the model id, which carries the full parameter set) so the executor can load
 * each model exactly once.</p>
 */
public final class AiIndexPlan {

    /** A single planned file together with the rule that routed it and its rough time estimate. */
    public static final class Entry {
        private final Path file;
        private final AiFieldGenerationConfig rule;
        private final long estimatedSeconds;
        private final boolean windowChecked;
        private final long sourceChars;
        private final long availableSourceChars;

        /**
         * Creates a plan entry.
         *
         * @param file                 the source file
         * @param rule                 the rule that selected it
         * @param estimatedSeconds     the rough estimated generation time in seconds
         * @param windowChecked        whether the context-window fit was computed for this entry
         * @param sourceChars          the source length in characters fed to the model (when checked)
         * @param availableSourceChars the source-character budget of the routed model+prompt (when checked)
         */
        public Entry(
                final Path file,
                final AiFieldGenerationConfig rule,
                final long estimatedSeconds,
                final boolean windowChecked,
                final long sourceChars,
                final long availableSourceChars) {
            this.file = file;
            this.rule = rule;
            this.estimatedSeconds = estimatedSeconds;
            this.windowChecked = windowChecked;
            this.sourceChars = sourceChars;
            this.availableSourceChars = availableSourceChars;
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
        public AiFieldGenerationConfig rule() {
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

    private final Map<String, List<Entry>> routesByModel = new LinkedHashMap<>();
    private final List<Path> skipped = new ArrayList<>();
    private final List<Path> unmatched = new ArrayList<>();

    /** Creates an empty plan. */
    public AiIndexPlan() {
        // no-op
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
            final String aiDefinitionKey,
            final Path file,
            final AiFieldGenerationConfig rule,
            final long estimatedSeconds) {
        routesByModel
                .computeIfAbsent(aiDefinitionKey, key -> new ArrayList<>())
                .add(new Entry(file, rule, estimatedSeconds, false, 0L, 0L));
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
            final AiFieldGenerationConfig rule,
            final long estimatedSeconds,
            final long sourceChars,
            final long availableSourceChars) {
        routesByModel
                .computeIfAbsent(aiDefinitionKey, key -> new ArrayList<>())
                .add(new Entry(file, rule, estimatedSeconds, true, sourceChars, availableSourceChars));
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
     * Returns the routed files grouped by model id, in first-seen order.
     *
     * @return an order-preserving map of model id to its planned entries
     */
    public Map<String, List<Entry>> routesByModel() {
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
        for (final List<Entry> entries : routesByModel.values()) {
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
        for (final List<Entry> entries : routesByModel.values()) {
            for (final Entry entry : entries) {
                if (entry.exceedsWindow()) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Returns the summed estimated seconds for one model's entries.
     *
     * @param entries the model's planned entries
     * @return the total estimated seconds
     */
    private long modelSeconds(final List<Entry> entries) {
        long total = 0;
        for (final Entry entry : entries) {
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
        for (final List<Entry> entries : routesByModel.values()) {
            total += modelSeconds(entries);
        }
        return total;
    }

    /**
     * Renders the plan as a Markdown document for the build log: a summary, one section + table per
     * model (file → prompt → rough time estimate) with a per-model subtotal, a grand total, and the
     * skipped / unmatched lists. Markdown so it can be copied straight out of the console. Paths are
     * relative to {@code baseDir}; the time column is a rough estimate (see {@code AiGenerationTimeEstimator}).
     *
     * @param baseDir directory to relativize file paths against for readability
     * @return the Markdown document
     */
    public String renderMarkdown(final Path baseDir) {
        final AiGenerationTimeEstimator estimator = new AiGenerationTimeEstimator();
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
                .append(" over window _(time is a rough estimate)_\n");
        for (final Map.Entry<String, List<Entry>> group : routesByModel.entrySet()) {
            final List<Entry> entries = group.getValue();
            sb.append("\n### Model `")
                    .append(group.getKey())
                    .append("` - ")
                    .append(entries.size())
                    .append(" file(s), est. ")
                    .append(estimator.formatDuration(modelSeconds(entries)))
                    .append("\n\n| File | Rule | Prompt | Window | Est. |\n|---|---|---|---|---|\n");
            for (final Entry entry : entries) {
                sb.append("| ")
                        .append(relativize(baseDir, entry.file()))
                        .append(" | ")
                        .append(ruleId(entry.rule()))
                        .append(" | ")
                        .append(entry.rule().getPromptKey())
                        .append(" | ")
                        .append(windowCell(entry))
                        .append(" | ")
                        .append(estimator.formatDuration(entry.estimatedSeconds()))
                        .append(" |\n");
            }
        }
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
        return sb.toString();
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
        for (final Map.Entry<String, List<Entry>> group : routesByModel.entrySet()) {
            for (final Entry entry : group.getValue()) {
                if (entry.exceedsWindow()) {
                    sb.append("- ")
                            .append(relativize(baseDir, entry.file()))
                            .append(" -> model `")
                            .append(group.getKey())
                            .append("` (source ")
                            .append(entry.sourceChars())
                            .append(" chars > window ")
                            .append(entry.availableSourceChars())
                            .append(" chars)\n");
                }
            }
        }
        sb.append("\nThis fails the build. Add a <fieldGeneration> rule with a size <condition> that routes")
                .append(" these files to a model with a large enough context window (config only; the build")
                .append(" never picks a model for you).\n");
    }

    /**
     * Returns the per-entry "Window" cell: {@code "-"} when not checked, {@code "ok"} when it fits, or a
     * {@code "(!) X>Y"} marker (source chars vs budget) when the source exceeds the window.
     *
     * @param entry the plan entry
     * @return the window cell text
     */
    private String windowCell(final Entry entry) {
        if (!entry.windowChecked()) {
            return "-";
        }
        if (entry.exceedsWindow()) {
            return "(!) " + entry.sourceChars() + ">" + entry.availableSourceChars();
        }
        return "ok";
    }

    /**
     * Returns the rule's id for display, or {@code "-"} when the rule has no id set.
     *
     * @param rule the routing rule
     * @return the rule id, or {@code "-"}
     */
    private String ruleId(final AiFieldGenerationConfig rule) {
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
