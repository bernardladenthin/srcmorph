// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * One round of the hierarchical reduce: the partial summaries packed into batches that fit a batch budget
 * (at most {@link MapReduceMath#MAX_REDUCE_FANIN} each). When no two partials fit one batch, each is
 * {@link MapReduceMath#shortened shortened} so two do, and the round counts them: no partial is
 * dropped. A round with one batch writes the final answer; in the others a batch of a single partial
 * passes it on as it is, since a summary of one summary only loses detail.
 */
@ToString
@EqualsAndHashCode
final class ReduceRound {

    private final List<List<String>> batches;

    private final int shortenedParts;

    private ReduceRound(final List<List<String>> batches, final int shortenedParts) {
        this.batches = batches;
        this.shortenedParts = shortenedParts;
    }

    /**
     * Packs one round.
     *
     * @param partials    the partial summaries of the round, more than one
     * @param batchBudget the characters the parts of one batch may take
     * @return the round
     */
    static ReduceRound of(final List<String> partials, final int batchBudget) {
        final List<List<String>> batches = MapReduceMath.reduceGroups(partials, batchBudget);
        if (batches.size() < partials.size()) {
            return new ReduceRound(batches, 0);
        }
        // no two partials fit one batch: shorten each so two do
        final int maxChars = MapReduceMath.pairablePartChars(batchBudget);
        final List<String> shortened = new ArrayList<>(partials.size());
        int count = 0;
        for (final String partial : partials) {
            final String head = MapReduceMath.shortened(partial, maxChars);
            if (head.length() < partial.length()) {
                count++;
            }
            shortened.add(head);
        }
        return new ReduceRound(MapReduceMath.reduceGroups(shortened, batchBudget), count);
    }

    /**
     * Returns the batches, each a list of partials in order.
     *
     * @return the batches
     */
    List<List<String>> batches() {
        return Collections.unmodifiableList(batches);
    }

    /**
     * Returns the partials this round shortened because no two fitted one batch.
     *
     * @return the count
     */
    int shortenedParts() {
        return shortenedParts;
    }

    /**
     * Returns whether the round's answers are parts the next round reads, rather than the final answer,
     * which has to be complete.
     *
     * @return {@code true} for more than one batch
     */
    boolean parts() {
        return batches.size() > 1;
    }

    /**
     * Returns whether a batch passes its partial on to the next round as it is: a batch of a single partial
     * in a round that is not the last.
     *
     * @param batch one of the {@link #batches() batches}
     * @return {@code true} when the batch is not summarized
     */
    boolean passesOn(final Collection<String> batch) {
        return parts() && batch.size() == 1;
    }
}
