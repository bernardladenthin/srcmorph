// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiProgressBarTest {

    @Test
    public void empty_zeroCompleted_isAllSpacesAndZeroPercent() {
        assertThat(AiProgressBar.render(0, 100, 10), is("[          ] 0%"));
    }

    @Test
    public void full_completedEqualsTotal_isAllHashesAndHundredPercent() {
        assertThat(AiProgressBar.render(100, 100, 10), is("[##########] 100%"));
    }

    @Test
    public void partial_roundsPercentAndFillCells() {
        // 42/100 -> 42%; round(0.42*10) = 4 filled cells, 6 empty.
        assertThat(AiProgressBar.render(42, 100, 10), is("[####      ] 42%"));
    }

    @Test
    public void partial_halfway() {
        // 50/100 -> 50%; round(0.5*10) = 5 filled cells.
        assertThat(AiProgressBar.render(50, 100, 10), is("[#####     ] 50%"));
    }

    @Test
    public void nonPositiveTotal_rendersFullBar() {
        // Nothing to do -> 100% rather than a divide-by-zero.
        assertThat(AiProgressBar.render(0, 0, 10), is("[##########] 100%"));
    }

    @Test
    public void completedAboveTotal_clampsToFull() {
        // Out-of-range high input clamps to 100% and a full bar (no overflow).
        assertThat(AiProgressBar.render(150, 100, 10), is("[##########] 100%"));
    }

    @Test
    public void negativeCompleted_clampsToEmpty() {
        // Out-of-range low input clamps to 0% and an empty bar.
        assertThat(AiProgressBar.render(-5, 100, 10), is("[          ] 0%"));
    }

    @Test
    public void negativeWidth_treatedAsZeroCells() {
        // No cells, but the percent is still computed.
        assertThat(AiProgressBar.render(50, 100, -3), is("[] 50%"));
    }

    @Test
    public void defaultWidthIsTwenty() {
        // The convenience overload uses DEFAULT_WIDTH (20) cells.
        final String bar = AiProgressBar.render(100, 100);
        assertThat(bar, is("[####################] 100%"));
        assertThat(AiProgressBar.DEFAULT_WIDTH, is(20));
    }
}
