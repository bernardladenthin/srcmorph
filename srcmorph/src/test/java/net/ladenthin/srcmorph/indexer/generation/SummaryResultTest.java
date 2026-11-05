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

    @Test
    public void complete_carriesTheBodyAndNoReason() {
        final SummaryResult result = new SummaryResult("A summary.");

        assertThat(result.body(), is("A summary."));
        assertThat(result.complete(), is(true));
        assertThat(result.incompleteReason(), is(nullValue()));
    }

    @Test
    public void incomplete_hasAnEmptyBodyAndSaysWhy() {
        final SummaryResult result = SummaryResult.incomplete("cut off");

        assertThat(result.body(), is(""));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is("cut off"));
    }

    @Test
    public void nullBodyOrReason_isRefused() {
        assertThrows(NullPointerException.class, () -> new SummaryResult(null));
        assertThrows(NullPointerException.class, () -> SummaryResult.incomplete(null));
    }

    @Test
    public void equality_coversBothFields() {
        assertThat(new SummaryResult(""), is(new SummaryResult("")));
        assertThat(new SummaryResult(""), is(not(SummaryResult.incomplete("x"))));
        assertThat(SummaryResult.incomplete("x"), is(SummaryResult.incomplete("x")));
    }
}
