// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class GenerateResultTest {

    @Test
    public void constructor_threadsEveryFieldThrough() {
        final GenerateResult result = new GenerateResult(false, 3, 5, 7);

        assertThat(result.planOnly(), is(false));
        assertThat(result.written(), is(3));
        assertThat(result.unchanged(), is(5));
        assertThat(result.skipped(), is(7));
    }

    @Test
    public void planned_isPlanOnlyWithEveryCountAtZero() {
        final GenerateResult result = GenerateResult.planned();

        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(result.unchanged(), is(0));
        assertThat(result.skipped(), is(0));
    }

    @Test
    public void equalsAndHashCode_reflectAllFields() {
        final GenerateResult a = new GenerateResult(false, 1, 2, 3);
        final GenerateResult b = new GenerateResult(false, 1, 2, 3);
        final GenerateResult differentWritten = new GenerateResult(false, 9, 2, 3);
        final GenerateResult differentPlanOnly = new GenerateResult(true, 1, 2, 3);

        assertThat(a, is(equalTo(b)));
        assertThat(a.hashCode(), is(equalTo(b.hashCode())));
        assertThat(a, org.hamcrest.CoreMatchers.not(equalTo(differentWritten)));
        assertThat(a, org.hamcrest.CoreMatchers.not(equalTo(differentPlanOnly)));
    }

    @Test
    public void toString_containsDistinguishingCounts() {
        final GenerateResult result = new GenerateResult(false, 11, 22, 33);
        assertThat(result.toString(), org.hamcrest.CoreMatchers.containsString("11"));
        assertThat(result.toString(), org.hamcrest.CoreMatchers.containsString("22"));
        assertThat(result.toString(), org.hamcrest.CoreMatchers.containsString("33"));
    }
}
