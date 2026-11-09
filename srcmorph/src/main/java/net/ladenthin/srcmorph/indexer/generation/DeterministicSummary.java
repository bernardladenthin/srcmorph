// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.indexer.generation;

import net.ladenthin.srcmorph.support.SourceLines;

/**
 * Builds a model-free, deterministic Markdown body for files too large (or too uninteresting) to feed to
 * an AI model — e.g. huge repetitive data files. The body states size and line count and shows a head
 * (and, when the file is long enough, a tail) sample, so the index still carries a useful, instant,
 * zero-cost description. Pure; no I/O.
 */
public final class DeterministicSummary {

    /** The character of a code fence. */
    private static final char FENCE_CHAR = '`';

    /** The shortest code fence CommonMark knows. */
    private static final int MIN_FENCE_LENGTH = 3;

    /** Utility class; not instantiable. */
    private DeterministicSummary() {
        // no-op
    }

    /**
     * Builds the deterministic body.
     *
     * @param source      the full source text
     * @param contextName a human-readable name for the source (e.g. the file name)
     * @param sampleLines number of leading (and, when the file is long, trailing) lines to show; values
     *                    {@code <= 0} show no sample, only the metadata line
     * @return the Markdown body
     */
    public static String body(final String source, final String contextName, final int sampleLines) {
        final int chars = source.length();
        final int lines = SourceLines.count(source);
        final StringBuilder sb = new StringBuilder(256);
        sb.append("> Large file summarized deterministically (no AI): `")
                .append(contextName)
                .append("` - ")
                .append(chars)
                .append(" chars, ")
                .append(lines)
                .append(" lines.\n");
        if (sampleLines <= 0 || lines == 0) {
            return sb.toString();
        }
        // a source that ends with a line break splits into one more, empty element: the lines are the first
        // `lines` elements
        final String[] all = SourceLines.split(source);
        if (lines <= sampleLines * 2) {
            appendFenced(sb, "Content", all, 0, lines);
        } else {
            appendFenced(sb, "First " + sampleLines + " lines", all, 0, sampleLines);
            appendFenced(sb, "Last " + sampleLines + " lines", all, lines - sampleLines, lines);
        }
        return sb.toString();
    }

    /**
     * Appends a labelled fenced block with {@code lines[from..toExclusive)}.
     *
     * @param sb          the buffer
     * @param label       the section label
     * @param lines       the split lines
     * @param from        start index (inclusive)
     * @param toExclusive end index (exclusive)
     */
    private static void appendFenced(
            final StringBuilder sb, final String label, final String[] lines, final int from, final int toExclusive) {
        final String fence = fence(lines, from, toExclusive);
        sb.append('\n').append(label).append(":\n").append(fence).append('\n');
        for (int i = from; i < toExclusive; i++) {
            sb.append(lines[i]).append('\n');
        }
        sb.append(fence).append('\n');
    }

    /**
     * Returns the fence of a code block of {@code lines[from..toExclusive)}: one {@link #FENCE_CHAR} more than
     * the longest run of them in the lines, at least {@link #MIN_FENCE_LENGTH}. A line of the content (the
     * fence of a Markdown source) can then never close the block (CommonMark: a closing fence is at least as
     * long as the opening one).
     *
     * @param lines       the split lines
     * @param from        start index (inclusive)
     * @param toExclusive end index (exclusive)
     * @return the fence
     */
    private static String fence(final String[] lines, final int from, final int toExclusive) {
        int longest = 0;
        for (int i = from; i < toExclusive; i++) {
            int run = 0;
            for (final char c : lines[i].toCharArray()) {
                run = c == FENCE_CHAR ? run + 1 : 0;
                longest = Math.max(longest, run);
            }
        }
        final int length = Math.max(MIN_FENCE_LENGTH, longest + 1);
        final StringBuilder fence = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            fence.append(FENCE_CHAR);
        }
        return fence.toString();
    }
}
