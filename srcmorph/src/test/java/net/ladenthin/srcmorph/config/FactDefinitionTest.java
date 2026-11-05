// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

public class FactDefinitionTest {

    // <editor-fold defaultstate="collapsed" desc="key">
    @Test
    public void key_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final FactDefinition d = new FactDefinition();

        // pre-assert
        assertThat(d.getKey(), is(nullValue()));

        // act
        d.setKey("java-facts");

        // assert
        assertThat(d.getKey(), is(equalTo("java-facts")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="facts">
    @Test
    public void facts_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final FactDefinition d = new FactDefinition();

        // pre-assert
        assertThat(d.getFacts(), is(nullValue()));

        // act
        d.setFacts(Collections.singletonList(new FactCounter()));

        // assert
        assertThat(d.getFacts(), is(notNullValue()));
        assertThat(d.getFacts().size(), is(equalTo(1)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factsFile">
    @Test
    public void factsFile_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final FactDefinition d = new FactDefinition();

        // pre-assert
        assertThat(d.getFactsFile(), is(nullValue()));

        // act
        d.setFactsFile("v2/sql-facts.tsv");

        // assert
        assertThat(d.getFactsFile(), is(equalTo("v2/sql-facts.tsv")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_keySet_includesKey() {
        // arrange
        final FactDefinition d = new FactDefinition();
        d.setKey("sql-facts");

        // act, assert
        assertThat(d.toString(), containsString("sql-facts"));
    }
    // </editor-fold>
}
