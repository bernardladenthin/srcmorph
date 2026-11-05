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

public class ConditionGroupTest {

    @Test
    public void conditionsDefaultNull() {
        assertThat(new ConditionGroup().getConditions(), is(nullValue()));
    }

    @Test
    public void setConditionsRoundTrip() {
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(java.util.Arrays.asList(new RuleCondition(), new RuleCondition()));
        assertThat(g.getConditions().size(), is(equalTo(2)));
    }

    @Test
    public void setConditionsDefensivelyCopied() {
        final ConditionGroup g = new ConditionGroup();
        final List<RuleCondition> source = new ArrayList<>();
        source.add(new RuleCondition());
        g.setConditions(source);
        source.add(new RuleCondition()); // mutating the source must not affect the stored value
        assertThat(g.getConditions().size(), is(equalTo(1)));
    }

    @Test
    public void setConditionsNullClears() {
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(java.util.Arrays.asList(new RuleCondition()));
        g.setConditions(null);
        assertThat(g.getConditions(), is(nullValue()));
    }
}
