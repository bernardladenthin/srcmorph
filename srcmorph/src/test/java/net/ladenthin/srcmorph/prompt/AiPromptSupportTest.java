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
    public void userMessage_absolutePath_putsItsNameThenBlankLineThenBody() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // Exact form: file name, a blank line, then the body. Kills the separator and the
        // getFileName()-vs-full-path mutants.
        final Path absolute = Paths.get("a", "Foo.java").toAbsolutePath();
        assertThat(support.userMessage(absolute, "the source"), is("Foo.java\n\nthe source"));
    }

    @Test
    public void userMessage_relativePath_putsTheWholePathWithSlashes() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // the file phase passes the path below the base directory: two web.xml of two modules read apart
        assertThat(
                support.userMessage(Paths.get("billing", "src", "main", "webapp", "WEB-INF", "web.xml"), "x"),
                is("billing/src/main/webapp/WEB-INF/web.xml\n\nx"));
        assertThat(support.userMessage(Paths.get("Foo.java"), "x"), is("Foo.java\n\nx"));
    }

    @Test
    public void userMessage_rootWithoutAName_putsThePathItself() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // A root has a null getFileName() on every OS; on Windows "/" is relative to the drive, so it
        // reads as a relative path, written with "/" either way.
        assertThat(support.userMessage(Paths.get("/"), "x"), is("/\n\nx"));
    }

    @Test
    public void userMessage_absoluteRoot_putsThePathItself() {
        AiPromptSupport support = new AiPromptSupport(Collections.<AiPromptDefinition>emptyList());
        // "/" on Linux, "C:\" on Windows: absolute on both, and without a name
        final Path root = Paths.get("/").toAbsolutePath();
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

    // <editor-fold defaultstate="collapsed" desc="composed keys">
    @Test
    public void composedKey_joinsTheKeys_orIsTheRuleKeyAlone() {
        assertThat(AiPromptSupport.composedKey("body", "map"), is("body+map"));
        assertThat(AiPromptSupport.composedKey("body", null), is("body"));
        assertThat(AiPromptSupport.composedKey("body", " "), is("body"));
    }

    @Test
    public void systemPrompt_ofAComposedKey_isBothTemplatesSeparatedByABlankLine() {
        final AiPromptSupport support = new AiPromptSupport(Arrays.asList(def("body", "BODY"), def("map", "MAP")));

        assertThat(support.systemPrompt(AiPromptSupport.composedKey("body", "map")), is("BODY\n\nMAP"));
    }

    @Test
    public void systemPrompt_ofAComposedKeyWithAnUnknownPart_throws() {
        final AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("body", "BODY")));

        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("body+missing"));
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("missing+body"));
    }

    @Test
    public void constructor_aKeyWithTheSeparator_isRefused() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> new AiPromptSupport(Collections.singletonList(def("a+b", "T"))));

        assertThat(thrown.getMessage(), containsString("'a+b' contains '+'"));
    }

    @Test
    public void constructor_aKeyStartingWithTheSeparator_isRefused() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> new AiPromptSupport(Collections.singletonList(def("+b", "T"))));

        assertThat(thrown.getMessage(), containsString("'+b' contains '+'"));
    }

    @Test
    public void systemPrompt_ofAKeyStartingWithTheSeparator_looksUpAnEmptyRuleKey() {
        final AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("body", "BODY")));

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("+body"));

        assertThat(thrown.getMessage(), is("Missing prompt template for key: "));
    }
    // </editor-fold>
}
