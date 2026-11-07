// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ProgressBarTest {

    // <editor-fold defaultstate="collapsed" desc="render">
    @Test
    public void render_zeroCompleted_isAllSpacesAndZeroPercent() {
        // act, assert
        assertThat(ProgressBar.render(0, 100, 10), is("[          ] 0%"));
    }

    @Test
    public void render_completedEqualsTotal_isAllHashesAndHundredPercent() {
        // act, assert
        assertThat(ProgressBar.render(100, 100, 10), is("[##########] 100%"));
    }

    @Test
    public void render_partialProgress_roundsPercentAndFillCells() {
        // 42/100 -> 42%; round(0.42*10) = 4 filled cells, 6 empty.
        // act, assert
        assertThat(ProgressBar.render(42, 100, 10), is("[####      ] 42%"));
    }

    @Test
    public void render_halfway_fillsHalfTheCells() {
        // 50/100 -> 50%; round(0.5*10) = 5 filled cells.
        // act, assert
        assertThat(ProgressBar.render(50, 100, 10), is("[#####     ] 50%"));
    }

    @Test
    public void render_nonPositiveTotal_rendersFullBar() {
        // Nothing to do -> 100% rather than a divide-by-zero.
        // act, assert
        assertThat(ProgressBar.render(0, 0, 10), is("[##########] 100%"));
    }

    @Test
    public void render_completedAboveTotal_clampsToFull() {
        // Out-of-range high input clamps to 100% and a full bar (no overflow).
        // act, assert
        assertThat(ProgressBar.render(150, 100, 10), is("[##########] 100%"));
    }

    @Test
    public void render_negativeCompleted_clampsToEmpty() {
        // Out-of-range low input clamps to 0% and an empty bar.
        // act, assert
        assertThat(ProgressBar.render(-5, 100, 10), is("[          ] 0%"));
    }

    @Test
    public void render_negativeWidth_treatedAsZeroCells() {
        // No cells, but the percent is still computed.
        // act, assert
        assertThat(ProgressBar.render(50, 100, -3), is("[] 50%"));
    }

    @Test
    public void render_defaultWidth_isTwenty() {
        // act
        // The convenience overload uses DEFAULT_WIDTH (20) cells.
        final String bar = ProgressBar.render(100, 100);

        // assert
        assertThat(bar, is("[####################] 100%"));
        assertThat(ProgressBar.DEFAULT_WIDTH, is(20));
    }
    // </editor-fold>
}
