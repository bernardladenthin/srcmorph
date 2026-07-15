// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.support;

/**
 * Builds a model-free, deterministic Markdown body for files too large (or too uninteresting) to feed to
 * an AI model — e.g. huge repetitive data files. The body states size and line count and shows a head
 * (and, when the file is long enough, a tail) sample, so the index still carries a useful, instant,
 * zero-cost description. Pure; no I/O.
 */
public final class AiDeterministicSummary {

    /** Newline used to split and count lines. */
    private static final char NEWLINE = '\n';

    /** Utility class; not instantiable. */
    private AiDeterministicSummary() {
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
        final int lines = countLines(source);
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
        final String[] all = source.split("\n", -1);
        if (lines <= sampleLines * 2) {
            appendFenced(sb, "Content", all, 0, all.length);
        } else {
            appendFenced(sb, "First " + sampleLines + " lines", all, 0, sampleLines);
            appendFenced(sb, "Last " + sampleLines + " lines", all, all.length - sampleLines, all.length);
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
        sb.append('\n').append(label).append(":\n```\n");
        for (int i = from; i < toExclusive; i++) {
            sb.append(lines[i]).append('\n');
        }
        sb.append("```\n");
    }

    /**
     * Counts lines: zero for empty input, otherwise the number of newlines plus one when the text does not
     * end with a newline.
     *
     * @param source the text
     * @return the line count
     */
    private static int countLines(final String source) {
        if (source.isEmpty()) {
            return 0;
        }
        int newlines = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == NEWLINE) {
                newlines++;
            }
        }
        return source.charAt(source.length() - 1) == NEWLINE ? newlines : newlines + 1;
    }
}
