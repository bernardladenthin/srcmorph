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

    @Test
    public void defaultIsFail() {
        assertThat(OversizeStrategy.DEFAULT, is(OversizeStrategy.FAIL));
    }

    @Test
    public void configValuesAreTheExpectedTokens() {
        assertThat(OversizeStrategy.FAIL.configValue(), is("fail"));
        assertThat(OversizeStrategy.SAMPLE.configValue(), is("sample"));
        assertThat(OversizeStrategy.MAP_REDUCE.configValue(), is("mapReduce"));
        assertThat(OversizeStrategy.SAMPLE_CHUNKS.configValue(), is("sampleChunks"));
        assertThat(OversizeStrategy.DETERMINISTIC.configValue(), is("deterministic"));
    }

    @Test
    public void fromConfig_nullOrBlank_isDefault() {
        assertThat(OversizeStrategy.fromConfig(null), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig(""), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig("   "), is(OversizeStrategy.FAIL));
    }

    @Test
    public void fromConfig_eachToken_parses() {
        assertThat(OversizeStrategy.fromConfig("fail"), is(OversizeStrategy.FAIL));
        assertThat(OversizeStrategy.fromConfig("sample"), is(OversizeStrategy.SAMPLE));
        assertThat(OversizeStrategy.fromConfig("mapReduce"), is(OversizeStrategy.MAP_REDUCE));
        assertThat(OversizeStrategy.fromConfig("sampleChunks"), is(OversizeStrategy.SAMPLE_CHUNKS));
        assertThat(OversizeStrategy.fromConfig("deterministic"), is(OversizeStrategy.DETERMINISTIC));
    }

    @Test
    public void fromConfig_isCaseInsensitiveAndTrimmed() {
        assertThat(OversizeStrategy.fromConfig("  MAPREDUCE  "), is(OversizeStrategy.MAP_REDUCE));
        assertThat(OversizeStrategy.fromConfig("Sample"), is(OversizeStrategy.SAMPLE));
    }

    @Test
    public void fromConfig_unknown_throwsWithValue() {
        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> OversizeStrategy.fromConfig("nonsense"));
        assertThat(ex.getMessage(), containsString("nonsense"));
    }
}
