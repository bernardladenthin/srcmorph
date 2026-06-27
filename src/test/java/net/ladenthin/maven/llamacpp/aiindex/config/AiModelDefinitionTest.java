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
}
