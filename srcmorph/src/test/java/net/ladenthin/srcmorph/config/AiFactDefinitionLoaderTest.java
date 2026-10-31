// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AiFactDefinitionLoaderTest {

    private static final String SQL_FACTS =
            "# SQL counters\n\ntables\t(?im)^\\s*CREATE\\s+TABLE\\b\nviews\t(?im)^\\s*CREATE\\s+VIEW\\b\n";

    @TempDir
    Path profileDirectory;

    private Path write(final String relative, final String text) throws IOException {
        final Path file = profileDirectory.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static AiFactDefinition fromFile(final String key, final @Nullable String factsFile) {
        final AiFactDefinition definition = new AiFactDefinition();
        definition.setKey(key);
        definition.setFactsFile(factsFile);
        return definition;
    }

    private static AiFactDefinition inline(final String key, final String label, final String pattern) {
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel(label);
        counter.setPattern(pattern);
        final AiFactDefinition definition = new AiFactDefinition();
        definition.setKey(key);
        definition.setFacts(Collections.singletonList(counter));
        return definition;
    }

    private List<AiFactDefinition> load(
            final @Nullable List<AiFactDefinition> definitions, final @Nullable String... usedKeys) throws IOException {
        return new AiFactDefinitionLoader(profileDirectory.toFile()).load(definitions, Arrays.asList(usedKeys));
    }

    private static List<String> describe(final AiFactDefinition definition) {
        final List<String> counters = new ArrayList<>();
        for (final AiFactCounter counter : Objects.requireNonNull(definition.getFacts())) {
            counters.add(counter.getLabel() + " = " + counter.getPattern());
        }
        return counters;
    }

    // <editor-fold defaultstate="collapsed" desc="parse">
    @Test
    public void parse_labelTabPattern_skippingCommentsAndEmptyLines() {
        final List<AiFactCounter> counters = AiFactDefinitionLoader.parse("f", SQL_FACTS);

        assertThat(counters.size(), is(2));
        assertThat(counters.get(0).getLabel(), is("tables"));
        assertThat(counters.get(0).getPattern(), is("(?im)^\\s*CREATE\\s+TABLE\\b"));
        assertThat(counters.get(1).getLabel(), is("views"));
    }

    @Test
    public void parse_patternKeepsLaterTabsAndSpaces_labelIsTrimmed() {
        final List<AiFactCounter> counters = AiFactDefinitionLoader.parse("f", " a b \t x\ty \n");

        assertThat(counters.get(0).getLabel(), is("a b"));
        assertThat(counters.get(0).getPattern(), is(" x\ty "));
    }

    @Test
    public void parse_windowsLineEndings_areNotPartOfThePattern() {
        final List<AiFactCounter> counters = AiFactDefinitionLoader.parse("f", "# c\r\na\tx\r\nb\ty\r\n");

        assertThat(counters.get(0).getPattern(), is("x"));
        assertThat(counters.get(1).getPattern(), is("y"));
    }

    @Test
    public void parse_lastLineWithoutLineBreak_counts() {
        assertThat(AiFactDefinitionLoader.parse("f", "a\tx\nb\ty").size(), is(2));
    }

    @Test
    public void parse_hashInsideAPattern_isNotAComment() {
        assertThat(AiFactDefinitionLoader.parse("f", "hash\t#\\w+\n").get(0).getPattern(), is("#\\w+"));
    }

    @Test
    public void parse_lineWithoutTab_isRefusedWithItsLineNumber() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f.tsv", "# c\na\tx\nb x\n"));

        assertThat(thrown.getMessage(), is("f.tsv line 3: expected label<TAB>pattern, found no tab"));
    }

    @Test
    public void parse_emptyLabel_isRefused() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f", " \tx\n"));

        assertThat(thrown.getMessage(), is("f line 1: the label and the pattern must not be empty"));
    }

    @Test
    public void parse_lineStartingWithTheTab_isAnEmptyLabelNotAMissingTab() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f", "\tx\n"));

        assertThat(thrown.getMessage(), is("f line 1: the label and the pattern must not be empty"));
    }

    @Test
    public void parse_blankPattern_isRefused() {
        assertThrows(IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f", "a\t \n"));
    }

    @Test
    public void parse_patternThatDoesNotCompile_isRefusedWithItsLineNumber() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f", "a\tx\nb\t(x\n"));

        assertThat(thrown.getMessage(), containsString("f line 2: the pattern does not compile: "));
    }

    @Test
    public void parse_onlyComments_isRefused() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> AiFactDefinitionLoader.parse("f", "# a\n\n# b\n"));

        assertThat(thrown.getMessage(), is("f: no counter, only comments"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: factsFile">
    @Test
    public void load_nothingConfiguredOrUsed_isEmpty() throws IOException {
        assertThat(load(null).isEmpty(), is(true));
    }

    @Test
    public void load_inlineDefinition_isPassedThroughUnchanged() throws IOException {
        final AiFactDefinition definition = inline("java-facts", "TODO", "TODO");

        final List<AiFactDefinition> loaded = load(Arrays.asList(definition), "java-facts");

        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_relativeFactsFile_isReadFromTheProfileDirectory() throws IOException {
        write("v2/sql-facts.tsv", SQL_FACTS);
        final AiFactDefinition definition = fromFile("sql-facts", "v2/sql-facts.tsv");

        final List<AiFactDefinition> loaded = load(Arrays.asList(definition));

        assertThat(loaded.get(0).getKey(), is("sql-facts"));
        assertThat(
                describe(loaded.get(0)),
                is(Arrays.asList("tables = (?im)^\\s*CREATE\\s+TABLE\\b", "views = (?im)^\\s*CREATE\\s+VIEW\\b")));
        assertThat(loaded.get(0).getFactsFile(), is(nullValue()));
        // the configured definition is not modified
        assertThat(definition.getFacts(), is(nullValue()));
        assertThat(definition.getFactsFile(), is("v2/sql-facts.tsv"));
    }

    @Test
    @AbsolutePathRequirement
    public void load_absoluteFactsFile_ignoresTheProfileDirectory(@TempDir final Path elsewhere) throws IOException {
        final Path file = elsewhere.resolve("mine.tsv");
        Files.write(file, "mine\tx\n".getBytes(StandardCharsets.UTF_8));

        final List<AiFactDefinition> loaded = load(Arrays.asList(fromFile("mine", file.toString())));

        assertThat(describe(loaded.get(0)), is(Collections.singletonList("mine = x")));
    }

    @Test
    public void load_factsFileWithByteOrderMark_dropsIt() throws IOException {
        write("bom.tsv", "﻿a\tx\n");

        assertThat(
                load(Arrays.asList(fromFile("bom", "bom.tsv")))
                        .get(0)
                        .getFacts()
                        .get(0)
                        .getLabel(),
                is("a"));
    }

    @Test
    public void load_factsAndFactsFile_isRefused() throws IOException {
        write("x.tsv", SQL_FACTS);
        final AiFactDefinition both = inline("sql-facts", "a", "x");
        both.setFactsFile("x.tsv");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(Arrays.asList(both)));

        assertThat(
                thrown.getMessage(),
                is("factDefinitions[0] (key sql-facts) sets both facts and factsFile; set one of them"));
    }

    @Test
    public void load_missingFactsFile_namesTheDefinitionAndThePath() {
        final IOException thrown = assertThrows(
                IOException.class,
                () -> load(Arrays.asList(inline("a", "a", "x"), fromFile("sql-facts", "v1/missing.tsv"))));

        assertThat(thrown.getMessage(), containsString("factDefinitions[1] (key sql-facts): factsFile "));
        assertThat(
                thrown.getMessage(),
                containsString(new File(profileDirectory.toFile(), "v1/missing.tsv").getPath() + " cannot be read: "));
    }

    @Test
    public void load_malformedFactsFile_namesTheDefinitionTheFileAndTheLine() throws IOException {
        write("bad.tsv", "a\tx\nno tab here\n");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(Arrays.asList(fromFile("bad", "bad.tsv"))));

        assertThat(thrown.getMessage(), containsString("factDefinitions[0] (key bad): factsFile "));
        assertThat(thrown.getMessage(), containsString("bad.tsv line 2: "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: used keys from the profile directory">
    @Test
    public void load_usedKeyWithoutDefinition_isReadFromItsFileInTheProfileDirectory() throws IOException {
        write("sql-facts.tsv", SQL_FACTS);

        final List<AiFactDefinition> loaded = load(null, "sql-facts");

        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0).getKey(), is("sql-facts"));
        assertThat(describe(loaded.get(0)).size(), is(2));
    }

    @Test
    public void load_usedKeys_comeAfterTheDefinitionsInTheOrderUsed() throws IOException {
        write("b.tsv", "b\tx\n");
        write("c.tsv", "c\tx\n");

        final List<AiFactDefinition> loaded = load(Arrays.asList(inline("a", "a", "x")), "c", "b");

        assertThat(loaded.get(0).getKey(), is("a"));
        assertThat(loaded.get(1).getKey(), is("c"));
        assertThat(loaded.get(2).getKey(), is("b"));
    }

    @Test
    public void load_usedKeyWithADefinition_keepsTheDefinition() throws IOException {
        write("java-facts.tsv", "from the file\tx\n");
        final AiFactDefinition definition = inline("java-facts", "inline", "x");

        final List<AiFactDefinition> loaded = load(Arrays.asList(definition), "java-facts");

        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_keyUsedTwiceAndNullKeys_readTheFileOnce() throws IOException {
        write("java-facts.tsv", "a\tx\n");

        assertThat(load(null, null, "java-facts", null, "java-facts").size(), is(1));
    }

    @Test
    public void load_withoutProfileDirectory_returnsTheConfiguredDefinitions() throws IOException {
        final AiFactDefinition definition = inline("java-facts", "TODO", "TODO");

        final List<AiFactDefinition> loaded = new AiFactDefinitionLoader(null)
                .load(Arrays.asList(definition), Arrays.<@Nullable String>asList("java-facts", "other"));

        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_usedKeyWithoutProfileDirectory_isNotLookedUp() throws IOException {
        final List<AiFactDefinition> loaded =
                new AiFactDefinitionLoader(null).load(null, Arrays.<@Nullable String>asList("java-facts"));

        assertThat(loaded.isEmpty(), is(true));
    }

    @Test
    public void load_usedKeyWithoutFile_isRefusedNamingTheFileLookedFor() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "sql-facts"));

        assertThat(
                thrown.getMessage(),
                is("factsKey sql-facts is used by a rule, but no factDefinition has it and the profile has no "
                        + new File(profileDirectory.toFile(), "sql-facts.tsv")));
    }

    @Test
    public void load_usedKeyNamingADirectory_isRefused() throws IOException {
        Files.createDirectories(profileDirectory.resolve("dir.tsv"));

        assertThrows(IllegalArgumentException.class, () -> load(null, "dir"));
    }

    @Test
    public void load_usedKeyWithAMalformedFile_namesTheKeyTheFileAndTheLine() throws IOException {
        write("java-facts.tsv", "a\t(\n");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "java-facts"));

        assertThat(thrown.getMessage(), containsString("factsKey java-facts: file "));
        assertThat(thrown.getMessage(), containsString("java-facts.tsv line 1: the pattern does not compile"));
    }

    @Test
    public void load_usedKeyWithAnEmptyFile_isRefused() throws IOException {
        write("java-facts.tsv", "");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "java-facts"));

        assertThat(thrown.getMessage(), containsString("factsKey java-facts: file "));
        assertThat(thrown.getMessage(), containsString(" is empty"));
    }
    // </editor-fold>

    @Test
    public void toString_namesTheProfileDirectory() {
        assertThat(
                new AiFactDefinitionLoader(profileDirectory.toFile()).toString(),
                containsString(profileDirectory.toString()));
    }
}
