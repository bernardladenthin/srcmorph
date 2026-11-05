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

    @Test
    public void facts_returnsTheGroupsCounters() {
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));
        assertThat(support.facts("sql-facts"), is(sameInstance(group)));
    }

    @Test
    public void facts_unknownKey_throws() {
        final FactGroups support = new FactGroups(Collections.singletonList(definition("k", Collections.emptyList())));
        assertThrows(IllegalArgumentException.class, () -> support.facts("nope"));
    }

    @Test
    public void facts_groupWithNullFacts_returnsEmptyNotThrow() {
        final FactGroups support = new FactGroups(Collections.singletonList(definition("empty", null)));
        assertThat(support.facts("empty").isEmpty(), is(true));
    }

    @Test
    public void constructor_aKeyDefinedTwice_throwsNamingItsSecondIndex() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new FactGroups(java.util.Arrays.asList(
                        definition("sql-facts", Collections.emptyList()),
                        definition("sql-facts", Collections.emptyList()))));
        assertThat(thrown.getMessage(), is("factDefinitions[1].key 'sql-facts' is defined twice"));
    }

    @Test
    public void constructor_nullDefinitions_yieldsEmptyRegistry() {
        final FactGroups support = new FactGroups(null);
        assertThrows(IllegalArgumentException.class, () -> support.facts("any"));
    }

    @Test
    public void constructor_nullKey_throwsNamingIndex() {
        final FactDefinition bad = new FactDefinition();
        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new FactGroups(Collections.singletonList(bad)));
        assertThat(ex.getMessage(), is(notNullValue()));
        assertThat(ex.getMessage(), containsString("factDefinitions[0].key is required"));
    }

    @Test
    public void resolveFactsKeys_setsRuleFactsFromRegistry() {
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(group)));
    }

    @Test
    public void resolveFactsKeys_ruleWithoutFactsKey_leavesInlineFactsUntouched() {
        final List<FactCounter> inline = Collections.singletonList(counter("x", "y"));
        final RoutingRule rule = new RoutingRule();
        rule.setFacts(inline);
        final FactGroups support = new FactGroups(null);

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(inline)));
    }

    @Test
    public void resolveFactsKeys_inlineFactsAndAFactsKey_areRefusedInsteadOfOneSilentlyLost() {
        final List<FactCounter> shared = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", shared)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");
        rule.setFacts(Collections.singletonList(counter("inline", "z")));

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> support.resolveFactsKeys(Collections.singletonList(rule)));

        assertThat(thrown.getMessage(), org.hamcrest.CoreMatchers.containsString("names its facts twice"));
    }

    @Test
    public void resolveFactsKeys_aFactsKeyAndAnEmptyFactsList_takeTheShared() {
        final List<FactCounter> shared = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", shared)));
        final RoutingRule rule = ruleWithFactsKey("sql-facts");
        rule.setFacts(Collections.<FactCounter>emptyList());

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(shared)));
    }

    @Test
    public void resolveFactsKeys_unknownFactsKey_throws() {
        final FactGroups support = new FactGroups(null);
        final RoutingRule rule = ruleWithFactsKey("missing");
        assertThrows(IllegalArgumentException.class, () -> support.resolveFactsKeys(Collections.singletonList(rule)));
    }

    @Test
    public void resolveFactsKeys_nullRuleEntry_skipped() {
        final FactGroups support = new FactGroups(null);
        final RoutingRule rule = new RoutingRule();
        support.resolveFactsKeys(Arrays.asList(null, rule));
        assertThat(rule.getFacts(), is(nullValue()));
    }

    @Test
    public void resolveFactsKeys_appliesToEveryReferencingRule() {
        final List<FactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final FactGroups support = new FactGroups(Collections.singletonList(definition("sql-facts", group)));
        final RoutingRule a = ruleWithFactsKey("sql-facts");
        final RoutingRule b = ruleWithFactsKey("sql-facts");

        support.resolveFactsKeys(Arrays.asList(a, b));

        assertThat(a.getFacts(), is(notNullValue()));
        assertThat(b.getFacts(), is(sameInstance(a.getFacts())));
    }
}
