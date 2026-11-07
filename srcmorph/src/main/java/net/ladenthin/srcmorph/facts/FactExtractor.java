// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.support.ChecksumSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import net.ladenthin.srcmorph.support.ReadBudget;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes a deterministic, language-agnostic "facts" block from a list of {@link FactCounter}s.
 *
 * <p>Each counter contributes one {@code label: count} entry, where {@code count} is the number of
 * non-overlapping matches of the counter's regex across the <em>whole</em> source. This gives exact,
 * model-free counts (rows, tables, boolean fields, functions, …) that a chunked/sampled AI summary
 * cannot reliably produce, and is prepended to the AI body on the oversize path. Pure; no I/O.</p>
 *
 * <p>The matches of an {@link FactCounter#isIgnore() ignore} counter are blanked out first, keeping
 * their line breaks, and the reported counters count what is left; a {@link FactCounter#isRaw() raw}
 * counter counts the source as written, a {@link FactCounter#isDistinct() distinct} one the different
 * texts it matched.</p>
 *
 * <p>A pattern has a time limit, counted in characters rather than seconds so it is the same on every
 * machine: it may read {@value ReadBudget#READS_PER_CHAR} characters per character of the source, plus
 * {@value ReadBudget#READ_ALLOWANCE}. A pattern that backtracks past that (a nested quantifier over a long line,
 * a lazy scan for a closer that never comes) is stopped: a counter is left out of the facts, an ignore
 * counter blanks nothing, and a warning names it. The patterns of the shipped profile read at most
 * about 1,160 characters per character (perl-facts' ignore line over one long line of nothing but
 * {@code <}; {@code ProfileFactPatternsLinearityTest} asserts that none reaches the limit). A pattern that
 * overflows the stack (a repeated group with an alternation, such as {@code (?:a|b)*}, recurses once per
 * repetition in {@code java.util.regex}) is stopped the same way.</p>
 */
public final class FactExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactExtractor.class);

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

    /**
     * The characters Java's regular expressions read as line terminators without {@code UNIX_LINES}:
     * carriage return, line feed, next line, line separator and paragraph separator. A blanked region
     * keeps them, so {@code ^} and {@code $} see the same lines as before.
     */
    private static final String LINE_BREAKS = "\r\n\u0085\u2028\u2029";

    /** Utility class; not instantiable. */
    private FactExtractor() {
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
    public static String factsBlock(final @Nullable List<FactCounter> counters, final String source) {
        if (counters == null || counters.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(64);
        for (final Map.Entry<String, Integer> count : counts(counters, source).entrySet()) {
            if (sb.length() > 0) {
                sb.append(ENTRY_SEPARATOR);
            }
            sb.append(count.getKey()).append(LABEL_COUNT_SEPARATOR).append(count.getValue());
        }
        if (sb.length() == 0) {
            return "";
        }
        return FACTS_HEADER + sb + FACTS_SUFFIX;
    }

    /**
     * Counts every reported counter over {@code source}, after the ignore counters blanked their matches
     * (a raw counter over {@code source} itself). A label that occurs twice keeps the later count; a
     * counter whose pattern ran out of its time limit is left out.
     *
     * @param counters the configured fact counters; {@code null} or empty yields an empty map
     * @param source   the full source text
     * @return the count per label, in counter order
     */
    public static Map<String, Integer> counts(
            final @Nullable List<? extends @Nullable FactCounter> counters, final String source) {
        final Map<String, Integer> counts = new LinkedHashMap<>();
        if (counters != null) {
            final String counted = withoutIgnored(counters, source);
            for (final @Nullable FactCounter counter : counters) {
                if (counter == null || counter.isIgnore()) {
                    continue;
                }
                final String label = counter.getLabel();
                final String pattern = counter.getPattern();
                if (label != null && pattern != null) {
                    final Integer count = count(counter, label, pattern, counter.isRaw() ? source : counted);
                    if (count != null) {
                        counts.put(label, count);
                    }
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
     * {@code ignore}), no ignore counter is also raw or distinct (it counts nothing), and at least one
     * counter is reported.
     *
     * @param where    the group, for the messages
     * @param counters the fact counters; {@code null} or empty is valid (nothing to check)
     * @throws IllegalArgumentException naming the group and the counter if any of that does not hold
     */
    public static void validate(final String where, final @Nullable List<? extends @Nullable FactCounter> counters) {
        validate(where, counters, true);
    }

    /**
     * Validates one group of fact counters as {@link #validate(String, List)} does, the rule that at least
     * one counter is reported only when asked: a facts file another includes may hold regions to blank
     * only, the counters come from the file that includes it.
     *
     * @param where           the group, for the messages
     * @param counters        the fact counters; {@code null} or empty is valid (nothing to check)
     * @param counterRequired whether at least one counter must be reported
     * @throws IllegalArgumentException naming the group and the counter if any of that does not hold
     */
    static void validate(
            final String where,
            final @Nullable List<? extends @Nullable FactCounter> counters,
            final boolean counterRequired) {
        if (counters == null || counters.isEmpty()) {
            return;
        }
        final Set<String> labels = new HashSet<>(new Java8CompatibilityHelper().hashMapCapacityFor(counters.size()));
        boolean reported = false;
        for (int i = 0; i < counters.size(); i++) {
            final @Nullable FactCounter counter = counters.get(i);
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
            if (counter.isIgnore() && (counter.isRaw() || counter.isDistinct())) {
                throw new IllegalArgumentException(entry + " ('" + label
                        + "') is an ignore counter, which counts nothing; raw and distinct apply to counters");
            }
            reported |= !counter.isIgnore();
        }
        if (counterRequired && !reported) {
            throw new IllegalArgumentException(where + ": no counter, only ignore patterns");
        }
    }

    /**
     * Blanks the matches of every ignore counter, in counter order, keeping their line breaks.
     *
     * @param counters the counters
     * @param source   the source text
     * @return the text the reported counters count
     */
    private static String withoutIgnored(final List<? extends @Nullable FactCounter> counters, final String source) {
        String text = source;
        for (final @Nullable FactCounter counter : counters) {
            final String pattern = counter == null || !counter.isIgnore() ? null : counter.getPattern();
            if (counter == null || pattern == null) {
                continue;
            }
            final Matcher matcher = matcher(pattern, text);
            final StringBuilder blanked = new StringBuilder(text.length());
            int last = 0;
            try {
                while (matcher.find()) {
                    final String breaks = lineBreaks(matcher.group());
                    // a region within one line leaves a space, so FROM t/* x */JOIN u still reads as two words,
                    // but not at the start of a line, so /* x */class A still starts its line with class; the
                    // line start is read from the blanked text, where a region before this one may have left
                    // nothing (/* a *//* b */class A)
                    blanked.append(text, last, matcher.start());
                    final int length = blanked.length();
                    final boolean lineStart = length == 0 || isLineBreak(blanked.charAt(length - 1));
                    blanked.append(!lineStart && breaks.isEmpty() ? " " : breaks);
                    last = matcher.end();
                }
            } catch (final ReadBudget.Exhausted e) {
                stopped(String.valueOf(counter.getLabel()), true, text, false);
                continue;
            } catch (final StackOverflowError e) {
                stopped(String.valueOf(counter.getLabel()), true, text, true);
                continue;
            }
            text = blanked.append(text, last, text.length()).toString();
        }
        return text;
    }

    /**
     * Returns the line breaks of a text, in order.
     *
     * @param text the text
     * @return its line break characters ({@link #isLineBreak(char)})
     */
    private static String lineBreaks(final String text) {
        final StringBuilder breaks = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (isLineBreak(c)) {
                breaks.append(c);
            }
        }
        return breaks.toString();
    }

    /**
     * Returns whether a character ends a line, as Java's {@code ^} and {@code $} read it in multiline mode
     * without {@code UNIX_LINES}.
     *
     * @param c the character
     * @return {@code true} for {@link #LINE_BREAKS}
     */
    private static boolean isLineBreak(final char c) {
        return LINE_BREAKS.indexOf(c) >= 0;
    }

    /**
     * Counts one counter over a text: its non-overlapping matches, or for a distinct counter the
     * different texts they matched.
     *
     * @param counter the counter
     * @param label   its label
     * @param pattern its pattern
     * @param text    the text to search
     * @return the count (zero-width matches, e.g. {@code (?m)^}, count once per position), or {@code null}
     *     when the pattern ran out of its time limit
     */
    private static @Nullable Integer count(
            final FactCounter counter, final String label, final String pattern, final String text) {
        final Matcher matcher = matcher(pattern, text);
        try {
            if (!counter.isDistinct()) {
                int count = 0;
                while (matcher.find()) {
                    count++;
                }
                return count;
            }
            final Set<String> names = new HashSet<>();
            while (matcher.find()) {
                final String group = matcher.groupCount() > 0 ? matcher.group(1) : null;
                names.add(group != null ? group : matcher.group());
            }
            return names.size();
        } catch (final ReadBudget.Exhausted e) {
            stopped(label, false, text, false);
            return null;
        } catch (final StackOverflowError e) {
            stopped(label, false, text, true);
            return null;
        }
    }

    /**
     * Returns a matcher of a pattern over a text that stops once it read the text's budget.
     *
     * @param pattern the regular expression
     * @param text    the text
     * @return the matcher
     */
    private static Matcher matcher(final String pattern, final String text) {
        return Pattern.compile(pattern).matcher(ReadBudget.over(text));
    }

    /**
     * Logs that a counter's pattern was stopped: it ran out of its time limit or overflowed the stack.
     *
     * @param label      the counter's label
     * @param ignore     whether it is an ignore counter
     * @param text       the text it read
     * @param overflowed whether it overflowed the stack rather than ran out of its time limit
     */
    private static void stopped(final String label, final boolean ignore, final String text, final boolean overflowed) {
        final int length = text.length();
        final String outcome = ignore ? "not blanked" : "left out of the facts";
        if (overflowed) {
            LOGGER.warn(
                    "Fact counter '{}' overflowed the stack over a {}-character source: its pattern repeats a group"
                            + " that recurses once per repetition, so it is {}",
                    label,
                    length,
                    outcome);
            return;
        }
        LOGGER.warn(
                "Fact counter '{}' stopped after reading {} characters of a {}-character source: its pattern"
                        + " backtracks too far, so it is {}",
                label,
                ReadBudget.limit(length),
                length,
                outcome);
    }

    /**
     * Returns the CRC32 of fact counters (label, pattern and ignore of each, and raw and distinct where
     * set, in order), empty without counters: the facts hash of a summary's header and of the run record.
     * A counter without raw and distinct hashes as it did before they existed, so a summary written
     * then keeps its hash.
     *
     * @param facts the counters
     * @return the hash, or an empty string
     */
    public static String factsHash(final @Nullable List<FactCounter> facts) {
        if (facts == null || facts.isEmpty()) {
            return "";
        }
        final StringBuilder text = new StringBuilder();
        for (final FactCounter counter : facts) {
            text.append(counter.getLabel())
                    .append('\t')
                    .append(counter.getPattern())
                    .append('\t')
                    .append(counter.isIgnore());
            if (counter.isRaw()) {
                text.append("\traw");
            }
            if (counter.isDistinct()) {
                text.append("\tdistinct");
            }
            text.append('\n');
        }
        return new ChecksumSupport().calculateCrc32Hex(text.toString());
    }
}
