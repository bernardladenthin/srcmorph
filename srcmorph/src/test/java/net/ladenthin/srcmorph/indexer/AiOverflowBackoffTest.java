// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import net.ladenthin.srcmorph.provider.AiContextOverflowException;
import org.junit.jupiter.api.Test;

public class AiOverflowBackoffTest {

    // <editor-fold defaultstate="collapsed" desc="nextWindow">
    @Test
    public void nextWindow_isThreeQuartersOfTheLastOne() {
        assertThat(AiOverflowBackoff.nextWindow(0, 8_000), is(6_000));
        assertThat(AiOverflowBackoff.nextWindow(2, 6_000), is(4_500));
    }

    @Test
    public void nextWindow_notBelowTheMinimum() {
        assertThat(AiOverflowBackoff.nextWindow(0, 1_200), is(AiOverflowBackoff.MIN_WINDOW_CHARS));
    }

    @Test
    public void nextWindow_atTheMinimum_thereIsNoSmallerOne() {
        assertThat(
                AiOverflowBackoff.nextWindow(0, AiOverflowBackoff.MIN_WINDOW_CHARS),
                is(AiOverflowBackoff.NO_SMALLER_WINDOW));
        assertThat(AiOverflowBackoff.nextWindow(0, 500), is(AiOverflowBackoff.NO_SMALLER_WINDOW));
    }

    @Test
    public void nextWindow_afterTheLastRetry_thereIsNoSmallerOne() {
        assertThat(AiOverflowBackoff.nextWindow(AiOverflowBackoff.MAX_RETRIES - 1, 8_000), is(6_000));
        assertThat(
                AiOverflowBackoff.nextWindow(AiOverflowBackoff.MAX_RETRIES, 8_000),
                is(AiOverflowBackoff.NO_SMALLER_WINDOW));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isContextOverflow">
    @Test
    public void isContextOverflow_theLocalModelsMessage() {
        assertThat(
                AiOverflowBackoff.isContextOverflow(new IllegalStateException(
                        "request (9000 tokens) exceeds the available context size (8192 tokens)")),
                is(true));
    }

    @Test
    public void isContextOverflow_aRemoteOverflow() {
        assertThat(AiOverflowBackoff.isContextOverflow(new AiContextOverflowException("too long")), is(true));
    }

    @Test
    public void isContextOverflow_anywhereInTheCauseChain() {
        final RuntimeException wrapped = new RuntimeException(
                "generation failed", new IOException("x", new AiContextOverflowException("too long")));

        assertThat(AiOverflowBackoff.isContextOverflow(wrapped), is(true));
    }

    @Test
    public void isContextOverflow_otherErrors_andNone() {
        assertThat(AiOverflowBackoff.isContextOverflow(new IOException("connection reset")), is(false));
        assertThat(AiOverflowBackoff.isContextOverflow(new IOException((String) null)), is(false));
        assertThat(AiOverflowBackoff.isContextOverflow(null), is(false));
    }
    // </editor-fold>

    @Test
    public void nextWindow_ofAHugeWindow_doesNotOverflow() {
        assertThat(AiOverflowBackoff.nextWindow(0, 40_000_000), is(30_000_000));
    }
}
