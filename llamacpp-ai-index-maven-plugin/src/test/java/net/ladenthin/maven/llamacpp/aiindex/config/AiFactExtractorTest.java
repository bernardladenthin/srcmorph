// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiFactExtractorTest {

    private static AiFactCounter counter(final String label, final String pattern) {
        final AiFactCounter c = new AiFactCounter();
        c.setLabel(label);
        c.setPattern(pattern);
        return c;
    }

    @Test
    public void factsBlock_nullCounters_returnsEmpty() {
        assertThat(AiFactExtractor.factsBlock(null, "anything"), is(""));
    }

    @Test
    public void factsBlock_emptyCounters_returnsEmpty() {
        assertThat(AiFactExtractor.factsBlock(Collections.emptyList(), "anything"), is(""));
    }

    @Test
    public void factsBlock_countsOccurrencesAcrossTheWholeSource() {
        // \bboolean\b matches each occurrence (2), regardless of line.
        final String block = AiFactExtractor.factsBlock(
                Collections.singletonList(counter("boolean fields", "\\bboolean\\b")), "boolean a; int i; boolean b;");
        assertThat(block, containsString("boolean fields: 2"));
    }

    @Test
    public void factsBlock_multilineAnchorCountsPerLine() {
        // (?m)^INSERT counts lines starting with INSERT (2), not the CREATE line.
        final String block = AiFactExtractor.factsBlock(
                Collections.singletonList(counter("rows", "(?m)^INSERT")), "INSERT a\nINSERT b\nCREATE c\n");
        assertThat(block, containsString("rows: 2"));
    }

    @Test
    public void factsBlock_zeroMatches_reportsZero() {
        final String block = AiFactExtractor.factsBlock(
                Collections.singletonList(counter("views", "(?m)^CREATE VIEW")), "no views here");
        assertThat(block, containsString("views: 0"));
    }

    @Test
    public void factsBlock_multipleCounters_joinedWithSeparatorHeaderAndSuffix() {
        final String block = AiFactExtractor.factsBlock(
                Arrays.asList(counter("tables", "(?m)^CREATE TABLE"), counter("rows", "(?m)^INSERT")),
                "CREATE TABLE t\nINSERT a\nINSERT b\n");
        assertThat(block, startsWith(AiFactExtractor.FACTS_HEADER));
        assertThat(block, containsString("tables" + AiFactExtractor.LABEL_COUNT_SEPARATOR + "1"));
        assertThat(block, containsString(AiFactExtractor.ENTRY_SEPARATOR + "rows: 2"));
        assertThat(block.endsWith(AiFactExtractor.FACTS_SUFFIX), is(true));
    }

    @Test
    public void factsBlock_singleCounter_hasNoEntrySeparator() {
        final String block =
                AiFactExtractor.factsBlock(Collections.singletonList(counter("rows", "(?m)^INSERT")), "INSERT a\n");
        assertThat(block.contains(AiFactExtractor.ENTRY_SEPARATOR), is(false));
    }

    @Test
    public void factsBlock_counterWithNullLabelOrPattern_isSkipped() {
        // Both counters malformed -> nothing to report -> empty string.
        assertThat(AiFactExtractor.factsBlock(Arrays.asList(counter(null, "x"), counter("y", null)), "xxxx"), is(""));
    }

    @Test
    public void factsBlock_mixOfValidAndSkipped_reportsOnlyValid() {
        final String block = AiFactExtractor.factsBlock(
                Arrays.asList(counter(null, "x"), counter("rows", "(?m)^INSERT")), "INSERT a\n");
        assertThat(block, startsWith(AiFactExtractor.FACTS_HEADER));
        assertThat(block, containsString("rows: 1"));
    }

    @Test
    public void validate_null_isAllowed() {
        assertDoesNotThrow(() -> AiFactExtractor.validate(null));
    }

    @Test
    public void validate_validCounters_doesNotThrow() {
        assertDoesNotThrow(() -> AiFactExtractor.validate(Collections.singletonList(counter("rows", "(?m)^INSERT"))));
    }

    @Test
    public void validate_nullLabel_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AiFactExtractor.validate(Collections.singletonList(counter(null, "x"))));
    }

    @Test
    public void validate_nullPattern_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AiFactExtractor.validate(Collections.singletonList(counter("x", null))));
    }

    @Test
    public void validate_invalidRegex_throwsNamingTheLabel() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> AiFactExtractor.validate(Collections.singletonList(counter("bad", "["))));
        assertThat(ex.getMessage(), containsString("bad"));
    }

    @Test
    public void validate_nullEntryInList_isSkipped() {
        assertDoesNotThrow(() -> AiFactExtractor.validate(Collections.singletonList(null)));
    }
}
