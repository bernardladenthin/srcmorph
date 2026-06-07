// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.prompt;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AiPreparedPromptTest {

    @Test
    public void accessorsReturnConstructorValues() {
        AiPreparedPrompt p = new AiPreparedPrompt("the prompt", "the source", true, 200, 150, 175);
        assertThat(p.prompt(), is("the prompt"));
        assertThat(p.sourceText(), is("the source"));
        assertThat(p.trimmed(), is(true));
        assertThat(p.originalSourceLength(), is(200));
        assertThat(p.trimmedSourceLength(), is(150));
        // Distinct non-zero value kills the "return 0" primitive mutant on availableSourceChars().
        assertThat(p.availableSourceChars(), is(175));
    }

    @Test
    public void untrimmedFlagIsPreserved() {
        AiPreparedPrompt p = new AiPreparedPrompt("x", "y", false, 5, 5, 5);
        assertThat(p.trimmed(), is(false));
    }

    @Test
    public void nullPromptRejected() {
        assertThrows(NullPointerException.class, () -> new AiPreparedPrompt(null, "s", false, 0, 0, 0));
    }

    @Test
    public void nullSourceTextRejected() {
        assertThrows(NullPointerException.class, () -> new AiPreparedPrompt("p", null, false, 0, 0, 0));
    }
}
