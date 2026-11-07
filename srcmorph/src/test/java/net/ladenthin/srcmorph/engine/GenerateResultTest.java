// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class GenerateResultTest {

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_fourValues_threadsEveryFieldThrough() {
        // act
        final GenerateResult result = new GenerateResult(false, 3, 5, 7);

        // assert
        assertThat(result.planOnly(), is(false));
        assertThat(result.written(), is(3));
        assertThat(result.unchanged(), is(5));
        assertThat(result.skipped(), is(7));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="planned">
    @Test
    public void planned_noArguments_isPlanOnlyWithEveryCountAtZero() {
        // act
        final GenerateResult result = GenerateResult.planned();

        // assert
        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(result.unchanged(), is(0));
        assertThat(result.skipped(), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="equals / hashCode">
    @Test
    public void equalsAndHashCode_sameOrDifferentFields_reflectAllFields() {
        // arrange
        final GenerateResult a = new GenerateResult(false, 1, 2, 3);
        final GenerateResult b = new GenerateResult(false, 1, 2, 3);
        final GenerateResult differentWritten = new GenerateResult(false, 9, 2, 3);
        final GenerateResult differentPlanOnly = new GenerateResult(true, 1, 2, 3);

        // act, assert
        assertThat(a, is(equalTo(b)));
        assertThat(a.hashCode(), is(equalTo(b.hashCode())));
        assertThat(a, not(equalTo(differentWritten)));
        assertThat(a, not(equalTo(differentPlanOnly)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_threeCounts_containsDistinguishingCounts() {
        // arrange
        final GenerateResult result = new GenerateResult(false, 11, 22, 33);

        // act, assert
        assertThat(result.toString(), containsString("11"));
        assertThat(result.toString(), containsString("22"));
        assertThat(result.toString(), containsString("33"));
    }
    // </editor-fold>
}
