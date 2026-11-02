// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiIndexTallyTest {

    @Test
    public void newTally_isEmpty() {
        final AiIndexTally tally = new AiIndexTally();

        assertThat(tally.written(), is(0));
        assertThat(tally.unchanged(), is(0));
        assertThat(tally.failed(), is(0));
    }

    @Test
    public void add_countsEachOutcomeOnItsOwn() {
        final AiIndexTally tally = new AiIndexTally();

        tally.add(AiIndexOutcome.WRITTEN);
        tally.add(AiIndexOutcome.UNCHANGED);
        tally.add(AiIndexOutcome.UNCHANGED);
        tally.add(AiIndexOutcome.FAILED);
        tally.add(AiIndexOutcome.FAILED);
        tally.add(AiIndexOutcome.FAILED);

        assertThat(tally.written(), is(1));
        assertThat(tally.unchanged(), is(2));
        assertThat(tally.failed(), is(3));
    }
}
