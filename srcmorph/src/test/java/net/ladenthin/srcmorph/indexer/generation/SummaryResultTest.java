// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SummaryResultTest {

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_aBody_carriesTheBodyAndNoReason() {
        // act
        final SummaryResult result = new SummaryResult("A summary.");

        // assert
        assertThat(result.body(), is("A summary."));
        assertThat(result.complete(), is(true));
        assertThat(result.incompleteReason(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incomplete">
    @Test
    public void incomplete_aReason_hasAnEmptyBodyAndSaysWhy() {
        // act
        final SummaryResult result = SummaryResult.incomplete("cut off");

        // assert
        assertThat(result.body(), is(""));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is("cut off"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="null arguments">
    @Test
    public void nullArguments_bodyOrReasonNull_throwsException() {
        // act, assert
        assertThrows(NullPointerException.class, () -> new SummaryResult(null));
        assertThrows(NullPointerException.class, () -> SummaryResult.incomplete(null));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="equals">
    @Test
    public void equals_sameOrOtherFields_coversBothFields() {
        // act, assert
        assertThat(new SummaryResult(""), is(new SummaryResult("")));
        assertThat(new SummaryResult(""), is(not(SummaryResult.incomplete("x"))));
        assertThat(SummaryResult.incomplete("x"), is(SummaryResult.incomplete("x")));
    }
    // </editor-fold>
}
