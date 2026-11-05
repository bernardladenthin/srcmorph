// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class RuleConditionTest {

    @Test
    public void allFieldsDefaultNull() {
        final RuleCondition c = new RuleCondition();
        assertThat(c.getAnd(), is(nullValue()));
        assertThat(c.getOr(), is(nullValue()));
        assertThat(c.getNot(), is(nullValue()));
        assertThat(c.getExtensions(), is(nullValue()));
        assertThat(c.getSize(), is(nullValue()));
        assertThat(c.getLines(), is(nullValue()));
        assertThat(c.getModifiedAfter(), is(nullValue()));
        assertThat(c.getModifiedBefore(), is(nullValue()));
        assertThat(c.getPathGlob(), is(nullValue()));
    }

    @Test
    public void andOrNotRoundTrip() {
        final RuleCondition leaf = new RuleCondition();
        final RuleCondition c = new RuleCondition();
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(Arrays.asList(leaf));
        c.setAnd(g);
        assertThat(c.getAnd().getConditions().size(), is(equalTo(1)));
        c.setOr(g);
        assertThat(c.getOr().getConditions().size(), is(equalTo(1)));
        c.setNot(leaf);
        assertThat(c.getNot(), is(leaf));
        c.setAnd(null);
        assertThat(c.getAnd(), is(nullValue()));
    }

    @Test
    public void extensionsDefensivelyCopied() {
        final RuleCondition c = new RuleCondition();
        c.setExtensions(Arrays.asList(".java", ".kt"));
        assertThat(c.getExtensions(), hasItem(".java"));
        assertThat(c.getExtensions(), hasItem(".kt"));
        c.setExtensions(null);
        assertThat(c.getExtensions(), is(nullValue()));
    }

    @Test
    public void sizeLinesRoundTrip() {
        final RuleCondition c = new RuleCondition();
        final RangeCondition size = new RangeCondition();
        size.setMax(100L);
        c.setSize(size);
        assertThat(c.getSize().getMax(), is(equalTo(100L)));
        final RangeCondition lines = new RangeCondition();
        lines.setMin(5L);
        c.setLines(lines);
        assertThat(c.getLines().getMin(), is(equalTo(5L)));
    }

    @Test
    public void stringLeavesRoundTrip() {
        final RuleCondition c = new RuleCondition();
        c.setModifiedAfter("2026-01-01T00:00:00Z");
        c.setModifiedBefore("2027-01-01T00:00:00Z");
        c.setPathGlob("**/x/**");
        assertThat(c.getModifiedAfter(), is(equalTo("2026-01-01T00:00:00Z")));
        assertThat(c.getModifiedBefore(), is(equalTo("2027-01-01T00:00:00Z")));
        assertThat(c.getPathGlob(), is(equalTo("**/x/**")));
    }

    @Test
    public void fileNamesDefensivelyCopiedAndContentMatchesRoundTrips() {
        final RuleCondition c = new RuleCondition();
        assertThat(c.getFileNames(), is(nullValue()));
        assertThat(c.getContentMatches(), is(nullValue()));
        final java.util.List<String> names = new java.util.ArrayList<>(Arrays.asList("Dockerfile", "Jenkinsfile"));
        c.setFileNames(names);
        names.clear();
        assertThat(c.getFileNames(), is(equalTo(Arrays.asList("Dockerfile", "Jenkinsfile"))));
        c.setFileNames(null);
        assertThat(c.getFileNames(), is(nullValue()));
        c.setContentMatches("(?m)^apiVersion:");
        assertThat(c.getContentMatches(), is(equalTo("(?m)^apiVersion:")));
    }

    @Test
    public void notRoundTripAndClear() {
        final RuleCondition c = new RuleCondition();
        final RuleCondition child = new RuleCondition();
        c.setNot(child);
        assertThat(c.getNot(), is(child));
        c.setNot(null);
        assertThat(c.getNot(), is(nullValue()));
    }
}
