// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.IncompleteStrategy;
import net.ladenthin.srcmorph.provider.IncompleteAnswerException;
import net.ladenthin.srcmorph.provider.IncompleteAnswerException.Reason;
import org.junit.jupiter.api.Test;

public class IncompleteAnswerActionTest {

    private static IncompleteAnswerException answer(final Reason reason, final String partialText) {
        return new IncompleteAnswerException(reason, "detail", partialText);
    }

    // <editor-fold defaultstate="collapsed" desc="of">
    @Test
    public void of_aPartCutOffWithText_isKeptAsAPart() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), true, IncompleteStrategy.FAIL, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.KEEP_PART));
    }

    @Test
    public void of_aFinalAnswerCutOffWithTextUnderKeep_isKept() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), false, IncompleteStrategy.KEEP, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.KEEP));
    }

    @Test
    public void of_aFinalAnswerCutOffWithTextUnderRetryOnce_isAskedAgainOnceThenFails() {
        // arrange
        final IncompleteAnswerException cut = answer(Reason.TRUNCATED, "text");

        // act, assert
        assertThat(
                IncompleteAnswerAction.of(cut, false, IncompleteStrategy.RETRY_ONCE, 1),
                is(IncompleteAnswerAction.ASK_AGAIN));
        assertThat(
                IncompleteAnswerAction.of(cut, false, IncompleteStrategy.RETRY_ONCE, 2),
                is(IncompleteAnswerAction.FAIL));
    }

    @Test
    public void of_underFail_failsAtOnce() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), false, IncompleteStrategy.FAIL, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.FAIL));
    }

    @Test
    public void of_aCutWithoutText_isNeverKept() {
        // arrange
        final IncompleteAnswerException blank = answer(Reason.TRUNCATED, "  \n");

        // act, assert
        assertThat(IncompleteAnswerAction.of(blank, true, IncompleteStrategy.KEEP, 1), is(IncompleteAnswerAction.FAIL));
        assertThat(
                IncompleteAnswerAction.of(blank, true, IncompleteStrategy.RETRY_ONCE, 1),
                is(IncompleteAnswerAction.ASK_AGAIN));
    }

    @Test
    public void of_anAnswerNotCutOffEvenWithText_isNeverKept() {
        // arrange
        final IncompleteAnswerException aborted = answer(Reason.ABORTED, "text");

        // act, assert
        assertThat(
                IncompleteAnswerAction.of(aborted, true, IncompleteStrategy.KEEP, 1), is(IncompleteAnswerAction.FAIL));
    }
    // </editor-fold>
}
