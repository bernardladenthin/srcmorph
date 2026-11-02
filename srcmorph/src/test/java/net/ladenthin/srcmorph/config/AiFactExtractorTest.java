// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class AiFactExtractorTest {

    private static AiFactCounter counter(final String label, final String pattern) {
        final AiFactCounter c = new AiFactCounter();
        c.setLabel(label);
        c.setPattern(pattern);
        return c;
    }

    private static AiFactCounter ignore(final String label, final String pattern) {
        final AiFactCounter c = counter(label, pattern);
        c.setIgnore(true);
        return c;
    }

    /** A Perl module with a sub inside its POD block, which documents and does not define it. */
    private static final String PERL_WITH_POD = "sub one {}\n=head1 NAME\n\nsub not_a_sub\n\n=cut\nsub two {}\n";

    private static final String POD = "(?ms)^=[a-z]\\w*.*?^=cut\\b[^\\n]*";

    private static final String SUBS = "(?m)^sub\\s+\\w+";

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

    // <editor-fold defaultstate="collapsed" desc="ignore counters">
    @Test
    public void counts_withoutIgnoreCounters_countsTheWholeSource() {
        assertThat(
                AiFactExtractor.counts(Collections.singletonList(counter("subs", SUBS)), PERL_WITH_POD)
                        .get("subs"),
                is(3));
    }

    @Test
    public void counts_ignoreCounter_blanksItsMatchesBeforeTheOthersCount() {
        final Map<String, Integer> counts =
                AiFactExtractor.counts(Arrays.asList(counter("subs", SUBS), ignore("POD", POD)), PERL_WITH_POD);
        assertThat(counts.get("subs"), is(2));
    }

    @Test
    public void counts_ignoreCounter_isNotReported() {
        final Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("subs", 2);
        assertThat(
                AiFactExtractor.counts(Arrays.asList(ignore("POD", POD), counter("subs", SUBS)), PERL_WITH_POD),
                is(expected));
    }

    @Test
    public void counts_ignoredRegion_keepsItsLineBreaks() {
        // Five lines before and after: line-anchored counters and line counts see the same lines.
        final String source = "a\r\n/* x\r\ny\r\n*/\r\nb\r\n";
        final Map<String, Integer> counts = AiFactExtractor.counts(
                Arrays.asList(
                        ignore("comments", "(?s)/\\*.*?\\*/"),
                        counter("line feeds", "\n"),
                        counter("carriage returns", "\r"),
                        counter("b at a line start", "(?m)^b")),
                source);
        assertThat(counts.get("line feeds"), is(5));
        assertThat(counts.get("carriage returns"), is(5));
        assertThat(counts.get("b at a line start"), is(1));
    }

    @Test
    public void counts_anIgnoredRegionWithinALine_keepsTheWordsAroundItApart() {
        final Map<String, Integer> counts = AiFactExtractor.counts(
                Arrays.asList(ignore("comments", "(?s)/\\*.*?\\*/"), counter("joins", "\\bJOIN\\b")),
                "SELECT * FROM t/* x */JOIN u");
        assertThat(counts.get("joins"), is(1));
    }

    @Test
    public void counts_twoIgnoreCounters_bothBlank() {
        final Map<String, Integer> counts = AiFactExtractor.counts(
                Arrays.asList(ignore("a", "x1"), ignore("b", "x2"), counter("x", "x\\d")), "x1 x2 x3");
        assertThat(counts.get("x"), is(1));
    }

    @Test
    public void counts_ignoreCounterWithoutPattern_isSkipped() {
        assertThat(
                AiFactExtractor.counts(Arrays.asList(ignore("a", null), counter("x", "x")), "xx")
                        .get("x"),
                is(2));
    }

    @Test
    public void counts_nullCounters_isEmpty() {
        assertThat(AiFactExtractor.counts(null, "x").isEmpty(), is(true));
    }

    @Test
    public void factsBlock_onlyIgnoreCounters_returnsEmpty() {
        assertThat(AiFactExtractor.factsBlock(Collections.singletonList(ignore("POD", POD)), PERL_WITH_POD), is(""));
    }

    @Test
    public void factsBlock_ignoreCounter_leavesItsLabelOut() {
        final String block =
                AiFactExtractor.factsBlock(Arrays.asList(ignore("POD", POD), counter("subs", SUBS)), PERL_WITH_POD);
        assertThat(block, is(AiFactExtractor.FACTS_HEADER + "subs: 2" + AiFactExtractor.FACTS_SUFFIX));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    private static String refusal(final AiFactCounter... counters) {
        return assertThrows(
                        IllegalArgumentException.class, () -> AiFactExtractor.validate("g", Arrays.asList(counters)))
                .getMessage();
    }

    @Test
    public void validate_nullOrEmpty_isAllowed() {
        assertDoesNotThrow(() -> AiFactExtractor.validate("g", null));
        assertDoesNotThrow(() -> AiFactExtractor.validate("g", Collections.<AiFactCounter>emptyList()));
    }

    @Test
    public void validate_reportedAndIgnoreCounters_doNotThrow() {
        assertDoesNotThrow(
                () -> AiFactExtractor.validate("g", Arrays.asList(ignore("POD", POD), counter("rows", "(?m)^INSERT"))));
    }

    @Test
    public void validate_missingEntry_isRefusedWithItsPosition() {
        assertThat(refusal(counter("a", "x"), null), is("g counter 2 is missing"));
    }

    @Test
    public void validate_nullOrBlankLabel_isRefused() {
        assertThat(refusal(counter(null, "x")), is("g counter 1 must have a label"));
        assertThat(refusal(counter("a", "x"), counter(" ", "x")), is("g counter 2 must have a label"));
    }

    @Test
    public void validate_labelStartingWithAMarker_isRefused() {
        assertThat(
                refusal(counter("[ignore] POD", "x")),
                is("g counter 1: the label '[ignore] POD' starts with '[', which marks a region in a facts file;"
                        + " set ignore instead"));
    }

    @Test
    public void validate_labelUsedTwice_isRefused() {
        assertThat(refusal(counter("rows", "x"), ignore("rows", "y")), is("g: the label 'rows' is used twice"));
    }

    @Test
    public void validate_nullOrBlankPattern_isRefused() {
        assertThat(refusal(counter("x", null)), is("g counter 1 ('x') must have a pattern"));
        assertThat(refusal(counter("x", " ")), is("g counter 1 ('x') must have a pattern"));
    }

    @Test
    public void validate_invalidRegex_isRefusedNamingTheLabel() {
        assertThat(refusal(counter("bad", "[")), startsWith("g: invalid fact pattern for label 'bad': "));
    }

    @Test
    public void validate_onlyIgnoreCounters_isRefused() {
        assertThat(refusal(ignore("POD", POD), ignore("comments", "#.*")), is("g: no counter, only ignore patterns"));
    }
    // </editor-fold>
}
