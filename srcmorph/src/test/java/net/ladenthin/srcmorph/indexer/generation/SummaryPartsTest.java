// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class SummaryPartsTest {

    @Test
    public void text_noAnswer_isEmpty() {
        // act + assert
        assertThat(new SummaryParts().text(), is(""));
    }

    @Test
    public void text_mapAndReduceAnswers_inTheOrderTheyCame() {
        // arrange
        final SummaryParts parts = new SummaryParts();

        // act
        parts.map(MapReduceMath.chunkLabel(1, 2, 2), "first");
        parts.map(MapReduceMath.halfLabel(MapReduceMath.chunkLabel(2, 2, 2), 1), "second");
        parts.reduce(1, 1, 1, 2, "merged");

        // assert
        assertThat(
                parts.text(),
                is("## Chunk 1 of 2\n\nfirst\n\n"
                        + "## Chunk 2 of 2, half 1\n\nsecond\n\n"
                        + "## Reduce round 1, batch 1 of 1 (2 parts)\n\nmerged\n\n"));
    }

    @Test
    public void map_aLabelWithoutItsEnd_isTheHeadingAsItIs() {
        // arrange
        final SummaryParts parts = new SummaryParts();

        // act
        parts.map("Chunk 1 of 1", "only");

        // assert
        assertThat(parts.text(), is("## Chunk 1 of 1\n\nonly\n\n"));
    }
}
