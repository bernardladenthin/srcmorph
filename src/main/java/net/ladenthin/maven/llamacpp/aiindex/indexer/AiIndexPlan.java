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

/**
 * The routing plan for one {@code generate} run: which files each AI model will index (with which
 * prompt), which files are skipped, and which matched no rule.
 *
 * <p>Built in a planning pass <em>before</em> any inference so the user sees the full mapping up front
 * (see {@link #renderTree}) and a misconfiguration fails fast. Routed files are grouped by
 * {@code aiDefinitionKey} (the model id, which carries the full parameter set) so the executor can load
 * each model exactly once.</p>
 */
public final class AiIndexPlan {

    /** A single planned file together with the rule that routed it. */
    public static final class Entry {
        private final Path file;
        private final AiFieldGenerationConfig rule;

        /**
         * Creates a plan entry.
         *
         * @param file the source file
         * @param rule the rule that selected it
         */
        public Entry(final Path file, final AiFieldGenerationConfig rule) {
            this.file = file;
            this.rule = rule;
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
     * @param aiDefinitionKey the model id (rule's {@code aiDefinitionKey})
     * @param file            the source file
     * @param rule            the routing rule
     */
    public void addRoute(final String aiDefinitionKey, final Path file, final AiFieldGenerationConfig rule) {
        routesByModel.computeIfAbsent(aiDefinitionKey, key -> new ArrayList<>()).add(new Entry(file, rule));
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
     * Renders a human-readable tree of the plan: each model id, the files it will index and their
     * prompt ids, then the skipped and unmatched files. Paths are shown relative to {@code baseDir}.
     *
     * @param baseDir directory to relativize file paths against for readability
     * @return the multi-line tree
     */
    public String renderTree(final Path baseDir) {
        final StringBuilder sb = new StringBuilder();
        sb.append("AI index plan: ")
                .append(routedCount())
                .append(" file(s) across ")
                .append(routesByModel.size())
                .append(" model(s), ")
                .append(skipped.size())
                .append(" skipped, ")
                .append(unmatched.size())
                .append(" unmatched");
        for (final Map.Entry<String, List<Entry>> group : routesByModel.entrySet()) {
            sb.append("\n  model ")
                    .append(group.getKey())
                    .append(" (")
                    .append(group.getValue().size())
                    .append("):");
            for (final Entry entry : group.getValue()) {
                sb.append("\n    - ")
                        .append(relativize(baseDir, entry.file()))
                        .append(" : prompt ")
                        .append(entry.rule().getPromptKey());
            }
        }
        if (!skipped.isEmpty()) {
            sb.append("\n  skipped (").append(skipped.size()).append("):");
            for (final Path file : skipped) {
                sb.append("\n    - ").append(relativize(baseDir, file));
            }
        }
        if (!unmatched.isEmpty()) {
            sb.append("\n  UNMATCHED - no rule and no fallback (")
                    .append(unmatched.size())
                    .append("):");
            for (final Path file : unmatched) {
                sb.append("\n    ! ").append(relativize(baseDir, file));
            }
        }
        return sb.toString();
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
