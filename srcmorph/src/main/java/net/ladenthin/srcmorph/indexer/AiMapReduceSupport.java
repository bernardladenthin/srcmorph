// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import java.util.ArrayList;
import java.util.List;

/**
 * The arithmetic and the text of a map-reduce summary, without a model: how large a chunk is, how much
 * consecutive chunks overlap, and how the partial summaries are packed into the batches of one reduce
 * round. {@link AiFieldGenerationSupport} runs the generations.
 */
public final class AiMapReduceSupport {

    /** Upper bound on the character overlap between map-reduce chunks. */
    public static final int MAX_OVERLAP_CHARS = 2000;

    /** Fraction (1/n) of the chunk size used as overlap when smaller than {@link #MAX_OVERLAP_CHARS}. */
    public static final int OVERLAP_DIVISOR = 20;

    /** Minimum per-chunk source budget, so a tiny window never yields zero-length chunks. */
    public static final int MIN_CHUNK_CHARS = 1000;

    /**
     * Safety headroom (percent) subtracted from the window before sizing chunks and reduce batches.
     * {@code charsPerToken} is only an <em>estimate</em>; token-dense content (e.g. SQL with many
     * digits and punctuation, about 2.8 chars per token) tokenizes to more tokens than a budget computed
     * from a higher {@code charsPerToken} assumes, which would otherwise let a chunk overflow the model's
     * context at run time. The margin keeps chunks inside the window while the real density is up to this
     * percent below the configured one.
     */
    public static final int TOKEN_HEADROOM_PERCENT = 20;

    /**
     * Maximum number of partial summaries combined into a single reduce call. Bounds the reduce prompt
     * and makes every round shrink the list (a fan-in of at least 2), so the reduce terminates.
     */
    public static final int MAX_REDUCE_FANIN = 16;

    /** Label prefix of one partial summary inside a reduce prompt. */
    public static final String PART_LABEL = "Part ";

    /** Approximate per-partial label and separator overhead when packing a reduce batch. */
    public static final int LABEL_OVERHEAD_CHARS = 16;

    /** Percent divisor. */
    private static final int PERCENT = 100;

    /** The start of the coverage line in front of the summary of an over-window source. */
    public static final String COVERAGE_HEADER = "**Coverage:** ";

    /** The end of the coverage line: a blank line before what follows. */
    public static final String COVERAGE_SUFFIX = "\n\n";

    /** Utility class; not instantiable. */
    private AiMapReduceSupport() {
        // no-op
    }

    /**
     * Returns the window of every map and reduce call: the model's window less the
     * {@link #TOKEN_HEADROOM_PERCENT token headroom}.
     *
     * @param maxInputChars the model's per-call window in characters
     * @return the window a chunk or a batch is sized against
     */
    public static int safeWindow(final int maxInputChars) {
        return maxInputChars - maxInputChars * TOKEN_HEADROOM_PERCENT / PERCENT;
    }

    /**
     * Returns the source budget of one chunk and one reduce batch: the safe window less the prompt
     * template, at least {@link #MIN_CHUNK_CHARS}.
     *
     * @param safeWindow       the {@link #safeWindow(int) safe window}
     * @param basePromptLength the length of the prompt template with an empty source
     * @return the budget in characters
     */
    public static int sourceBudget(final int safeWindow, final int basePromptLength) {
        return Math.max(MIN_CHUNK_CHARS, safeWindow - basePromptLength);
    }

    /**
     * Returns the overlap between consecutive chunks: a twentieth of the budget, at most
     * {@link #MAX_OVERLAP_CHARS}.
     *
     * @param sourceBudget the {@link #sourceBudget(int, int) chunk budget}
     * @return the overlap in characters
     */
    public static int overlap(final int sourceBudget) {
        return Math.min(MAX_OVERLAP_CHARS, sourceBudget / OVERLAP_DIVISOR);
    }

