// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RuleConditionTest {

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_noArguments_allFieldsDefaultNull() {
        // arrange
        final RuleCondition c = new RuleCondition();

        // act, assert
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="andOrNot">
    @Test
    public void andOrNot_setAndCleared_roundTrip() {
        // arrange
        final RuleCondition leaf = new RuleCondition();
        final RuleCondition c = new RuleCondition();
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(Arrays.asList(leaf));

        // act, assert
        c.setAnd(g);
        assertThat(c.getAnd().getConditions().size(), is(equalTo(1)));
        c.setOr(g);
        assertThat(c.getOr().getConditions().size(), is(equalTo(1)));
        c.setNot(leaf);
        assertThat(c.getNot(), is(leaf));
        c.setAnd(null);
        assertThat(c.getAnd(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setExtensions">
    @Test
    public void setExtensions_listThenNull_isDefensivelyCopied() {
        // arrange
        final RuleCondition c = new RuleCondition();

        // act, assert
        c.setExtensions(Arrays.asList(".java", ".kt"));
        assertThat(c.getExtensions(), hasItem(".java"));
        assertThat(c.getExtensions(), hasItem(".kt"));
        c.setExtensions(null);
        assertThat(c.getExtensions(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sizeAndLines">
    @Test
    public void sizeAndLines_set_roundTrip() {
        // arrange
        final RuleCondition c = new RuleCondition();
        final RangeCondition size = new RangeCondition();
        size.setMax(100L);

        // act, assert
        c.setSize(size);
        assertThat(c.getSize().getMax(), is(equalTo(100L)));
        final RangeCondition lines = new RangeCondition();
        lines.setMin(5L);
        c.setLines(lines);
        assertThat(c.getLines().getMin(), is(equalTo(5L)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="stringLeaves">
    @Test
    public void stringLeaves_set_roundTrip() {
        // arrange
        final RuleCondition c = new RuleCondition();

        // act
        c.setModifiedAfter("2026-01-01T00:00:00Z");
        c.setModifiedBefore("2027-01-01T00:00:00Z");
        c.setPathGlob("**/x/**");

        // assert
        assertThat(c.getModifiedAfter(), is(equalTo("2026-01-01T00:00:00Z")));
        assertThat(c.getModifiedBefore(), is(equalTo("2027-01-01T00:00:00Z")));
        assertThat(c.getPathGlob(), is(equalTo("**/x/**")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileNamesAndContentMatches">
    @Test
    public void fileNamesAndContentMatches_setAndCleared_fileNamesDefensivelyCopiedAndContentMatchesRoundTrips() {
        // arrange
        final RuleCondition c = new RuleCondition();

        // pre-assert
        assertThat(c.getFileNames(), is(nullValue()));
        assertThat(c.getContentMatches(), is(nullValue()));

        // act, assert
        final List<String> names = new ArrayList<>(Arrays.asList("Dockerfile", "Jenkinsfile"));
        c.setFileNames(names);
        names.clear();
        assertThat(c.getFileNames(), is(equalTo(Arrays.asList("Dockerfile", "Jenkinsfile"))));
        c.setFileNames(null);
        assertThat(c.getFileNames(), is(nullValue()));
        c.setContentMatches("(?m)^apiVersion:");
        assertThat(c.getContentMatches(), is(equalTo("(?m)^apiVersion:")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setNot">
    @Test
    public void setNot_childThenNull_roundTripsAndClears() {
        // arrange
        final RuleCondition c = new RuleCondition();
        final RuleCondition child = new RuleCondition();

        // act, assert
        c.setNot(child);
        assertThat(c.getNot(), is(child));
        c.setNot(null);
        assertThat(c.getNot(), is(nullValue()));
    }
    // </editor-fold>
}
