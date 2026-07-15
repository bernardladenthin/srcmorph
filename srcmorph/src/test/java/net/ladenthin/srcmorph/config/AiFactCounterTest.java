// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiFactCounterTest {

    @Test
    public void label_defaultsNullAndRoundTrips() {
        final AiFactCounter c = new AiFactCounter();
        assertThat(c.getLabel(), is(nullValue()));
        c.setLabel("INSERT rows");
        assertThat(c.getLabel(), is(equalTo("INSERT rows")));
    }

    @Test
    public void pattern_defaultsNullAndRoundTrips() {
        final AiFactCounter c = new AiFactCounter();
        assertThat(c.getPattern(), is(nullValue()));
        c.setPattern("(?m)^INSERT");
        assertThat(c.getPattern(), is(equalTo("(?m)^INSERT")));
    }

    @Test
    public void toString_includesBothFields() {
        final AiFactCounter c = new AiFactCounter();
        c.setLabel("tables");
        c.setPattern("(?m)^CREATE TABLE");
        final String text = c.toString();
        assertThat(text, containsString("tables"));
        assertThat(text, containsString("(?m)^CREATE TABLE"));
    }
}
