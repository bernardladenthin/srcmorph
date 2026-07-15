// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiConditionGroupTest {

    @Test
    public void conditionsDefaultNull() {
        assertThat(new AiConditionGroup().getConditions(), is(nullValue()));
    }

    @Test
    public void setConditionsRoundTrip() {
        final AiConditionGroup g = new AiConditionGroup();
        g.setConditions(java.util.Arrays.asList(new AiCondition(), new AiCondition()));
        assertThat(g.getConditions().size(), is(equalTo(2)));
    }

    @Test
    public void setConditionsDefensivelyCopied() {
        final AiConditionGroup g = new AiConditionGroup();
        final List<AiCondition> source = new ArrayList<>();
        source.add(new AiCondition());
        g.setConditions(source);
        source.add(new AiCondition()); // mutating the source must not affect the stored value
        assertThat(g.getConditions().size(), is(equalTo(1)));
    }

    @Test
    public void setConditionsNullClears() {
        final AiConditionGroup g = new AiConditionGroup();
        g.setConditions(java.util.Arrays.asList(new AiCondition()));
        g.setConditions(null);
        assertThat(g.getConditions(), is(nullValue()));
    }
}
