// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiTokenWindowTest {

    // <editor-fold defaultstate="collapsed" desc="promptTokenLimit">
    @Test
    public void promptTokenLimit_isTheWindowLessTheAnswer() {
        // the window less the answer and the tokens a count leaves out
        assertThat(AiTokenWindow.promptTokenLimit(16_384, 1_536), is(16_384 - 1_536 - AiTokenWindow.UNCOUNTED_TOKENS));
    }

    @Test
    public void promptTokenLimit_ofAnUnknownWindow_isNoLimit() {
        assertThat(AiTokenWindow.promptTokenLimit(0, 64), is(AiTokenWindow.NO_LIMIT));
        assertThat(AiTokenWindow.promptTokenLimit(-1, 64), is(AiTokenWindow.NO_LIMIT));
    }

    @Test
    public void promptTokenLimit_isAtLeastOneToken() {
        assertThat(AiTokenWindow.promptTokenLimit(64, 64), is(1));
        assertThat(AiTokenWindow.promptTokenLimit(64, 100), is(1));
        assertThat(AiTokenWindow.promptTokenLimit(1, 0), is(1));
    }

    @Test
    public void promptTokenLimit_aNegativeAnswerBudget_takesNothingFromTheWindow() {
        assertThat(AiTokenWindow.promptTokenLimit(100, -5), is(100 - AiTokenWindow.UNCOUNTED_TOKENS));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="measuredWindow">
    @Test
    public void measuredWindow_isTheLimitTimesTheCharactersPerToken() {
        // 30 000 chars in 12 000 tokens is 2.5 chars per token: 8 000 tokens hold 20 000 chars
        assertThat(AiTokenWindow.measuredWindow(8_000, 30_000, 12_000), is(20_000));
    }

    @Test
    public void measuredWindow_roundsDown() {
        assertThat(AiTokenWindow.measuredWindow(10, 7, 3), is(23));
    }

    @Test
    public void measuredWindow_ofAFittingPrompt_isAtLeastThePrompt() {
        assertThat(AiTokenWindow.measuredWindow(1_000, 4_000, 1_000), is(4_000));
    }

    @Test
    public void measuredWindow_doesNotOverflow() {
        assertThat(AiTokenWindow.measuredWindow(1_000_000, 2_000_000_000, 1), is(Integer.MAX_VALUE));
        assertThat(AiTokenWindow.measuredWindow(100_000, 1_600_000, 600_000), is(266_666));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="smallerWindow">
    @Test
    public void smallerWindow_isTheMeasuredWindowLessTheMargin() {
        // the measured window of 20 000 chars, 2 % below it
        assertThat(AiTokenWindow.smallerWindow(30_000, 8_000, 30_000, 12_000), is(19_600));
    }

    @Test
    public void smallerWindow_isAlwaysBelowTheWindow() {
        // a measured window far above the window still shrinks it by one
        assertThat(AiTokenWindow.smallerWindow(100, 1_000, 100, 10), is(99));
    }
    // </editor-fold>
}
