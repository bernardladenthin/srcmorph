// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.RuleCondition;
import org.junit.jupiter.api.Test;

public class RuleSelectorTest {

    private static final String MODEL = "model-x";
    private final RuleSelector selector = new RuleSelector();

    private static RuleCondition extCond(final String... ext) {
        final RuleCondition c = new RuleCondition();
        c.setExtensions(Arrays.asList(ext));
        return c;
    }

    private static RoutingRule route(final String prompt, final RuleCondition cond, final int priority) {
        final RoutingRule c = new RoutingRule();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setCondition(cond);
        c.setPriority(priority);
        return c;
    }

    private static RoutingRule skipRule(final RuleCondition cond, final int priority) {
        final RoutingRule c = new RoutingRule();
        c.setSkip(true);
        c.setCondition(cond);
        c.setPriority(priority);
        return c;
    }

    private static RoutingRule fallback(final String prompt) {
        final RoutingRule c = new RoutingRule();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setFallback(true);
        return c;
    }

    private static FileContext ctx(final String fileName) {
        return new FileContext(fileName, "src/" + fileName, 500L, 10, 1000L);
    }

    // <editor-fold defaultstate="collapsed" desc="select">
    @Test
    public void select_conditionMatch() {
        final RoutingRule java = route("java", extCond(".java"), 0);
        assertThat(
                selector.select(Arrays.asList(java, fallback("fb")), ctx("Foo.java"))
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void select_higherPriorityWinsRegardlessOfOrder() {
        final RoutingRule low = route("low", extCond(".java"), 1);
        final RoutingRule high = route("high", extCond(".java"), 10);
        assertThat(selector.select(Arrays.asList(low, high), ctx("Foo.java")).getPromptKey(), is(equalTo("high")));
        assertThat(selector.select(Arrays.asList(high, low), ctx("Foo.java")).getPromptKey(), is(equalTo("high")));
    }

    @Test
    public void select_tieBrokenByDeclarationOrder() {
        final RoutingRule first = route("first", extCond(".java"), 5);
        final RoutingRule second = route("second", extCond(".java"), 5);
        assertThat(
                selector.select(Arrays.asList(first, second), ctx("Foo.java")).getPromptKey(), is(equalTo("first")));
    }

    @Test
    public void select_skipRuleCanWinByPriority() {
        final RoutingRule route = route("route", extCond(".java"), 0);
        final RoutingRule skip = skipRule(extCond(".java"), 10);
        assertThat(selector.select(Arrays.asList(route, skip), ctx("Foo.java")).isSkip(), is(true));
    }

    @Test
    public void select_fallbackUsedWhenNoMatch() {
        final RoutingRule java = route("java", extCond(".java"), 0);
        assertThat(
                selector.select(Arrays.asList(java, fallback("fb")), ctx("data.json"))
                        .getPromptKey(),
                is(equalTo("fb")));
    }

    @Test
    public void select_noMatchNoFallback_returnsNull() {
        final RoutingRule java = route("java", extCond(".java"), 0);
        assertThat(selector.select(Collections.singletonList(java), ctx("data.json")), is(nullValue()));
    }

    @Test
    public void select_nullEntrySkipped() {
        final RoutingRule java = route("java", extCond(".java"), 0);
        assertThat(
                selector.select(Arrays.<RoutingRule>asList(null, java), ctx("Foo.java"))
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void select_firstFallbackWinsWhenSeveral() {
        assertThat(
                selector.select(Arrays.asList(fallback("first"), fallback("second")), ctx("x.txt"))
                        .getPromptKey(),
                is(equalTo("first")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    @Test
    public void validate_validRuleSetPasses() {
        assertDoesNotThrow(() -> selector.validate(
                Arrays.asList(route("java", extCond(".java"), 0), skipRule(extCond(".tmp"), 10), fallback("fb"))));
    }

    @Test
    public void validate_nullEntryIgnored() {
        assertDoesNotThrow(
                () -> selector.validate(Arrays.<RoutingRule>asList(null, route("java", extCond(".java"), 0))));
    }

    @Test
    public void validate_moreThanOneFallback_throws() {
        assertThrows(
                IllegalArgumentException.class, () -> selector.validate(Arrays.asList(fallback("a"), fallback("b"))));
    }

    @Test
    public void validate_nonFallbackWithoutCondition_throws() {
        final RoutingRule noCond = new RoutingRule();
        noCond.setPromptKey("p");
        noCond.setAiDefinitionKey(MODEL);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(noCond)));
    }

    @Test
    public void validate_fallbackWithCondition_throws() {
        final RoutingRule fb = fallback("p");
        fb.setCondition(extCond(".java"));
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(fb)));
    }

    @Test
    public void validate_fallbackThatIsAlsoSkip_throws() {
        final RoutingRule fb = fallback("p");
        fb.setSkip(true);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(fb)));
    }

    @Test
    public void validate_invalidOnOversize_throws() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("nonsense");
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_invalidOnIncomplete_throws() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnIncomplete("retry");
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_validOnOversize_passes() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("mapReduce");
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_sampleChunksWithMaxChunks_passes() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("sampleChunks");
        r.setMaxChunks(6);
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_sampleChunksWithoutMaxChunks_throws() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("sampleChunks");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));

        assertThat(thrown.getMessage(), containsString("onOversize: sampleChunks needs a maxChunks above 0"));
    }

    @Test
    public void validate_maxChunksWithAnotherStrategy_throws() {
        for (final String strategy : new String[] {"mapReduce", "sample", "fail", "deterministic", ""}) {
            final RoutingRule r = route("java", extCond(".java"), 0);
            r.setOnOversize(strategy);
            r.setMaxChunks(1);

            final IllegalArgumentException thrown =
                    assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));

            assertThat(thrown.getMessage(), containsString("maxChunks applies only to onOversize: sampleChunks"));
        }
    }

    @Test
    public void validate_routeMissingPromptKey_throws() {
        final RoutingRule bad = new RoutingRule();
        bad.setAiDefinitionKey(MODEL);
        bad.setCondition(extCond(".java"));
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_routeMissingModel_throws() {
        final RoutingRule bad = new RoutingRule();
        bad.setPromptKey("p");
        bad.setCondition(extCond(".java"));
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_fallbackMissingKeys_throws() {
        final RoutingRule bad = new RoutingRule();
        bad.setFallback(true);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_skipRuleNeedsNoKeysButNeedsCondition() {
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(skipRule(extCond(".tmp"), 0))));
    }

    @Test
    public void validate_invalidConditionInRule_throws() {
        // a condition with no branch set is invalid -> selector.validate must propagate the error
        final RoutingRule bad = route("p", new RuleCondition(), 0);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_invalidFactPattern_throws() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        final FactCounter bad = new FactCounter();
        bad.setLabel("broken");
        bad.setPattern("[");
        r.setFacts(Collections.singletonList(bad));
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));
        // a rule without an id is named by its prompt key
        assertThat(thrown.getMessage().startsWith("the facts of rule 'java': invalid fact pattern"), is(true));
    }

    @Test
    public void validate_onlyIgnoreFacts_isRefusedNamingTheRuleById() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setId("big-java");
        final FactCounter ignore = new FactCounter();
        ignore.setLabel("comments");
        ignore.setPattern("//.*");
        ignore.setIgnore(true);
        r.setFacts(Collections.singletonList(ignore));
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(r)));
        assertThat(thrown.getMessage(), is("the facts of rule 'big-java': no counter, only ignore patterns"));
    }

    @Test
    public void validate_validFactPattern_passes() {
        final RoutingRule r = route("java", extCond(".java"), 0);
        final FactCounter ok = new FactCounter();
        ok.setLabel("rows");
        ok.setPattern("(?m)^INSERT");
        r.setFacts(Collections.singletonList(ok));
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="map and reduce prompts">
    @Test
    public void validateRule_aMapOrReducePromptWithoutChunks_throws() {
        for (final String strategy : new String[] {"sample", "fail", "deterministic", ""}) {
            final RoutingRule map = route("java", extCond(".java"), 0);
            map.setOnOversize(strategy);
            map.setMapPromptKey("map-chunk");
            final RoutingRule reduce = route("java", extCond(".java"), 0);
            reduce.setOnOversize(strategy);
            reduce.setReducePromptKey("reduce-parts");

            for (final RoutingRule rule : Arrays.asList(map, reduce)) {
                final IllegalArgumentException thrown =
                        assertThrows(IllegalArgumentException.class, () -> selector.validateRule(rule));
                assertThat(
                        thrown.getMessage(),
                        containsString("mapPromptKey and reducePromptKey apply only to onOversize: mapReduce"));
            }
        }
    }

    @Test
    public void validateRule_mapAndReducePromptsWithChunks_pass() {
        final RoutingRule mapReduce = route("java", extCond(".java"), 0);
        mapReduce.setOnOversize("mapReduce");
        mapReduce.setMapPromptKey("map-chunk");
        mapReduce.setReducePromptKey("reduce-parts");
        final RoutingRule sampled = route("java", extCond(".java"), 0);
        sampled.setOnOversize("sampleChunks");
        sampled.setMaxChunks(2);
        sampled.setMapPromptKey("map-chunk");
        sampled.setReducePromptKey("reduce-parts");

        assertDoesNotThrow(() -> selector.validateRule(mapReduce));
        assertDoesNotThrow(() -> selector.validateRule(sampled));
    }

    @Test
    public void copy_keepsTheMapAndReducePrompts() {
        final RoutingRule rule = route("java", extCond(".java"), 0);
        rule.setMapPromptKey("map-chunk");
        rule.setReducePromptKey("reduce-parts");

        final RoutingRule copy = rule.copy();

        assertThat(copy.getMapPromptKey(), is("map-chunk"));
        assertThat(copy.getReducePromptKey(), is("reduce-parts"));
    }
    // </editor-fold>
}
