// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiSourceChunkerTest {

    @Test
    public void maxCharsBelowOne_throws() {
        assertThrows(IllegalArgumentException.class, () -> AiSourceChunker.chunk("abc", 0, 0, 0));
    }

    @Test
    public void emptySource_returnsEmptyList() {
        assertThat(AiSourceChunker.chunk("", 10, 0, 0), is(Arrays.asList()));
    }

    @Test
    public void sourceFitsInOneChunk() {
        assertThat(AiSourceChunker.chunk("abc", 10, 0, 0), is(Arrays.asList("abc")));
    }

    @Test
    public void splitsAtLineBoundaries() {
        // "L1\nL2\nL3\n" with maxChars 4 -> each chunk ends at a newline; rejoined == source.
        final List<String> chunks = AiSourceChunker.chunk("L1\nL2\nL3\n", 4, 0, 0);
        assertThat(chunks, is(Arrays.asList("L1\n", "L2\n", "L3\n")));
        assertThat(String.join("", chunks), is("L1\nL2\nL3\n"));
    }

    @Test
    public void hardCutsWhenNoNewlineInWindow() {
        // No newline anywhere -> hard cut at maxChars.
        assertThat(AiSourceChunker.chunk("ABCDEFGH", 3, 0, 0), is(Arrays.asList("ABC", "DEF", "GH")));
    }

    @Test
    public void overlapIsClampedAndProducesOverlappingChunks() {
        // overlap 100 with maxChars 4 -> clamped to 3; consecutive chunks share 3 chars.
        final List<String> chunks = AiSourceChunker.chunk("ABCDEFGH", 4, 100, 0);
        assertThat(chunks, is(Arrays.asList("ABCD", "BCDE", "CDEF", "DEFG", "EFGH")));
    }

    @Test
    public void maxChunksZero_returnsAll() {
        assertThat(AiSourceChunker.chunk("ABCDEFGHIJ", 2, 0, 0), is(Arrays.asList("AB", "CD", "EF", "GH", "IJ")));
    }

    @Test
    public void maxChunksAtOrAboveCount_returnsAll() {
        assertThat(AiSourceChunker.chunk("ABCDEFGHIJ", 2, 0, 10), is(Arrays.asList("AB", "CD", "EF", "GH", "IJ")));
    }

    @Test
    public void maxChunksOne_keepsTheFirst() {
        assertThat(AiSourceChunker.chunk("ABCDEFGHIJ", 2, 0, 1), is(Arrays.asList("AB")));
    }

    @Test
    public void maxChunksSamplesFirstSpreadAndLast() {
        // 5 chunks, cap 3 -> indices round(i*4/2) = 0,2,4 -> head/middle/tail.
        assertThat(AiSourceChunker.chunk("ABCDEFGHIJ", 2, 0, 3), is(Arrays.asList("AB", "EF", "IJ")));
    }

    @Test
    public void maxChunksUsesRoundingNotFloorForSpread() {
        // 4 chunks, cap 3 -> indices round(i*3/2) = 0, round(1.5)=2, 3 -> AB, EF, GH (floor would pick CD).
        assertThat(AiSourceChunker.chunk("ABCDEFGH", 2, 0, 3), is(Arrays.asList("AB", "EF", "GH")));
    }
}