    /**
     * Packs one reduce round: consecutive partials go into one batch while it holds fewer than
     * {@link #MAX_REDUCE_FANIN} and the next one still fits {@code sourceBudget}; a batch always takes
     * at least one partial, however long. Every partial is labelled {@code Part n:} with its number in
     * the batch.
     *
     * @param partials     the partial summaries of the round
     * @param sourceBudget the characters one batch may hold
     * @return the batch texts, in order
     */
    public static List<String> reduceBatches(final List<String> partials, final int sourceBudget) {
        final List<String> batches = new ArrayList<>(partials.size());
        int i = 0;
        while (i < partials.size()) {
            final StringBuilder batch = new StringBuilder();
            int inBatch = 0;
            while (inBatch < MAX_REDUCE_FANIN
                    && i < partials.size()
                    && (inBatch == 0
                            || batch.length() + partials.get(i).length() + LABEL_OVERHEAD_CHARS <= sourceBudget)) {
                appendPart(batch, inBatch + 1, partials.get(i));
                inBatch++;
                i++;
            }
            batches.add(batch.toString());
        }
        return batches;
    }

    /**
     * Returns every partial in one labelled text: the last resort when the partials are too long to
     * combine even two at a time, so the reduce ends with one (trimmed) call instead of not shrinking.
     *
     * @param partials the partial summaries
     * @return the labelled text of all of them
     */
    public static String combineAll(final Iterable<String> partials) {
        final StringBuilder all = new StringBuilder();
        int number = 0;
        for (final String partial : partials) {
            appendPart(all, ++number, partial);
        }
        return all.toString();
    }

    /**
     * Returns the coverage line of a map-reduce: how many of the source's chunks the summary read, and
     * how many of its calls a context overflow made cut to a smaller window.
     *
     * @param summarized the chunks summarized
     * @param total      the chunks of the source
     * @param cutCalls   the map and reduce calls whose input was cut to fit the window
     * @return {@code **Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window)},
     *         without {@code sampled} when every chunk was read and without the cut calls when none was
     */
    public static String chunkCoverage(final int summarized, final int total, final int cutCalls) {
        final List<String> notes = new ArrayList<>(2);
        if (summarized < total) {
            notes.add("sampled");
        }
        if (cutCalls > 0) {
            notes.add(cutCalls + (cutCalls == 1 ? " call" : " calls") + " cut to fit the window");
        }
        return COVERAGE_HEADER + summarized + " of " + total + " chunks summarized"
                + (notes.isEmpty() ? "" : " (" + String.join("; ", notes) + ")") + COVERAGE_SUFFIX;
    }

    /**
     * Returns the coverage line of a sampled head: how much of the source the one call read.
     *
     * @param read  the characters read
     * @param total the characters of the source
     * @return {@code **Coverage:** the first 12000 of 90000 characters (sampled head)}
     */
    public static String headCoverage(final int read, final int total) {
        return COVERAGE_HEADER + "the first " + read + " of " + total + " characters (sampled head)" + COVERAGE_SUFFIX;
    }

    /**
     * Returns how many reduce calls a map-reduce of {@code chunks} partials makes, when every batch holds
     * {@link #MAX_REDUCE_FANIN} partials (fewer when they are long): one per batch of every round, until
     * one summary is left; none for a single chunk.
     *
     * @param chunks the partials of the first round
     * @return the reduce calls
     */
    public static long reduceCalls(final long chunks) {
        long calls = 0;
        for (long level = chunks; level > 1; level = (level + MAX_REDUCE_FANIN - 1) / MAX_REDUCE_FANIN) {
            calls += (level + MAX_REDUCE_FANIN - 1) / MAX_REDUCE_FANIN;
        }
        return calls;
    }

    /**
     * Returns how many chunks {@link net.ladenthin.srcmorph.support.AiSourceChunker} cuts a source of
     * {@code sourceChars} into with the given budget and overlap, ignoring the line-boundary rounding
     * (which can add a chunk): a chunk advances {@code budget - overlap} characters.
     *
     * @param sourceChars  the source length
     * @param sourceBudget the characters of one chunk
     * @param overlap      the overlap of consecutive chunks
     * @return the chunks, at least one
     */
    public static long chunkCount(final long sourceChars, final int sourceBudget, final int overlap) {
        // the first chunk takes the budget, every further one advances by a step
        final long step = Math.max(1, sourceBudget - overlap);
        return 1 + (Math.max(0, sourceChars - sourceBudget) + step - 1) / step;
    }

    private static void appendPart(final StringBuilder target, final int number, final String partial) {
        target.append(PART_LABEL).append(number).append(":\n").append(partial).append("\n\n");
    }
}
