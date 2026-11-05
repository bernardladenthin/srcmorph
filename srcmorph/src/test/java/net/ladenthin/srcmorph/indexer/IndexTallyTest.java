// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class IndexTallyTest {

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_newTally_isEmpty() {
        // act
        final IndexTally tally = new IndexTally();

        // assert
        assertThat(tally.written(), is(0));
        assertThat(tally.unchanged(), is(0));
        assertThat(tally.failed(), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="add">
    @Test
    public void add_mixedOutcomes_countsEachOutcomeOnItsOwn() {
        // arrange
        final IndexTally tally = new IndexTally();

        // act
        tally.add(IndexOutcome.WRITTEN);
        tally.add(IndexOutcome.UNCHANGED);
        tally.add(IndexOutcome.UNCHANGED);
        tally.add(IndexOutcome.FAILED);
        tally.add(IndexOutcome.FAILED);
        tally.add(IndexOutcome.FAILED);

        // assert
        assertThat(tally.written(), is(1));
        assertThat(tally.unchanged(), is(2));
        assertThat(tally.failed(), is(3));
    }
    // </editor-fold>
}
