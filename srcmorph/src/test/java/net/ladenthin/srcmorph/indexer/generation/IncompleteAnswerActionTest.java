// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.AiIncompleteStrategy;
import net.ladenthin.srcmorph.provider.AiIncompleteAnswerException;
import net.ladenthin.srcmorph.provider.AiIncompleteAnswerException.Reason;
import org.junit.jupiter.api.Test;

public class IncompleteAnswerActionTest {

    private static AiIncompleteAnswerException answer(final Reason reason, final String partialText) {
        return new AiIncompleteAnswerException(reason, "detail", partialText);
    }

    @Test
    public void of_aPartCutOffWithText_isKeptAsAPart() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), true, AiIncompleteStrategy.FAIL, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.KEEP_PART));
    }

    @Test
    public void of_aFinalAnswerCutOffWithText_underKeep_isKept() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), false, AiIncompleteStrategy.KEEP, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.KEEP));
    }

    @Test
    public void of_aFinalAnswerCutOffWithText_underRetryOnce_isAskedAgainOnceThenFails() {
        // arrange
        final AiIncompleteAnswerException cut = answer(Reason.TRUNCATED, "text");

        // act + assert
        assertThat(
                IncompleteAnswerAction.of(cut, false, AiIncompleteStrategy.RETRY_ONCE, 1),
                is(IncompleteAnswerAction.ASK_AGAIN));
        assertThat(
                IncompleteAnswerAction.of(cut, false, AiIncompleteStrategy.RETRY_ONCE, 2),
                is(IncompleteAnswerAction.FAIL));
    }

    @Test
    public void of_underFail_failsAtOnce() {
        // act
        final IncompleteAnswerAction action =
                IncompleteAnswerAction.of(answer(Reason.TRUNCATED, "text"), false, AiIncompleteStrategy.FAIL, 1);

        // assert
        assertThat(action, is(IncompleteAnswerAction.FAIL));
    }

    @Test
    public void of_aCutWithoutText_isNeverKept() {
        // arrange
        final AiIncompleteAnswerException blank = answer(Reason.TRUNCATED, "  \n");

        // act + assert
        assertThat(
                IncompleteAnswerAction.of(blank, true, AiIncompleteStrategy.KEEP, 1), is(IncompleteAnswerAction.FAIL));
        assertThat(
                IncompleteAnswerAction.of(blank, true, AiIncompleteStrategy.RETRY_ONCE, 1),
                is(IncompleteAnswerAction.ASK_AGAIN));
    }

    @Test
    public void of_anAnswerNotCutOff_isNeverKept_evenWithText() {
        // arrange
        final AiIncompleteAnswerException aborted = answer(Reason.ABORTED, "text");

        // act + assert
        assertThat(
                IncompleteAnswerAction.of(aborted, true, AiIncompleteStrategy.KEEP, 1),
                is(IncompleteAnswerAction.FAIL));
    }
}
