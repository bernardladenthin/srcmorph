// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class RangeConditionTest {

    // <editor-fold defaultstate="collapsed" desc="minMax">
    @Test
    public void minMax_defaultAndSet_roundTrips() {
        // arrange
        final RangeCondition r = new RangeCondition();

        // pre-assert
        assertThat(r.getMin(), is(equalTo(0L)));
        assertThat(r.getMax(), is(equalTo(0L)));

        // act
        r.setMin(16384L);
        r.setMax(49152L);

        // assert
        assertThat(r.getMin(), is(equalTo(16384L)));
        assertThat(r.getMax(), is(equalTo(49152L)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="contains">
    @Test
    public void contains_minAndMax_minExclusiveMaxInclusive() {
        // arrange
        final RangeCondition r = new RangeCondition();
        r.setMin(1000L);
        r.setMax(2000L);

        // act, assert
        assertThat(r.contains(1000L), is(false)); // min exclusive
        assertThat(r.contains(1001L), is(true));
        assertThat(r.contains(2000L), is(true)); // max inclusive
        assertThat(r.contains(2001L), is(false));
    }

    @Test
    public void contains_noBound_isUnbounded() {
        // arrange
        final RangeCondition r = new RangeCondition();

        // act, assert
        assertThat(r.contains(0L), is(true));
        assertThat(r.contains(Long.MAX_VALUE), is(true));
    }

    @Test
    public void contains_onlyMin_isMinExclusive() {
        // arrange
        final RangeCondition r = new RangeCondition();
        r.setMin(1000L);

        // act, assert
        assertThat(r.contains(1000L), is(false));
        assertThat(r.contains(1001L), is(true));
    }

    @Test
    public void contains_onlyMax_isMaxInclusive() {
        // arrange
        final RangeCondition r = new RangeCondition();
        r.setMax(1000L);

        // act, assert
        assertThat(r.contains(1000L), is(true));
        assertThat(r.contains(1001L), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasBound">
    @Test
    public void hasBound_noneOrMinOrMax_isTrueOnlyWithABound() {
        // act, assert
        assertThat(new RangeCondition().hasBound(), is(false));
        final RangeCondition min = new RangeCondition();
        min.setMin(1L);
        assertThat(min.hasBound(), is(true));
        final RangeCondition max = new RangeCondition();
        max.setMax(1L);
        assertThat(max.hasBound(), is(true));
    }
    // </editor-fold>
}
