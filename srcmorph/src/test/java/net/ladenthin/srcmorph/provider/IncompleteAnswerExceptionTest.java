// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class IncompleteAnswerExceptionTest {

    @Test
    public void withoutText_carriesReasonMessageAndNoPartialText() {
        final IncompleteAnswerException e =
                new IncompleteAnswerException(IncompleteAnswerException.Reason.EMPTY, "nothing for A.java");

        assertThat(e.reason(), is(IncompleteAnswerException.Reason.EMPTY));
        assertThat(e.getMessage(), is("nothing for A.java"));
        assertThat(e.detail(), is("nothing for A.java"));
        assertThat(e.partialText(), is(""));
    }

    @Test
    public void withText_carriesWhatTheModelWrote() {
        final IncompleteAnswerException e =
                new IncompleteAnswerException(IncompleteAnswerException.Reason.TRUNCATED, "cut off", "half a summary");

        assertThat(e.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
        assertThat(e.partialText(), is("half a summary"));
    }

    @Test
    public void nullReasonOrPartialText_isRefused() {
        assertThrows(NullPointerException.class, () -> new IncompleteAnswerException(null, "m"));
        assertThrows(
                NullPointerException.class,
                () -> new IncompleteAnswerException(IncompleteAnswerException.Reason.TRUNCATED, "m", null));
    }
}
