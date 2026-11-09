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

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_allValuesGiven_accessorsReturnWhatTheConstructorGot() {
        // act
        final RemoteModel model = new RemoteModel("x-ai/grok-4.7", 500000, new BigDecimal("2"), new BigDecimal("6"));

        // assert
        assertThat(model.id(), is("x-ai/grok-4.7"));
        assertThat(model.contextLength(), is(500000));
        assertThat(model.inputUsdPerMillion(), is(new BigDecimal("2")));
        assertThat(model.outputUsdPerMillion(), is(new BigDecimal("6")));
    }

    @Test
    public void constructor_nullPrices_pricesAreUnknown() {
        // act
        final RemoteModel model = new RemoteModel("auto", RemoteModel.UNKNOWN_CONTEXT_LENGTH, null, null);

        // assert
        assertThat(model.contextLength(), is(-1));
        assertThat(model.inputUsdPerMillion(), is(nullValue()));
        assertThat(model.outputUsdPerMillion(), is(nullValue()));
    }

    @Test
    public void constructor_nullId_throws() {
        // act, assert
        assertThrows(NullPointerException.class, () -> new RemoteModel(null, 0, null, null));
    }
    // </editor-fold>
}
