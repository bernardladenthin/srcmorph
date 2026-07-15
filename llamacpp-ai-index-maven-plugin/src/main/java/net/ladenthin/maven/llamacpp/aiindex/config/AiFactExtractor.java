// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jspecify.annotations.Nullable;

/**
 * Computes a deterministic, language-agnostic "facts" block from a list of {@link AiFactCounter}s.
 *
 * <p>Each counter contributes one {@code label: count} entry, where {@code count} is the number of
 * non-overlapping matches of the counter's regex across the <em>whole</em> source. This gives exact,
 * model-free counts (rows, tables, boolean fields, functions, …) that a chunked/sampled AI summary
 * cannot reliably produce, and is prepended to the AI body on the oversize path. Pure; no I/O.</p>
 */
public final class AiFactExtractor {

    /** Header prefixing the facts line, marking the counts as exact and whole-file (not AI-estimated). */
    public static final String FACTS_HEADER = "**Facts (exact, whole file):** ";

    /** Separator between {@code label} and its count. */
    public static final String LABEL_COUNT_SEPARATOR = ": ";

    /** Separator between consecutive {@code label: count} entries. */
    public static final String ENTRY_SEPARATOR = "; ";

    /** Trailing separator between the facts line and the AI body that follows it. */
    public static final String FACTS_SUFFIX = "\n\n";

    /** Utility class; not instantiable. */
    private AiFactExtractor() {
        // no-op
    }

    /**
     * Builds the facts block for the given counters over {@code source}, or an empty string when there are
     * no counters. Counters with a {@code null} label or pattern are skipped (they are rejected up front by
     * {@link #validate(List)}).
     *
     * @param counters the configured fact counters; {@code null} or empty yields an empty string
     * @param source   the full source text the regexes are counted over
     * @return the facts block (ending in a blank line), or {@code ""} when there is nothing to report
     */
    public static String factsBlock(final @Nullable List<AiFactCounter> counters, final String source) {
        if (counters == null || counters.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(64);
        for (final AiFactCounter counter : counters) {
            if (counter == null || counter.getLabel() == null || counter.getPattern() == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(ENTRY_SEPARATOR);
            }
            sb.append(counter.getLabel())
                    .append(LABEL_COUNT_SEPARATOR)
                    .append(countMatches(counter.getPattern(), source));
        }
        if (sb.length() == 0) {
            return "";
        }
        return FACTS_HEADER + sb + FACTS_SUFFIX;
    }

    /**
     * Validates that every counter has a non-null label and a compilable pattern, throwing
     * {@link IllegalArgumentException} so the build fails fast on a misconfigured {@code <facts>} list.
     *
     * @param counters the configured fact counters; {@code null} or empty is valid (nothing to check)
     * @throws IllegalArgumentException if a counter is missing its label/pattern or has an invalid regex
     */
    public static void validate(final @Nullable List<AiFactCounter> counters) {
        if (counters == null) {
            return;
        }
        for (final AiFactCounter counter : counters) {
            if (counter == null) {
                continue;
            }
            if (counter.getLabel() == null) {
                throw new IllegalArgumentException("A fact counter must have a label: " + counter);
            }
            if (counter.getPattern() == null) {
                throw new IllegalArgumentException("A fact counter must have a pattern: " + counter);
            }
            try {
                Pattern.compile(counter.getPattern());
            } catch (final PatternSyntaxException e) {
                throw new IllegalArgumentException(
                        "Invalid fact pattern for label '" + counter.getLabel() + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Counts the non-overlapping matches of {@code pattern} in {@code source}.
     *
     * @param pattern the regular expression
     * @param source  the text to search
     * @return the number of matches (zero-width matches, e.g. {@code (?m)^}, count once per position)
     */
    private static int countMatches(final String pattern, final String source) {
        final Matcher matcher = Pattern.compile(pattern).matcher(source);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
