// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiGenerationConfigTest {

    @Test
    public void samplingScalarGettersReflectSetters() {
        AiGenerationConfig c = new AiGenerationConfig();
        c.setTopP(0.73f);
        c.setTopK(42);
        c.setRepeatPenalty(1.27f);
        // Distinct non-zero values kill the "return 0" primitive mutants on these getters.
        assertThat(c.getTopP(), is(0.73f));
        assertThat(c.getTopK(), is(42));
        assertThat(c.getRepeatPenalty(), is(1.27f));
    }

    @Test
    public void stopStringsEmptyByDefault() {
        // The field defaults to an empty (non-null) list. Asserting non-null/empty kills the
        // negate mutant on the getStopStrings null-guard (which would return null instead).
        assertThat(new AiGenerationConfig().getStopStrings(), is(Collections.<String>emptyList()));
    }

    @Test
    public void stopStringsRoundTripUnmodifiable() {
        AiGenerationConfig c = new AiGenerationConfig();
        c.setStopStrings(Arrays.asList("</s>", "STOP"));
        assertThat(c.getStopStrings(), hasItem("</s>"));
        assertThat(c.getStopStrings(), hasItem("STOP"));
        assertThrows(
                UnsupportedOperationException.class, () -> c.getStopStrings().add("x"));
    }

    @Test
    public void setStopStringsNullResetsToEmptyList() {
        AiGenerationConfig c = new AiGenerationConfig();
        c.setStopStrings(Collections.singletonList("a"));
        c.setStopStrings(null);
        // null arg resets to an EMPTY (non-null) list — kills the negate mutant on the setter ternary.
        assertThat(c.getStopStrings(), is(Collections.<String>emptyList()));
    }

    @Test
    public void cachePromptDefaultsTrueAndTogglesFalse() {
        AiGenerationConfig c = new AiGenerationConfig();
        // Default true kills the false-default / "return false" getter mutants.
        assertThat(c.isCachePrompt(), is(true));
        c.setCachePrompt(false);
        // Observing false after the setter kills the "return true" getter mutant and the
        // removed-assignment setter mutant.
        assertThat(c.isCachePrompt(), is(false));
    }

    @Test
    public void reasoningEffortDefaultsLowAndRoundTrips() {
        AiGenerationConfig c = new AiGenerationConfig();
        // Default "low" kills the empty/null return mutants on the getter.
        assertThat(c.getReasoningEffort(), is("low"));
        c.setReasoningEffort("high");
        // Round-tripped value kills the removed-assignment setter mutant.
        assertThat(c.getReasoningEffort(), is("high"));
    }
}
