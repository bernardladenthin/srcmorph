// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AiIncompleteStrategyTest {

    @Test
    public void defaultIsFail() {
        assertThat(AiIncompleteStrategy.DEFAULT, is(AiIncompleteStrategy.FAIL));
    }

    @Test
    public void configValuesAreTheExpectedTokens() {
        assertThat(AiIncompleteStrategy.FAIL.configValue(), is("fail"));
        assertThat(AiIncompleteStrategy.RETRY_ONCE.configValue(), is("retryOnce"));
        assertThat(AiIncompleteStrategy.KEEP.configValue(), is("keep"));
    }

    @Test
    public void attempts_twoOnlyForRetryOnce() {
        assertThat(AiIncompleteStrategy.FAIL.attempts(), is(1));
        assertThat(AiIncompleteStrategy.RETRY_ONCE.attempts(), is(2));
        assertThat(AiIncompleteStrategy.KEEP.attempts(), is(1));
    }

    @Test
    public void fromConfig_nullOrBlank_isDefault() {
        assertThat(AiIncompleteStrategy.fromConfig(null), is(AiIncompleteStrategy.FAIL));
        assertThat(AiIncompleteStrategy.fromConfig(""), is(AiIncompleteStrategy.FAIL));
        assertThat(AiIncompleteStrategy.fromConfig("   "), is(AiIncompleteStrategy.FAIL));
    }

    @Test
    public void fromConfig_eachTokenInAnyCaseAndTrimmed_parses() {
        assertThat(AiIncompleteStrategy.fromConfig("fail"), is(AiIncompleteStrategy.FAIL));
        assertThat(AiIncompleteStrategy.fromConfig(" RETRYONCE "), is(AiIncompleteStrategy.RETRY_ONCE));
        assertThat(AiIncompleteStrategy.fromConfig("Keep"), is(AiIncompleteStrategy.KEEP));
    }

    @Test
    public void fromConfig_unknown_isRefusedNamingTheValueAndTheChoices() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> AiIncompleteStrategy.fromConfig("retry"));
        assertThat(
                thrown.getMessage(),
                is("Unknown onIncomplete strategy: 'retry' (expected one of fail/retryOnce/keep)"));
    }
}
