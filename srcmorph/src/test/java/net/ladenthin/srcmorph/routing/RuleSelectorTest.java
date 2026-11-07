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
import java.util.List;
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
    public void select_matchingCondition_returnsItsRule() {
        // arrange
        final RoutingRule java = route("java", extCond(".java"), 0);

        // act, assert
        assertThat(
                selector.select(Arrays.asList(java, fallback("fb")), ctx("Foo.java"))
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void select_twoMatchingRules_higherPriorityWinsRegardlessOfOrder() {
        // arrange
        final RoutingRule low = route("low", extCond(".java"), 1);
        final RoutingRule high = route("high", extCond(".java"), 10);

        // act, assert
        assertThat(selector.select(Arrays.asList(low, high), ctx("Foo.java")).getPromptKey(), is(equalTo("high")));
        assertThat(selector.select(Arrays.asList(high, low), ctx("Foo.java")).getPromptKey(), is(equalTo("high")));
    }

    @Test
    public void select_equalPriority_tieBrokenByDeclarationOrder() {
        // arrange
        final RoutingRule first = route("first", extCond(".java"), 5);
        final RoutingRule second = route("second", extCond(".java"), 5);

        // act, assert
        assertThat(
                selector.select(Arrays.asList(first, second), ctx("Foo.java")).getPromptKey(), is(equalTo("first")));
    }

    @Test
    public void select_skipRuleWithHigherPriority_skipRuleWins() {
        // arrange
        final RoutingRule route = route("route", extCond(".java"), 0);
        final RoutingRule skip = skipRule(extCond(".java"), 10);

        // act, assert
        assertThat(selector.select(Arrays.asList(route, skip), ctx("Foo.java")).isSkip(), is(true));
    }

    @Test
    public void select_noMatch_returnsTheFallback() {
        // arrange
        final RoutingRule java = route("java", extCond(".java"), 0);

        // act, assert
        assertThat(
                selector.select(Arrays.asList(java, fallback("fb")), ctx("data.json"))
                        .getPromptKey(),
                is(equalTo("fb")));
    }

    @Test
    public void select_noMatchNoFallback_returnsNull() {
        // arrange
        final RoutingRule java = route("java", extCond(".java"), 0);

        // act, assert
        assertThat(selector.select(Collections.singletonList(java), ctx("data.json")), is(nullValue()));
    }

    @Test
    public void select_nullEntry_isSkipped() {
        // arrange
        final RoutingRule java = route("java", extCond(".java"), 0);

        // act, assert
        assertThat(
                selector.select(Arrays.<RoutingRule>asList(null, java), ctx("Foo.java"))
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void select_severalFallbacks_firstFallbackWins() {
        // act, assert
        assertThat(
                selector.select(Arrays.asList(fallback("first"), fallback("second")), ctx("x.txt"))
                        .getPromptKey(),
                is(equalTo("first")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    @Test
    public void validate_validRuleSet_passes() {
        // act, assert
        assertDoesNotThrow(() -> selector.validate(
                Arrays.asList(route("java", extCond(".java"), 0), skipRule(extCond(".tmp"), 10), fallback("fb"))));
    }

    @Test
    public void validate_nullEntry_isIgnored() {
        // act, assert
        assertDoesNotThrow(
                () -> selector.validate(Arrays.<RoutingRule>asList(null, route("java", extCond(".java"), 0))));
    }

    @Test
    public void validate_moreThanOneFallback_throws() {
        // arrange
        final List<RoutingRule> rules = Arrays.asList(fallback("a"), fallback("b"));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_nonFallbackWithoutCondition_throws() {
        // arrange
        final RoutingRule noCond = new RoutingRule();
        noCond.setPromptKey("p");
        noCond.setAiDefinitionKey(MODEL);
        final List<RoutingRule> rules = Collections.singletonList(noCond);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_fallbackWithCondition_throws() {
        // arrange
        final RoutingRule fb = fallback("p");
        fb.setCondition(extCond(".java"));
        final List<RoutingRule> rules = Collections.singletonList(fb);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_fallbackThatIsAlsoSkip_throws() {
        // arrange
        final RoutingRule fb = fallback("p");
        fb.setSkip(true);
        final List<RoutingRule> rules = Collections.singletonList(fb);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_invalidOnOversize_throws() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("nonsense");
        final List<RoutingRule> rules = Collections.singletonList(r);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_invalidOnIncomplete_throws() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnIncomplete("retry");
        final List<RoutingRule> rules = Collections.singletonList(r);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_validOnOversize_passes() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("mapReduce");

        // act, assert
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_sampleChunksWithMaxChunks_passes() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("sampleChunks");
        r.setMaxChunks(6);

        // act, assert
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }

    @Test
    public void validate_sampleChunksWithoutMaxChunks_throws() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setOnOversize("sampleChunks");
        final List<RoutingRule> rules = Collections.singletonList(r);

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));

        // assert
        assertThat(thrown.getMessage(), containsString("onOversize: sampleChunks needs a maxChunks above 0"));
    }

    @Test
    public void validate_maxChunksWithAnotherStrategy_throws() {
        for (final String strategy : new String[] {"mapReduce", "sample", "fail", "deterministic", ""}) {
            // arrange
            final RoutingRule r = route("java", extCond(".java"), 0);
            r.setOnOversize(strategy);
            r.setMaxChunks(1);
            final List<RoutingRule> rules = Collections.singletonList(r);

            // act
            final IllegalArgumentException thrown =
                    assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));

            // assert
            assertThat(thrown.getMessage(), containsString("maxChunks applies only to onOversize: sampleChunks"));
        }
    }

    @Test
    public void validate_routeMissingPromptKey_throws() {
        // arrange
        final RoutingRule bad = new RoutingRule();
        bad.setAiDefinitionKey(MODEL);
        bad.setCondition(extCond(".java"));
        final List<RoutingRule> rules = Collections.singletonList(bad);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_routeMissingModel_throws() {
        // arrange
        final RoutingRule bad = new RoutingRule();
        bad.setPromptKey("p");
        bad.setCondition(extCond(".java"));
        final List<RoutingRule> rules = Collections.singletonList(bad);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_fallbackMissingKeys_throws() {
        // arrange
        final RoutingRule bad = new RoutingRule();
        bad.setFallback(true);
        final List<RoutingRule> rules = Collections.singletonList(bad);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_skipRuleWithConditionAndNoKeys_passes() {
        // act, assert
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(skipRule(extCond(".tmp"), 0))));
    }

    @Test
    public void validate_invalidConditionInRule_throws() {
        // arrange
        // a condition with no branch set is invalid -> selector.validate must propagate the error
        final RoutingRule bad = route("p", new RuleCondition(), 0);
        final List<RoutingRule> rules = Collections.singletonList(bad);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_invalidFactPattern_throws() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        final FactCounter bad = new FactCounter();
        bad.setLabel("broken");
        bad.setPattern("[");
        r.setFacts(Collections.singletonList(bad));
        final List<RoutingRule> rules = Collections.singletonList(r);

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));

        // assert
        // a rule without an id is named by its prompt key
        assertThat(thrown.getMessage().startsWith("the facts of rule 'java': invalid fact pattern"), is(true));
    }

    @Test
    public void validate_onlyIgnoreFacts_isRefusedNamingTheRuleById() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        r.setId("big-java");
        final FactCounter ignore = new FactCounter();
        ignore.setLabel("comments");
        ignore.setPattern("//.*");
        ignore.setIgnore(true);
        r.setFacts(Collections.singletonList(ignore));
        final List<RoutingRule> rules = Collections.singletonList(r);

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));

        // assert
        assertThat(thrown.getMessage(), is("the facts of rule 'big-java': no counter, only ignore patterns"));
    }

    @Test
    public void validate_validFactPattern_passes() {
        // arrange
        final RoutingRule r = route("java", extCond(".java"), 0);
        final FactCounter ok = new FactCounter();
        ok.setLabel("rows");
        ok.setPattern("(?m)^INSERT");
        r.setFacts(Collections.singletonList(ok));

        // act, assert
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(r)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validateRule: map and reduce prompts">
    @Test
    public void validateRule_aMapOrReducePromptWithoutChunks_throws() {
        for (final String strategy : new String[] {"sample", "fail", "deterministic", ""}) {
            // arrange
            final RoutingRule map = route("java", extCond(".java"), 0);
            map.setOnOversize(strategy);
            map.setMapPromptKey("map-chunk");
            final RoutingRule reduce = route("java", extCond(".java"), 0);
            reduce.setOnOversize(strategy);
            reduce.setReducePromptKey("reduce-parts");

            for (final RoutingRule rule : Arrays.asList(map, reduce)) {
                // act
                final IllegalArgumentException thrown =
                        assertThrows(IllegalArgumentException.class, () -> selector.validateRule(rule));

                // assert
                assertThat(
                        thrown.getMessage(),
                        containsString("mapPromptKey and reducePromptKey apply only to onOversize: mapReduce"));
            }
        }
    }

    @Test
    public void validateRule_mapAndReducePromptsWithChunks_pass() {
        // arrange
        final RoutingRule mapReduce = route("java", extCond(".java"), 0);
        mapReduce.setOnOversize("mapReduce");
        mapReduce.setMapPromptKey("map-chunk");
        mapReduce.setReducePromptKey("reduce-parts");
        final RoutingRule sampled = route("java", extCond(".java"), 0);
        sampled.setOnOversize("sampleChunks");
        sampled.setMaxChunks(2);
        sampled.setMapPromptKey("map-chunk");
        sampled.setReducePromptKey("reduce-parts");

        // act, assert
        assertDoesNotThrow(() -> selector.validateRule(mapReduce));
        assertDoesNotThrow(() -> selector.validateRule(sampled));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="copy">
    @Test
    public void copy_ruleWithMapAndReducePrompts_keepsThem() {
        // arrange
        final RoutingRule rule = route("java", extCond(".java"), 0);
        rule.setMapPromptKey("map-chunk");
        rule.setReducePromptKey("reduce-parts");

        // act
        final RoutingRule copy = rule.copy();

        // assert
        assertThat(copy.getMapPromptKey(), is("map-chunk"));
        assertThat(copy.getReducePromptKey(), is("reduce-parts"));
    }
    // </editor-fold>
}
