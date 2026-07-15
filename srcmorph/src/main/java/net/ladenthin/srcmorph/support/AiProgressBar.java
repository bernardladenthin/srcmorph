// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.support;

/**
 * Renders a simple ASCII progress bar from a completed/total ratio, e.g. {@code [#####     ] 42%}.
 *
 * <p>Pure string formatting with no time model: the caller decides what {@code completed}/{@code total}
 * mean (here, summed per-file <em>estimated</em> seconds, so progress advances by each file's estimate as
 * it finishes — no re-estimation). ASCII-only for CI logs.</p>
 */
public final class AiProgressBar {

    /** Default bar width in characters (the count of fill cells between the brackets). */
    public static final int DEFAULT_WIDTH = 20;

    /** Character for completed cells. */
    private static final char FILLED_CHAR = '#';

    /** Character for remaining cells. */
    private static final char EMPTY_CHAR = ' ';

    /** Percent value representing a full bar. */
    private static final int FULL_PERCENT = 100;

    /** Utility class; not instantiable. */
    private AiProgressBar() {
        // no-op
    }

    /**
     * Renders the bar at the {@link #DEFAULT_WIDTH default width}.
     *
     * @param completed the completed amount (e.g. summed estimated seconds of finished files)
     * @param total     the total amount (e.g. the grand-total estimated seconds)
     * @return the rendered bar, e.g. {@code [#####     ] 42%}
     */
    public static String render(final long completed, final long total) {
        return render(completed, total, DEFAULT_WIDTH);
    }

    /**
     * Renders the bar at the given width. A non-positive {@code total} renders a full bar ({@code 100%});
     * the ratio is clamped to {@code [0, 1]} so out-of-range inputs never overflow the bar.
     *
     * @param completed the completed amount
     * @param total     the total amount
     * @param width     the bar width in cells (values {@code < 0} are treated as {@code 0})
     * @return the rendered bar, e.g. {@code [#####     ] 42%}
     */
    public static String render(final long completed, final long total, final int width) {
        final int cells = Math.max(0, width);
        final double fraction = total <= 0L ? 1.0d : clampFraction((double) completed / (double) total);
        final int percent = (int) Math.round(fraction * FULL_PERCENT);
        final int filled = (int) Math.round(fraction * cells);
        final StringBuilder sb = new StringBuilder(cells + 8);
        sb.append('[');
        for (int i = 0; i < cells; i++) {
            sb.append(i < filled ? FILLED_CHAR : EMPTY_CHAR);
        }
        sb.append("] ").append(percent).append('%');
        return sb.toString();
    }

    /**
     * Clamps a fraction to the closed interval {@code [0, 1]}.
     *
     * @param fraction the raw fraction
     * @return the clamped fraction
     */
    private static double clampFraction(final double fraction) {
        return Math.max(0.0d, Math.min(1.0d, fraction));
    }
}
