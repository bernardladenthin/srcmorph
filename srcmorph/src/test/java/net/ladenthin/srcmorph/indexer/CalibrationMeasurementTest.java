// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class CalibrationMeasurementTest {

    // <editor-fold defaultstate="collapsed" desc="accessors">
    @Test
    public void accessors_constructorValues_returnConstructorValues() {
        // act
        final CalibrationMeasurement m = new CalibrationMeasurement(3.5d, 900.0d, 45.0d, 4.2d, 1200.0d, 480);

        // assert
        assertThat(m.loadSeconds(), is(3.5d));
        assertThat(m.prefillTokensPerSecond(), is(900.0d));
        assertThat(m.decodeTokensPerSecond(), is(45.0d));
        assertThat(m.charsPerToken(), is(4.2d));
        assertThat(m.midPrefillTokensPerSecond(), is(1200.0d));
        assertThat(m.cachedPromptTokens(), is(480));
    }
    // </editor-fold>
}
