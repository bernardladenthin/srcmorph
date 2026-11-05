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

public class FactCounterTest {

    // <editor-fold defaultstate="collapsed" desc="label">
    @Test
    public void label_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final FactCounter c = new FactCounter();

        // pre-assert
        assertThat(c.getLabel(), is(nullValue()));

        // act
        c.setLabel("INSERT statements");

        // assert
        assertThat(c.getLabel(), is(equalTo("INSERT statements")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="pattern">
    @Test
    public void pattern_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final FactCounter c = new FactCounter();

        // pre-assert
        assertThat(c.getPattern(), is(nullValue()));

        // act
        c.setPattern("(?m)^INSERT");

        // assert
        assertThat(c.getPattern(), is(equalTo("(?m)^INSERT")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ignore">
    @Test
    public void ignore_defaultAndSet_defaultsFalseAndRoundTrips() {
        // arrange
        final FactCounter c = new FactCounter();

        // pre-assert
        assertThat(c.isIgnore(), is(false));

        // act
        c.setIgnore(true);

        // assert
        assertThat(c.isIgnore(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_labelAndPatternSet_includesBothFields() {
        // arrange
        final FactCounter c = new FactCounter();
        c.setLabel("tables");
        c.setPattern("(?m)^CREATE TABLE");

        // act
        final String text = c.toString();

        // assert
        assertThat(text, containsString("tables"));
        assertThat(text, containsString("(?m)^CREATE TABLE"));
        assertThat(text, containsString("ignore=false"));
    }
    // </editor-fold>
}
