// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class RemoteModelTest {

    @Test
    public void accessors_returnWhatTheConstructorGot() {
        final RemoteModel model = new RemoteModel("x-ai/grok-4.7", 500000, new BigDecimal("2"), new BigDecimal("6"));

        assertThat(model.id(), is("x-ai/grok-4.7"));
        assertThat(model.contextLength(), is(500000));
        assertThat(model.inputUsdPerMillion(), is(new BigDecimal("2")));
        assertThat(model.outputUsdPerMillion(), is(new BigDecimal("6")));
    }

    @Test
    public void prices_mayBeUnknown() {
        final RemoteModel model = new RemoteModel("auto", RemoteModel.UNKNOWN_CONTEXT_LENGTH, null, null);

        assertThat(model.contextLength(), is(0));
        assertThat(model.inputUsdPerMillion(), is(nullValue()));
        assertThat(model.outputUsdPerMillion(), is(nullValue()));
    }

    @Test
    public void constructor_nullId_throws() {
        assertThrows(NullPointerException.class, () -> new RemoteModel(null, 0, null, null));
    }
}
