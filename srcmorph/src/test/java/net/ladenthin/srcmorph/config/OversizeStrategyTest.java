// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class OversizeStrategyTest {

    // <editor-fold defaultstate="collapsed" desc="DEFAULT">
    @Test
    public void defaultStrategy_constant_isFail() {
        // act, assert
        assertThat(OversizeStrategy.DEFAULT, is(OversizeStrategy.FAIL));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="chunks">
    @Test
    public void chunks_eachConstant_isTrueForTheChunkingStrategiesOnly() {
        // act, assert
        assertThat(OversizeStrategy.MAP_REDUCE.chunks(), is(true));
        assertThat(OversizeStrategy.SAMPLE_CHUNKS.chunks(), is(true));
        assertThat(OversizeStrategy.FAIL.chunks(), is(false));
        assertThat(OversizeStrategy.SAMPLE.chunks(), is(false));
        assertThat(OversizeStrategy.DETERMINISTIC.chunks(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="configValue">
    @Test
    public void configValue_eachConstant_isTheExpectedToken() {
        // act, assert
        assertThat(OversizeStrategy.FAIL.configValue(), is("fail"));
        assertThat(OversizeStrategy.SAMPLE.configValue(), is("sample"));
        assertThat(OversizeStrategy.MAP_REDUCE.configValue(), is("mapReduce"));
        assertThat(OversizeStrategy.SAMPLE_CHUNKS.configValue(), is("sampleChunks"));
        assertThat(OversizeStrategy.DETERMINISTIC.configValue(), is("deterministic"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fromConfig">
    @Test
    public void fromConfig_nullOrBlank_isDefault() {
        // act, assert
        assertThat(OversizeStrategy.fromConfig(null), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig(""), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig("   "), is(OversizeStrategy.FAIL));
    }

    @Test
    public void fromConfig_eachToken_parses() {
        // act, assert
        assertThat(OversizeStrategy.fromConfig("fail"), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig("sample"), is(OversizeStrategy.SAMPLE));
        assertThat(OversizeStrategy.fromConfig("mapReduce"), is(OversizeStrategy.MAP_REDUCE));
        assertThat(OversizeStrategy.fromConfig("sampleChunks"), is(OversizeStrategy.SAMPLE_CHUNKS));
        assertThat(OversizeStrategy.fromConfig("deterministic"), is(OversizeStrategy.DETERMINISTIC));
    }

    @Test
    public void fromConfig_mixedCaseAndPadded_isCaseInsensitiveAndTrimmed() {
        // act, assert
        assertThat(OversizeStrategy.fromConfig("  MAPREDUCE  "), is(OversizeStrategy.MAP_REDUCE));
        assertThat(OversizeStrategy.fromConfig("Sample"), is(OversizeStrategy.SAMPLE));
    }

    @Test
    public void fromConfig_unknown_throwsWithValue() {
        // act
        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> OversizeStrategy.fromConfig("nonsense"));

        // assert
        assertThat(ex.getMessage(), containsString("nonsense"));
    }
    // </editor-fold>
}
