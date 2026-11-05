// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class IndexTallyTest {

    @Test
    public void newTally_isEmpty() {
        final IndexTally tally = new IndexTally();

        assertThat(tally.written(), is(0));
        assertThat(tally.unchanged(), is(0));
        assertThat(tally.failed(), is(0));
    }

    @Test
    public void add_countsEachOutcomeOnItsOwn() {
        final IndexTally tally = new IndexTally();

        tally.add(IndexOutcome.WRITTEN);
        tally.add(IndexOutcome.UNCHANGED);
        tally.add(IndexOutcome.UNCHANGED);
        tally.add(IndexOutcome.FAILED);
        tally.add(IndexOutcome.FAILED);
        tally.add(IndexOutcome.FAILED);

        assertThat(tally.written(), is(1));
        assertThat(tally.unchanged(), is(2));
        assertThat(tally.failed(), is(3));
    }
}
