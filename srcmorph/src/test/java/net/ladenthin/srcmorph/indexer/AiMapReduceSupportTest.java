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
                AiMapReduceSupport.chunkCoverage(9, 9, 0, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 0, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled)\n\n")));
    }

    @Test
    public void chunkCoverage_namesTheCallsCutToFit() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(9, 9, 1, 0),
                is(equalTo("**Coverage:** 9 of 9 chunks summarized (1 call cut to fit the window)\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 2, 0),
                is(equalTo("**Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window)\n\n")));
    }

    @Test
    public void chunkCoverage_namesThePartSummariesCutAtTheOutputBudget() {
        assertThat(
                AiMapReduceSupport.chunkCoverage(11, 11, 0, 1),
                is(equalTo("**Coverage:** 11 of 11 chunks summarized (1 part summary cut at the output budget)\n\n")));
        assertThat(
                AiMapReduceSupport.chunkCoverage(6, 9, 1, 5),
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
}
