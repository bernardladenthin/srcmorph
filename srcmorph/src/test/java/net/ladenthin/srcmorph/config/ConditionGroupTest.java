// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ConditionGroupTest {

    // <editor-fold defaultstate="collapsed" desc="getConditions">
    @Test
    public void getConditions_newInstance_returnsNull() {
        // act, assert
        assertThat(new ConditionGroup().getConditions(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setConditions">
    @Test
    public void setConditions_twoConditions_roundTrips() {
        // arrange
        final ConditionGroup g = new ConditionGroup();

        // act
        g.setConditions(Arrays.asList(new RuleCondition(), new RuleCondition()));

        // assert
        assertThat(g.getConditions().size(), is(equalTo(2)));
    }

    @Test
    public void setConditions_sourceMutatedAfterwards_isDefensivelyCopied() {
        // arrange
        final ConditionGroup g = new ConditionGroup();
        final List<RuleCondition> source = new ArrayList<>();
        source.add(new RuleCondition());

        // act
        g.setConditions(source);
        source.add(new RuleCondition()); // mutating the source must not affect the stored value

        // assert
        assertThat(g.getConditions().size(), is(equalTo(1)));
    }

    @Test
    public void setConditions_null_clearsTheConditions() {
        // arrange
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(Arrays.asList(new RuleCondition()));

        // act
        g.setConditions(null);

        // assert
        assertThat(g.getConditions(), is(nullValue()));
    }
    // </editor-fold>
}
