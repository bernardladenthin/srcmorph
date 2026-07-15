// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiCalibrationTest {

    @Test
    public void prefill_defaultsZeroAndRoundTrips() {
        final AiCalibration c = new AiCalibration();
        assertThat(c.getPrefillTokensPerSecond(), is(0.0d));
        c.setPrefillTokensPerSecond(1234.5d);
        assertThat(c.getPrefillTokensPerSecond(), is(1234.5d));
    }

    @Test
    public void decode_defaultsZeroAndRoundTrips() {
        final AiCalibration c = new AiCalibration();
        assertThat(c.getDecodeTokensPerSecond(), is(0.0d));
        c.setDecodeTokensPerSecond(45.6d);
        assertThat(c.getDecodeTokensPerSecond(), is(45.6d));
    }

    @Test
    public void charsPerToken_defaultsZeroAndRoundTrips() {
        final AiCalibration c = new AiCalibration();
        assertThat(c.getCharsPerToken(), is(0.0d));
        c.setCharsPerToken(3.7d);
        assertThat(c.getCharsPerToken(), is(3.7d));
    }

    @Test
    public void toString_includesTheRates() {
        final AiCalibration c = new AiCalibration();
        c.setPrefillTokensPerSecond(1000.0d);
        assertThat(c.toString(), containsString("1000.0"));
    }
}
