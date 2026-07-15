// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.support.ConvertToRecord;

/**
 * The outcome of one {@link GenerateEngine#execute()} run.
 *
 * <p>Record-shaped value type marked {@link ConvertToRecord} for the future Java&nbsp;17+ migration.</p>
 */
@ConvertToRecord
@ToString
@EqualsAndHashCode
public final class GenerateResult {

    private final boolean planOnly;
    private final int written;
    private final int unchanged;
    private final int skipped;

    /**
     * Creates a new {@link GenerateResult}.
     *
     * @param planOnly  {@code true} when the run stopped after printing the routing plan (no model was
     *                  loaded and nothing was generated)
     * @param written   number of {@code .ai.md} files written or refreshed
     * @param unchanged number of routed files left untouched (already up to date)
     * @param skipped   number of files matched by a {@code <skip>} rule
     */
    public GenerateResult(final boolean planOnly, final int written, final int unchanged, final int skipped) {
        this.planOnly = planOnly;
        this.written = written;
        this.unchanged = unchanged;
        this.skipped = skipped;
    }

    /**
     * Creates a {@link GenerateResult} for a {@code planOnly} run: the plan was printed and validated, but
     * no model was loaded and nothing was generated.
     *
     * @return a plan-only result with every count at zero
     */
    public static GenerateResult planned() {
        return new GenerateResult(true, 0, 0, 0);
    }

    /**
     * Returns whether the run stopped after printing the routing plan.
     *
     * @return {@code true} when the run was {@code planOnly}
     */
    public boolean planOnly() {
        return planOnly;
    }

    /**
     * Returns the number of {@code .ai.md} files written or refreshed.
     *
     * @return the written count
     */
    public int written() {
        return written;
    }

    /**
     * Returns the number of routed files left untouched (already up to date).
     *
     * @return the unchanged count
     */
    public int unchanged() {
        return unchanged;
    }

    /**
     * Returns the number of files matched by a {@code <skip>} rule.
     *
     * @return the skipped count
     */
    public int skipped() {
        return skipped;
    }
}
