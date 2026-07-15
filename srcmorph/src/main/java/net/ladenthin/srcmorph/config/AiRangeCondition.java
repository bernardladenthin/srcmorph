// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import lombok.ToString;

/**
 * A numeric range leaf used by {@link AiCondition} for the {@code size} (bytes) and {@code lines}
 * conditions. The lower bound {@link #min} is <em>exclusive</em> and the upper bound {@link #max} is
 * <em>inclusive</em>; either is disabled when {@code <= 0}. Adjacent, non-overlapping ranges are
 * therefore written as {@code range2.min == range1.max} (a value exactly on the boundary belongs to the
 * lower range).
 *
 * <p>Mutable JavaBean so the Maven plugin framework can populate {@code <min>}/{@code <max>}.</p>
 */
@ToString
public class AiRangeCondition {

    private long min;
    private long max;

    /** Creates a new {@link AiRangeCondition}. */
    public AiRangeCondition() {
        // no-op
    }

    /**
     * Returns the exclusive lower bound ({@code <= 0} disables it).
     *
     * @return the exclusive lower bound
     */
    public long getMin() {
        return min;
    }

    /**
     * Sets the exclusive lower bound.
     *
     * @param min the exclusive lower bound ({@code <= 0} disables it)
     */
    public void setMin(final long min) {
        this.min = min;
    }

    /**
     * Returns the inclusive upper bound ({@code <= 0} disables it).
     *
     * @return the inclusive upper bound
     */
    public long getMax() {
        return max;
    }

    /**
     * Sets the inclusive upper bound.
     *
     * @param max the inclusive upper bound ({@code <= 0} disables it)
     */
    public void setMax(final long max) {
        this.max = max;
    }

    /**
     * Returns {@code true} when {@code value} is within this range (exclusive lower, inclusive upper).
     *
     * @param value the value to test
     * @return {@code true} if the value is in range
     */
    public boolean contains(final long value) {
        if (min > 0 && value <= min) {
            return false;
        }
        if (max > 0 && value > max) {
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} when at least one bound is set (a usable range).
     *
     * @return {@code true} if {@code min} or {@code max} is positive
     */
    public boolean hasBound() {
        return min > 0 || max > 0;
    }
}
