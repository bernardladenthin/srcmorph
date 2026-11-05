// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class CalibrationTest {

    // <editor-fold defaultstate="collapsed" desc="prefillTokensPerSecond">
    @Test
    public void prefillTokensPerSecond_defaultAndSet_defaultsZeroAndRoundTrips() {
        // arrange
        final Calibration c = new Calibration();

        // pre-assert
        assertThat(c.getPrefillTokensPerSecond(), is(0.0d));

        // act
        c.setPrefillTokensPerSecond(1234.5d);

        // assert
        assertThat(c.getPrefillTokensPerSecond(), is(1234.5d));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="decodeTokensPerSecond">
    @Test
    public void decodeTokensPerSecond_defaultAndSet_defaultsZeroAndRoundTrips() {
        // arrange
        final Calibration c = new Calibration();

        // pre-assert
        assertThat(c.getDecodeTokensPerSecond(), is(0.0d));

        // act
        c.setDecodeTokensPerSecond(45.6d);

        // assert
        assertThat(c.getDecodeTokensPerSecond(), is(45.6d));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="charsPerToken">
    @Test
    public void charsPerToken_defaultAndSet_defaultsZeroAndRoundTrips() {
        // arrange
        final Calibration c = new Calibration();

        // pre-assert
        assertThat(c.getCharsPerToken(), is(0.0d));

        // act
        c.setCharsPerToken(3.7d);

        // assert
        assertThat(c.getCharsPerToken(), is(3.7d));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_prefillRateSet_includesTheRates() {
        // arrange
        final Calibration c = new Calibration();
        c.setPrefillTokensPerSecond(1000.0d);

        // act, assert
        assertThat(c.toString(), containsString("1000.0"));
    }
    // </editor-fold>
}
