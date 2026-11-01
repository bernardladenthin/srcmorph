// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiMapReduceSupportTest {

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
        assertThat(AiMapReduceSupport.safeWindow(10_000), is(8_000));
        // integer arithmetic: 20 % of 1234 is 246 (rounded down), the window keeps the rest
        assertThat(AiMapReduceSupport.safeWindow(1_234), is(988));
    }

    @Test
    public void sourceBudget_isTheSafeWindowLessThePrompt_atLeastTheMinimum() {
        assertThat(AiMapReduceSupport.sourceBudget(8_000, 500), is(7_500));
        assertThat(AiMapReduceSupport.sourceBudget(1_200, 500), is(AiMapReduceSupport.MIN_CHUNK_CHARS));
        assertThat(AiMapReduceSupport.sourceBudget(1_500, 500), is(1_000));
    }

    @Test
    public void overlap_isATwentiethOfTheBudget_atMostTheMaximum() {
        assertThat(AiMapReduceSupport.overlap(10_000), is(500));
        assertThat(AiMapReduceSupport.overlap(100_000), is(AiMapReduceSupport.MAX_OVERLAP_CHARS));
        assertThat(AiMapReduceSupport.overlap(40_000), is(2_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduceBatches">
    @Test
    public void reduceBatches_labelsEveryPartWithItsNumberInTheBatch() {
        assertThat(
                AiMapReduceSupport.reduceBatches(Arrays.asList("a", "b"), 1_000),
                is(equalTo(Collections.singletonList("Part 1:\na\n\nPart 2:\nb\n\n"))));
    }

    @Test
    public void reduceBatches_nothingToReduce_isNoBatch() {
        assertThat(
                AiMapReduceSupport.reduceBatches(Collections.<String>emptyList(), 1_000)
                        .isEmpty(),
                is(true));
    }

    @Test
    public void reduceBatches_aBatchEndsWhereTheNextPartWouldNotFit() {
        // the first part takes 100 + 11 chars of label and separators; the second needs 100 + 16 more
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        final List<String> exactlyFits =
                AiMapReduceSupport.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 16);
        final List<String> oneTooFew =
                AiMapReduceSupport.reduceBatches(Arrays.asList(part, part), firstBatch + 100 + 15);

        assertThat(exactlyFits.size(), is(1));
        assertThat(oneTooFew.size(), is(2));
        assertThat(oneTooFew.get(1), is(equalTo("Part 1:\n" + part + "\n\n")));
    }

    @Test
    public void reduceBatches_aPartLongerThanTheBudget_stillGetsABatch() {
        final List<String> batches = AiMapReduceSupport.reduceBatches(Arrays.asList(chars(50), chars(50)), 10);

        assertThat(batches.size(), is(2));
    }

    @Test
    public void reduceBatches_atMostTheFanInPerBatch() {
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < AiMapReduceSupport.MAX_REDUCE_FANIN + 1; i++) {
            partials.add("p");
        }

        final List<String> batches = AiMapReduceSupport.reduceBatches(partials, 1_000_000);

        assertThat(batches.size(), is(2));
        assertThat(batches.get(0).contains("Part " + AiMapReduceSupport.MAX_REDUCE_FANIN + ":"), is(true));
        assertThat(batches.get(1), is(equalTo("Part 1:\np\n\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="combineAll">
    @Test
    public void combineAll_labelsEveryPartInOrder() {
        assertThat(
                AiMapReduceSupport.combineAll(Arrays.asList("a", "b", "c")),
                is(equalTo("Part 1:\na\n\nPart 2:\nb\n\nPart 3:\nc\n\n")));
    }

    @Test
    public void combineAll_ofNothing_isEmpty() {
        assertThat(AiMapReduceSupport.combineAll(Collections.<String>emptyList()), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="coverage and chunk arithmetic">
    @Test
    public void chunkCoverage_allOrSampled() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(9, 9, 0, 0, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 0, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled)\n\n")));
    }

    @Test
    public void chunkCoverage_namesTheCallsCutToFit() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(9, 9, 1, 0, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized (1 call cut to fit the window)\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 2, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window)\n\n")));
    }

    @Test
    public void chunkCoverage_namesThePartSummariesCutAtTheOutputBudget() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(11, 11, 0, 1, 0),
                is(equalTo("**Coverage:** 11 of 11 chunks summarized (1 part summary cut at the output budget)\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 1, 5, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 1 call cut to fit the window;"
                        + " 5 part summaries cut at the output budget)\n\n")));
    }

    @Test
    public void headCoverage_namesTheCharactersRead() {
        assertThat(
                AiMapReduceSupport.headCoverage(12_000, 90_000),
                is(equalTo("**Coverage:** the first 12000 of 90000 characters (sampled head)\n\n")));
    }

    @Test
    public void reduceCalls_oneRoundUpTo16_moreRoundsAbove() {
        assertThat(AiMapReduceSupport.reduceCalls(0), is(0L));
        assertThat(AiMapReduceSupport.reduceCalls(1), is(0L));
        assertThat(AiMapReduceSupport.reduceCalls(2), is(1L));
        assertThat(AiMapReduceSupport.reduceCalls(16), is(1L));
        assertThat(AiMapReduceSupport.reduceCalls(17), is(3L));
        assertThat(AiMapReduceSupport.reduceCalls(256), is(17L));
        assertThat(AiMapReduceSupport.reduceCalls(257), is(20L));
    }

    @Test
    public void chunkCount_advancesTheBudgetLessTheOverlap() {
        assertThat(AiMapReduceSupport.chunkCount(0, 8_000, 400), is(1L));
        assertThat(AiMapReduceSupport.chunkCount(8_000, 8_000, 400), is(1L));
        assertThat(AiMapReduceSupport.chunkCount(8_001, 8_000, 400), is(2L));
        assertThat(AiMapReduceSupport.chunkCount(15_600, 8_000, 400), is(2L));
        assertThat(AiMapReduceSupport.chunkCount(15_601, 8_000, 400), is(3L));
        // an overlap as large as the budget still advances one character
        assertThat(AiMapReduceSupport.chunkCount(10, 5, 5), is(6L));
    }

    @Test
    public void chunkCount_matchesTheChunkerOnTextWithoutLineBreaks() {
        final String source = chars(30_000);
        final int budget = 8_000;
        final int overlap = AiMapReduceSupport.overlap(budget);

        assertThat(AiMapReduceSupport.chunkCount(source.length(), budget, overlap), is((long)
                net.ladenthin.srcmorph.support.AiSourceChunker.chunk(source, budget, overlap, 0)
                        .size()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunkLabel">
    @Test
    public void chunkLabel_namesTheChunkAndTheChunks() {
        assertThat(AiMapReduceSupport.chunkLabel(3, 44, 44), is(equalTo("Chunk 3 of 44:\n")));
    }

    @Test
    public void chunkLabel_ofASample_namesTheChunksItWasSampledFrom() {
        assertThat(AiMapReduceSupport.chunkLabel(3, 6, 44), is(equalTo("Chunk 3 of 6, sampled from 44 chunks:\n")));
    }

    @Test
    public void chunkLabel_ofTheLongestNumbers_fitsItsReserve() {
        assertThat(
                AiMapReduceSupport.chunkLabel(99_999, 99_999, 100_000).length()
                        <= AiMapReduceSupport.CHUNK_LABEL_RESERVE,
                is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="review round 2">
    @Test
    public void reduceGroups_areTheBatchesWithoutLabels() {
        final String part = chars(100);
        final int firstBatch = ("Part 1:\n" + part + "\n\n").length();

        final List<List<String>> groups =
                AiMapReduceSupport.reduceGroups(Arrays.asList(part, part, "c"), firstBatch + 100 + 16);

        assertThat(groups, is(equalTo(Arrays.asList(Arrays.asList(part, part), Collections.singletonList("c")))));
    }

    @Test
    public void reduceGroups_countTheLabelsOfTwoDigitParts() {
        // twelve parts of one char: the label of part 10 and above is one char longer
        final List<String> partials = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            partials.add("p");
        }
        final String nine = AiMapReduceSupport.combineAll(partials.subList(0, 11));

        final List<List<String>> fits = AiMapReduceSupport.reduceGroups(partials, nine.length() + 1 + 16);
        final List<List<String>> short1 = AiMapReduceSupport.reduceGroups(partials, nine.length() + 1 + 15);

        assertThat(fits.get(0).size(), is(12));
        assertThat(short1.get(0).size(), is(11));
    }

    @Test
    public void pairablePartChars_letTwoPartsFitOneBatch() {
        final int budget = 1_000;
        final String part = chars(AiMapReduceSupport.pairablePartChars(budget));

        assertThat(AiMapReduceSupport.pairablePartChars(budget), is(484));
        assertThat(
                AiMapReduceSupport.reduceGroups(Arrays.asList(part, part), budget)
                        .size(),
                is(1));
        assertThat(AiMapReduceSupport.pairablePartChars(10), is(1));
    }

    @Test
    public void shortened_cutsAtTheLastLineEndAndMarksIt() {
        assertThat(AiMapReduceSupport.shortened("abc", 3), is(equalTo("abc")));
        assertThat(
                AiMapReduceSupport.shortened("line one\nline two\nline three", 20),
                is(equalTo("line one" + AiMapReduceSupport.SHORTENED_MARKER)));
        // a line end only at the start is no place to cut: the room ends it
        assertThat(
                AiMapReduceSupport.shortened("\n" + chars(30), 10),
                is(equalTo("\n" + chars(5) + AiMapReduceSupport.SHORTENED_MARKER)));
        // one long line is cut where the room ends
        assertThat(
                AiMapReduceSupport.shortened(chars(30), 10),
                is(equalTo(chars(10 - AiMapReduceSupport.SHORTENED_MARKER.length())
                        + AiMapReduceSupport.SHORTENED_MARKER)));
    }

    @Test
    public void shortened_neverExceedsItsRoom() {
        final String partial = "a\nbb\nccc\ndddd\neeeee";
        for (int max = AiMapReduceSupport.SHORTENED_MARKER.length(); max < partial.length(); max++) {
            assertThat(AiMapReduceSupport.shortened(partial, max).length() <= max, is(true));
        }
    }

    @Test
    public void reduceFacts_aLineThatFits_isTheLine_aLongOneEndsAtACounter() {
        final String facts = "**Facts (exact, whole file):** a: 1; bb: 22; ccc: 333\n\n";

        assertThat(AiMapReduceSupport.reduceFacts(facts, facts.length()), is(equalTo(facts)));
        assertThat(
                AiMapReduceSupport.reduceFacts(facts, 50),
                is(equalTo("**Facts (exact, whole file):** a: 1; bb: 22; ...\n\n")));
        assertThat(AiMapReduceSupport.reduceFacts(facts, 50).length() <= 50, is(true));
        assertThat(AiMapReduceSupport.reduceFacts("", 0), is(equalTo("")));
    }

    @Test
    public void reduceFacts_endsAtTheLastCounterThatLeavesRoomForTheEllipsis() {
        // the counter "c: 3" would fit without the ellipsis, not with it
        final String facts = "F: a: 1; b: 2; c: 3; d: 4\n\n";

        assertThat(AiMapReduceSupport.reduceFacts(facts, 20), is(equalTo("F: a: 1; b: 2; ...\n\n")));
        assertThat(AiMapReduceSupport.reduceFacts(facts, 20).length(), is(20));
    }

    @Test
    public void reduceFacts_withoutACounterThatFits_isTheEllipsisAlone() {
        assertThat(AiMapReduceSupport.reduceFacts(chars(100), 20), is(equalTo("; ...\n\n")));
    }

    @Test
    public void halfSplit_isAfterALineEndNearTheMiddle() {
        assertThat(AiMapReduceSupport.halfSplit("aaaa\nbbbb\ncccc\n"), is(5));
        // no line end before the middle: the first one after it
        assertThat(AiMapReduceSupport.halfSplit("aaaaaaa\nbb"), is(8));
        // a single line: the middle
        assertThat(AiMapReduceSupport.halfSplit("abcdef"), is(3));
        // a line end only as the last character: the middle
        assertThat(AiMapReduceSupport.halfSplit("abcdef\n"), is(3));
        // a line end at the very start counts as one before the middle
        assertThat(AiMapReduceSupport.halfSplit("\nabcdefgh"), is(1));
        // the line end before the middle wins over one at it
        assertThat(AiMapReduceSupport.halfSplit("a\nbcd\nefgh"), is(2));
        // a line end at the middle itself, none before it
        assertThat(AiMapReduceSupport.halfSplit("abc\ndef"), is(4));
    }

    @Test
    public void halfLabel_namesTheHalf() {
        final String label = AiMapReduceSupport.chunkLabel(3, 44, 44);

        assertThat(AiMapReduceSupport.halfLabel(label, 1), is(equalTo("Chunk 3 of 44, half 1:\n")));
        assertThat(
                AiMapReduceSupport.halfLabel(AiMapReduceSupport.halfLabel(label, 2), 1),
                is(equalTo("Chunk 3 of 44, half 2, half 1:\n")));
        assertThat(AiMapReduceSupport.halfLabel("x", 2), is(equalTo("x, half 2:\n")));
    }

    @Test
    public void chunkCoverage_namesThePartSummariesShortenedForTheReduce() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(4, 4, 0, 0, 1),
                is(equalTo("**Coverage:** 4 of 4 chunks summarized (1 part summary shortened to fit the reduce)\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(4, 4, 0, 0, 3),
                is(equalTo(
                        "**Coverage:** 4 of 4 chunks summarized (3 part summaries shortened to fit the reduce)\n\n")));
    }

    @Test
    public void safeWindow_ofAHugeWindow_doesNotOverflow() {
        assertThat(AiMapReduceSupport.safeWindow(200_000_000), is(160_000_000));
    }
    // </editor-fold>
}
