// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.equalTo;
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

    // <editor-fold defaultstate="collapsed" desc="chunk boundary conditions">

    /**
     * {@code maxChars = 1} is the smallest legal window and must be accepted.
     *
     * <p>Pins the lower bound of the argument check: a mutant relaxing {@code maxChars < 1} to
     * {@code <= 1} would reject it.
     */
    @Test
    public void maxCharsExactlyOne_isAccepted() {
        // act
        final List<String> chunks = AiSourceChunker.chunk("ab", 1, 0, 0);

        // assert
        assertThat(chunks, is(equalTo(Arrays.asList("a", "b"))));
    }

    /**
     * A source that fits entirely in one window must stay one chunk, even when it contains a newline
     * before its end.
     *
     * <p>This is the boundary that matters most here. The newline-trim block is guarded by
     * {@code end < length}, i.e. "only trim when this is NOT the last chunk". A mutant relaxing that
     * to {@code <=} trims the final chunk back to the last newline and then keeps looping, so the
     * tail is emitted as a second chunk — the source is silently re-split. The existing
     * fits-in-one-chunk test cannot see this because its fixture has no newline to trim to.
     */
    @Test
    public void sourceFitsInOneChunkButContainsANewline_isNotSplit() {
        // arrange -- a newline at index 4, content after it, whole thing inside the window
        final String source = "aaaa\nbb";

        // act
        final List<String> chunks = AiSourceChunker.chunk(source, 10, 0, 0);

        // assert
        assertThat(chunks, is(equalTo(Arrays.asList(source))));
    }

    /**
     * A chunk that starts exactly on a newline must not be cut down to that single character.
     *
     * <p>The trim only applies when the last newline lies strictly after the chunk start
     * ({@code lastNewline > pos}); relaxing that to {@code >=} makes a chunk beginning on a newline
     * collapse to just {@code "\n"}, and every following boundary shifts. The fixture forces exactly
     * that state: a hard cut at index 4 lands the next chunk start on the newline itself.
     */
    @Test
    public void chunkStartingOnANewline_isNotCutToASingleCharacter() {
        // arrange -- "abcd" hard-cuts at 4, and source[4] is the newline
        final String source = "abcd\nefghij";

        // act
        final List<String> chunks = AiSourceChunker.chunk(source, 4, 0, 0);

        // assert
        assertThat(chunks, is(equalTo(Arrays.asList("abcd", "\nefg", "hij"))));
    }

    @Test
    public void chunk_aLineLongerThanTheBudget_isCutHardInsteadOfRepeatingTheOverlap() {
        // arrange: a short header, then one line of 80 000 characters, as a dump with one INSERT per line
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("-- header line ").append(i).append('\n');
        }
        final String header = sb.toString();
        for (int i = 0; i < 80_000; i++) {
            sb.append('x');
        }
        sb.append('\n');

        // act
        final List<String> chunks = AiSourceChunker.chunk(sb.toString(), 20_000, 1_000, 0);

        // assert: the header, then the long line in hard cuts of 20 000 overlapping by 1 000
        assertThat(chunks.size(), is(6));
        assertThat(chunks.get(0), is(header));
        assertThat(chunks.get(1).length(), is(20_000));
        assertThat(chunks.get(5).endsWith("x\n"), is(true));
    }

    @Test
    public void chunk_shortLines_stillCutAtTheLastLineEnd() {
        // arrange: lines of 9 characters; a budget of 20 holds two of them
        final String source = "aaaaaaaa\nbbbbbbbb\ncccccccc\ndddddddd\n";

        // act
        final List<String> chunks = AiSourceChunker.chunk(source, 20, 0, 0);

        // assert
        assertThat(chunks, is(equalTo(Arrays.asList("aaaaaaaa\nbbbbbbbb\n", "cccccccc\ndddddddd\n"))));
    }

    // </editor-fold>
}
