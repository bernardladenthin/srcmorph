// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PreparedPromptTest {

    // <editor-fold defaultstate="collapsed" desc="accessors">
    @Test
    public void accessors_constructedWithValues_returnConstructorValues() {
        // arrange
        PreparedPrompt p = new PreparedPrompt("the prompt", "the source", true, 200, 150, 175);

        // act, assert
        assertThat(p.prompt(), is("the prompt"));
        assertThat(p.sourceText(), is("the source"));
        assertThat(p.trimmed(), is(true));
        assertThat(p.originalSourceLength(), is(200));
        assertThat(p.trimmedSourceLength(), is(150));
        // Distinct non-zero value kills the "return 0" primitive mutant on availableSourceChars().
        assertThat(p.availableSourceChars(), is(175));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="trimmed">
    @Test
    public void trimmed_untrimmedFlagGiven_isPreserved() {
        // arrange
        PreparedPrompt p = new PreparedPrompt("x", "y", false, 5, 5, 5);

        // act, assert
        assertThat(p.trimmed(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_nullPrompt_throwsException() {
        // act, assert
        assertThrows(NullPointerException.class, () -> new PreparedPrompt(null, "s", false, 0, 0, 0));
    }

    @Test
    public void constructor_nullSourceText_throwsException() {
        // act, assert
        assertThrows(NullPointerException.class, () -> new PreparedPrompt("p", null, false, 0, 0, 0));
    }
    // </editor-fold>
}
