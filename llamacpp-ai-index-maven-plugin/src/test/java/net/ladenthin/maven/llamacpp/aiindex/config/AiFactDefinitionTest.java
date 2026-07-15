// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiFactDefinitionTest {

    @Test
    public void key_defaultsNullAndRoundTrips() {
        final AiFactDefinition d = new AiFactDefinition();
        assertThat(d.getKey(), is(nullValue()));
        d.setKey("java-facts");
        assertThat(d.getKey(), is(equalTo("java-facts")));
    }

    @Test
    public void facts_defaultsNullAndRoundTrips() {
        final AiFactDefinition d = new AiFactDefinition();
        assertThat(d.getFacts(), is(nullValue()));
        d.setFacts(Collections.singletonList(new AiFactCounter()));
        assertThat(d.getFacts(), is(notNullValue()));
        assertThat(d.getFacts().size(), is(equalTo(1)));
    }

    @Test
    public void toString_includesKey() {
        final AiFactDefinition d = new AiFactDefinition();
        d.setKey("sql-facts");
        assertThat(d.toString(), containsString("sql-facts"));
    }
}
