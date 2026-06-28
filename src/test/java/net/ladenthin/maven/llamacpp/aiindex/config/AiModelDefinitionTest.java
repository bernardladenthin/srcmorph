// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AiModelDefinitionTest {

    @Test
    public void samplingScalarGettersReflectSetters() {
        AiModelDefinition d = new AiModelDefinition();
        d.setTopP(0.66f);
        d.setTopK(33);
        d.setRepeatPenalty(1.4f);
        // Distinct non-zero values kill the "return 0" primitive mutants on these getters.
        assertThat(d.getTopP(), is(0.66f));
        assertThat(d.getTopK(), is(33));
        assertThat(d.getRepeatPenalty(), is(1.4f));
    }

    @Test
    public void stopStringsNullByDefault() {
        assertThat(new AiModelDefinition().getStopStrings(), is(nullValue()));
    }

    @Test
    public void stopStringsRoundTrip() {
        AiModelDefinition d = new AiModelDefinition();
        d.setStopStrings(Arrays.asList("</s>", "END"));
        // Asserting content kills both the empty-return mutant on the getter and the
        // negate mutant on the setter null-guard (which would null the field for a non-null arg).
        assertThat(d.getStopStrings(), hasItem("</s>"));
        assertThat(d.getStopStrings(), hasItem("END"));
    }

    @Test
    public void setStopStringsNullClearsToNull() {
        AiModelDefinition d = new AiModelDefinition();
        d.setStopStrings(Arrays.asList("a"));
        d.setStopStrings(null);
        assertThat(d.getStopStrings(), is(nullValue()));
    }

    @Test
    public void cachePromptDefaultsTrueAndTogglesFalse() {
        AiModelDefinition d = new AiModelDefinition();
        // Default true kills the false-default / "return false" getter mutants.
        assertThat(d.isCachePrompt(), is(true));
        d.setCachePrompt(false);
        // Observing false after the setter kills the "return true" getter mutant and the
        // removed-assignment setter mutant.
        assertThat(d.isCachePrompt(), is(false));
    }

    @Test
    public void reasoningEffortDefaultsLowAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        // Default "low" kills the empty/null return mutants on the getter.
        assertThat(d.getReasoningEffort(), is("low"));
        d.setReasoningEffort("medium");
        // Round-tripped value kills the removed-assignment setter mutant.
        assertThat(d.getReasoningEffort(), is("medium"));
    }

    @Test
    public void minPDefaultsDisabledAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        // Default 0.0 (disabled) kills the inline-constant / "return 1.0" getter mutants.
        assertThat(d.getMinP(), is(0.0f));
        d.setMinP(0.05f);
        // Round-tripped non-zero value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(d.getMinP(), is(0.05f));
    }

    @Test
    public void topNSigmaDefaultsDisabledAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        // Default -1.0 (disabled) kills the inline-constant / "return 0" getter mutants.
        assertThat(d.getTopNSigma(), is(-1.0f));
        d.setTopNSigma(1.5f);
        // Round-tripped value kills the getter "return 0" and removed-assignment setter mutants.
        assertThat(d.getTopNSigma(), is(1.5f));
    }

    @Test
    public void swaFullDefaultsFalseAndTogglesTrue() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.isSwaFull(), is(false));
        d.setSwaFull(true);
        assertThat(d.isSwaFull(), is(true));
    }

    @Test
    public void cacheReuseDefaultsZeroAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getCacheReuse(), is(0));
        d.setCacheReuse(256);
        assertThat(d.getCacheReuse(), is(256));
    }

    @Test
    public void reasoningBudgetTokensDefaultsUnrestrictedAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getReasoningBudgetTokens(), is(-1));
        d.setReasoningBudgetTokens(2048);
        assertThat(d.getReasoningBudgetTokens(), is(2048));
    }

    @Test
    public void dryMultiplierDefaultsDisabledAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getDryMultiplier(), is(0.0f));
        d.setDryMultiplier(0.8f);
        assertThat(d.getDryMultiplier(), is(0.8f));
    }

    @Test
    public void dryBaseDefaultsAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getDryBase(), is(1.75f));
        d.setDryBase(1.5f);
        assertThat(d.getDryBase(), is(1.5f));
    }

    @Test
    public void dryAllowedLengthDefaultsAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getDryAllowedLength(), is(2));
        d.setDryAllowedLength(5);
        assertThat(d.getDryAllowedLength(), is(5));
    }

    @Test
    public void dryPenaltyLastNDefaultsWholeContextAndRoundTrips() {
        AiModelDefinition d = new AiModelDefinition();
        assertThat(d.getDryPenaltyLastN(), is(-1));
        d.setDryPenaltyLastN(256);
        assertThat(d.getDryPenaltyLastN(), is(256));
    }

    @Test
    public void drySequenceBreakersNullByDefault() {
        assertThat(new AiModelDefinition().getDrySequenceBreakers(), is(nullValue()));
    }

    @Test
    public void drySequenceBreakersRoundTrip() {
        AiModelDefinition d = new AiModelDefinition();
        d.setDrySequenceBreakers(Arrays.asList("\n", ":"));
        assertThat(d.getDrySequenceBreakers(), hasItem("\n"));
        assertThat(d.getDrySequenceBreakers(), hasItem(":"));
    }

    @Test
    public void setDrySequenceBreakersNullClearsToNull() {
        AiModelDefinition d = new AiModelDefinition();
        d.setDrySequenceBreakers(Arrays.asList("a"));
        d.setDrySequenceBreakers(null);
        assertThat(d.getDrySequenceBreakers(), is(nullValue()));
    }
}
