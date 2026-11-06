// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MapReduceMathTest {

    /** A character outside the Basic Multilingual Plane: two {@code char}s, a surrogate pair. */
    private static final String EMOJI = new String(Character.toChars(0x1F600));

    private static String chars(final int count) {
        final StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append('x');
        }
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="safeWindow">
    @Test
    public void safeWindow_aWindow_isTheWindowLessTheHeadroom() {
        // act, assert
        assertThat(MapReduceMath.safeWindow(10_000), is(8_000));
        // integer arithmetic: 20 % of 1234 is 246 (rounded down), the window keeps the rest
        assertThat(MapReduceMath.safeWindow(1_234), is(988));
    }

    @Test
    public void safeWindow_ofAHugeWindow_doesNotOverflow() {
        // act, assert
        assertThat(MapReduceMath.safeWindow(200_000_000), is(160_000_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sourceBudget">
    @Test
    public void sourceBudget_isTheSafeWindowLessThePrompt_atLeastTheMinimum() {
        // act, assert
        assertThat(MapReduceMath.sourceBudget(8_000, 500), is(7_500));
        assertThat(MapReduceMath.sourceBudget(1_200, 500), is(MapReduceMath.MIN_CHUNK_CHARS));
        assertThat(MapReduceMath.sourceBudget(1_500, 500), is(1_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="overlap">
    @Test
    public void overlap_isATwentiethOfTheBudget_atMostTheMaximum() {
        // act, assert
        assertThat(MapReduceMath.overlap(10_000), is(500));
        assertThat(MapReduceMath.overlap(100_000), is(MapReduceMath.MAX_OVERLAP_CHARS));
        assertThat(MapReduceMath.overlap(40_000), is(2_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceBatches">
    @Test
    public void reduceBatches_partsThatFit_labelsEveryPartWithItsNumberInTheBatch() {
        // act, assert
        assertThat(
                MapReduceMath.reduceBatches(Arrays.asList("a", "b"), 1_000),
                is(equalTo(Collections.singletonList("Part 1:\na\n\nPart 2:\nb\n\n"))));
    }

    @Test
    public void reduceBatches_nothingToReduce_isNoBatch() {
        // act, assert
        assertThat(
                MapReduceMath.reduceBatches(Collections.<String>emptyList(), 1_000)
                        .isEmpty(),
                is(true));
    }

    @Test
    public void reduceBatches_aNextPartThatWouldNotFit_endsTheBatch() {
        // arrange
        // the first part takes 100 + 11 chars of label and separators; the second needs 100 + 16 more
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        // act
        final List<String> exactlyFits = MapReduceMath.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 16);
        final List<String> oneTooFew = MapReduceMath.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 15);

        // assert
        assertThat(exactlyFits.size(), is(1));
        assertThat(oneTooFew.size(), is(2));
        assertThat(oneTooFew.get(1), is(equalTo("Part 1:\n" + part + "\n\n")));
    }

    @Test
    public void reduceBatches_aPartLongerThanTheBudget_stillGetsABatch() {
        // act
        final List<String> batches = MapReduceMath.reduceBatches(Arrays.asList(chars(50), chars(50)), 10);

        // assert
        assertThat(batches.size(), is(2));
    }

    @Test
    public void reduceBatches_morePartsThanTheFanIn_atMostTheFanInPerBatch() {
        // arrange
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < MapReduceMath.MAX_REDUCE_FANIN + 1; i++) {
            partials.add("p");
        }

        // act
        final List<String> batches = MapReduceMath.reduceBatches(partials, 1_000_000);

        // assert
        assertThat(batches.size(), is(2));
        assertThat(batches.get(0).contains("Part " + MapReduceMath.MAX_REDUCE_FANIN + ":"), is(true));
        assertThat(batches.get(1), is(equalTo("Part 1:\np\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="combineAll">
    @Test
    public void combineAll_threeParts_labelsEveryPartInOrder() {
        // act, assert
        assertThat(
                MapReduceMath.combineAll(Arrays.asList("a", "b", "c")),
                is(equalTo("Part 1:\na\n\nPart 2:\nb\n\nPart 3:\nc\n\n")));
    }

    @Test
    public void combineAll_ofNothing_isEmpty() {
        // act, assert
        assertThat(MapReduceMath.combineAll(Collections.<String>emptyList()), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunkCoverage">
    @Test
    public void chunkCoverage_allOrSampled_namesTheChunksSummarized() {
        // act, assert
        assertThat(
                MapReduceMath.chunkCoverage(9, 9, 0, 0, 0), is(equalTo("**Coverage:** 9 of 9 chunks summarized\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 0, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled)\n\n")));
    }

    @Test
    public void chunkCoverage_callsCutToFit_namesTheCalls() {
        // act, assert
        assertThat(
                MapReduceMath.chunkCoverage(9, 9, 1, 0, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized (1 call cut to fit the window)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 2, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window)\n\n")));
    }

    @Test
    public void chunkCoverage_partSummariesCutAtTheOutputBudget_namesThePartSummaries() {
        // act, assert
        assertThat(
                MapReduceMath.chunkCoverage(11, 11, 0, 1, 0),
                is(equalTo("**Coverage:** 11 of 11 chunks summarized (1 part summary cut at the output budget)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 1, 5, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 1 call cut to fit the window;"
                        + " 5 part summaries cut at the output budget)\n\n")));
    }

    @Test
    public void chunkCoverage_partSummariesShortenedForTheReduce_namesThePartSummaries() {
        // act, assert
        assertThat(
                MapReduceMath.chunkCoverage(4, 4, 0, 0, 1),
                is(equalTo("**Coverage:** 4 of 4 chunks summarized (1 part summary shortened to fit the reduce)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(4, 4, 0, 0, 3),
                is(equalTo(
                        "**Coverage:** 4 of 4 chunks summarized (3 part summaries shortened to fit the reduce)\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="headCoverage">
    @Test
    public void headCoverage_aSampledHead_namesTheCharactersRead() {
        // act, assert
        assertThat(
                MapReduceMath.headCoverage(12_000, 90_000),
                is(equalTo("**Coverage:** the first 12000 of 90000 characters (sampled head)\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceCalls">
    @Test
    public void reduceCalls_oneRoundUpTo16_moreRoundsAbove() {
        // act, assert
        assertThat(MapReduceMath.reduceCalls(0), is(0L));
        assertThat(MapReduceMath.reduceCalls(1), is(0L));
        assertThat(MapReduceMath.reduceCalls(2), is(1L));
        assertThat(MapReduceMath.reduceCalls(16), is(1L));
        assertThat(MapReduceMath.reduceCalls(17), is(3L));
        assertThat(MapReduceMath.reduceCalls(256), is(17L));
        assertThat(MapReduceMath.reduceCalls(257), is(20L));
    }

    @Test
    public void reduceCalls_aSmallerFanInHeldToTwoAndSixteen_takesMoreRounds() {
        // act, assert
        assertThat(MapReduceMath.reduceCalls(8, 2), is(7L));
        assertThat(MapReduceMath.reduceCalls(9, 3), is(4L));
        assertThat(MapReduceMath.reduceCalls(8, 1), is(7L));
        assertThat(MapReduceMath.reduceCalls(17, 99), is(3L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="splittable">
    @Test
    public void splittable_fromTwiceTheMinimumChunk_halvesAreNoShorterThanIt() {
        // act, assert
        assertThat(MapReduceMath.splittable(2 * MapReduceMath.MIN_CHUNK_CHARS - 1), is(false));
        assertThat(MapReduceMath.splittable(2 * MapReduceMath.MIN_CHUNK_CHARS), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="expectedFanIn">
    @Test
    public void expectedFanIn_thePartSummariesThatFitTheBudget_twoToSixteen() {
        // act, assert
        // a part of 950 tokens at 4 characters is 3 800 characters and a 64-character label: 3 864
        assertThat(MapReduceMath.expectedFanIn(3_864L * 5, 4.0d, 950), is(5));
        assertThat(MapReduceMath.expectedFanIn(3_864L * 5 - 1, 4.0d, 950), is(4));
        assertThat(MapReduceMath.expectedFanIn(100L, 4.0d, 950), is(2));
        assertThat(MapReduceMath.expectedFanIn(10_000_000L, 4.0d, 950), is(16));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunkCount">
    @Test
    public void chunkCount_sourceLengths_advancesTheBudgetLessTheOverlap() {
        // act, assert
        assertThat(MapReduceMath.chunkCount(0, 8_000, 400), is(1L));
        assertThat(MapReduceMath.chunkCount(8_000, 8_000, 400), is(1L));
        assertThat(MapReduceMath.chunkCount(8_001, 8_000, 400), is(2L));
        assertThat(MapReduceMath.chunkCount(15_600, 8_000, 400), is(2L));
        assertThat(MapReduceMath.chunkCount(15_601, 8_000, 400), is(3L));
        // an overlap as large as the budget still advances one character
        assertThat(MapReduceMath.chunkCount(10, 5, 5), is(6L));
    }

    @Test
    public void chunkCount_textWithoutLineBreaks_matchesTheChunker() {
        // arrange
        final String source = chars(30_000);
        final int budget = 8_000;
        final int overlap = MapReduceMath.overlap(budget);

        // act, assert
        assertThat(MapReduceMath.chunkCount(source.length(), budget, overlap), is((long)
                SourceChunker.chunk(source, budget, overlap, 0).size()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunkLabel">
    @Test
    public void chunkLabel_everyChunkRead_namesTheChunkAndTheChunks() {
        // act, assert
        assertThat(MapReduceMath.chunkLabel(3, 44, 44), is(equalTo("Chunk 3 of 44:\n")));
    }

    @Test
    public void chunkLabel_ofASample_namesTheChunksItWasSampledFrom() {
        // act, assert
        assertThat(MapReduceMath.chunkLabel(3, 6, 44), is(equalTo("Chunk 3 of 6, sampled from 44 chunks:\n")));
    }

    @Test
    public void chunkLabel_ofTheLongestNumbers_fitsItsReserve() {
        // act, assert
        assertThat(
                MapReduceMath.chunkLabel(99_999, 99_999, 100_000).length() <= MapReduceMath.CHUNK_LABEL_RESERVE,
                is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceGroups">
    @Test
    public void reduceGroups_partsThatFit_areTheBatchesWithoutLabels() {
        // arrange
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        // act
        final List<List<String>> groups =
                MapReduceMath.reduceGroups(Arrays.asList(part, part, "c"), firstBatch + 100 + 16);

        // assert
        assertThat(groups, is(equalTo(Arrays.asList(Arrays.asList(part, part), Collections.singletonList("c")))));
    }

    @Test
    public void reduceGroups_twoDigitParts_countTheLabels() {
        // arrange
        // twelve parts of one char: the label of part 10 and above is one char longer
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            partials.add("p");
        }
        final String nine = MapReduceMath.combineAll(partials.subList(0, 11));

        // act
        final List<List<String>> fits = MapReduceMath.reduceGroups(partials, nine.length() + 1 + 16);
        final List<List<String>> short1 = MapReduceMath.reduceGroups(partials, nine.length() + 1 + 15);

        // assert
        assertThat(fits.get(0).size(), is(12));
        assertThat(short1.get(0).size(), is(11));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="pairablePartChars">
    @Test
    public void pairablePartChars_aBudget_letTwoPartsFitOneBatch() {
        // arrange
        final int budget = 1_000;
        final String part = chars(MapReduceMath.pairablePartChars(budget));

        // act, assert
        assertThat(MapReduceMath.pairablePartChars(budget), is(484));
        assertThat(MapReduceMath.reduceGroups(Arrays.asList(part, part), budget).size(), is(1));
        assertThat(MapReduceMath.pairablePartChars(10), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="shortened">
    @Test
    public void shortened_aPartAboveItsRoom_cutsAtTheLastLineEndAndMarksIt() {
        // act, assert
        assertThat(MapReduceMath.shortened("abc", 3), is(equalTo("abc")));
        assertThat(
                MapReduceMath.shortened("line one\nline two\nline three", 20),
                is(equalTo("line one" + MapReduceMath.SHORTENED_MARKER)));
        // a line end only at the start is no place to cut: the room ends it
        assertThat(
                MapReduceMath.shortened("\n" + chars(30), 10),
                is(equalTo("\n" + chars(5) + MapReduceMath.SHORTENED_MARKER)));
        // one long line is cut where the room ends
        assertThat(
                MapReduceMath.shortened(chars(30), 10),
                is(equalTo(chars(10 - MapReduceMath.SHORTENED_MARKER.length()) + MapReduceMath.SHORTENED_MARKER)));
    }

    @Test
    public void shortened_everyRoom_neverExceedsItsRoom() {
        // arrange
        final String partial = "a\nbb\nccc\ndddd\neeeee";

        // act, assert
        for (int max = MapReduceMath.SHORTENED_MARKER.length(); max < partial.length(); max++) {
            assertThat(MapReduceMath.shortened(partial, max).length() <= max, is(true));
        }
    }

    @Test
    public void shortened_aRoomEndingInsideASurrogatePair_keepsThePairWhole() {
        // arrange: the room of 10 less the marker ends between the two chars of a character outside the BMP
        final String partial = chars(5) + EMOJI + chars(20);

        // act, assert
        assertThat(MapReduceMath.shortened(partial, 10), is(equalTo(chars(5) + MapReduceMath.SHORTENED_MARKER)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceFacts">
    @Test
    public void reduceFacts_aLineThatFitsOrALongOne_isTheLineOrEndsAtACounter() {
        // arrange
        final String facts = "**Facts (exact, whole file):** a: 1; bb: 22; ccc: 333\n\n";

        // act, assert
        assertThat(MapReduceMath.reduceFacts(facts, facts.length()), is(equalTo(facts)));
        assertThat(
                MapReduceMath.reduceFacts(facts, 50),
                is(equalTo("**Facts (exact, whole file):** a: 1; bb: 22; ...\n\n")));
        assertThat(MapReduceMath.reduceFacts(facts, 50).length() <= 50, is(true));
        assertThat(MapReduceMath.reduceFacts("", 0), is(equalTo("")));
    }

    @Test
    public void reduceFacts_aLongLine_endsAtTheLastCounterThatLeavesRoomForTheEllipsis() {
        // arrange
        // the counter "c: 3" would fit without the ellipsis, not with it
        final String facts = "F: a: 1; b: 2; c: 3; d: 4\n\n";

        // act, assert
        assertThat(MapReduceMath.reduceFacts(facts, 20), is(equalTo("F: a: 1; b: 2; ...\n\n")));
        assertThat(MapReduceMath.reduceFacts(facts, 20).length(), is(20));
    }

    @Test
    public void reduceFacts_withoutACounterThatFits_isTheEllipsisAlone() {
        // act, assert
        assertThat(MapReduceMath.reduceFacts(chars(100), 20), is(equalTo("; ...\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="halfSplit">
    @Test
    public void halfSplit_lineEndsNearTheMiddle_isAfterALineEndNearTheMiddle() {
        // act, assert
        // the line end before the middle is nearer
        assertThat(MapReduceMath.halfSplit("aaaa\nbbbb\ncccc\n"), is(5));
        // the line end after the middle is nearer
        assertThat(MapReduceMath.halfSplit("a\nbcd\nefgh"), is(6));
        // as near on both sides: the one before
        assertThat(MapReduceMath.halfSplit("abc\nd\nfghi"), is(4));
        // no line end before the middle: the first one after it
        assertThat(MapReduceMath.halfSplit("aaaaaaa\nbb"), is(8));
        // a line end at the middle itself, none before it
        assertThat(MapReduceMath.halfSplit("abc\ndef"), is(4));
        // a single line: the middle
        assertThat(MapReduceMath.halfSplit("abcdef"), is(3));
        assertThat(MapReduceMath.halfSplit("ab"), is(1));
    }

    @Test
    public void halfSplit_aLineEndThatLeavesAHalfBelowAQuarter_isNotTaken() {
        // act, assert
        // a quarter of 8 is 2: a first half of 2 is taken, of 1 is not
        assertThat(MapReduceMath.halfSplit("a\nbbbbbb"), is(2));
        assertThat(MapReduceMath.halfSplit("\nbbbbbbb"), is(4));
        // a second half of 2 is taken, of 1 is not
        assertThat(MapReduceMath.halfSplit("bbbbb\nbb"), is(6));
        assertThat(MapReduceMath.halfSplit("bbbbbb\nb"), is(4));
        // a line end only as the last character
        assertThat(MapReduceMath.halfSplit("abcdef\n"), is(3));
        // blank lines in front of one long line: the middle, not an almost empty first half
        final String dense = "\n\n" + "x".repeat(3_000);
        assertThat(MapReduceMath.halfSplit(dense), is(dense.length() / 2));
    }

    @Test
    public void halfSplit_aMiddleInsideASurrogatePair_isBeforeThePair() {
        // act, assert: the middle of 6 falls between the two chars of the character outside the BMP
        assertThat(MapReduceMath.halfSplit("ab" + EMOJI + "cd"), is(2));
        // a chunk of the pair alone cannot be split whole: the middle, so both halves keep a char
        assertThat(MapReduceMath.halfSplit(EMOJI), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cutPart">
    @Test
    public void cutPart_aPartCutOff_dropsTheLineTheCutEndedInAndMarksIt() {
        // act, assert
        assertThat(
                MapReduceMath.cutPart("- a\n- b\n- c, cut in the mi"),
                is(equalTo("- a\n- b" + MapReduceMath.SHORTENED_MARKER)));
        // one line: kept whole, with the marker
        assertThat(MapReduceMath.cutPart("- a part"), is(equalTo("- a part" + MapReduceMath.SHORTENED_MARKER)));
        // a line end only at the start leaves the text whole
        assertThat(MapReduceMath.cutPart("\n- a part"), is(equalTo("\n- a part" + MapReduceMath.SHORTENED_MARKER)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="halfLabel">
    @Test
    public void halfLabel_aChunkLabel_namesTheHalf() {
        // arrange
        final String label = MapReduceMath.chunkLabel(3, 44, 44);

        // act, assert
        assertThat(MapReduceMath.halfLabel(label, 1), is(equalTo("Chunk 3 of 44, half 1:\n")));
        assertThat(
                MapReduceMath.halfLabel(MapReduceMath.halfLabel(label, 2), 1),
                is(equalTo("Chunk 3 of 44, half 2, half 1:\n")));
        assertThat(MapReduceMath.halfLabel("x", 2), is(equalTo("x, half 2:\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sampleNote">
    @Test
    public void sampleNote_namesTheChunksRead_orIsEmpty() {
        // act, assert
        assertThat(
                MapReduceMath.sampleNote(6, 44),
                is(equalTo("**Sample:** the parts summarize 6 of 44 chunks; the rest of the input was not read.\n\n")));
        assertThat(MapReduceMath.sampleNote(44, 44), is(equalTo("")));
        assertThat(MapReduceMath.sampleNote(45, 44), is(equalTo("")));
    }
    // </editor-fold>
}
