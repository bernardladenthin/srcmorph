// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.config.OversizeStrategy;

/**
 * The chunks a {@code mapReduce} or {@code sampleChunks} rule reads of a source that does not fit its
 * window, and the budgets its map and reduce calls share: one budget sizes the chunks and the reduce
 * batches, against the longer of the two prompts and a chunk's label, in a window shrunk by
 * {@link MapReduceMath#TOKEN_HEADROOM_PERCENT} so a token-dense chunk cannot overflow the model's
 * context. {@code mapReduce} reads every chunk, {@code sampleChunks} the rule's {@code maxChunks}
 * representative ones.
 */
@ToString
@EqualsAndHashCode
final class ChunkPlan {

    /** The share (1/n) of a reduce batch the facts line may take; a longer one is cut for the reduce. */
    static final int REDUCE_FACTS_SHARE = 4;

    private final int safeWindow;

    private final int sourceBudget;

    private final List<String> chunks;

    private final int total;

    private ChunkPlan(final int safeWindow, final int sourceBudget, final List<String> chunks, final int total) {
        this.safeWindow = safeWindow;
        this.sourceBudget = sourceBudget;
        this.chunks = chunks;
        this.total = total;
    }

    /**
     * Returns the characters a chunk has in a window next to the longer of the map and reduce prompts and a
     * chunk's label.
     *
     * @param window             the budget of a whole call's prompt, in characters
     * @param mapPromptLength    the length of the map prompt with an empty source
     * @param reducePromptLength the length of the reduce prompt with an empty source
     * @return the characters, negative when the prompts alone exceed the window
     */
    static int room(final int window, final int mapPromptLength, final int reducePromptLength) {
        return MapReduceMath.safeWindow(window)
                - Math.max(mapPromptLength, reducePromptLength)
                - MapReduceMath.CHUNK_LABEL_RESERVE;
    }

    /**
     * Plans the chunks of a source.
     *
     * @param source             the whole source
     * @param window             the budget of a whole call's prompt, in characters
     * @param mapPromptLength    the length of the map prompt with an empty source
     * @param reducePromptLength the length of the reduce prompt with an empty source
     * @param strategy           the rule's {@code onOversize}: {@code sampleChunks} reads {@code maxChunks}
     *                           of the chunks, anything else all of them
     * @param maxChunks          the chunks {@code sampleChunks} reads
     * @return the plan
     * @throws IllegalArgumentException if the {@link #room room} for a chunk is below
     *                                  {@link MapReduceMath#MIN_CHUNK_CHARS}: a chunk of the minimum
     *                                  would be cut in every call, and most of the source never read
     */
    static ChunkPlan of(
            final String source,
            final int window,
            final int mapPromptLength,
            final int reducePromptLength,
            final OversizeStrategy strategy,
            final int maxChunks) {
        final int room = room(window, mapPromptLength, reducePromptLength);
        if (room < MapReduceMath.MIN_CHUNK_CHARS) {
            throw new IllegalArgumentException("the window leaves " + room + " characters for a chunk, fewer than "
                    + MapReduceMath.MIN_CHUNK_CHARS);
        }
        // the room is the budget of a chunk's source and of a reduce batch
        final List<String> all = SourceChunker.chunk(source, room, MapReduceMath.overlap(room), 0);
        final List<String> chunks =
                strategy == OversizeStrategy.SAMPLE_CHUNKS ? SourceChunker.select(all, maxChunks) : all;
        return new ChunkPlan(
                MapReduceMath.safeWindow(window),
                room,
                Collections.unmodifiableList(new ArrayList<>(chunks)),
                all.size());
    }

    /**
     * Returns the window every map and reduce call is prepared for.
     *
     * @return the window in characters
     */
    int safeWindow() {
        return safeWindow;
    }

    /**
     * Returns the source characters of a chunk and of a reduce batch.
     *
     * @return the budget in characters
     */
    int sourceBudget() {
        return sourceBudget;
    }

    /**
     * Returns the chunks that are read, in source order.
     *
     * @return the chunks
     */
    List<String> chunks() {
        return chunks;
    }

    /**
     * Returns the chunks the whole source has.
     *
     * @return the count
     */
    int total() {
        return total;
    }

    /**
     * Returns whether the answer to a chunk is one part of several, which the reduce reads, rather than the
     * final answer, which has to be complete.
     *
     * @return {@code true} for more than one chunk
     */
    boolean parts() {
        return chunks.size() > 1;
    }

    /**
     * Returns the label line of a chunk ({@link MapReduceMath#chunkLabel}).
     *
     * @param number the chunk's number among those read, from 1
     * @return the label
     */
    String label(final int number) {
        return MapReduceMath.chunkLabel(number, chunks.size(), total);
    }

    /**
     * Returns what every reduce call reads in front of its parts: the facts line of the whole source, cut to
     * a {@link #REDUCE_FACTS_SHARE quarter} of a batch, and the note on a sample.
     *
     * @param facts the exact facts line of the whole source, {@code ""} for none
     * @return the text
     */
    String reduceFacts(final String facts) {
        return MapReduceMath.reduceFacts(facts, sourceBudget / REDUCE_FACTS_SHARE)
                + MapReduceMath.sampleNote(chunks.size(), total);
    }

    /**
     * Returns the characters the parts of one reduce batch may take next to what the batch reads in front of
     * them.
     *
     * @param reduceFacts what a reduce call reads in front of its parts ({@link #reduceFacts})
     * @return the characters, at least 1
     */
    int batchBudget(final String reduceFacts) {
        return Math.max(1, sourceBudget - reduceFacts.length());
    }

    /**
     * Returns the coverage line of the summary ({@link MapReduceMath#chunkCoverage}).
     *
     * @param cutCalls       the calls whose input a context overflow cut to a smaller window
     * @param cutParts       the part summaries cut off at the output budget and kept
     * @param shortenedParts the part summaries the reduce shortened
     * @return the line
     */
    String coverage(final int cutCalls, final int cutParts, final int shortenedParts) {
        return MapReduceMath.chunkCoverage(chunks.size(), total, cutCalls, cutParts, shortenedParts);
    }
}
