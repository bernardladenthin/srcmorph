// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReduceRoundTest {

    private static final int BUDGET = 1000;

    private static String text(final char c, final int length) {
        final char[] chars = new char[length];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    // <editor-fold defaultstate="collapsed" desc="of">
    @Test
    public void of_partialsThatFitOneBatch_areOneBatchOfTheFinalAnswer() {
        // arrange
        final List<String> partials = Arrays.asList("a", "b", "c");

        // act
        final ReduceRound round = ReduceRound.of(partials, BUDGET);

        // assert
        assertThat(round.batches(), is(Collections.singletonList(partials)));
        assertThat(round.shortenedParts(), is(0));
        assertThat(round.parts(), is(false));
        assertThat(round.passesOn(partials), is(false));
    }

    @Test
    public void of_noTwoPartialsFit_shortensTheLongOnesSoTwoDo() {
        // arrange
        final String a = text('a', 600);
        final String b = text('b', 400);
        final String c = text('c', 600);
        final int maxChars = MapReduceMath.pairablePartChars(BUDGET);

        // act
        final ReduceRound round = ReduceRound.of(Arrays.asList(a, b, c), BUDGET);

        // assert
        assertThat(round.shortenedParts(), is(2));
        assertThat(
                round.batches(),
                is(MapReduceMath.reduceGroups(
                        Arrays.asList(MapReduceMath.shortened(a, maxChars), b, MapReduceMath.shortened(c, maxChars)),
                        BUDGET)));
    }

    @Test
    public void of_onePartial_isTheFinalAnswerAndNotPassedOn() {
        // arrange
        final List<String> partials = Collections.singletonList("only");

        // act
        final ReduceRound round = ReduceRound.of(partials, BUDGET);

        // assert
        assertThat(round.batches(), is(Collections.singletonList(partials)));
        assertThat(round.shortenedParts(), is(0));
        assertThat(round.passesOn(partials), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="passesOn">
    @Test
    public void passesOn_aBatchOfOnePartialInARoundOfSeveralBatches_isTrue() {
        // arrange
        final String x = text('x', 600);
        final List<String> rest = Arrays.asList(text('y', 600), "z", "w");

        // act
        final ReduceRound round = ReduceRound.of(Arrays.asList(x, rest.get(0), rest.get(1), rest.get(2)), BUDGET);

        // assert
        assertThat(round.batches(), is(Arrays.asList(Collections.singletonList(x), rest)));
        assertThat(round.parts(), is(true));
        assertThat(round.passesOn(Collections.singletonList(x)), is(true));
        assertThat(round.passesOn(rest), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="batches">
    @Test
    public void batches_addCalled_throwsException() {
        // arrange
        final ReduceRound round = ReduceRound.of(Arrays.asList("a", "b"), BUDGET);
        final List<List<String>> batches = round.batches();

        // act, assert
        assertThrows(UnsupportedOperationException.class, () -> batches.add(Collections.<String>emptyList()));
    }
    // </editor-fold>
}
