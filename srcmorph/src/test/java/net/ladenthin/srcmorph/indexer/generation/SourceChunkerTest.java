// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SourceChunkerTest {

    // <editor-fold defaultstate="collapsed" desc="chunk">
    @Test
    public void chunk_maxCharsBelowOne_throwsException() {
        // act, assert
        assertThrows(IllegalArgumentException.class, () -> SourceChunker.chunk("abc", 0, 0, 0));
    }

    @Test
    public void chunk_emptySource_returnsEmptyList() {
        // act, assert
        assertThat(SourceChunker.chunk("", 10, 0, 0), is(Arrays.asList()));
    }

    @Test
    public void chunk_sourceShorterThanMaxChars_returnsOneChunk() {
        // act, assert
        assertThat(SourceChunker.chunk("abc", 10, 0, 0), is(Arrays.asList("abc")));
    }

    @Test
    public void chunk_sourceWithLineEnds_splitsAtLineBoundaries() {
        // act
        // "L1\nL2\nL3\n" with maxChars 4 -> each chunk ends at a newline; rejoined == source.
        final List<String> chunks = SourceChunker.chunk("L1\nL2\nL3\n", 4, 0, 0);

        // assert
        assertThat(chunks, is(Arrays.asList("L1\n", "L2\n", "L3\n")));
        assertThat(String.join("", chunks), is("L1\nL2\nL3\n"));
    }

    @Test
    public void chunk_noNewlineInWindow_hardCuts() {
        // act, assert
        // No newline anywhere -> hard cut at maxChars.
        assertThat(SourceChunker.chunk("ABCDEFGH", 3, 0, 0), is(Arrays.asList("ABC", "DEF", "GH")));
    }

    @Test
    public void chunk_overlapAboveMaxChars_isClampedAndProducesOverlappingChunks() {
        // act
        // overlap 100 with maxChars 4 -> clamped to 3; consecutive chunks share 3 chars.
        final List<String> chunks = SourceChunker.chunk("ABCDEFGH", 4, 100, 0);

        // assert
        assertThat(chunks, is(Arrays.asList("ABCD", "BCDE", "CDEF", "DEFG", "EFGH")));
    }

    @Test
    public void chunk_maxChunksZero_returnsAll() {
        // act, assert
        assertThat(SourceChunker.chunk("ABCDEFGHIJ", 2, 0, 0), is(Arrays.asList("AB", "CD", "EF", "GH", "IJ")));
    }

    @Test
    public void chunk_maxChunksAtOrAboveCount_returnsAll() {
        // act, assert
        assertThat(SourceChunker.chunk("ABCDEFGHIJ", 2, 0, 10), is(Arrays.asList("AB", "CD", "EF", "GH", "IJ")));
    }

    @Test
    public void chunk_maxChunksOne_keepsTheFirst() {
        // act, assert
        assertThat(SourceChunker.chunk("ABCDEFGHIJ", 2, 0, 1), is(Arrays.asList("AB")));
    }

    @Test
    public void chunk_maxChunksBelowCount_samplesFirstSpreadAndLast() {
        // act, assert
        // 5 chunks, cap 3 -> indices round(i*4/2) = 0,2,4 -> head/middle/tail.
        assertThat(SourceChunker.chunk("ABCDEFGHIJ", 2, 0, 3), is(Arrays.asList("AB", "EF", "IJ")));
    }

    @Test
    public void chunk_maxChunksBelowCount_usesRoundingNotFloorForSpread() {
        // act, assert
        // 4 chunks, cap 3 -> indices round(i*3/2) = 0, round(1.5)=2, 3 -> AB, EF, GH (floor would pick CD).
        assertThat(SourceChunker.chunk("ABCDEFGH", 2, 0, 3), is(Arrays.asList("AB", "EF", "GH")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunk boundary conditions">

    /**
     * {@code maxChars = 1} is the smallest legal window and must be accepted.
     *
     * <p>Pins the lower bound of the argument check: a mutant relaxing {@code maxChars < 1} to
     * {@code <= 1} would reject it.
     */
    @Test
    public void chunk_maxCharsExactlyOne_isAccepted() {
        // act
        final List<String> chunks = SourceChunker.chunk("ab", 1, 0, 0);

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
    public void chunk_sourceFitsInOneChunkButContainsANewline_isNotSplit() {
        // arrange -- a newline at index 4, content after it, whole thing inside the window
        final String source = "aaaa\nbb";

        // act
        final List<String> chunks = SourceChunker.chunk(source, 10, 0, 0);

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
    public void chunk_aChunkStartingOnANewline_isNotCutToASingleCharacter() {
        // arrange -- "abcd" hard-cuts at 4, and source[4] is the newline
        final String source = "abcd\nefghij";

        // act
        final List<String> chunks = SourceChunker.chunk(source, 4, 0, 0);

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
        final List<String> chunks = SourceChunker.chunk(sb.toString(), 20_000, 1_000, 0);

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
        final List<String> chunks = SourceChunker.chunk(source, 20, 0, 0);

        // assert
        assertThat(chunks, is(equalTo(Arrays.asList("aaaaaaaa\nbbbbbbbb\n", "cccccccc\ndddddddd\n"))));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunk overlap">
    @Test
    public void chunk_overlapOverShortLines_startsAtALineStart() {
        // arrange
        final StringBuilder source = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            source.append("line number ").append(i).append('\n');
        }

        // act
        final List<String> chunks = SourceChunker.chunk(source.toString(), 300, 40, 0);

        // assert
        for (int i = 1; i < chunks.size(); i++) {
            assertThat(chunks.get(i).startsWith("line number "), is(true));
        }
    }

    @Test
    public void chunk_anOverlapWithinOneLine_startsWhereTheCharactersSay() {
        // act
        // no line end inside the overlap: it starts mid-line rather than not at all
        final List<String> chunks = SourceChunker.chunk("abcdefghij\nklmnopqrst", 10, 3, 0);

        // assert
        assertThat(chunks.get(1), is(equalTo("hij\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunk surrogate pairs">
    @Test
    public void chunk_hardCutsAndOverlapsInALineOfPairs_neverSplitAPair() {
        // arrange: one line of characters outside the BMP between ASCII ones, so a cut lands inside a pair
        final String emoji = new String(Character.toChars(0x1F600));
        final StringBuilder source = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            source.append('a').append(emoji);
        }

        // act, assert
        for (int maxChars = 2; maxChars <= 9; maxChars++) {
            for (int overlap = 0; overlap < maxChars; overlap++) {
                for (final String chunk : SourceChunker.chunk(source.toString(), maxChars, overlap, 0)) {
                    assertThat(Character.isHighSurrogate(chunk.charAt(chunk.length() - 1)), is(false));
                    assertThat(Character.isLowSurrogate(chunk.charAt(0)), is(false));
                }
            }
        }
    }
    // </editor-fold>
}
