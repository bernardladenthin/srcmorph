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
import java.util.Objects;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AiPromptTemplateLoaderTest {

    private static final String JAVA_PROMPT = "You are a Java code indexer.\nWrite the lead first.\n";

    @TempDir
    Path promptDirectory;

    private static AiPromptDefinition inline(final String key, final String template) {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey(key);
        definition.setTemplate(template);
        return definition;
    }

    private static AiPromptDefinition fromFile(final String key, final @Nullable String templateFile) {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey(key);
        definition.setTemplateFile(templateFile);
        return definition;
    }

    private Path write(final String relative, final String text) throws IOException {
        final Path file = promptDirectory.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private List<AiPromptDefinition> load(final AiPromptDefinition... definitions) throws IOException {
        return Objects.requireNonNull(
                new AiPromptTemplateLoader(promptDirectory.toFile()).load(Arrays.asList(definitions)));
    }

    // <editor-fold defaultstate="collapsed" desc="load">
    @Test
    public void load_nullList_staysNull() throws IOException {
        assertThat(new AiPromptTemplateLoader(promptDirectory.toFile()).load(null), is(nullValue()));
    }

    @Test
    public void load_emptyList_staysEmpty() throws IOException {
        assertThat(
                new AiPromptTemplateLoader(null)
                        .load(Collections.<AiPromptDefinition>emptyList())
                        .isEmpty(),
                is(true));
    }

    @Test
    public void load_inlineTemplate_isPassedThroughUnchanged() throws IOException {
        final AiPromptDefinition definition = inline("file-body-java", JAVA_PROMPT);

        final List<AiPromptDefinition> loaded = load(definition);

        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_relativeTemplateFile_isReadFromThePromptDirectory() throws IOException {
        write("v1/file-body-java.md", JAVA_PROMPT);

        final List<AiPromptDefinition> loaded = load(fromFile("file-body-java", "v1/file-body-java.md"));

        assertThat(loaded.get(0).getKey(), is("file-body-java"));
        assertThat(loaded.get(0).getTemplate(), is(JAVA_PROMPT));
        assertThat(loaded.get(0).getTemplateFile(), is(nullValue()));
    }

    @Test
    @AbsolutePathRequirement
    public void load_absoluteTemplateFile_ignoresThePromptDirectory(@TempDir final Path elsewhere) throws IOException {
        final Path file = elsewhere.resolve("project-body.md");
        Files.write(file, "Write the overview.".getBytes(StandardCharsets.UTF_8));

        final List<AiPromptDefinition> loaded = load(fromFile("project-body", file.toString()));

        assertThat(loaded.get(0).getTemplate(), is("Write the overview."));
    }

    /**
     * Without a prompt directory the path is relative to the working directory, the module directory
     * under Maven; the file goes below {@code target} so a temp directory on another drive does not
     * matter.
     */
    @Test
    public void load_withoutPromptDirectory_resolvesAgainstTheWorkingDirectory() throws IOException {
        final Path relative = Paths.get("target", "prompt-loader-test", "cwd-relative.md");
        Files.createDirectories(relative.getParent());
        Files.write(relative, JAVA_PROMPT.getBytes(StandardCharsets.UTF_8));
        try {
            final List<AiPromptDefinition> loaded = Objects.requireNonNull(new AiPromptTemplateLoader(null)
                    .load(Arrays.asList(fromFile("file-body-java", relative.toString()))));

            assertThat(loaded.get(0).getTemplate(), is(JAVA_PROMPT));
        } finally {
            Files.delete(relative);
        }
    }

    /** The file is the system message verbatim: line breaks and trailing whitespace stay. */
    @Test
    public void load_templateFile_keepsWhitespaceAsWritten() throws IOException {
        final String text = "\n  Indent kept.\r\nTrailing spaces kept.   \n\n";
        write("raw.md", text);

        assertThat(load(fromFile("raw", "raw.md")).get(0).getTemplate(), is(text));
    }

    @Test
    public void load_leadingByteOrderMark_isDropped() throws IOException {
        write("bom.md", "﻿" + JAVA_PROMPT);

        assertThat(load(fromFile("bom", "bom.md")).get(0).getTemplate(), is(JAVA_PROMPT));
    }

    @Test
    public void load_byteOrderMarkLaterInTheText_staysPartOfIt() throws IOException {
        write("inner.md", "a﻿b");

        assertThat(load(fromFile("inner", "inner.md")).get(0).getTemplate(), is("a﻿b"));
    }

    @Test
    public void load_mixedDefinitions_keepTheirOrder() throws IOException {
        write("package-body.md", "Summarize the package.");
        final AiPromptDefinition first = inline("file-body-java", JAVA_PROMPT);

        final List<AiPromptDefinition> loaded =
                load(first, fromFile("package-body", "package-body.md"), inline("project-body", "Overview."));

        assertThat(loaded.size(), is(3));
        assertThat(loaded.get(0), is(sameInstance(first)));
        assertThat(loaded.get(1).getTemplate(), is("Summarize the package."));
        assertThat(loaded.get(2).getKey(), is("project-body"));
    }

    @Test
    public void load_doesNotModifyTheConfiguredDefinition() throws IOException {
        write("x.md", JAVA_PROMPT);
        final AiPromptDefinition configured = fromFile("x", "x.md");

        final AiPromptDefinition loaded = load(configured).get(0);

        assertThat(loaded, is(not(sameInstance(configured))));
        assertThat(configured.getTemplate(), is(nullValue()));
        assertThat(configured.getTemplateFile(), is("x.md"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: refused configurations">
    @Test
    public void load_templateAndTemplateFile_isRefused() throws IOException {
        write("x.md", JAVA_PROMPT);
        final AiPromptDefinition both = fromFile("file-body-java", "x.md");
        both.setTemplate(JAVA_PROMPT);

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(both));

        assertThat(
                thrown.getMessage(),
                is("promptDefinitions[0] (key file-body-java) sets both template and templateFile; set one of them"));
    }

    @Test
    public void load_missingTemplateFile_namesTheDefinitionAndThePath() {
        final IOException thrown = assertThrows(
                IOException.class, () -> load(inline("a", "A."), fromFile("package-body", "v1/missing.md")));

        assertThat(thrown.getMessage(), containsString("promptDefinitions[1] (key package-body): templateFile "));
        assertThat(
                thrown.getMessage(),
                containsString(new File(promptDirectory.toFile(), "v1/missing.md").getPath() + " cannot be read: "));
    }

    @Test
    public void load_unparsablePath_isReportedAsUnreadable() {
        final IOException thrown = assertThrows(IOException.class, () -> load(fromFile("bad", "bad\u0000.md")));

        assertThat(thrown.getMessage(), containsString("promptDefinitions[0] (key bad): templateFile "));
    }

    @Test
    public void load_whitespaceOnlyFile_isRefused() throws IOException {
        write("blank.md", " \n\t\n");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(fromFile("blank", "blank.md")));

        assertThat(thrown.getMessage(), containsString("promptDefinitions[0] (key blank): templateFile "));
        assertThat(thrown.getMessage(), containsString(" is empty"));
    }

    @Test
    public void load_emptyFile_isRefused() throws IOException {
        write("empty.md", "");

        assertThrows(IllegalArgumentException.class, () -> load(fromFile("empty", "empty.md")));
    }

    @Test
    public void load_fileWithOnlyAByteOrderMark_isRefused() throws IOException {
        write("bom-only.md", "﻿");

        assertThrows(IllegalArgumentException.class, () -> load(fromFile("bom-only", "bom-only.md")));
    }
    // </editor-fold>

    @Test
    public void toString_namesThePromptDirectory() {
        assertThat(
                new AiPromptTemplateLoader(promptDirectory.toFile()).toString(),
                containsString(promptDirectory.toString()));
    }
}
