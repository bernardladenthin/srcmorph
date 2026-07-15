// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AiOversizeStrategyTest {

    @Test
    public void defaultIsFail() {
        assertThat(AiOversizeStrategy.DEFAULT, is(AiOversizeStrategy.FAIL));
    }

    @Test
    public void configValuesAreTheExpectedTokens() {
        assertThat(AiOversizeStrategy.FAIL.configValue(), is("fail"));
        assertThat(AiOversizeStrategy.SAMPLE.configValue(), is("sample"));
        assertThat(AiOversizeStrategy.MAP_REDUCE.configValue(), is("mapReduce"));
        assertThat(AiOversizeStrategy.DETERMINISTIC.configValue(), is("deterministic"));
    }

    @Test
    public void fromConfig_nullOrBlank_isDefault() {
        assertThat(AiOversizeStrategy.fromConfig(null), is(AiOversizeStrategy.FAIL));
        assertThat(AiOversizeStrategy.fromConfig(""), is(AiOversizeStrategy.FAIL));
        assertThat(AiOversizeStrategy.fromConfig("   "), is(AiOversizeStrategy.FAIL));
    }

    @Test
    public void fromConfig_eachToken_parses() {
        assertThat(AiOversizeStrategy.fromConfig("fail"), is(AiOversizeStrategy.FAIL));
        assertThat(AiOversizeStrategy.fromConfig("sample"), is(AiOversizeStrategy.SAMPLE));
        assertThat(AiOversizeStrategy.fromConfig("mapReduce"), is(AiOversizeStrategy.MAP_REDUCE));
        assertThat(AiOversizeStrategy.fromConfig("deterministic"), is(AiOversizeStrategy.DETERMINISTIC));
    }

    @Test
    public void fromConfig_isCaseInsensitiveAndTrimmed() {
        assertThat(AiOversizeStrategy.fromConfig("  MAPREDUCE  "), is(AiOversizeStrategy.MAP_REDUCE));
        assertThat(AiOversizeStrategy.fromConfig("Sample"), is(AiOversizeStrategy.SAMPLE));
    }

    @Test
    public void fromConfig_unknown_throwsWithValue() {
        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> AiOversizeStrategy.fromConfig("nonsense"));
        assertThat(ex.getMessage(), containsString("nonsense"));
    }
}
