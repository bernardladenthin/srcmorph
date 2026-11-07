// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.FactDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import org.junit.jupiter.api.Test;

public class FactGroupsTest {

    private static FactCounter counter(final String label, final String pattern) {
        final FactCounter c = new FactCounter();
        c.setLabel(label);
        c.setPattern(pattern);
        return c;
    }

    private static FactDefinition definition(final String key, final List<FactCounter> facts) {
        final FactDefinition d = new FactDefinition();
        d.setKey(key);
        d.setFacts(facts);
        return d;
    }

    private static RoutingRule ruleWithFactsKey(final String factsKey) {
        final RoutingRule r = new RoutingRule();
        r.setFactsKey(factsKey);
        return r;
    }

    // <editor-fold defaultstate="collapsed" desc="facts">
    @Test
    public void facts_knownKey_returnsTheGroupsCounters() {
        // arrange
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));

        // act, assert
        assertThat(support.facts("sql-facts"), is(sameInstance(group)));
    }

    @Test
    public void facts_unknownKey_throws() {
        // arrange
        final FactGroups support = new FactGroups(Collections.singletonList(definition("k", Collections.emptyList())));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> support.facts("nope"));
    }

    @Test
    public void facts_groupWithNullFacts_returnsEmptyNotThrow() {
        // arrange
        final FactGroups support = new FactGroups(Collections.singletonList(definition("empty", null)));

        // act, assert
        assertThat(support.facts("empty").isEmpty(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_aKeyDefinedTwice_throwsNamingItsSecondIndex() {
        // arrange
        final List<FactDefinition> definitions = Arrays.asList(
                definition("sql-facts", Collections.emptyList()), definition("sql-facts", Collections.emptyList()));

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> new FactGroups(definitions));

        // assert
        assertThat(thrown.getMessage(), is("factDefinitions[1].key 'sql-facts' is defined twice"));
    }

    @Test
    public void constructor_nullDefinitions_yieldsEmptyRegistry() {
        // act
        final FactGroups support = new FactGroups(null);

        // assert
        assertThrows(IllegalArgumentException.class, () -> support.facts("any"));
    }

    @Test
    public void constructor_nullKey_throwsNamingIndex() {
        // arrange
        final FactDefinition bad = new FactDefinition();
        final List<FactDefinition> definitions = Collections.singletonList(bad);

        // act
        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new FactGroups(definitions));

        // assert
        assertThat(ex.getMessage(), is(notNullValue()));
        assertThat(ex.getMessage(), containsString("factDefinitions[0].key is required"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="resolveFactsKeys">
    @Test
    public void resolveFactsKeys_ruleWithFactsKey_setsRuleFactsFromRegistry() {
        // arrange
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");

        // act
        support.resolveFactsKeys(Collections.singletonList(rule));

        // assert
        assertThat(rule.getFacts(), is(sameInstance(group)));
    }

    @Test
    public void resolveFactsKeys_ruleWithoutFactsKey_leavesInlineFactsUntouched() {
        // arrange
        final List<FactCounter> inline = Collections.singletonList(counter("x", "y"));
        final RoutingRule rule = new RoutingRule();
        rule.setFacts(inline);
        final FactGroups support = new FactGroups(null);

        // act
        support.resolveFactsKeys(Collections.singletonList(rule));

        // assert
        assertThat(rule.getFacts(), is(sameInstance(inline)));
    }

    @Test
    public void resolveFactsKeys_inlineFactsAndAFactsKey_areRefusedInsteadOfOneSilentlyLost() {
        // arrange
        final List<FactCounter> shared = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", shared)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");
        rule.setFacts(Collections.singletonList(counter("inline", "z")));
        final List<RoutingRule> rules = Collections.singletonList(rule);

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> support.resolveFactsKeys(rules));

        // assert
        assertThat(thrown.getMessage(), containsString("names its facts twice"));
    }

    @Test
    public void resolveFactsKeys_aFactsKeyAndAnEmptyFactsList_takeTheShared() {
        // arrange
        final List<FactCounter> shared = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", shared)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");
        rule.setFacts(Collections.<FactCounter>emptyList());

        // act
        support.resolveFactsKeys(Collections.singletonList(rule));

        // assert
        assertThat(rule.getFacts(), is(sameInstance(shared)));
    }

    @Test
    public void resolveFactsKeys_unknownFactsKey_throws() {
        // arrange
        final FactGroups support = new FactGroups(null);
        final RoutingRule rule = ruleWithFactsKey("missing");
        final List<RoutingRule> rules = Collections.singletonList(rule);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> support.resolveFactsKeys(rules));
    }

    @Test
    public void resolveFactsKeys_nullRuleEntry_skipped() {
        // arrange
        final FactGroups support = new FactGroups(null);
        final RoutingRule rule = new RoutingRule();

        // act
        support.resolveFactsKeys(Arrays.asList(null, rule));

        // assert
        assertThat(rule.getFacts(), is(nullValue()));
    }

    @Test
    public void resolveFactsKeys_severalReferencingRules_appliesToEveryReferencingRule() {
        // arrange
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));
        final RoutingRule a = ruleWithFactsKey("sql-facts");
        final RoutingRule b = ruleWithFactsKey("sql-facts");

        // act
        support.resolveFactsKeys(Arrays.asList(a, b));

        // assert
        assertThat(a.getFacts(), is(notNullValue()));
        assertThat(b.getFacts(), is(sameInstance(a.getFacts())));
    }
    // </editor-fold>
}
