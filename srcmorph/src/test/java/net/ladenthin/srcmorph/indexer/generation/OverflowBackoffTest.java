// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import net.ladenthin.srcmorph.provider.ContextOverflowException;
import org.junit.jupiter.api.Test;

public class OverflowBackoffTest {

    // <editor-fold defaultstate="collapsed" desc="nextWindow">
    @Test
    public void nextWindow_aWindowAboveTheMinimum_isThreeQuartersOfTheLastOne() {
        // act, assert
        assertThat(OverflowBackoff.nextWindow(0, 8_000), is(6_000));
        assertThat(OverflowBackoff.nextWindow(2, 6_000), is(4_500));
    }

    @Test
    public void nextWindow_threeQuartersBelowTheMinimum_isNotBelowTheMinimum() {
        // act, assert
        assertThat(OverflowBackoff.nextWindow(0, 1_200), is(OverflowBackoff.MIN_WINDOW_CHARS));
    }

    @Test
    public void nextWindow_atTheMinimum_thereIsNoSmallerOne() {
        // act, assert
        assertThat(
                OverflowBackoff.nextWindow(0, OverflowBackoff.MIN_WINDOW_CHARS), is(OverflowBackoff.NO_SMALLER_WINDOW));
        assertThat(OverflowBackoff.nextWindow(0, 500), is(OverflowBackoff.NO_SMALLER_WINDOW));
    }

    @Test
    public void nextWindow_afterTheLastRetry_thereIsNoSmallerOne() {
        // act, assert
        assertThat(OverflowBackoff.nextWindow(OverflowBackoff.MAX_RETRIES - 1, 8_000), is(6_000));
        assertThat(
                OverflowBackoff.nextWindow(OverflowBackoff.MAX_RETRIES, 8_000), is(OverflowBackoff.NO_SMALLER_WINDOW));
    }

    @Test
    public void nextWindow_ofAHugeWindow_doesNotOverflow() {
        // act, assert
        assertThat(OverflowBackoff.nextWindow(0, 40_000_000), is(30_000_000));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isContextOverflow">
    @Test
    public void isContextOverflow_theLocalModelsMessage_isTrue() {
        // act, assert
        assertThat(
                OverflowBackoff.isContextOverflow(new IllegalStateException(
                        "request (9000 tokens) exceeds the available context size (8192 tokens)")),
                is(true));
    }

    @Test
    public void isContextOverflow_aRemoteOverflow_isTrue() {
        // act, assert
        assertThat(OverflowBackoff.isContextOverflow(new ContextOverflowException("too long")), is(true));
    }

    @Test
    public void isContextOverflow_anywhereInTheCauseChain_isTrue() {
        // arrange
        final RuntimeException wrapped = new RuntimeException(
                "generation failed", new IOException("x", new ContextOverflowException("too long")));

        // act, assert
        assertThat(OverflowBackoff.isContextOverflow(wrapped), is(true));
    }

    @Test
    public void isContextOverflow_otherErrorsAndNone_isFalse() {
        // act, assert
        assertThat(OverflowBackoff.isContextOverflow(new IOException("connection reset")), is(false));
        assertThat(OverflowBackoff.isContextOverflow(new IOException((String) null)), is(false));
        assertThat(OverflowBackoff.isContextOverflow(null), is(false));
    }
    // </editor-fold>
}
