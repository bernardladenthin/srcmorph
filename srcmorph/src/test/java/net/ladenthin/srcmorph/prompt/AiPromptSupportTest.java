// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiPromptSupportTest {

    private static AiPromptDefinition def(String key, String template) {
        AiPromptDefinition d = new AiPromptDefinition();
        d.setKey(key);
        d.setTemplate(template);
        return d;
    }

    // <editor-fold defaultstate="collapsed" desc="systemPrompt">
    @Test
    public void systemPromptReturnsTemplateVerbatim() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "INSTRUCTIONS %x")));
        // The template is the system message, returned verbatim — no placeholder substitution.
        assertThat(support.systemPrompt("summary"), is("INSTRUCTIONS %x"));
    }

    @Test
    public void systemPromptThrowsWhenKeyUnknown() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "tpl")));
        // template == null branch.
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("missing"));
    }

    @Test
    public void systemPromptThrowsWhenTemplateBlank() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("blank", "   ")));
        // isBlank(template) branch — kills the negate mutant on the template guard.
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("blank"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="userMessage">
    @Test
    public void userMessagePutsNameThenBlankLineThenBody() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // Exact form: file name, a blank line, then the body. Kills the separator and the
        // getFileName()-vs-full-path mutants.
        assertThat(support.userMessage(Paths.get("a", "Foo.java"), "the source"), is("Foo.java\n\nthe source"));
    }

    @Test
    public void userMessageUsesFullPathWhenFileNameNull() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // A root path has a null getFileName() on every OS, so userMessage falls back to the full
        // path. Derive the expected separator from the same Path so the assertion holds on Windows.
        Path root = Paths.get("/");
        assertThat(support.userMessage(root, "x"), is(root + "\n\nx"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildPrompt">
    @Test
    public void buildPromptCombinesSystemThenUser() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "SYS")));
        // Combined length proxy = systemPrompt + "\n\n" + userMessage. Exact form kills the
        // join-separator mutant.
        assertThat(support.buildPrompt("summary", Paths.get("Foo.java"), "BODY"), is("SYS\n\nFoo.java\n\nBODY"));
    }

    @Test
    public void buildPromptIncludesBothSystemAndUserParts() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "HEADER-INSTRUCTIONS")));
        String combined = support.buildPrompt("summary", Paths.get("Bar.java"), "class Bar {}");
        assertThat(combined, containsString("HEADER-INSTRUCTIONS"));
        assertThat(combined, containsString("Bar.java"));
        assertThat(combined, containsString("class Bar {}"));
    }

    @Test
    public void buildPromptThrowsWhenKeyUnknown() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "x")));
        assertThrows(IllegalArgumentException.class, () -> support.buildPrompt("missing", Paths.get("F"), "s"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="construction">
    @Test
    public void nullKeyInDefinitionThrowsNamedNpe() {
        // Exercises the requireNonNull(key) message supplier lambda.
        NullPointerException ex = assertThrows(
                NullPointerException.class, () -> new AiPromptSupport(Collections.singletonList(def(null, "tpl"))));
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].key"));
    }

    @Test
    public void nullTemplateInDefinitionThrowsNamedNpe() {
        // Exercises the requireNonNull(template) message supplier lambda.
        NullPointerException ex = assertThrows(
                NullPointerException.class, () -> new AiPromptSupport(Collections.singletonList(def("key", null))));
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].template"));
    }

    @Test
    public void nullDefinitionsListIsTreatedAsEmpty() {
        AiPromptSupport support = new AiPromptSupport(null);
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("any"));
    }

    @Test
    public void multipleDefinitionsAreAllRegistered() {
        AiPromptSupport support = new AiPromptSupport(Arrays.asList(def("a", "A-TPL"), def("b", "B-TPL")));
        assertThat(support.systemPrompt("a"), is("A-TPL"));
        assertThat(support.systemPrompt("b"), is("B-TPL"));
    }
    // </editor-fold>
}
