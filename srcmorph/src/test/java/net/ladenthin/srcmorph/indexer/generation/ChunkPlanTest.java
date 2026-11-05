// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import net.ladenthin.srcmorph.config.AiOversizeStrategy;
import org.junit.jupiter.api.Test;

public class ChunkPlanTest {

    /** A window whose safe window (1064) leaves exactly {@link AiMapReduceSupport#MIN_CHUNK_CHARS} for a chunk. */
    private static final int MIN_WINDOW = 1330;

    private static String source(final int lines) {
        final StringBuilder text = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            text.append("line ").append(i).append(" of the source, long enough to fill a chunk\n");
        }
        return text.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="room">
    @Test
    public void room_isTheSafeWindowLessTheLongerPromptAndTheLabel() {
        // act
        final int mapLonger = ChunkPlan.room(10_000, 300, 100);
        final int reduceLonger = ChunkPlan.room(10_000, 100, 300);

        // assert
        assertThat(mapLonger, is(8000 - 300 - AiMapReduceSupport.CHUNK_LABEL_RESERVE));
        assertThat(reduceLonger, is(mapLonger));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="of">
    @Test
    public void of_roomOfTheMinimum_isPlanned() {
        // act
        final ChunkPlan plan = ChunkPlan.of(source(10), MIN_WINDOW, 0, 0, AiOversizeStrategy.MAP_REDUCE, 0);

        // assert
        assertThat(plan.sourceBudget(), is(AiMapReduceSupport.MIN_CHUNK_CHARS));
        assertThat(plan.safeWindow(), is(AiMapReduceSupport.safeWindow(MIN_WINDOW)));
    }

    @Test
    public void of_roomBelowTheMinimum_isRefused() {
        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ChunkPlan.of(source(10), MIN_WINDOW - 2, 0, 0, AiOversizeStrategy.MAP_REDUCE, 0));

        // assert
        assertThat(thrown.getMessage(), is("the window leaves 999 characters for a chunk, fewer than 1000"));
    }

    @Test
    public void of_mapReduce_readsEveryChunkOfTheRoom() {
        // arrange
        final String source = source(300);
        final int room = ChunkPlan.room(4000, 200, 100);
        final List<String> all = AiSourceChunker.chunk(source, room, AiMapReduceSupport.overlap(room), 0);

        // act
        final ChunkPlan plan = ChunkPlan.of(source, 4000, 200, 100, AiOversizeStrategy.MAP_REDUCE, 2);

        // assert
        assertThat(all.size(), greaterThan(2));
        assertThat(plan.chunks(), is(all));
        assertThat(plan.total(), is(all.size()));
        assertThat(plan.sourceBudget(), is(room));
        assertThat(plan.safeWindow(), is(3200));
    }

    @Test
    public void of_sampleChunks_readsMaxChunksOfThem() {
        // arrange
        final String source = source(100);
        final int room = ChunkPlan.room(4000, 0, 0);
        final List<String> all = AiSourceChunker.chunk(source, room, AiMapReduceSupport.overlap(room), 0);

        // act
        final ChunkPlan plan = ChunkPlan.of(source, 4000, 0, 0, AiOversizeStrategy.SAMPLE_CHUNKS, 2);

        // assert
        assertThat(plan.chunks(), is(AiSourceChunker.select(all, 2)));
        assertThat(plan.total(), is(all.size()));
    }

    @Test
    public void chunks_cannotBeChanged() {
        // arrange
        final ChunkPlan plan = ChunkPlan.of(source(10), 4000, 0, 0, AiOversizeStrategy.MAP_REDUCE, 0);

        // act + assert
        assertThrows(UnsupportedOperationException.class, () -> plan.chunks().add("x"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parts and labels">
    @Test
    public void parts_oneChunk_isTheFinalAnswer() {
        // act
        final ChunkPlan plan = ChunkPlan.of("short", 4000, 0, 0, AiOversizeStrategy.MAP_REDUCE, 0);

        // assert
        assertThat(plan.chunks().size(), is(1));
        assertThat(plan.parts(), is(false));
    }

    @Test
    public void parts_twoChunks_areParts() {
        // act
        final ChunkPlan plan = ChunkPlan.of(source(100), 4000, 0, 0, AiOversizeStrategy.SAMPLE_CHUNKS, 2);

        // assert
        assertThat(plan.chunks().size(), is(2));
        assertThat(plan.parts(), is(true));
    }

    @Test
    public void label_namesTheChunkAmongThoseReadAndAll() {
        // arrange
        final ChunkPlan plan = ChunkPlan.of(source(100), 4000, 0, 0, AiOversizeStrategy.SAMPLE_CHUNKS, 2);

        // act
        final String label = plan.label(2);

        // assert
        assertThat(label, is(AiMapReduceSupport.chunkLabel(2, 2, plan.total())));
    }

    @Test
    public void coverage_countsTheChunksReadOfAll() {
        // arrange
        final ChunkPlan plan = ChunkPlan.of(source(100), 4000, 0, 0, AiOversizeStrategy.SAMPLE_CHUNKS, 2);

        // act
        final String coverage = plan.coverage(1, 2, 3);

        // assert
        assertThat(coverage, is(AiMapReduceSupport.chunkCoverage(2, plan.total(), 1, 2, 3)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reduce">
    @Test
    public void reduceFacts_cutsTheFactsToAQuarterOfABatch_andNotesTheSample() {
        // arrange
        final ChunkPlan plan = ChunkPlan.of(source(100), MIN_WINDOW, 0, 0, AiOversizeStrategy.SAMPLE_CHUNKS, 2);
        final String facts = source(10);

        // act
        final String reduceFacts = plan.reduceFacts(facts);

        // assert
        assertThat(
                reduceFacts,
                is(AiMapReduceSupport.reduceFacts(facts, 250) + AiMapReduceSupport.sampleNote(2, plan.total())));
    }

    @Test
    public void batchBudget_isTheBudgetLessWhatTheBatchReadsFirst_atLeastOne() {
        // arrange
        final ChunkPlan plan = ChunkPlan.of(source(10), MIN_WINDOW, 0, 0, AiOversizeStrategy.MAP_REDUCE, 0);

        // act + assert
        assertThat(plan.batchBudget(""), is(1000));
        assertThat(plan.batchBudget(new String(new char[999])), is(1));
        assertThat(plan.batchBudget(new String(new char[1000])), is(1));
        assertThat(plan.batchBudget(new String(new char[5000])), is(1));
    }
    // </editor-fold>
}
