// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

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
import org.junit.jupiter.api.Test;

public class AiFactDefinitionSupportTest {

    private static AiFactCounter counter(final String label, final String pattern) {
        final AiFactCounter c = new AiFactCounter();
        c.setLabel(label);
        c.setPattern(pattern);
        return c;
    }

    private static AiFactDefinition definition(final String key, final List<AiFactCounter> facts) {
        final AiFactDefinition d = new AiFactDefinition();
        d.setKey(key);
        d.setFacts(facts);
        return d;
    }

    private static AiFieldGenerationConfig ruleWithFactsKey(final String factsKey) {
        final AiFieldGenerationConfig r = new AiFieldGenerationConfig();
        r.setFactsKey(factsKey);
        return r;
    }

    @Test
    public void facts_returnsTheGroupsCounters() {
        final List<AiFactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("sql-facts", group)));
        assertThat(support.facts("sql-facts"), is(sameInstance(group)));
    }

    @Test
    public void facts_unknownKey_throws() {
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("k", Collections.emptyList())));
        assertThrows(IllegalArgumentException.class, () -> support.facts("nope"));
    }

    @Test
    public void facts_groupWithNullFacts_returnsEmptyNotThrow() {
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("empty", null)));
        assertThat(support.facts("empty").isEmpty(), is(true));
    }

    @Test
    public void constructor_nullDefinitions_yieldsEmptyRegistry() {
        final AiFactDefinitionSupport support = new AiFactDefinitionSupport(null);
        assertThrows(IllegalArgumentException.class, () -> support.facts("any"));
    }

    @Test
    public void constructor_nullKey_throwsNamingIndex() {
        final AiFactDefinition bad = new AiFactDefinition();
        final NullPointerException ex = assertThrows(
                NullPointerException.class, () -> new AiFactDefinitionSupport(Collections.singletonList(bad)));
        assertThat(ex.getMessage(), is(notNullValue()));
        assertThat(ex.getMessage(), containsString("factDefinitions[0]"));
    }

    @Test
    public void resolveFactsKeys_setsRuleFactsFromRegistry() {
        final List<AiFactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("sql-facts", group)));
        final AiFieldGenerationConfig rule = ruleWithFactsKey("sql-facts");

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(group)));
    }

    @Test
    public void resolveFactsKeys_ruleWithoutFactsKey_leavesInlineFactsUntouched() {
        final List<AiFactCounter> inline = Collections.singletonList(counter("x", "y"));
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setFacts(inline);
        final AiFactDefinitionSupport support = new AiFactDefinitionSupport(null);

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(inline)));
    }

    @Test
    public void resolveFactsKeys_factsKeyOverridesInlineFacts() {
        final List<AiFactCounter> shared = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("sql-facts", shared)));
        final AiFieldGenerationConfig rule = ruleWithFactsKey("sql-facts");
        rule.setFacts(Collections.singletonList(counter("inline", "z")));

        support.resolveFactsKeys(Collections.singletonList(rule));

        assertThat(rule.getFacts(), is(sameInstance(shared)));
    }

    @Test
    public void resolveFactsKeys_unknownFactsKey_throws() {
        final AiFactDefinitionSupport support = new AiFactDefinitionSupport(null);
        final AiFieldGenerationConfig rule = ruleWithFactsKey("missing");
        assertThrows(IllegalArgumentException.class, () -> support.resolveFactsKeys(Collections.singletonList(rule)));
    }

    @Test
    public void resolveFactsKeys_nullRuleEntry_skipped() {
        final AiFactDefinitionSupport support = new AiFactDefinitionSupport(null);
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        support.resolveFactsKeys(Arrays.asList(null, rule));
        assertThat(rule.getFacts(), is(nullValue()));
    }

    @Test
    public void resolveFactsKeys_appliesToEveryReferencingRule() {
        final List<AiFactCounter> group = Collections.singletonList(counter("rows", "(?m)^INSERT"));
        final AiFactDefinitionSupport support =
                new AiFactDefinitionSupport(Collections.singletonList(definition("sql-facts", group)));
        final AiFieldGenerationConfig a = ruleWithFactsKey("sql-facts");
        final AiFieldGenerationConfig b = ruleWithFactsKey("sql-facts");

        support.resolveFactsKeys(Arrays.asList(a, b));

        assertThat(a.getFacts(), is(notNullValue()));
        assertThat(b.getFacts(), is(sameInstance(a.getFacts())));
    }
}
