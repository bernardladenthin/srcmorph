// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiRangeConditionTest {

    @Test
    public void minMaxRoundTrip() {
        final AiRangeCondition r = new AiRangeCondition();
        assertThat(r.getMin(), is(equalTo(0L)));
        assertThat(r.getMax(), is(equalTo(0L)));
        r.setMin(16384L);
        r.setMax(49152L);
        assertThat(r.getMin(), is(equalTo(16384L)));
        assertThat(r.getMax(), is(equalTo(49152L)));
    }

    @Test
    public void contains_minExclusiveMaxInclusive() {
        final AiRangeCondition r = new AiRangeCondition();
        r.setMin(1000L);
        r.setMax(2000L);
        assertThat(r.contains(1000L), is(false)); // min exclusive
        assertThat(r.contains(1001L), is(true));
        assertThat(r.contains(2000L), is(true)); // max inclusive
        assertThat(r.contains(2001L), is(false));
    }

    @Test
    public void contains_unbounded() {
        final AiRangeCondition r = new AiRangeCondition();
        assertThat(r.contains(0L), is(true));
        assertThat(r.contains(Long.MAX_VALUE), is(true));
    }

    @Test
    public void contains_onlyMin() {
        final AiRangeCondition r = new AiRangeCondition();
        r.setMin(1000L);
        assertThat(r.contains(1000L), is(false));
        assertThat(r.contains(1001L), is(true));
    }

    @Test
    public void contains_onlyMax() {
        final AiRangeCondition r = new AiRangeCondition();
        r.setMax(1000L);
        assertThat(r.contains(1000L), is(true));
        assertThat(r.contains(1001L), is(false));
    }

    @Test
    public void hasBound() {
        assertThat(new AiRangeCondition().hasBound(), is(false));
        final AiRangeCondition min = new AiRangeCondition();
        min.setMin(1L);
        assertThat(min.hasBound(), is(true));
        final AiRangeCondition max = new AiRangeCondition();
        max.setMax(1L);
        assertThat(max.hasBound(), is(true));
    }
}
