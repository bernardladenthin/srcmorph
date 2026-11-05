// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class IncompleteStrategyTest {

    // <editor-fold defaultstate="collapsed" desc="DEFAULT">
    @Test
    public void defaultStrategy_constant_isFail() {
        // act, assert
        assertThat(IncompleteStrategy.DEFAULT, is(IncompleteStrategy.FAIL));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="configValue">
    @Test
    public void configValue_eachConstant_isTheExpectedToken() {
        // act, assert
        assertThat(IncompleteStrategy.FAIL.configValue(), is("fail"));
        assertThat(IncompleteStrategy.RETRY_ONCE.configValue(), is("retryOnce"));
        assertThat(IncompleteStrategy.KEEP.configValue(), is("keep"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="attempts">
    @Test
    public void attempts_eachConstant_twoOnlyForRetryOnce() {
        // act, assert
        assertThat(IncompleteStrategy.FAIL.attempts(), is(1));
        assertThat(IncompleteStrategy.RETRY_ONCE.attempts(), is(2));
        assertThat(IncompleteStrategy.KEEP.attempts(), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fromConfig">
    @Test
    public void fromConfig_nullOrBlank_isDefault() {
        // act, assert
        assertThat(IncompleteStrategy.fromConfig(null), is(IncompleteStrategy.FAIL));
        assertThat(IncompleteStrategy.fromConfig(""), is(IncompleteStrategy.FAIL));
        assertThat(IncompleteStrategy.fromConfig("   "), is(IncompleteStrategy.FAIL));
    }

    @Test
    public void fromConfig_eachTokenInAnyCaseAndTrimmed_parses() {
        // act, assert
        assertThat(IncompleteStrategy.fromConfig("fail"), is(IncompleteStrategy.FAIL));
        assertThat(IncompleteStrategy.fromConfig(" RETRYONCE "), is(IncompleteStrategy.RETRY_ONCE));
        assertThat(IncompleteStrategy.fromConfig("Keep"), is(IncompleteStrategy.KEEP));
    }

    @Test
    public void fromConfig_unknown_isRefusedNamingTheValueAndTheChoices() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> IncompleteStrategy.fromConfig("retry"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("Unknown onIncomplete strategy: 'retry' (expected one of fail/retryOnce/keep)"));
    }
    // </editor-fold>
}
