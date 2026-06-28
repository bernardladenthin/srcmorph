// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiFieldGenerationSelectorTest {

    private static final String MODEL = "model-x";
    private final AiFieldGenerationSelector selector = new AiFieldGenerationSelector();

    private static AiFieldGenerationConfig route(final String prompt, final List<String> ext, final int priority) {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setFileExtensions(ext);
        c.setPriority(priority);
        return c;
    }

    private static AiFieldGenerationConfig skipRule(final List<String> ext, final int priority) {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setFileExtensions(ext);
        c.setSkip(true);
        c.setPriority(priority);
        return c;
    }

    private static AiFieldGenerationConfig fallback(final String prompt) {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setFallback(true);
        return c;
    }

    private static AiFieldGenerationConfig sized(final String prompt, final long min, final long max) {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setMinFileSizeBytes(min);
        c.setMaxFileSizeBytes(max);
        return c;
    }

    private static AiFieldGenerationConfig lined(final String prompt, final int min, final int max) {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setPromptKey(prompt);
        c.setAiDefinitionKey(MODEL);
        c.setMinLines(min);
        c.setMaxLines(max);
        return c;
    }

    // <editor-fold defaultstate="collapsed" desc="select: extension + priority + skip + fallback">
    @Test
    public void select_extensionMatch() {
        final AiFieldGenerationConfig java = route("java", Arrays.asList(".java"), 0);
        final AiFieldGenerationConfig fb = fallback("fb");
        assertThat(
                selector.select(Arrays.asList(java, fb), "Foo.java", 500L, 10).getPromptKey(), is(equalTo("java")));
    }

    @Test
    public void select_higherPriorityWinsRegardlessOfOrder() {
        final AiFieldGenerationConfig low = route("low", Arrays.asList(".java"), 1);
        final AiFieldGenerationConfig high = route("high", Arrays.asList(".java"), 10);
        // declared low-then-high: high must still win
        assertThat(
                selector.select(Arrays.asList(low, high), "Foo.java", 500L, 10).getPromptKey(), is(equalTo("high")));
        // declared high-then-low: high still wins
        assertThat(
                selector.select(Arrays.asList(high, low), "Foo.java", 500L, 10).getPromptKey(), is(equalTo("high")));
    }

    @Test
    public void select_priorityTieBrokenByDeclarationOrder() {
        final AiFieldGenerationConfig first = route("first", Arrays.asList(".java"), 5);
        final AiFieldGenerationConfig second = route("second", Arrays.asList(".java"), 5);
        assertThat(
                selector.select(Arrays.asList(first, second), "Foo.java", 500L, 10)
                        .getPromptKey(),
                is(equalTo("first")));
    }

    @Test
    public void select_skipRuleCanWinByPriority() {
        final AiFieldGenerationConfig route = route("route", Arrays.asList(".java"), 0);
        final AiFieldGenerationConfig skip = skipRule(Arrays.asList(".java"), 10);
        final AiFieldGenerationConfig winner = selector.select(Arrays.asList(route, skip), "Foo.java", 500L, 10);
        assertThat(winner.isSkip(), is(true));
    }

    @Test
    public void select_fallbackUsedWhenNoNonFallbackMatches() {
        final AiFieldGenerationConfig java = route("java", Arrays.asList(".java"), 0);
        final AiFieldGenerationConfig fb = fallback("fb");
        assertThat(
                selector.select(Arrays.asList(java, fb), "data.json", 500L, 10).getPromptKey(), is(equalTo("fb")));
    }

    @Test
    public void select_noMatchNoFallback_returnsNull() {
        final AiFieldGenerationConfig java = route("java", Arrays.asList(".java"), 0);
        assertThat(selector.select(Collections.singletonList(java), "data.json", 500L, 10), is(nullValue()));
    }

    @Test
    public void select_nullEntrySkipped() {
        final AiFieldGenerationConfig java = route("java", Arrays.asList(".java"), 0);
        assertThat(
                selector.select(Arrays.<AiFieldGenerationConfig>asList(null, java), "Foo.java", 500L, 10)
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void select_firstFallbackWinsWhenSeveral() {
        final AiFieldGenerationConfig first = fallback("first");
        final AiFieldGenerationConfig second = fallback("second");
        assertThat(
                selector.select(Arrays.asList(first, second), "data.json", 500L, 10)
                        .getPromptKey(),
                is(equalTo("first")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="select: size + line bounds">
    @Test
    public void select_sizeMinExclusiveMaxInclusive() {
        final AiFieldGenerationConfig big = sized("big", 1000L, 0L);
        assertThat(selector.select(Collections.singletonList(big), "Foo.java", 1000L, 0), is(nullValue()));
        assertThat(
                selector.select(Collections.singletonList(big), "Foo.java", 1001L, 0)
                        .getPromptKey(),
                is(equalTo("big")));
        final AiFieldGenerationConfig small = sized("small", 0L, 1000L);
        assertThat(
                selector.select(Collections.singletonList(small), "Foo.java", 1000L, 0)
                        .getPromptKey(),
                is(equalTo("small")));
        assertThat(selector.select(Collections.singletonList(small), "Foo.java", 1001L, 0), is(nullValue()));
    }

    @Test
    public void select_lineMinExclusiveMaxInclusive() {
        final AiFieldGenerationConfig many = lined("many", 100, 0);
        assertThat(selector.select(Collections.singletonList(many), "Foo.java", 0L, 100), is(nullValue()));
        assertThat(
                selector.select(Collections.singletonList(many), "Foo.java", 0L, 101)
                        .getPromptKey(),
                is(equalTo("many")));
        final AiFieldGenerationConfig few = lined("few", 0, 100);
        assertThat(
                selector.select(Collections.singletonList(few), "Foo.java", 0L, 100)
                        .getPromptKey(),
                is(equalTo("few")));
        assertThat(selector.select(Collections.singletonList(few), "Foo.java", 0L, 101), is(nullValue()));
    }

    @Test
    public void select_allDimensionsMustMatch() {
        final AiFieldGenerationConfig combined = new AiFieldGenerationConfig();
        combined.setPromptKey("combined");
        combined.setAiDefinitionKey(MODEL);
        combined.setFileExtensions(Arrays.asList(".java"));
        combined.setMaxFileSizeBytes(1000L);
        combined.setMaxLines(50);
        final List<AiFieldGenerationConfig> list = Arrays.asList(combined, fallback("fb"));

        assertThat(selector.select(list, "Foo.java", 500L, 30).getPromptKey(), is(equalTo("combined")));
        assertThat(selector.select(list, "Foo.sql", 500L, 30).getPromptKey(), is(equalTo("fb"))); // ext fails
        assertThat(selector.select(list, "Foo.java", 5000L, 30).getPromptKey(), is(equalTo("fb"))); // size fails
        assertThat(selector.select(list, "Foo.java", 500L, 999).getPromptKey(), is(equalTo("fb"))); // lines fail
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    @Test
    public void validate_validRuleSetPasses() {
        final List<AiFieldGenerationConfig> rules = Arrays.asList(
                route("java", Arrays.asList(".java"), 0), skipRule(Arrays.asList(".tmp"), 10), fallback("fb"));
        assertDoesNotThrow(() -> selector.validate(rules));
    }

    @Test
    public void validate_nullEntryIgnored() {
        final List<AiFieldGenerationConfig> rules =
                Arrays.<AiFieldGenerationConfig>asList(null, route("java", Arrays.asList(".java"), 0));
        assertDoesNotThrow(() -> selector.validate(rules));
    }

    @Test
    public void validate_moreThanOneFallback_throws() {
        final List<AiFieldGenerationConfig> rules = Arrays.asList(fallback("a"), fallback("b"));
        assertThrows(IllegalArgumentException.class, () -> selector.validate(rules));
    }

    @Test
    public void validate_nonFallbackWithoutFilter_throws() {
        final AiFieldGenerationConfig noFilter = new AiFieldGenerationConfig();
        noFilter.setPromptKey("p");
        noFilter.setAiDefinitionKey(MODEL); // no extension/size/line filter, not a fallback
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(noFilter)));
    }

    @Test
    public void validate_fallbackThatIsAlsoSkip_throws() {
        final AiFieldGenerationConfig bad = fallback("p");
        bad.setSkip(true);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_routeMissingPromptKey_throws() {
        final AiFieldGenerationConfig bad = route(null, Arrays.asList(".java"), 0);
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_routeMissingAiDefinitionKey_throws() {
        final AiFieldGenerationConfig bad = new AiFieldGenerationConfig();
        bad.setPromptKey("p");
        bad.setFileExtensions(Arrays.asList(".java")); // has a filter but no model
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_fallbackMissingKeys_throws() {
        final AiFieldGenerationConfig bad = new AiFieldGenerationConfig();
        bad.setFallback(true); // no promptKey / aiDefinitionKey
        assertThrows(IllegalArgumentException.class, () -> selector.validate(Collections.singletonList(bad)));
    }

    @Test
    public void validate_skipRuleNeedsNoKeys() {
        // a skip rule with a filter and no prompt/model is valid
        assertDoesNotThrow(() -> selector.validate(Collections.singletonList(skipRule(Arrays.asList(".tmp"), 0))));
    }
    // </editor-fold>
}
