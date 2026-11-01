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

    private static void appendPart(final StringBuilder target, final int number, final String partial) {
        target.append(PART_LABEL).append(number).append(":\n").append(partial).append("\n\n");
    }
}
