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

    private static String chars(final int count) {
        final StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append('x');
        }
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="sizing">
    @Test
    public void safeWindow_isTheWindowLessTheHeadroom() {
        assertThat(MapReduceMath.safeWindow(10_000), is(8_000));
        // integer arithmetic: 20 % of 1234 is 246 (rounded down), the window keeps the rest
        assertThat(MapReduceMath.safeWindow(1_234), is(988));
    }

    @Test
    public void sourceBudget_isTheSafeWindowLessThePrompt_atLeastTheMinimum() {
        assertThat(MapReduceMath.sourceBudget(8_000, 500), is(7_500));
        assertThat(MapReduceMath.sourceBudget(1_200, 500), is(MapReduceMath.MIN_CHUNK_CHARS));
        assertThat(MapReduceMath.sourceBudget(1_500, 500), is(1_000));
    }

    @Test
    public void overlap_isATwentiethOfTheBudget_atMostTheMaximum() {
        assertThat(MapReduceMath.overlap(10_000), is(500));
        assertThat(MapReduceMath.overlap(100_000), is(MapReduceMath.MAX_OVERLAP_CHARS));
        assertThat(MapReduceMath.overlap(40_000), is(2_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceBatches">
    @Test
    public void reduceBatches_labelsEveryPartWithItsNumberInTheBatch() {
        assertThat(
                MapReduceMath.reduceBatches(Arrays.asList("a", "b"), 1_000),
                is(equalTo(Collections.singletonList("Part 1:\na\n\nPart 2:\nb\n\n"))));
    }

    @Test
    public void reduceBatches_nothingToReduce_isNoBatch() {
        assertThat(
                MapReduceMath.reduceBatches(Collections.<String>emptyList(), 1_000)
                        .isEmpty(),
                is(true));
    }

    @Test
    public void reduceBatches_aBatchEndsWhereTheNextPartWouldNotFit() {
        // the first part takes 100 + 11 chars of label and separators; the second needs 100 + 16 more
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        final List<String> exactlyFits = MapReduceMath.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 16);
        final List<String> oneTooFew = MapReduceMath.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 15);

        assertThat(exactlyFits.size(), is(1));
        assertThat(oneTooFew.size(), is(2));
        assertThat(oneTooFew.get(1), is(equalTo("Part 1:\n" + part + "\n\n")));
    }

    @Test
    public void reduceBatches_aPartLongerThanTheBudget_stillGetsABatch() {
        final List<String> batches = MapReduceMath.reduceBatches(Arrays.asList(chars(50), chars(50)), 10);

        assertThat(batches.size(), is(2));
    }

    @Test
    public void reduceBatches_atMostTheFanInPerBatch() {
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < MapReduceMath.MAX_REDUCE_FANIN + 1; i++) {
            partials.add("p");
        }

        final List<String> batches = MapReduceMath.reduceBatches(partials, 1_000_000);

        assertThat(batches.size(), is(2));
        assertThat(batches.get(0).contains("Part " + MapReduceMath.MAX_REDUCE_FANIN + ":"), is(true));
        assertThat(batches.get(1), is(equalTo("Part 1:\np\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="combineAll">
    @Test
    public void combineAll_labelsEveryPartInOrder() {
        assertThat(
                MapReduceMath.combineAll(Arrays.asList("a", "b", "c")),
                is(equalTo("Part 1:\na\n\nPart 2:\nb\n\nPart 3:\nc\n\n")));
    }

    @Test
    public void combineAll_ofNothing_isEmpty() {
        assertThat(MapReduceMath.combineAll(Collections.<String>emptyList()), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="coverage and chunk arithmetic">
    @Test
    public void chunkCoverage_allOrSampled() {
        assertThat(
                MapReduceMath.chunkCoverage(9, 9, 0, 0, 0), is(equalTo("**Coverage:** 9 of 9 chunks summarized\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 0, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled)\n\n")));
    }

    @Test
    public void chunkCoverage_namesTheCallsCutToFit() {
        assertThat(
                MapReduceMath.chunkCoverage(9, 9, 1, 0, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized (1 call cut to fit the window)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 2, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window)\n\n")));
    }

    @Test
    public void chunkCoverage_namesThePartSummariesCutAtTheOutputBudget() {
        assertThat(
                MapReduceMath.chunkCoverage(11, 11, 0, 1, 0),
                is(equalTo("**Coverage:** 11 of 11 chunks summarized (1 part summary cut at the output budget)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(6, 9, 1, 5, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 1 call cut to fit the window;"
                        + " 5 part summaries cut at the output budget)\n\n")));
    }

    @Test
    public void headCoverage_namesTheCharactersRead() {
        assertThat(
                MapReduceMath.headCoverage(12_000, 90_000),
                is(equalTo("**Coverage:** the first 12000 of 90000 characters (sampled head)\n\n")));
    }

    @Test
    public void reduceCalls_oneRoundUpTo16_moreRoundsAbove() {
        assertThat(MapReduceMath.reduceCalls(0), is(0L));
        assertThat(MapReduceMath.reduceCalls(1), is(0L));
        assertThat(MapReduceMath.reduceCalls(2), is(1L));
        assertThat(MapReduceMath.reduceCalls(16), is(1L));
        assertThat(MapReduceMath.reduceCalls(17), is(3L));
        assertThat(MapReduceMath.reduceCalls(256), is(17L));
        assertThat(MapReduceMath.reduceCalls(257), is(20L));
    }

    @Test
    public void splittable_fromTwiceTheMinimumChunk_halvesAreNoShorterThanIt() {
        assertThat(MapReduceMath.splittable(2 * MapReduceMath.MIN_CHUNK_CHARS - 1), is(false));
        assertThat(MapReduceMath.splittable(2 * MapReduceMath.MIN_CHUNK_CHARS), is(true));
    }

    @Test
    public void reduceCalls_aSmallerFanIn_takesMoreRounds_heldToTwoAndSixteen() {
        assertThat(MapReduceMath.reduceCalls(8, 2), is(7L));
        assertThat(MapReduceMath.reduceCalls(9, 3), is(4L));
        assertThat(MapReduceMath.reduceCalls(8, 1), is(7L));
        assertThat(MapReduceMath.reduceCalls(17, 99), is(3L));
    }

    @Test
    public void expectedFanIn_thePartSummariesThatFitTheBudget_twoToSixteen() {
        // a part of 950 tokens at 4 characters is 3 800 characters and a 64-character label: 3 864
        assertThat(MapReduceMath.expectedFanIn(3_864L * 5, 4.0d, 950), is(5));
        assertThat(MapReduceMath.expectedFanIn(3_864L * 5 - 1, 4.0d, 950), is(4));
        assertThat(MapReduceMath.expectedFanIn(100L, 4.0d, 950), is(2));
        assertThat(MapReduceMath.expectedFanIn(10_000_000L, 4.0d, 950), is(16));
    }

    @Test
    public void chunkCount_advancesTheBudgetLessTheOverlap() {
        assertThat(MapReduceMath.chunkCount(0, 8_000, 400), is(1L));
        assertThat(MapReduceMath.chunkCount(8_000, 8_000, 400), is(1L));
        assertThat(MapReduceMath.chunkCount(8_001, 8_000, 400), is(2L));
        assertThat(MapReduceMath.chunkCount(15_600, 8_000, 400), is(2L));
        assertThat(MapReduceMath.chunkCount(15_601, 8_000, 400), is(3L));
        // an overlap as large as the budget still advances one character
        assertThat(MapReduceMath.chunkCount(10, 5, 5), is(6L));
    }

    @Test
    public void chunkCount_matchesTheChunkerOnTextWithoutLineBreaks() {
        final String source = chars(30_000);
        final int budget = 8_000;
        final int overlap = MapReduceMath.overlap(budget);

        assertThat(MapReduceMath.chunkCount(source.length(), budget, overlap), is((long)
                net.ladenthin.srcmorph.indexer.generation.SourceChunker.chunk(source, budget, overlap, 0)
                        .size()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunkLabel">
    @Test
    public void chunkLabel_namesTheChunkAndTheChunks() {
        assertThat(MapReduceMath.chunkLabel(3, 44, 44), is(equalTo("Chunk 3 of 44:\n")));
    }

    @Test
    public void chunkLabel_ofASample_namesTheChunksItWasSampledFrom() {
        assertThat(MapReduceMath.chunkLabel(3, 6, 44), is(equalTo("Chunk 3 of 6, sampled from 44 chunks:\n")));
    }

    @Test
    public void chunkLabel_ofTheLongestNumbers_fitsItsReserve() {
        assertThat(
                MapReduceMath.chunkLabel(99_999, 99_999, 100_000).length() <= MapReduceMath.CHUNK_LABEL_RESERVE,
                is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="review round 2">
    @Test
    public void reduceGroups_areTheBatchesWithoutLabels() {
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        final List<List<String>> groups =
                MapReduceMath.reduceGroups(Arrays.asList(part, part, "c"), firstBatch + 100 + 16);

        assertThat(groups, is(equalTo(Arrays.asList(Arrays.asList(part, part), Collections.singletonList("c")))));
    }

    @Test
    public void reduceGroups_countTheLabelsOfTwoDigitParts() {
        // twelve parts of one char: the label of part 10 and above is one char longer
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            partials.add("p");
        }
        final String nine = MapReduceMath.combineAll(partials.subList(0, 11));

        final List<List<String>> fits = MapReduceMath.reduceGroups(partials, nine.length() + 1 + 16);
        final List<List<String>> short1 = MapReduceMath.reduceGroups(partials, nine.length() + 1 + 15);

        assertThat(fits.get(0).size(), is(12));
        assertThat(short1.get(0).size(), is(11));
    }

    @Test
    public void pairablePartChars_letTwoPartsFitOneBatch() {
        final int budget = 1_000;
        final String part = chars(MapReduceMath.pairablePartChars(budget));

        assertThat(MapReduceMath.pairablePartChars(budget), is(484));
        assertThat(MapReduceMath.reduceGroups(Arrays.asList(part, part), budget).size(), is(1));
        assertThat(MapReduceMath.pairablePartChars(10), is(1));
    }

    @Test
    public void shortened_cutsAtTheLastLineEndAndMarksIt() {
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
    public void shortened_neverExceedsItsRoom() {
        final String partial = "a\nbb\nccc\ndddd\neeeee";
        for (int max = MapReduceMath.SHORTENED_MARKER.length(); max < partial.length(); max++) {
            assertThat(MapReduceMath.shortened(partial, max).length() <= max, is(true));
        }
    }

    @Test
    public void reduceFacts_aLineThatFits_isTheLine_aLongOneEndsAtACounter() {
        final String facts = "**Facts (exact, whole file):** a: 1; bb: 22; ccc: 333\n\n";

        assertThat(MapReduceMath.reduceFacts(facts, facts.length()), is(equalTo(facts)));
        assertThat(
                MapReduceMath.reduceFacts(facts, 50),
                is(equalTo("**Facts (exact, whole file):** a: 1; bb: 22; ...\n\n")));
        assertThat(MapReduceMath.reduceFacts(facts, 50).length() <= 50, is(true));
        assertThat(MapReduceMath.reduceFacts("", 0), is(equalTo("")));
    }

    @Test
    public void reduceFacts_endsAtTheLastCounterThatLeavesRoomForTheEllipsis() {
        // the counter "c: 3" would fit without the ellipsis, not with it
        final String facts = "F: a: 1; b: 2; c: 3; d: 4\n\n";

        assertThat(MapReduceMath.reduceFacts(facts, 20), is(equalTo("F: a: 1; b: 2; ...\n\n")));
        assertThat(MapReduceMath.reduceFacts(facts, 20).length(), is(20));
    }

    @Test
    public void reduceFacts_withoutACounterThatFits_isTheEllipsisAlone() {
        assertThat(MapReduceMath.reduceFacts(chars(100), 20), is(equalTo("; ...\n\n")));
    }

    @Test
    public void halfSplit_isAfterALineEndNearTheMiddle() {
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
    public void cutPart_dropsTheLineTheCutEndedInAndMarksIt() {
        assertThat(
                MapReduceMath.cutPart("- a\n- b\n- c, cut in the mi"),
                is(equalTo("- a\n- b" + MapReduceMath.SHORTENED_MARKER)));
        // one line: kept whole, with the marker
        assertThat(MapReduceMath.cutPart("- a part"), is(equalTo("- a part" + MapReduceMath.SHORTENED_MARKER)));
        // a line end only at the start leaves the text whole
        assertThat(MapReduceMath.cutPart("\n- a part"), is(equalTo("\n- a part" + MapReduceMath.SHORTENED_MARKER)));
    }

    @Test
    public void halfLabel_namesTheHalf() {
        final String label = MapReduceMath.chunkLabel(3, 44, 44);

        assertThat(MapReduceMath.halfLabel(label, 1), is(equalTo("Chunk 3 of 44, half 1:\n")));
        assertThat(
                MapReduceMath.halfLabel(MapReduceMath.halfLabel(label, 2), 1),
                is(equalTo("Chunk 3 of 44, half 2, half 1:\n")));
        assertThat(MapReduceMath.halfLabel("x", 2), is(equalTo("x, half 2:\n")));
    }

    @Test
    public void chunkCoverage_namesThePartSummariesShortenedForTheReduce() {
        assertThat(
                MapReduceMath.chunkCoverage(4, 4, 0, 0, 1),
                is(equalTo("**Coverage:** 4 of 4 chunks summarized (1 part summary shortened to fit the reduce)\n\n")));
        assertThat(
                MapReduceMath.chunkCoverage(4, 4, 0, 0, 3),
                is(equalTo(
                        "**Coverage:** 4 of 4 chunks summarized (3 part summaries shortened to fit the reduce)\n\n")));
    }

    @Test
    public void safeWindow_ofAHugeWindow_doesNotOverflow() {
        assertThat(MapReduceMath.safeWindow(200_000_000), is(160_000_000));
    }
    // </editor-fold>

    @Test
    public void sampleNote_namesTheChunksRead_orIsEmpty() {
        assertThat(
                MapReduceMath.sampleNote(6, 44),
                is(equalTo("**Sample:** the parts summarize 6 of 44 chunks; the rest of the input was not read.\n\n")));
        assertThat(MapReduceMath.sampleNote(44, 44), is(equalTo("")));
        assertThat(MapReduceMath.sampleNote(45, 44), is(equalTo("")));
    }
}
