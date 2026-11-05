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
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoutingRuleTest {

    @Test
    public void id_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getId(), is(nullValue()));
        c.setId("java-small");
        assertThat(c.getId(), is(equalTo("java-small")));
    }

    @Test
    public void promptKeyRoundTrips() {
        final RoutingRule c = new RoutingRule();
        c.setPromptKey("file-body-java");
        assertThat(c.getPromptKey(), is(equalTo("file-body-java")));
    }

    @Test
    public void aiDefinitionKeyRoundTrips() {
        final RoutingRule c = new RoutingRule();
        c.setAiDefinitionKey("gpt-oss-20B-c96k");
        assertThat(c.getAiDefinitionKey(), is(equalTo("gpt-oss-20B-c96k")));
    }

    @Test
    public void requirePromptKey_set_isTheKey() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setPromptKey("file-body-java");

        // act + assert
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

    @Test
    public void requireAiDefinitionKey_set_isTheKey() {
        // arrange
        final RoutingRule c = new RoutingRule();
        c.setAiDefinitionKey("gpt-oss-20B-c96k");

        // act + assert
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

    @Test
    public void condition_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getCondition(), is(nullValue()));
        final RuleCondition condition = new RuleCondition();
        c.setCondition(condition);
        assertThat(c.getCondition(), is(notNullValue()));
        assertThat(c.getCondition(), is(condition));
    }

    @Test
    public void priority_defaultsZeroAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getPriority(), is(equalTo(0)));
        c.setPriority(10);
        assertThat(c.getPriority(), is(equalTo(10)));
    }

    @Test
    public void fallback_defaultsFalseAndTogglesTrue() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.isFallback(), is(false));
        c.setFallback(true);
        assertThat(c.isFallback(), is(true));
    }

    @Test
    public void skip_defaultsFalseAndTogglesTrue() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.isSkip(), is(false));
        c.setSkip(true);
        assertThat(c.isSkip(), is(true));
    }

    @Test
    public void onOversize_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getOnOversize(), is(nullValue()));
        c.setOnOversize("mapReduce");
        assertThat(c.getOnOversize(), is(equalTo("mapReduce")));
    }

    @Test
    public void oversizeStrategy_defaultsFailAndReflectsToken() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.oversizeStrategy(), is(OversizeStrategy.FAIL));
        c.setOnOversize("deterministic");
        assertThat(c.oversizeStrategy(), is(OversizeStrategy.DETERMINISTIC));
    }

    @Test
    public void onIncomplete_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getOnIncomplete(), is(nullValue()));
        c.setOnIncomplete("retryOnce");
        assertThat(c.getOnIncomplete(), is(equalTo("retryOnce")));
    }

    @Test
    public void incompleteStrategy_defaultsFailAndReflectsToken() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.incompleteStrategy(), is(IncompleteStrategy.FAIL));
        c.setOnIncomplete("keep");
        assertThat(c.incompleteStrategy(), is(IncompleteStrategy.KEEP));
    }

    @Test
    public void maxChunks_defaultsZeroAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getMaxChunks(), is(equalTo(0)));
        c.setMaxChunks(7);
        assertThat(c.getMaxChunks(), is(equalTo(7)));
    }

    @Test
    public void facts_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getFacts(), is(nullValue()));
        final FactCounter counter = new FactCounter();
        c.setFacts(java.util.Collections.singletonList(counter));
        assertThat(c.getFacts(), is(notNullValue()));
        assertThat(c.getFacts().size(), is(equalTo(1)));
    }

    @Test
    public void factsKey_defaultsNullAndRoundTrips() {
        final RoutingRule c = new RoutingRule();
        assertThat(c.getFactsKey(), is(nullValue()));
        c.setFactsKey("java-facts");
        assertThat(c.getFactsKey(), is(equalTo("java-facts")));
    }

    @Test
    public void copy_carriesEveryField_andOwnsItsFactsList() {
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

        final RoutingRule copy = c.copy();

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

        facts.clear();
        c.setPromptKey("changed");
        assertThat(copy.getFacts(), is(equalTo(Arrays.asList(counter))));
        assertThat(copy.getPromptKey(), is(equalTo("file-body-sql")));
    }

    @Test
    public void copy_ofAnEmptyRule_keepsTheNulls() {
        final RoutingRule copy = new RoutingRule().copy();

        assertThat(copy.getFacts(), is(nullValue()));
        assertThat(copy.getCondition(), is(nullValue()));
        assertThat(copy.isFallback(), is(false));
        assertThat(copy.isSkip(), is(false));
    }

    @Test
    public void mapAndReducePromptKeys_blank_areUnset() {
        final RoutingRule rule = new RoutingRule();
        rule.setMapPromptKey(" ");
        rule.setReducePromptKey("");

        assertThat(rule.getMapPromptKey(), is(nullValue()));
        assertThat(rule.getReducePromptKey(), is(nullValue()));
    }
}
