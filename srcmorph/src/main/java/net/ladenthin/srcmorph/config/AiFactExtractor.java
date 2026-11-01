// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.ladenthin.srcmorph.support.AiChecksumSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Computes a deterministic, language-agnostic "facts" block from a list of {@link AiFactCounter}s.
 *
 * <p>Each counter contributes one {@code label: count} entry, where {@code count} is the number of
 * non-overlapping matches of the counter's regex across the <em>whole</em> source. This gives exact,
 * model-free counts (rows, tables, boolean fields, functions, …) that a chunked/sampled AI summary
 * cannot reliably produce, and is prepended to the AI body on the oversize path. Pure; no I/O.</p>
 *
 * <p>The matches of an {@link AiFactCounter#isIgnore() ignore} counter are blanked out first, keeping
 * their line breaks, and the reported counters count what is left.</p>
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

    /** No label starts with this: it opens a marker such as {@code [ignore]} in a facts file. */
    public static final String LABEL_MARKER_START = "[";

    /** Utility class; not instantiable. */
    private AiFactExtractor() {
        // no-op
    }

    /**
     * Builds the facts block for the given counters over {@code source}, or an empty string when there are
     * no counters. Counters with a {@code null} label or pattern are skipped (they are rejected up front by
     * {@link #validate(String, List)}).
     *
     * @param counters the configured fact counters; {@code null} or empty yields an empty string
     * @param source   the full source text the regexes are counted over
     * @return the facts block (ending in a blank line), or {@code ""} when there is nothing to report
     */
    public static String factsBlock(final @Nullable List<AiFactCounter> counters, final String source) {
        if (counters == null || counters.isEmpty()) {
            return "";
        }
        final String counted = withoutIgnored(counters, source);
        final StringBuilder sb = new StringBuilder(64);
        for (final AiFactCounter counter : counters) {
            if (!isReported(counter)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(ENTRY_SEPARATOR);
            }
            sb.append(counter.getLabel())
                    .append(LABEL_COUNT_SEPARATOR)
                    .append(countMatches(counter.getPattern(), counted));
        }
        if (sb.length() == 0) {
            return "";
        }
        return FACTS_HEADER + sb + FACTS_SUFFIX;
    }

    /**
     * Counts every reported counter over {@code source}, after the ignore counters blanked their matches.
     * A label that occurs twice keeps the later count.
     *
     * @param counters the configured fact counters; {@code null} or empty yields an empty map
     * @param source   the full source text
     * @return the count per label, in counter order
     */
    public static Map<String, Integer> counts(final @Nullable List<AiFactCounter> counters, final String source) {
        final Map<String, Integer> counts = new LinkedHashMap<>();
        if (counters != null) {
            final String counted = withoutIgnored(counters, source);
            for (final AiFactCounter counter : counters) {
                if (isReported(counter)) {
                    counts.put(counter.getLabel(), countMatches(counter.getPattern(), counted));
                }
            }
        }
        return counts;
    }

    /**
     * Validates one group of fact counters, from a configuration's {@code facts} or from a facts file, so
     * both accept exactly the same groups: every entry is present and has a non-blank label and a
     * non-blank pattern that compiles, no label is used twice or starts with {@value #LABEL_MARKER_START}
     * (a marker such as {@code [ignore]} belongs to the facts-file syntax; a configuration sets
     * {@code ignore}), and at least one counter is reported.
     *
     * @param where    the group, for the messages
     * @param counters the fact counters; {@code null} or empty is valid (nothing to check)
     * @throws IllegalArgumentException naming the group and the counter if any of that does not hold
     */
    public static void validate(final String where, final @Nullable List<AiFactCounter> counters) {
        if (counters == null || counters.isEmpty()) {
            return;
        }
        final Set<String> labels = new HashSet<>(new Java8CompatibilityHelper().hashMapCapacityFor(counters.size()));
        boolean reported = false;
        for (int i = 0; i < counters.size(); i++) {
            final AiFactCounter counter = counters.get(i);
            final String entry = where + " counter " + (i + 1);
            if (counter == null) {
                throw new IllegalArgumentException(entry + " is missing");
            }
            final String label = counter.getLabel();
            if (label == null || label.trim().isEmpty()) {
                throw new IllegalArgumentException(entry + " must have a label");
            }
            if (label.startsWith(LABEL_MARKER_START)) {
                throw new IllegalArgumentException(entry + ": the label '" + label + "' starts with '"
                        + LABEL_MARKER_START + "', which marks a region in a facts file; set ignore instead");
            }
            if (!labels.add(label)) {
                throw new IllegalArgumentException(where + ": the label '" + label + "' is used twice");
            }
            final String pattern = counter.getPattern();
            if (pattern == null || pattern.trim().isEmpty()) {
                throw new IllegalArgumentException(entry + " ('" + label + "') must have a pattern");
            }
            try {
                Pattern.compile(pattern);
            } catch (final PatternSyntaxException e) {
                throw new IllegalArgumentException(
                        where + ": invalid fact pattern for label '" + label + "': " + e.getMessage(), e);
            }
            reported |= !counter.isIgnore();
        }
        if (!reported) {
            throw new IllegalArgumentException(where + ": no counter, only ignore patterns");
        }
    }

    /**
     * Returns whether a counter is counted and reported: a complete counter that is not an ignore counter.
     *
     * @param counter the counter, possibly {@code null}
     * @return {@code true} when it is reported
     */
    private static boolean isReported(final @Nullable AiFactCounter counter) {
        return counter != null && !counter.isIgnore() && counter.getLabel() != null && counter.getPattern() != null;
    }

    /**
     * Blanks the matches of every ignore counter, in counter order, keeping their line breaks.
     *
     * @param counters the counters
     * @param source   the source text
     * @return the text the reported counters count
     */
    private static String withoutIgnored(final List<AiFactCounter> counters, final String source) {
        String text = source;
        for (final AiFactCounter counter : counters) {
            if (counter == null || !counter.isIgnore() || counter.getPattern() == null) {
                continue;
            }
            final Matcher matcher = Pattern.compile(counter.getPattern()).matcher(text);
            final StringBuilder blanked = new StringBuilder(text.length());
            int last = 0;
            while (matcher.find()) {
                blanked.append(text, last, matcher.start()).append(lineBreaks(matcher.group()));
                last = matcher.end();
            }
            text = blanked.append(text, last, text.length()).toString();
        }
        return text;
    }

    /**
     * Returns the line breaks of a text, in order.
     *
     * @param text the text
     * @return its {@code \r} and {@code \n} characters
     */
    private static String lineBreaks(final String text) {
        final StringBuilder breaks = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == '\r' || c == '\n') {
                breaks.append(c);
            }
        }
        return breaks.toString();
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

    /**
     * Returns the CRC32 of fact counters (label, pattern and ignore of each, in order), empty without
     * counters: the facts hash of a summary's header and of the run record.
     *
     * @param facts the counters
     * @return the hash, or an empty string
     */
    public static String factsHash(final @Nullable List<AiFactCounter> facts) {
        if (facts == null || facts.isEmpty()) {
            return "";
        }
        final StringBuilder text = new StringBuilder();
        for (final AiFactCounter counter : facts) {
            text.append(counter.getLabel())
                    .append('\t')
                    .append(counter.getPattern())
                    .append('\t')
                    .append(counter.isIgnore())
                    .append('\n');
        }
        return new AiChecksumSupport().calculateCrc32Hex(text.toString());
    }
}
