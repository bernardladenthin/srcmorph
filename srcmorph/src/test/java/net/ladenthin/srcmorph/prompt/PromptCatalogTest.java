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
import java.util.List;
import net.ladenthin.srcmorph.config.PromptDefinition;
import org.junit.jupiter.api.Test;

public class PromptCatalogTest {

    private static PromptDefinition def(String key, String template) {
        PromptDefinition d = new PromptDefinition();
        d.setKey(key);
        d.setTemplate(template);
        return d;
    }

    // <editor-fold defaultstate="collapsed" desc="systemPrompt">
    @Test
    public void systemPrompt_knownKey_returnsTemplateVerbatim() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("summary", "INSTRUCTIONS %x")));

        // act, assert
        // The template is the system message, returned verbatim — no placeholder substitution.
        assertThat(support.systemPrompt("summary"), is("INSTRUCTIONS %x"));
    }

    @Test
    public void systemPrompt_unknownKey_throwsException() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("summary", "tpl")));

        // act, assert
        // template == null branch.
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("missing"));
    }

    @Test
    public void systemPrompt_blankTemplate_throwsException() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("blank", "   ")));

        // act, assert
        // isBlank(template) branch — kills the negate mutant on the template guard.
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("blank"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="userMessage">
    @Test
    public void userMessage_absolutePath_putsItsNameThenBlankLineThenBody() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.<PromptDefinition>emptyList());
        // Exact form: file name, a blank line, then the body. Kills the separator and the
        // getFileName()-vs-full-path mutants.
        final Path absolute = Paths.get("a", "Foo.java").toAbsolutePath();

        // act, assert
        assertThat(support.userMessage(absolute, "the source"), is("Foo.java\n\nthe source"));
    }

    @Test
    public void userMessage_relativePath_putsTheWholePathWithSlashes() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.<PromptDefinition>emptyList());

        // act, assert
        // the file phase passes the path below the base directory: two web.xml of two modules read apart
        assertThat(
                support.userMessage(Paths.get("billing", "src", "main", "webapp", "WEB-INF", "web.xml"), "x"),
                is("billing/src/main/webapp/WEB-INF/web.xml\n\nx"));
        assertThat(support.userMessage(Paths.get("Foo.java"), "x"), is("Foo.java\n\nx"));
    }

    @Test
    public void userMessage_rootWithoutAName_putsThePathItself() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.<PromptDefinition>emptyList());

        // act, assert
        // A root has a null getFileName() on every OS; on Windows "/" is relative to the drive, so it
        // reads as a relative path, written with "/" either way.
        assertThat(support.userMessage(Paths.get("/"), "x"), is("/\n\nx"));
    }

    @Test
    public void userMessage_absoluteRoot_putsThePathItself() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.<PromptDefinition>emptyList());
        // "/" on Linux, "C:\" on Windows: absolute on both, and without a name
        final Path root = Paths.get("/").toAbsolutePath();

        // act, assert
        assertThat(support.userMessage(root, "x"), is(root + "\n\nx"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildPrompt">
    @Test
    public void buildPrompt_knownKey_combinesSystemThenUser() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("summary", "SYS")));

        // act, assert
        // Combined length proxy = systemPrompt + "\n\n" + userMessage. Exact form kills the
        // join-separator mutant.
        assertThat(support.buildPrompt("summary", Paths.get("Foo.java"), "BODY"), is("SYS\n\nFoo.java\n\nBODY"));
    }

    @Test
    public void buildPrompt_knownKey_includesBothSystemAndUserParts() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("summary", "HEADER-INSTRUCTIONS")));

        // act
        String combined = support.buildPrompt("summary", Paths.get("Bar.java"), "class Bar {}");

        // assert
        assertThat(combined, containsString("HEADER-INSTRUCTIONS"));
        assertThat(combined, containsString("Bar.java"));
        assertThat(combined, containsString("class Bar {}"));
    }

    @Test
    public void buildPrompt_unknownKey_throwsException() {
        // arrange
        PromptCatalog support = new PromptCatalog(Collections.singletonList(def("summary", "x")));
        final Path file = Paths.get("F");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> support.buildPrompt("missing", file, "s"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="construction">
    @Test
    public void constructor_nullKeyInDefinition_throwsExceptionNamingTheIndex() {
        // arrange
        final List<PromptDefinition> definitions = Collections.singletonList(def(null, "tpl"));

        // act
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new PromptCatalog(definitions));

        // assert
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].key is required"));
    }

    @Test
    public void constructor_keyDefinedTwice_throwsExceptionNamingItsSecondIndex() {
        // arrange
        final List<PromptDefinition> definitions = Arrays.asList(def("summary", "a"), def("summary", "b"));

        // act
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new PromptCatalog(definitions));

        // assert
        assertThat(ex.getMessage(), is("promptDefinitions[1].key 'summary' is defined twice"));
    }

    @Test
    public void constructor_nullTemplateInDefinition_throwsExceptionNamingTheIndex() {
        // arrange
        final List<PromptDefinition> definitions = Collections.singletonList(def("key", null));

        // act
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new PromptCatalog(definitions));

        // assert
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].template is required"));
    }

    @Test
    public void constructor_nullDefinitionsList_isTreatedAsEmpty() {
        // act
        PromptCatalog support = new PromptCatalog(null);

        // assert
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("any"));
    }

    @Test
    public void constructor_multipleDefinitions_registersAll() {
        // act
        PromptCatalog support = new PromptCatalog(Arrays.asList(def("a", "A-TPL"), def("b", "B-TPL")));

        // assert
        assertThat(support.systemPrompt("a"), is("A-TPL"));
        assertThat(support.systemPrompt("b"), is("B-TPL"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="composedKey">
    @Test
    public void composedKey_joinsTheKeys_orIsTheRuleKeyAlone() {
        // act, assert
        assertThat(PromptCatalog.composedKey("body", "map"), is("body+map"));
        assertThat(PromptCatalog.composedKey("body", null), is("body"));
        assertThat(PromptCatalog.composedKey("body", " "), is("body"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="systemPrompt: composed keys">
    @Test
    public void systemPrompt_ofAComposedKey_isBothTemplatesSeparatedByABlankLine() {
        // arrange
        final PromptCatalog support = new PromptCatalog(Arrays.asList(def("body", "BODY"), def("map", "MAP")));

        // act, assert
        assertThat(support.systemPrompt(PromptCatalog.composedKey("body", "map")), is("BODY\n\nMAP"));
    }

    @Test
    public void systemPrompt_ofAComposedKeyWithAnUnknownPart_throws() {
        // arrange
        final PromptCatalog support = new PromptCatalog(Collections.singletonList(def("body", "BODY")));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("body+missing"));
        assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("missing+body"));
    }

    @Test
    public void systemPrompt_ofAKeyStartingWithTheSeparator_looksUpAnEmptyRuleKey() {
        // arrange
        final PromptCatalog support = new PromptCatalog(Collections.singletonList(def("body", "BODY")));

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> support.systemPrompt("+body"));

        // assert
        assertThat(thrown.getMessage(), is("Missing prompt template for key: "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructor: composed-key separator">
    @Test
    public void constructor_aKeyWithTheSeparator_isRefused() {
        // arrange
        final List<PromptDefinition> definitions = Collections.singletonList(def("a+b", "T"));

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> new PromptCatalog(definitions));

        // assert
        assertThat(thrown.getMessage(), containsString("'a+b' contains '+'"));
    }

    @Test
    public void constructor_aKeyStartingWithTheSeparator_isRefused() {
        // arrange
        final List<PromptDefinition> definitions = Collections.singletonList(def("+b", "T"));

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> new PromptCatalog(definitions));

        // assert
        assertThat(thrown.getMessage(), containsString("'+b' contains '+'"));
    }
    // </editor-fold>
}
