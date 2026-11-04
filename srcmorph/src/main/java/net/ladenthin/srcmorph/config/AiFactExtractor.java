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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes a deterministic, language-agnostic "facts" block from a list of {@link AiFactCounter}s.
 *
 * <p>Each counter contributes one {@code label: count} entry, where {@code count} is the number of
 * non-overlapping matches of the counter's regex across the <em>whole</em> source. This gives exact,
 * model-free counts (rows, tables, boolean fields, functions, …) that a chunked/sampled AI summary
 * cannot reliably produce, and is prepended to the AI body on the oversize path. Pure; no I/O.</p>
 *
 * <p>The matches of an {@link AiFactCounter#isIgnore() ignore} counter are blanked out first, keeping
 * their line breaks, and the reported counters count what is left; a {@link AiFactCounter#isRaw() raw}
 * counter counts the source as written, a {@link AiFactCounter#isDistinct() distinct} one the different
 * texts it matched.</p>
 *
 * <p>A pattern has a time limit, counted in characters rather than seconds so it is the same on every
 * machine: it may read {@value #READS_PER_CHAR} characters per character of the source, plus
 * {@value #READ_ALLOWANCE}. A pattern that backtracks past that (a nested quantifier over a long line,
 * a lazy scan for a closer that never comes) is stopped: a counter is left out of the facts, an ignore
 * counter blanks nothing, and a warning names it. The patterns of the shipped profile read at most
 * about 830 characters per character (java-facts' field initializer over a file of nothing but
 * {@code public a b=} lines; {@code ProfileFactPatternsLinearityTest} asserts that none reaches the
 * limit).</p>
 */
public final class AiFactExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiFactExtractor.class);

    /** Characters a pattern may read per character of the source before it is stopped. */
    public static final long READS_PER_CHAR = 2_000L;

    /** Characters a pattern may read on top of {@value #READS_PER_CHAR} per character of the source. */
    public static final long READ_ALLOWANCE = 1_000_000L;

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
    public static Map<String, Integer> counts(final @Nullable List<AiFactCounter> counters, final String source) {
        final Map<String, Integer> counts = new LinkedHashMap<>();
        if (counters != null) {
            final String counted = withoutIgnored(counters, source);
            for (final AiFactCounter counter : counters) {
                if (isReported(counter)) {
                    final Integer count = count(counter, counter.isRaw() ? source : counted);
                    if (count != null) {
                        counts.put(counter.getLabel(), count);
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
            if (counter.isIgnore() && (counter.isRaw() || counter.isDistinct())) {
                throw new IllegalArgumentException(entry + " ('" + label
                        + "') is an ignore counter, which counts nothing; raw and distinct apply to counters");
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
            final Matcher matcher = matcher(counter.getPattern(), text);
            final StringBuilder blanked = new StringBuilder(text.length());
            int last = 0;
            try {
                while (matcher.find()) {
                    final String breaks = lineBreaks(matcher.group());
                    // a region within one line leaves a space, so FROM t/* x */JOIN u still reads as two words
                    blanked.append(text, last, matcher.start()).append(breaks.isEmpty() ? " " : breaks);
                    last = matcher.end();
                }
            } catch (final ReadBudget.Exhausted e) {
                stopped(counter, text);
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
     * Counts one counter over a text: its non-overlapping matches, or for a distinct counter the
     * different texts they matched.
     *
     * @param counter the counter
     * @param text    the text to search
     * @return the count (zero-width matches, e.g. {@code (?m)^}, count once per position), or {@code null}
     *     when the pattern ran out of its time limit
     */
    private static @Nullable Integer count(final AiFactCounter counter, final String text) {
        final Matcher matcher = matcher(counter.getPattern(), text);
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
            stopped(counter, text);
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
        return Pattern.compile(pattern).matcher(new ReadBudget(text, readBudget(text.length())));
    }

    /**
     * Returns how many characters a pattern may read of a text of the given length.
     *
     * @param length the text's length
     * @return {@value #READS_PER_CHAR} per character plus {@value #READ_ALLOWANCE}
     */
    static long readBudget(final int length) {
        return READS_PER_CHAR * length + READ_ALLOWANCE;
    }

    /**
     * Logs that a counter's pattern ran out of its time limit.
     *
     * @param counter the counter
     * @param text    the text it read
     */
    private static void stopped(final AiFactCounter counter, final String text) {
        final int length = text.length();
        LOGGER.warn(
                "Fact counter '{}' stopped after reading {} characters of a {}-character source: its pattern"
                        + " backtracks too far, so it is {}",
                counter.getLabel(),
                readBudget(length),
                length,
                counter.isIgnore() ? "not blanked" : "left out of the facts");
    }

    /** A text that lets a matcher read a fixed number of characters and then stops it. */
    static final class ReadBudget implements CharSequence {

        private final CharSequence text;

        private final long budget;

        private long reads;

        /**
         * Creates a budget over a text.
         *
         * @param text   the text
         * @param budget the characters the matcher may read
         */
        ReadBudget(final CharSequence text, final long budget) {
            this.text = text;
            this.budget = budget;
        }

        @Override
        public int length() {
            return text.length();
        }

        @Override
        public char charAt(final int index) {
            reads++;
            if (reads > budget) {
                throw new Exhausted();
            }
            return text.charAt(index);
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return text.subSequence(start, end);
        }

        @Override
        public String toString() {
            return text.toString();
        }

        /** Thrown by {@link #charAt(int)} once the budget is read. */
        static final class Exhausted extends RuntimeException {

            private static final long serialVersionUID = 1L;

            /** Creates the exception without a message, a cause or a stack trace. */
            Exhausted() {
                super(null, null, false, false);
            }
        }
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
                    .append(counter.isIgnore());
            if (counter.isRaw()) {
                text.append("\traw");
            }
            if (counter.isDistinct()) {
                text.append("\tdistinct");
            }
            text.append('\n');
        }
        return new AiChecksumSupport().calculateCrc32Hex(text.toString());
    }
}
