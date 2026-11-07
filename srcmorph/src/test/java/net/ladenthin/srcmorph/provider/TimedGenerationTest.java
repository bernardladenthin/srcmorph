// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class TimedGenerationTest {

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_allValuesGiven_accessorsReturnConstructorValues() {
        // act
        final TimedGeneration t = new TimedGeneration("summary", 1500, 512, 900.0d, 64, 45.0d);

        // assert
        assertThat(t.text(), is("summary"));
        assertThat(t.promptTokens(), is(1500));
        assertThat(t.cachedPromptTokens(), is(512));
        assertThat(t.prefillTokensPerSecond(), is(900.0d));
        assertThat(t.predictedTokens(), is(64));
        assertThat(t.decodeTokensPerSecond(), is(45.0d));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="totalPromptTokens">
    /**
     * The whole prompt is the evaluated tokens <em>plus</em> the cache-served ones. The two operands are
     * deliberately distinct and non-zero so a swapped, dropped or sign-flipped term produces a different
     * number rather than the same one by coincidence.
     */
    @Test
    public void totalPromptTokens_cacheServedPrefix_addsItToTheEvaluatedTokens() {
        // act, assert
        assertThat(new TimedGeneration("s", 1500, 512, 900.0d, 64, 45.0d).totalPromptTokens(), is(2012));
    }

    /** With nothing reused the total collapses onto the evaluated count -- the no-cache baseline. */
    @Test
    public void totalPromptTokens_withoutCacheReuse_equalsTheEvaluatedTokens() {
        // act, assert
        assertThat(new TimedGeneration("s", 1500, 0, 900.0d, 64, 45.0d).totalPromptTokens(), is(1500));
    }

    /**
     * A fully reused prompt evaluates nothing yet still has a size; reading {@code promptTokens()} alone
     * would report an empty prompt, which is exactly the confusion the split invites.
     */
    @Test
    public void totalPromptTokens_withAFullyCachedPrompt_isTheCachedCount() {
        // arrange
        final TimedGeneration fullyCached = new TimedGeneration("s", 0, 777, 900.0d, 64, 45.0d);

        // act, assert
        assertThat(fullyCached.promptTokens(), is(0));
        assertThat(fullyCached.totalPromptTokens(), is(777));
    }
    // </editor-fold>
}
