// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

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
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.FactDefinition;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FactDefinitionLoaderTest {

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

    private static FactDefinition fromFile(final String key, final @Nullable String factsFile) {
        final FactDefinition definition = new FactDefinition();
        definition.setKey(key);
        definition.setFactsFile(factsFile);
        return definition;
    }

    private static FactDefinition inline(final String key, final String label, final String pattern) {
        final FactCounter counter = new FactCounter();
        counter.setLabel(label);
        counter.setPattern(pattern);
        final FactDefinition definition = new FactDefinition();
        definition.setKey(key);
        definition.setFacts(Collections.singletonList(counter));
        return definition;
    }

    private List<FactDefinition> load(
            final @Nullable List<FactDefinition> definitions, final @Nullable String... usedKeys) throws IOException {
        return new FactDefinitionLoader(profileDirectory.toFile()).load(definitions, Arrays.asList(usedKeys));
    }

    private static List<String> describe(final FactDefinition definition) {
        final List<String> counters = new ArrayList<>();
        for (final FactCounter counter : Objects.requireNonNull(definition.getFacts())) {
            counters.add(counter.getLabel() + " = " + counter.getPattern());
        }
        return counters;
    }

    // <editor-fold defaultstate="collapsed" desc="load: keys">
    @Test
    public void load_aDefinitionWithoutKey_isRefusedNamingItsIndex() {
        // arrange
        final FactDefinition keyless = inline("a", "a", "x");
        keyless.setKey(null);
        final List<FactDefinition> definitions = Arrays.asList(inline("b", "b", "y"), keyless);

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(definitions));

        // assert
        assertThat(thrown.getMessage(), is("factDefinitions[1].key is required (bad entry: " + keyless + ")"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parse">
    @Test
    public void parse_labelTabPattern_skippingCommentsAndEmptyLines() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse("f", SQL_FACTS);

        // assert
        assertThat(counters.size(), is(2));
        assertThat(counters.get(0).getLabel(), is("tables"));
        assertThat(counters.get(0).getPattern(), is("(?im)^\\s*CREATE\\s+TABLE\\b"));
        assertThat(counters.get(1).getLabel(), is("views"));
    }

    @Test
    public void parse_patternKeepsLaterTabsAndSpaces_labelIsTrimmed() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse("f", " a b \t x\ty \n");

        // assert
        assertThat(counters.get(0).getLabel(), is("a b"));
        assertThat(counters.get(0).getPattern(), is(" x\ty "));
    }

    @Test
    public void parse_windowsLineEndings_areNotPartOfThePattern() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse("f", "# c\r\na\tx\r\nb\ty\r\n");

        // assert
        assertThat(counters.get(0).getPattern(), is("x"));
        assertThat(counters.get(1).getPattern(), is("y"));
    }

    @Test
    public void parse_lastLineWithoutLineBreak_counts() {
        // act, assert
        assertThat(FactDefinitionLoader.parse("f", "a\tx\nb\ty").size(), is(2));
    }

    @Test
    public void parse_hashInsideAPattern_isNotAComment() {
        // act, assert
        assertThat(FactDefinitionLoader.parse("f", "hash\t#\\w+\n").get(0).getPattern(), is("#\\w+"));
    }

    @Test
    public void parse_lineWithoutTab_isRefusedWithItsLineNumber() {
        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f.tsv", "# c\na\tx\nb x\n"));

        // assert
        assertThat(thrown.getMessage(), is("f.tsv line 3: expected label<TAB>pattern, found no tab"));
    }

    @Test
    public void parse_emptyLabel_isRefused() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", " \tx\n"));

        // assert
        assertThat(thrown.getMessage(), is("f line 1: the label and the pattern must not be empty"));
    }

    @Test
    public void parse_lineStartingWithTheTab_isAnEmptyLabelNotAMissingTab() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "\tx\n"));

        // assert
        assertThat(thrown.getMessage(), is("f line 1: the label and the pattern must not be empty"));
    }

    @Test
    public void parse_blankPattern_isRefused() {
        // act, assert
        assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "a\t \n"));
    }

    @Test
    public void parse_patternThatDoesNotCompile_isRefusedWithItsLineNumber() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "a\tx\nb\t(x\n"));

        // assert
        assertThat(thrown.getMessage(), containsString("f line 2: the pattern does not compile: "));
    }

    @Test
    public void parse_onlyComments_isRefused() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "# a\n\n# b\n"));

        // assert
        assertThat(thrown.getMessage(), is("f: no counter, only comments"));
    }

    @Test
    public void parse_ignorePrefix_marksAnIgnoreCounterUnderTheRestOfTheLabel() {
        // act
        final List<FactCounter> counters =
                FactDefinitionLoader.parse("f", "[ignore] POD\t(?ms)^=pod.*?^=cut\nsubs\tx\n");

        // assert
        assertThat(counters.get(0).isIgnore(), is(true));
        assertThat(counters.get(0).getLabel(), is("POD"));
        assertThat(counters.get(0).getPattern(), is("(?ms)^=pod.*?^=cut"));
        assertThat(counters.get(1).isIgnore(), is(false));
        assertThat(counters.get(1).getLabel(), is("subs"));
    }

    @Test
    public void parse_ignorePrefixWithoutAName_isRefused() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "[ignore] \tx\n"));

        // assert
        assertThat(thrown.getMessage(), is("f line 1: the label and the pattern must not be empty"));
    }

    @Test
    public void parse_labelContainingThePrefixLater_isAnOrdinaryCounter() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse("f", "rows [ignore] x\ty\n");

        // assert
        assertThat(counters.get(0).isIgnore(), is(false));
        assertThat(counters.get(0).getLabel(), is("rows [ignore] x"));
    }

    @Test
    public void parse_onlyIgnoreCounters_isRefused() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "[ignore] a\tx\n"));

        // assert
        assertThat(thrown.getMessage(), is("f: no counter, only ignore patterns"));
    }

    @Test
    public void parse_ignorePrefixInAnyCase_marksAnIgnoreCounter() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse("f", "[IGNORE] POD\tx\n[Ignore] c\ty\nsubs\tz\n");

        // assert
        assertThat(counters.get(0).isIgnore(), is(true));
        assertThat(counters.get(0).getLabel(), is("POD"));
        assertThat(counters.get(1).isIgnore(), is(true));
        assertThat(counters.get(1).getLabel(), is("c"));
    }

    @Test
    public void parse_unknownMarker_isRefusedWithItsLineNumber() {
        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "subs\tx\n[skip] POD\ty\n"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("f line 2: unknown marker in '[skip] POD'; the only ones are [ignore], [raw], [distinct] and"
                        + " [include]"));
    }

    @Test
    public void parse_rawAndDistinctMarkers_markTheCounterUnderTheRestOfTheLabel() {
        // act
        final List<FactCounter> counters = FactDefinitionLoader.parse(
                "f", "[raw] TODO\tx\n[distinct] names\ty\n[Distinct] [RAW] both\tz\nplain\tw\n");

        // assert
        assertThat(counters.get(0).getLabel(), is("TODO"));
        assertThat(counters.get(0).isRaw(), is(true));
        assertThat(counters.get(0).isDistinct(), is(false));
        assertThat(counters.get(1).getLabel(), is("names"));
        assertThat(counters.get(1).isRaw(), is(false));
        assertThat(counters.get(1).isDistinct(), is(true));
        assertThat(counters.get(2).getLabel(), is("both"));
        assertThat(counters.get(2).isRaw(), is(true));
        assertThat(counters.get(2).isDistinct(), is(true));
        assertThat(counters.get(3).isRaw(), is(false));
        assertThat(counters.get(3).isDistinct(), is(false));
        assertThat(counters.get(3).isIgnore(), is(false));
    }

    @Test
    public void parse_ignoreMarkerWithRaw_isRefused() {
        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "[ignore] [raw] c\tx\nn\ty\n"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("f counter 1 ('c') is an ignore counter, which counts nothing; raw and distinct apply to counters"));
    }

    @Test
    public void parse_labelUsedTwice_isRefused() {
        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "subs\tx\n[ignore] subs\ty\n"));

        // assert
        assertThat(thrown.getMessage(), is("f: the label 'subs' is used twice"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: factsFile">
    @Test
    public void load_nothingConfiguredOrUsed_isEmpty() throws IOException {
        // act, assert
        assertThat(load(null).isEmpty(), is(true));
    }

    @Test
    public void load_inlineDefinition_isPassedThroughUnchanged() throws IOException {
        // arrange
        final FactDefinition definition = inline("java-facts", "TODO", "TODO");

        // act
        final List<FactDefinition> loaded = load(Arrays.asList(definition), "java-facts");

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_relativeFactsFile_isReadFromTheProfileDirectory() throws IOException {
        // arrange
        write("v2/sql-facts.tsv", SQL_FACTS);
        final FactDefinition definition = fromFile("sql-facts", "v2/sql-facts.tsv");

        // act
        final List<FactDefinition> loaded = load(Arrays.asList(definition));

        // assert
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
        // arrange
        final Path file = elsewhere.resolve("mine.tsv");
        Files.write(file, "mine\tx\n".getBytes(StandardCharsets.UTF_8));

        // act
        final List<FactDefinition> loaded = load(Arrays.asList(fromFile("mine", file.toString())));

        // assert
        assertThat(describe(loaded.get(0)), is(Collections.singletonList("mine = x")));
    }

    @Test
    public void load_factsFileWithByteOrderMark_dropsIt() throws IOException {
        // arrange
        write("bom.tsv", "﻿a\tx\n");

        // act, assert
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
        // arrange
        write("x.tsv", SQL_FACTS);
        final FactDefinition both = inline("sql-facts", "a", "x");
        both.setFactsFile("x.tsv");
        final List<FactDefinition> definitions = Arrays.asList(both);

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(definitions));

        // assert
        assertThat(
                thrown.getMessage(),
                is("factDefinitions[0] (key sql-facts) sets both facts and factsFile; set one of them"));
    }

    @Test
    public void load_missingFactsFile_namesTheDefinitionAndThePath() {
        // arrange
        final List<FactDefinition> definitions =
                Arrays.asList(inline("a", "a", "x"), fromFile("sql-facts", "v1/missing.tsv"));

        // act
        final IOException thrown = assertThrows(IOException.class, () -> load(definitions));

        // assert
        assertThat(thrown.getMessage(), containsString("factDefinitions[1] (key sql-facts): factsFile "));
        assertThat(
                thrown.getMessage(),
                containsString(new File(profileDirectory.toFile(), "v1/missing.tsv").getPath() + " cannot be read: "));
    }

    @Test
    public void load_malformedFactsFile_namesTheDefinitionTheFileAndTheLine() throws IOException {
        // arrange
        write("bad.tsv", "a\tx\nno tab here\n");
        final List<FactDefinition> definitions = Arrays.asList(fromFile("bad", "bad.tsv"));

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(definitions));

        // assert
        assertThat(thrown.getMessage(), containsString("factDefinitions[0] (key bad): factsFile "));
        assertThat(thrown.getMessage(), containsString("bad.tsv line 2: "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="load: used keys from the profile directory">
    @Test
    public void load_usedKeyWithoutDefinition_isReadFromItsFileInTheProfileDirectory() throws IOException {
        // arrange
        write("sql-facts.tsv", SQL_FACTS);

        // act
        final List<FactDefinition> loaded = load(null, "sql-facts");

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0).getKey(), is("sql-facts"));
        assertThat(describe(loaded.get(0)).size(), is(2));
    }

    @Test
    public void load_usedKeys_comeAfterTheDefinitionsInTheOrderUsed() throws IOException {
        // arrange
        write("b.tsv", "b\tx\n");
        write("c.tsv", "c\tx\n");

        // act
        final List<FactDefinition> loaded = load(Arrays.asList(inline("a", "a", "x")), "c", "b");

        // assert
        assertThat(loaded.get(0).getKey(), is("a"));
        assertThat(loaded.get(1).getKey(), is("c"));
        assertThat(loaded.get(2).getKey(), is("b"));
    }

    @Test
    public void load_usedKeyWithADefinition_keepsTheDefinition() throws IOException {
        // arrange
        write("java-facts.tsv", "from the file\tx\n");
        final FactDefinition definition = inline("java-facts", "inline", "x");

        // act
        final List<FactDefinition> loaded = load(Arrays.asList(definition), "java-facts");

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_keyUsedTwiceAndNullKeys_readTheFileOnce() throws IOException {
        // arrange
        write("java-facts.tsv", "a\tx\n");

        // act, assert
        assertThat(load(null, null, "java-facts", null, "java-facts").size(), is(1));
    }

    @Test
    public void load_withoutProfileDirectory_returnsTheConfiguredDefinitions() throws IOException {
        // arrange
        final FactDefinition definition = inline("java-facts", "TODO", "TODO");

        // act
        final List<FactDefinition> loaded = new FactDefinitionLoader(null)
                .load(Arrays.asList(definition), Arrays.<@Nullable String>asList("java-facts", "other"));

        // assert
        assertThat(loaded.size(), is(1));
        assertThat(loaded.get(0), is(sameInstance(definition)));
    }

    @Test
    public void load_usedKeyWithoutProfileDirectory_isNotLookedUp() throws IOException {
        // act
        final List<FactDefinition> loaded =
                new FactDefinitionLoader(null).load(null, Arrays.<@Nullable String>asList("java-facts"));

        // assert
        assertThat(loaded.isEmpty(), is(true));
    }

    @Test
    public void load_usedKeyWithoutFile_isRefusedNamingTheFileLookedFor() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "sql-facts"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("factsKey sql-facts is used by a rule, but no factDefinition has it and the profile has no "
                        + new File(profileDirectory.toFile(), "sql-facts.tsv")));
    }

    @Test
    public void load_usedKeyNamingADirectory_isRefused() throws IOException {
        // arrange
        Files.createDirectories(profileDirectory.resolve("dir.tsv"));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> load(null, "dir"));
    }

    @Test
    public void load_usedKeyWithAMalformedFile_namesTheKeyTheFileAndTheLine() throws IOException {
        // arrange
        write("java-facts.tsv", "a\t(\n");

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "java-facts"));

        // assert
        assertThat(thrown.getMessage(), containsString("factsKey java-facts: file "));
        assertThat(thrown.getMessage(), containsString("java-facts.tsv line 1: the pattern does not compile"));
    }

    @Test
    public void load_usedKeyWithAnEmptyFile_isRefused() throws IOException {
        // arrange
        write("java-facts.tsv", "");

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> load(null, "java-facts"));

        // assert
        assertThat(thrown.getMessage(), containsString("factsKey java-facts: file "));
        assertThat(thrown.getMessage(), containsString(" is empty"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="[include]">
    @Test
    public void load_include_putsTheIncludedCountersAtItsPlace() throws IOException {
        // arrange
        write("xml-facts.tsv", "[ignore] comments\t<!--.*?-->\nelements\t<\\w\n");
        write("jee-facts.tsv", "first\ta\n[include]\txml-facts\nservlets\t<servlet\n");

        // act
        final FactDefinition jee = load(null, "jee-facts").get(0);

        // assert
        assertThat(
                describe(jee),
                is(Arrays.asList("first = a", "comments = <!--.*?-->", "elements = <\\w", "servlets = <servlet")));
        assertThat(Objects.requireNonNull(jee.getFacts()).get(1).isIgnore(), is(true));
    }

    @Test
    public void load_includeMarkerInAnyCaseKeyWithBlanksAndNested_readsEveryCounter() throws IOException {
        // arrange
        write("a.tsv", "a\tx\n");
        write("b.tsv", "[INCLUDE]\t a \nb\ty\n");
        write("c.tsv", "[Include]\tb\nc\tz\n");

        // act, assert
        assertThat(describe(load(null, "c").get(0)), is(Arrays.asList("a = x", "b = y", "c = z")));
    }

    @Test
    public void load_include_aloneIsAFile() throws IOException {
        // arrange
        write("a.tsv", "a\tx\n");
        write("b.tsv", "# only the counters of a\n[include]\ta\n");

        // act, assert
        assertThat(describe(load(null, "b").get(0)), is(Arrays.asList("a = x")));
    }

    @Test
    public void load_includeOfItself_isRefusedNamingTheCycle() throws IOException {
        // arrange
        write("a.tsv", "a\tx\n[include]\tb\n");
        write("b.tsv", "[include]\ta\nb\ty\n");
        write("self.tsv", "[include]\tself\n");

        // act
        final IllegalArgumentException cycle = assertThrows(IllegalArgumentException.class, () -> load(null, "a"));
        final IllegalArgumentException self = assertThrows(IllegalArgumentException.class, () -> load(null, "self"));

        // assert
        assertThat(cycle.getMessage(), is("the facts file a includes itself: a -> b -> a"));
        assertThat(self.getMessage(), is("the facts file self includes itself: self -> self"));
    }

    @Test
    public void load_includeOfTheSameFileTwice_isRefusedForTheLabelUsedTwice() throws IOException {
        // arrange
        write("a.tsv", "a\tx\n");
        write("b.tsv", "[include]\ta\n[include]\ta\n");

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(null, "b"));

        // assert
        assertThat(thrown.getMessage(), containsString("the label 'a' is used twice"));
    }

    @Test
    public void load_includeOfAMissingFile_namesIt() throws IOException {
        // arrange
        write("b.tsv", "[include]\tnowhere\n");

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(null, "b"));

        // assert
        assertThat(thrown.getMessage(), containsString("factsKey nowhere is included, but there is no "));
        assertThat(thrown.getMessage(), containsString("nowhere.tsv"));
    }

    @Test
    public void load_includeWithoutAKey_isRefused() throws IOException {
        // arrange
        write("b.tsv", "b\ty\n[include]\t \n");

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(null, "b"));

        // assert
        assertThat(thrown.getMessage(), containsString("line 2: [include] names no facts key"));
    }

    @Test
    public void load_factsFileWithInclude_readsItFromTheProfileDirectory() throws IOException {
        // arrange
        write("a.tsv", "a\tx\n");
        write("own/b.tsv", "[include]\ta\nb\ty\n");

        // act
        final List<FactDefinition> loaded = load(Collections.singletonList(fromFile("mine", "own/b.tsv")));

        // assert
        assertThat(describe(loaded.get(0)), is(Arrays.asList("a = x", "b = y")));
    }

    @Test
    public void load_factsFileWithIncludeAndNoProfileDirectory_isRefused() throws IOException {
        // arrange
        write("own/a.tsv", "a\tx\n");
        final Path b = write("own/b.tsv", "[include]\ta\nb\ty\n");
        final FactDefinition definition = fromFile("mine", b.toAbsolutePath().toString());
        final FactDefinitionLoader loader = new FactDefinitionLoader(null);
        final List<FactDefinition> definitions = Collections.singletonList(definition);
        final List<@Nullable String> usedKeys = Collections.emptyList();

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> loader.load(definitions, usedKeys));

        // assert
        assertThat(
                thrown.getMessage(), containsString("line 1: [include] a needs a profile directory to read it from"));
    }

    @Test
    public void load_includeWithALabelAfterTheMarker_isAnUnknownMarker() throws IOException {
        // arrange
        write("b.tsv", "[include] more\ta\n");

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(null, "b"));

        // assert
        assertThat(thrown.getMessage(), containsString("unknown marker in '[include] more'"));
    }

    @Test
    public void load_includedFileUnreadable_throwsTheIoException() throws IOException {
        // arrange
        write("b.tsv", "[include]\tdir\n");
        Files.createDirectories(profileDirectory.resolve("dir.tsv"));

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> load(null, "b"));

        // assert
        assertThat(thrown.getMessage(), containsString("factsKey dir is included, but there is no "));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parse: [include]">
    @Test
    public void parse_includeWithoutADirectory_isRefused() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "[include]\ta\n"));

        // assert
        assertThat(thrown.getMessage(), is("f line 1: [include] a needs a profile directory to read it from"));
    }

    @Test
    public void parse_unknownMarker_namesEveryMarker() {
        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> FactDefinitionLoader.parse("f", "[count]\ta\n"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("f line 1: unknown marker in '[count]'; the only ones are [ignore], [raw], [distinct] and"
                        + " [include]"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_loaderWithProfileDirectory_namesTheProfileDirectory() {
        // act, assert
        assertThat(
                new FactDefinitionLoader(profileDirectory.toFile()).toString(),
                containsString(profileDirectory.toString()));
    }
    // </editor-fold>
}
