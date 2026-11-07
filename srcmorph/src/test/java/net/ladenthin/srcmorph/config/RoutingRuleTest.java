// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoutingRuleTest {

    // <editor-fold defaultstate="collapsed" desc="id">
    @Test
    public void id_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getId(), is(nullValue()));

        // act
        c.setId("java-small");

        // assert
        assertThat(c.getId(), is(equalTo("java-small")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="promptKey">
    @Test
    public void promptKey_set_roundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // act
        c.setPromptKey("file-body-java");

        // assert
        assertThat(c.getPromptKey(), is(equalTo("file-body-java")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aiDefinitionKey">
    @Test
    public void aiDefinitionKey_set_roundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // act
        c.setAiDefinitionKey("gpt-oss-20B-c96k");

        // assert
        assertThat(c.getAiDefinitionKey(), is(equalTo("gpt-oss-20B-c96k")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requirePromptKey">
    @Test
    public void requirePromptKey_set_isTheKey() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setPromptKey("file-body-java");

        // act, assert
        assertThat(c.requirePromptKey(), is(equalTo("file-body-java")));
    }

    @Test
    public void requirePromptKey_ofASkipRule_isRefused() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setSkip(true);

        // act
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, c::requirePromptKey);

        // assert
        assertThat(thrown.getMessage(), is(equalTo("The rule has no promptKey: " + c)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requireAiDefinitionKey">
    @Test
    public void requireAiDefinitionKey_set_isTheKey() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setAiDefinitionKey("gpt-oss-20B-c96k");

        // act, assert
        assertThat(c.requireAiDefinitionKey(), is(equalTo("gpt-oss-20B-c96k")));
    }

    @Test
    public void requireAiDefinitionKey_ofASkipRule_isRefused() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setSkip(true);

        // act
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, c::requireAiDefinitionKey);

        // assert
        assertThat(thrown.getMessage(), is(equalTo("The rule has no aiDefinitionKey: " + c)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="condition">
    @Test
    public void condition_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getCondition(), is(nullValue()));

        // act
        final RuleCondition condition = new RuleCondition();
        c.setCondition(condition);

        // assert
        assertThat(c.getCondition(), is(notNullValue()));
        assertThat(c.getCondition(), is(condition));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="priority">
    @Test
    public void priority_defaultAndSet_defaultsZeroAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getPriority(), is(equalTo(0)));

        // act
        c.setPriority(10);

        // assert
        assertThat(c.getPriority(), is(equalTo(10)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fallback">
    @Test
    public void fallback_defaultAndSet_defaultsFalseAndTogglesTrue() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.isFallback(), is(false));

        // act
        c.setFallback(true);

        // assert
        assertThat(c.isFallback(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="skip">
    @Test
    public void skip_defaultAndSet_defaultsFalseAndTogglesTrue() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.isSkip(), is(false));

        // act
        c.setSkip(true);

        // assert
        assertThat(c.isSkip(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onOversize">
    @Test
    public void onOversize_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getOnOversize(), is(nullValue()));

        // act
        c.setOnOversize("mapReduce");

        // assert
        assertThat(c.getOnOversize(), is(equalTo("mapReduce")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="oversizeStrategy">
    @Test
    public void oversizeStrategy_defaultAndOnOversizeSet_defaultsFailAndReflectsToken() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.oversizeStrategy(), is(OversizeStrategy.FAIL));

        // act
        c.setOnOversize("deterministic");

        // assert
        assertThat(c.oversizeStrategy(), is(OversizeStrategy.DETERMINISTIC));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="onIncomplete">
    @Test
    public void onIncomplete_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getOnIncomplete(), is(nullValue()));

        // act
        c.setOnIncomplete("retryOnce");

        // assert
        assertThat(c.getOnIncomplete(), is(equalTo("retryOnce")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incompleteStrategy">
    @Test
    public void incompleteStrategy_defaultAndOnIncompleteSet_defaultsFailAndReflectsToken() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.incompleteStrategy(), is(IncompleteStrategy.FAIL));

        // act
        c.setOnIncomplete("keep");

        // assert
        assertThat(c.incompleteStrategy(), is(IncompleteStrategy.KEEP));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="maxChunks">
    @Test
    public void maxChunks_defaultAndSet_defaultsZeroAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getMaxChunks(), is(equalTo(0)));

        // act
        c.setMaxChunks(7);

        // assert
        assertThat(c.getMaxChunks(), is(equalTo(7)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="facts">
    @Test
    public void facts_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getFacts(), is(nullValue()));

        // act
        final FactCounter counter = new FactCounter();
        c.setFacts(Collections.singletonList(counter));

        // assert
        assertThat(c.getFacts(), is(notNullValue()));
        assertThat(c.getFacts().size(), is(equalTo(1)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factsKey">
    @Test
    public void factsKey_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final RoutingRule c = new RoutingRule();

        // pre-assert
        assertThat(c.getFactsKey(), is(nullValue()));

        // act
        c.setFactsKey("java-facts");

        // assert
        assertThat(c.getFactsKey(), is(equalTo("java-facts")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="copy">
    @Test
    public void copy_everyFieldSet_carriesEveryFieldAndOwnsItsFactsList() {
        // arrange
        final RuleCondition condition = new RuleCondition();
        final FactCounter counter = new FactCounter();
        counter.setLabel("tables");
        final List<FactCounter> facts = new ArrayList<>(Arrays.asList(counter));
        final RoutingRule c = new RoutingRule();
        c.setId("sql");
        c.setPromptKey("file-body-sql");
        c.setAiDefinitionKey("m");
        c.setCondition(condition);
        c.setPriority(7);
        c.setFallback(true);
        c.setSkip(true);
        c.setOnOversize("mapReduce");
        c.setOnIncomplete("keep");
        c.setMaxChunks(5);
        c.setFacts(facts);
        c.setFactsKey("sql-facts");

        // act
        final RoutingRule copy = c.copy();

        // assert
        assertThat(copy.getId(), is(equalTo("sql")));
        assertThat(copy.getPromptKey(), is(equalTo("file-body-sql")));
        assertThat(copy.getAiDefinitionKey(), is(equalTo("m")));
        assertThat(copy.getCondition(), is(sameInstance(condition)));
        assertThat(copy.getPriority(), is(equalTo(7)));
        assertThat(copy.isFallback(), is(true));
        assertThat(copy.isSkip(), is(true));
        assertThat(copy.getOnOversize(), is(equalTo("mapReduce")));
        assertThat(copy.getOnIncomplete(), is(equalTo("keep")));
        assertThat(copy.getMaxChunks(), is(equalTo(5)));
        assertThat(copy.getFacts(), is(equalTo(facts)));
        assertThat(copy.getFactsKey(), is(equalTo("sql-facts")));

        // act
        facts.clear();
        c.setPromptKey("changed");

        // assert
        assertThat(copy.getFacts(), is(equalTo(Arrays.asList(counter))));
        assertThat(copy.getPromptKey(), is(equalTo("file-body-sql")));
    }

    @Test
    public void copy_ofAnEmptyRule_keepsTheNulls() {
        // act
        final RoutingRule copy = new RoutingRule().copy();

        // assert
        assertThat(copy.getFacts(), is(nullValue()));
        assertThat(copy.getCondition(), is(nullValue()));
        assertThat(copy.isFallback(), is(false));
        assertThat(copy.isSkip(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="mapAndReducePromptKeys">
    @Test
    public void mapAndReducePromptKeys_blank_areUnset() {
        // arrange
        final RoutingRule rule = new RoutingRule();

        // act
        rule.setMapPromptKey(" ");
        rule.setReducePromptKey("");

        // assert
        assertThat(rule.getMapPromptKey(), is(nullValue()));
        assertThat(rule.getReducePromptKey(), is(nullValue()));
    }
    // </editor-fold>
}
