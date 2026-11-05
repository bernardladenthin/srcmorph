// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PromptTemplateLoaderTest {

    private static final String JAVA_PROMPT = "You are a Java code indexer.\nWrite the lead first.\n";

    @TempDir
    Path profileDirectory;

    private static PromptDefinition inline(final String key, final String template) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey(key);
        definition.setTemplate(template);
        return definition;
    }

    private static PromptDefinition fromFile(final String key, final @Nullable String templateFile) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey(key);
        definition.setTemplateFile(templateFile);
        return definition;
    }

    private Path write(final String relative, final String text) throws IOException {
        final Path file = profileDirectory.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private List<PromptDefinition> load(final PromptDefinition... definitions) throws IOException {
        return new PromptTemplateLoader(profileDirectory.toFile())
                .load(Arrays.asList(definitions), Collections.<@Nullable String>emptyList());
    }

    // <editor-fold defaultstate="collapsed" desc="load: keys">
    @Test
    public void load_aDefinitionWithoutKey_isRefusedNamingItsIndex() {
        // arrange
        final PromptDefinition keyless = inline("a", "A.");
        keyless.setKey(null);
        final PromptDefinition keyed = inline("b", "B.");

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(keyed, keyless));

        // assert
        assertThat(thrown.getMessage(), is("promptDefinitions[1].key is required (bad entry: " + keyless + ")"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load">
    @Test
    public void load_nullList_isEmpty() throws IOException {
        // act
        final List<PromptDefinition> loaded = new PromptTemplateLoader(profileDirectory.toFile())
                .load(null, Collections.<@Nullable String>emptyList());

        // assert
        assertThat(loaded.isEmpty(), is(true));
    }

    @Test
    public void load_emptyList_staysEmpty() throws IOException {
        // act, assert
        assertThat(
                new PromptTemplateLoader(null)
                        .load(Collections.<PromptDefinition>emptyList(), Collections.<@Nullable String>emptyList())
                        .isEmpty(),
                is(true));
    }

    @Test
    public void load_inlineTemplate_isPassedThroughUnchanged() throws IOException {
        // arrange
        final PromptDefinition definition = inline("file-body-java", JAVA_PROMPT);

        // act
        final List<PromptDefinition> loaded = load(definition);

        // assert
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_relativeTemplateFile_isReadFromTheProfileDirectory() throws IOException {
        // arrange
        write("v1/file-body-java.md", JAVA_PROMPT);

        // act
        final List<PromptDefinition> loaded = load(fromFile("file-body-java", "v1/file-body-java.md"));

        // assert
        assertThat(loaded.get(0).getKey(), is("file-body-java"));
        assertThat(loaded.get(0).getTemplate(), is(JAVA_PROMPT));
        assertThat(loaded.get(0).getTemplateFile(), is(nullValue()));
    }

    @Test
    @AbsolutePathRequirement
    public void load_absoluteTemplateFile_ignoresTheProfileDirectory(@TempDir final Path elsewhere) throws IOException {
        // arrange
        final Path file = elsewhere.resolve("project-body.md");
        Files.write(file, "Write the overview.".getBytes(StandardCharsets.UTF_8));

        // act
        final List<PromptDefinition> loaded = load(fromFile("project-body", file.toString()));

        // assert
        assertThat(loaded.get(0).getTemplate(), is("Write the overview."));
    }

    /**
     * Without a profile directory the path is relative to the working directory, the module directory
     * under Maven; the file goes below {@code target} so a temp directory on another drive does not
     * matter.
     */
    @Test
    public void load_withoutProfileDirectory_resolvesAgainstTheWorkingDirectory() throws IOException {
        // arrange
        final Path relative = Paths.get("target", "prompt-loader-test", "cwd-relative.md");
        Files.createDirectories(relative.getParent());
        Files.write(relative, JAVA_PROMPT.getBytes(StandardCharsets.UTF_8));
        try {
            // act
            final List<PromptDefinition> loaded = new PromptTemplateLoader(null)
                    .load(
                            Arrays.asList(fromFile("file-body-java", relative.toString())),
                            Collections.<@Nullable String>emptyList());

            // assert
            assertThat(loaded.get(0).getTemplate(), is(JAVA_PROMPT));
        } finally {
            Files.delete(relative);
        }
    }

    /** The file is the system message verbatim: line breaks and trailing whitespace stay. */
    @Test
    public void load_templateFile_keepsWhitespaceAsWritten() throws IOException {
        // arrange
        final String text = "\n  Indent kept.\r\nTrailing spaces kept.   \n\n";
        write("raw.md", text);

        // act, assert
        assertThat(load(fromFile("raw", "raw.md")).get(0).getTemplate(), is(text));
    }

    @Test
    public void load_leadingByteOrderMark_isDropped() throws IOException {
        // arrange
        write("bom.md", "\uFEFF" + JAVA_PROMPT);

        // act, assert
        assertThat(load(fromFile("bom", "bom.md")).get(0).getTemplate(), is(JAVA_PROMPT));
    }

    @Test
    public void load_byteOrderMarkLaterInTheText_staysPartOfIt() throws IOException {
        // arrange
        write("inner.md", "a\uFEFFb");

        // act, assert
        assertThat(load(fromFile("inner", "inner.md")).get(0).getTemplate(), is("a\uFEFFb"));
    }

    @Test
    public void load_mixedDefinitions_keepTheirOrder() throws IOException {
        // arrange
        write("package-body.md", "Summarize the package.");
        final PromptDefinition first = inline("file-body-java", JAVA_PROMPT);

        // act
        final List<PromptDefinition> loaded =
                load(first, fromFile("package-body", "package-body.md"), inline("project-body", "Overview."));

        // assert
        assertThat(loaded.size(), is(3));
        assertThat(loaded.get(0), is(sameInstance(first)));
        assertThat(loaded.get(1).getTemplate(), is("Summarize the package."));
        assertThat(loaded.get(2).getKey(), is("project-body"));
    }

    @Test
    public void load_templateFileDefinition_doesNotModifyTheConfiguredDefinition() throws IOException {
        // arrange
        write("x.md", JAVA_PROMPT);
        final PromptDefinition configured = fromFile("x", "x.md");

        // act
        final PromptDefinition loaded = load(configured).get(0);

        // assert
        assertThat(loaded, is(not(sameInstance(configured))));
        assertThat(configured.getTemplate(), is(nullValue()));
        assertThat(configured.getTemplateFile(), is("x.md"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: used keys from the profile directory">
    private List<PromptDefinition> loadUsing(
            final @Nullable List<PromptDefinition> definitions, final @Nullable String... usedKeys) throws IOException {
        return new PromptTemplateLoader(profileDirectory.toFile()).load(definitions, Arrays.asList(usedKeys));
    }

    @Test
    public void load_usedKeyWithoutDefinition_isReadFromItsFileInTheProfileDirectory() throws IOException {
        // arrange
        write("file-body-java.md", JAVA_PROMPT);

        // act
        final List<PromptDefinition> loaded = loadUsing(null, "file-body-java");

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0).getKey(), is("file-body-java"));
        assertThat(loaded.get(0).getTemplate(), is(JAVA_PROMPT));
        assertThat(loaded.get(0).getTemplateFile(), is(nullValue()));
    }

    @Test
    public void load_usedKeys_comeAfterTheDefinitionsInTheOrderUsed() throws IOException {
        // arrange
        write("b.md", "B.");
        write("c.md", "C.");

        // act
        final List<PromptDefinition> loaded = loadUsing(Arrays.asList(inline("a", "A.")), "c", "b");

        // assert
        assertThat(loaded.get(0).getKey(), is("a"));
        assertThat(loaded.get(1).getTemplate(), is("C."));
        assertThat(loaded.get(2).getTemplate(), is("B."));
    }

    @Test
    public void load_usedKeyWithADefinition_keepsTheDefinition() throws IOException {
        // arrange
        write("file-body-java.md", "the file, not used");
        final PromptDefinition definition = inline("file-body-java", JAVA_PROMPT);

        // act
        final List<PromptDefinition> loaded = loadUsing(Arrays.asList(definition), "file-body-java");

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_keyUsedTwiceAndNullKeys_readTheFileOnce() throws IOException {
        // arrange
        write("package-body.md", "P.");

        // act
        final List<PromptDefinition> loaded = loadUsing(null, null, "package-body", "package-body", null);

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0).getTemplate(), is("P."));
    }

    @Test
    public void load_usedKeyWithoutProfileDirectory_isNotLookedUp() throws IOException {
        // act
        final List<PromptDefinition> loaded = new PromptTemplateLoader(null)
                .load(Arrays.asList(inline("a", "A.")), Arrays.<@Nullable String>asList("a", "missing"));

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0).getKey(), is("a"));
    }

    @Test
    public void load_usedKeyFile_dropsALeadingByteOrderMark() throws IOException {
        // arrange
        write("x.md", "\uFEFF" + JAVA_PROMPT);

        // act, assert
        assertThat(loadUsing(null, "x").get(0).getTemplate(), is(JAVA_PROMPT));
    }

    @Test
    public void load_usedKeyWithoutFile_isRefusedNamingTheFileLookedFor() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> loadUsing(null, "file-body-sql"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("prompt key file-body-sql is used by a rule, but no promptDefinition has it and the profile has no "
                        + new File(profileDirectory.toFile(), "file-body-sql.md")));
    }

    @Test
    public void load_usedKeyNamingADirectory_isRefused() throws IOException {
        // arrange
        Files.createDirectories(profileDirectory.resolve("dir.md"));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> loadUsing(null, "dir"));
    }

    @Test
    public void load_usedKeyWithAnEmptyFile_isRefusedNamingTheKey() throws IOException {
        // arrange
        write("blank.md", " \n");

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> loadUsing(null, "blank"));

        // assert
        assertThat(thrown.getMessage(), containsString("prompt key blank: file "));
        assertThat(thrown.getMessage(), containsString(" is empty"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: refused configurations">
    @Test
    public void load_templateAndTemplateFile_isRefused() throws IOException {
        // arrange
        write("x.md", JAVA_PROMPT);
        final PromptDefinition both = fromFile("file-body-java", "x.md");
        both.setTemplate(JAVA_PROMPT);

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(both));

        // assert
        assertThat(
                thrown.getMessage(),
                is("promptDefinitions[0] (key file-body-java) sets both template and templateFile; set one of them"));
    }

    @Test
    public void load_missingTemplateFile_namesTheDefinitionAndThePath() {
        // arrange
        final PromptDefinition inlineDefinition = inline("a", "A.");
        final PromptDefinition missing = fromFile("package-body", "v1/missing.md");

        // act
        final IOException thrown = assertThrows(IOException.class, () -> load(inlineDefinition, missing));

        // assert
        assertThat(thrown.getMessage(), containsString("promptDefinitions[1] (key package-body): templateFile "));
        assertThat(
                thrown.getMessage(),
                containsString(new File(profileDirectory.toFile(), "v1/missing.md").getPath() + " cannot be read: "));
    }

    @Test
    public void load_unparsablePath_isReportedAsUnreadable() {
        // arrange
        final PromptDefinition bad = fromFile("bad", "bad\u0000.md");

        // act
        final IOException thrown = assertThrows(IOException.class, () -> load(bad));

        // assert
        assertThat(thrown.getMessage(), containsString("promptDefinitions[0] (key bad): templateFile "));
    }

    @Test
    public void load_whitespaceOnlyFile_isRefused() throws IOException {
        // arrange
        write("blank.md", " \n\t\n");
        final PromptDefinition blank = fromFile("blank", "blank.md");

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(blank));

        // assert
        assertThat(thrown.getMessage(), containsString("promptDefinitions[0] (key blank): templateFile "));
        assertThat(thrown.getMessage(), containsString(" is empty"));
    }

    @Test
    public void load_emptyFile_isRefused() throws IOException {
        // arrange
        write("empty.md", "");
        final PromptDefinition empty = fromFile("empty", "empty.md");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> load(empty));
    }

    @Test
    public void load_fileWithOnlyAByteOrderMark_isRefused() throws IOException {
        // arrange
        write("bom-only.md", "\uFEFF");
        final PromptDefinition bomOnly = fromFile("bom-only", "bom-only.md");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> load(bomOnly));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_withProfileDirectory_namesTheProfileDirectory() {
        // act, assert
        assertThat(
                new PromptTemplateLoader(profileDirectory.toFile()).toString(),
                containsString(profileDirectory.toString()));
    }
    // </editor-fold>
}
