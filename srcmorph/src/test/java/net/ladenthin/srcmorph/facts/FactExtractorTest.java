// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.support.ReadBudget;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class FactExtractorTest {

    private static FactCounter counter(final String label, final String pattern) {
        final FactCounter c = new FactCounter();
        c.setLabel(label);
        c.setPattern(pattern);
        return c;
    }

    private static FactCounter ignore(final String label, final String pattern) {
        final FactCounter c = counter(label, pattern);
        c.setIgnore(true);
        return c;
    }

    /** A Perl module with a sub inside its POD block, which documents and does not define it. */
    private static final String PERL_WITH_POD = "sub one {}\n=head1 NAME\n\nsub not_a_sub\n\n=cut\nsub two {}\n";

    private static final String POD = "(?ms)^=[a-z]\\w*.*?^=cut\\b[^\\n]*";

    private static final String SUBS = "(?m)^sub\\s+\\w+";

    // <editor-fold defaultstate="collapsed" desc="factsBlock">
    @Test
    public void factsBlock_nullCounters_returnsEmpty() {
        // act, assert
        assertThat(FactExtractor.factsBlock(null, "anything"), is(""));
    }

    @Test
    public void factsBlock_emptyCounters_returnsEmpty() {
        // act, assert
        assertThat(FactExtractor.factsBlock(Collections.emptyList(), "anything"), is(""));
    }

    @Test
    public void factsBlock_wordPattern_countsOccurrencesAcrossTheWholeSource() {
        // act
        // \bboolean\b matches each occurrence (2), regardless of line.
        final String block = FactExtractor.factsBlock(
                Collections.singletonList(counter("boolean fields", "\\bboolean\\b")), "boolean a; int i; boolean b;");

        // assert
        assertThat(block, containsString("boolean fields: 2"));
    }

    @Test
    public void factsBlock_multilineAnchor_countsPerLine() {
        // act
        // (?m)^INSERT counts lines starting with INSERT (2), not the CREATE line.
        final String block = FactExtractor.factsBlock(
                Collections.singletonList(counter("rows", "(?m)^INSERT")), "INSERT a\nINSERT b\nCREATE c\n");

        // assert
        assertThat(block, containsString("rows: 2"));
    }

    @Test
    public void factsBlock_zeroMatches_reportsZero() {
        // act
        final String block = FactExtractor.factsBlock(
                Collections.singletonList(counter("views", "(?m)^CREATE VIEW")), "no views here");

        // assert
        assertThat(block, containsString("views: 0"));
    }

    @Test
    public void factsBlock_multipleCounters_joinedWithSeparatorHeaderAndSuffix() {
        // act
        final String block = FactExtractor.factsBlock(
                Arrays.asList(counter("tables", "(?m)^CREATE TABLE"), counter("rows", "(?m)^INSERT")),
                "CREATE TABLE t\nINSERT a\nINSERT b\n");

        // assert
        assertThat(block, startsWith(FactExtractor.FACTS_HEADER));
        assertThat(block, containsString("tables" + FactExtractor.LABEL_COUNT_SEPARATOR + "1"));
        assertThat(block, containsString(FactExtractor.ENTRY_SEPARATOR + "rows: 2"));
        assertThat(block.endsWith(FactExtractor.FACTS_SUFFIX), is(true));
    }

    @Test
    public void factsBlock_singleCounter_hasNoEntrySeparator() {
        // act
        final String block =
                FactExtractor.factsBlock(Collections.singletonList(counter("rows", "(?m)^INSERT")), "INSERT a\n");

        // assert
        assertThat(block.contains(FactExtractor.ENTRY_SEPARATOR), is(false));
    }

    @Test
    public void factsBlock_counterWithNullLabelOrPattern_isSkipped() {
        // act, assert
        // Both counters malformed -> nothing to report -> empty string.
        assertThat(FactExtractor.factsBlock(Arrays.asList(counter(null, "x"), counter("y", null)), "xxxx"), is(""));
    }

    @Test
    public void factsBlock_mixOfValidAndSkipped_reportsOnlyValid() {
        // act
        final String block = FactExtractor.factsBlock(
                Arrays.asList(counter(null, "x"), counter("rows", "(?m)^INSERT")), "INSERT a\n");

        // assert
        assertThat(block, startsWith(FactExtractor.FACTS_HEADER));
        assertThat(block, containsString("rows: 1"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ignore counters">
    @Test
    public void counts_withoutIgnoreCounters_countsTheWholeSource() {
        // act, assert
        assertThat(
                FactExtractor.counts(Collections.singletonList(counter("subs", SUBS)), PERL_WITH_POD)
                        .get("subs"),
                is(3));
    }

    @Test
    public void counts_ignoreCounter_blanksItsMatchesBeforeTheOthersCount() {
        // act
        final Map<String, Integer> counts =
                FactExtractor.counts(Arrays.asList(counter("subs", SUBS), ignore("POD", POD)), PERL_WITH_POD);

        // assert
        assertThat(counts.get("subs"), is(2));
    }

    @Test
    public void counts_ignoreCounter_isNotReported() {
        // arrange
        final Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("subs", 2);

        // act, assert
        assertThat(
                FactExtractor.counts(Arrays.asList(ignore("POD", POD), counter("subs", SUBS)), PERL_WITH_POD),
                is(expected));
    }

    @Test
    public void counts_ignoredRegion_keepsItsLineBreaks() {
        // arrange
        // Five lines before and after: line-anchored counters and line counts see the same lines.
        final String source = "a\r\n/* x\r\ny\r\n*/\r\nb\r\n";

        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(
                        ignore("comments", "(?s)/\\*.*?\\*/"),
                        counter("line feeds", "\n"),
                        counter("carriage returns", "\r"),
                        counter("b at a line start", "(?m)^b")),
                source);

        // assert
        assertThat(counts.get("line feeds"), is(5));
        assertThat(counts.get("carriage returns"), is(5));
        assertThat(counts.get("b at a line start"), is(1));
    }

    @Test
    public void counts_anIgnoredRegionWithinALine_keepsTheWordsAroundItApart() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(ignore("comments", "(?s)/\\*.*?\\*/"), counter("joins", "\\bJOIN\\b")),
                "SELECT * FROM t/* x */JOIN u");

        // assert
        assertThat(counts.get("joins"), is(1));
    }

    @Test
    public void counts_anIgnoredRegionAtTheStartOfALine_leavesTheLineStartingWithWhatFollows() {
        // act
        // the first region starts the text, the second follows a line feed, the third a carriage return
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(ignore("comments", "(?s)/\\*.*?\\*/"), counter("types", "(?m)^class\\b")),
                "/* x */class A {}\n/* y */class B {}\r/* z */class C {}\n");

        // assert
        assertThat(counts.get("types"), is(3));
    }

    /**
     * Java's ^ also starts a line after NEL, the line separator and the paragraph separator: a region
     * holding one keeps it, and a region right after one starts a line.
     */
    @Test
    public void counts_theOtherLineTerminatorsOfJavaRegex_areLineBreaksToo() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(ignore("comments", "(?s)/\\*.*?\\*/"), counter("types", "(?m)^class\\b")),
                "a/* x */class A {}\nb/* y\u0085*/class B {}\n /* z */class C {}\n");

        // assert
        assertThat(counts.get("types"), is(3));
    }

    @Test
    public void counts_twoIgnoreCounters_bothBlank() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(ignore("a", "x1"), ignore("b", "x2"), counter("x", "x\\d")), "x1 x2 x3");

        // assert
        assertThat(counts.get("x"), is(1));
    }

    @Test
    public void counts_ignoreCounterWithoutPattern_isSkipped() {
        // act, assert
        assertThat(
                FactExtractor.counts(Arrays.asList(ignore("a", null), counter("x", "x")), "xx")
                        .get("x"),
                is(2));
    }

    @Test
    public void counts_nullCounters_isEmpty() {
        // act, assert
        assertThat(FactExtractor.counts(null, "x").isEmpty(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factsBlock: ignore counters">
    @Test
    public void factsBlock_onlyIgnoreCounters_returnsEmpty() {
        // act, assert
        assertThat(FactExtractor.factsBlock(Collections.singletonList(ignore("POD", POD)), PERL_WITH_POD), is(""));
    }

    @Test
    public void factsBlock_ignoreCounter_leavesItsLabelOut() {
        // act
        final String block =
                FactExtractor.factsBlock(Arrays.asList(ignore("POD", POD), counter("subs", SUBS)), PERL_WITH_POD);

        // assert
        assertThat(block, is(FactExtractor.FACTS_HEADER + "subs: 2" + FactExtractor.FACTS_SUFFIX));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    private static String refusal(final FactCounter... counters) {
        final List<FactCounter> list = Arrays.asList(counters);
        return assertThrows(IllegalArgumentException.class, () -> FactExtractor.validate("g", list))
                .getMessage();
    }

    @Test
    public void validate_nullOrEmpty_isAllowed() {
        // act, assert
        assertDoesNotThrow(() -> FactExtractor.validate("g", null));
        assertDoesNotThrow(() -> FactExtractor.validate("g", Collections.<FactCounter>emptyList()));
    }

    @Test
    public void validate_reportedAndIgnoreCounters_doNotThrow() {
        // act, assert
        assertDoesNotThrow(
                () -> FactExtractor.validate("g", Arrays.asList(ignore("POD", POD), counter("rows", "(?m)^INSERT"))));
    }

    @Test
    public void validate_missingEntry_isRefusedWithItsPosition() {
        // act, assert
        assertThat(refusal(counter("a", "x"), null), is("g counter 2 is missing"));
    }

    @Test
    public void validate_nullOrBlankLabel_isRefused() {
        // act, assert
        assertThat(refusal(counter(null, "x")), is("g counter 1 must have a label"));
        assertThat(refusal(counter("a", "x"), counter(" ", "x")), is("g counter 2 must have a label"));
    }

    @Test
    public void validate_labelStartingWithAMarker_isRefused() {
        // act, assert
        assertThat(
                refusal(counter("[ignore] POD", "x")),
                is("g counter 1: the label '[ignore] POD' starts with '[', which marks a region in a facts file;"
                        + " set ignore instead"));
    }

    @Test
    public void validate_labelUsedTwice_isRefused() {
        // act, assert
        assertThat(refusal(counter("rows", "x"), ignore("rows", "y")), is("g: the label 'rows' is used twice"));
    }

    @Test
    public void validate_nullOrBlankPattern_isRefused() {
        // act, assert
        assertThat(refusal(counter("x", null)), is("g counter 1 ('x') must have a pattern"));
        assertThat(refusal(counter("x", " ")), is("g counter 1 ('x') must have a pattern"));
    }

    @Test
    public void validate_invalidRegex_isRefusedNamingTheLabel() {
        // act, assert
        assertThat(refusal(counter("bad", "[")), startsWith("g: invalid fact pattern for label 'bad': "));
    }

    @Test
    public void validate_onlyIgnoreCounters_isRefused() {
        // act, assert
        assertThat(refusal(ignore("POD", POD), ignore("comments", "#.*")), is("g: no counter, only ignore patterns"));
    }

    @Test
    public void validate_ignoreCounterThatIsRawOrDistinct_isRefused() {
        // arrange
        final FactCounter raw = ignore("comments", "#.*");
        raw.setRaw(true);
        final FactCounter distinct = ignore("comments", "#.*");
        distinct.setDistinct(true);

        // act, assert
        assertThat(
                refusal(counter("subs", SUBS), raw),
                is("g counter 2 ('comments') is an ignore counter, which counts nothing; raw and distinct apply to"
                        + " counters"));
        assertThat(
                refusal(counter("subs", SUBS), distinct),
                is("g counter 2 ('comments') is an ignore counter, which counts nothing; raw and distinct apply to"
                        + " counters"));
    }

    @Test
    public void validate_rawAndDistinctCounters_doNotThrow() {
        // arrange
        final FactCounter both = counter("names", "(\\w+)");
        both.setRaw(true);
        both.setDistinct(true);

        // act, assert
        assertDoesNotThrow(() -> FactExtractor.validate("g", Arrays.asList(ignore("comments", "#.*"), both)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="raw and distinct counters">
    private static FactCounter raw(final String label, final String pattern) {
        final FactCounter c = counter(label, pattern);
        c.setRaw(true);
        return c;
    }

    private static FactCounter distinct(final String label, final String pattern) {
        final FactCounter c = counter(label, pattern);
        c.setDistinct(true);
        return c;
    }

    @Test
    public void counts_rawCounter_countsTheSourceBeforeTheIgnoreCountersBlankIt() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(
                        ignore("comments", "//[^\\n]*"),
                        raw("TODO raw", "\\bTODO\\b"),
                        counter("TODO blanked", "\\bTODO\\b")),
                "int a; // TODO one\nint b; // TODO two\n");

        // assert
        assertThat(counts.get("TODO raw"), is(2));
        assertThat(counts.get("TODO blanked"), is(0));
    }

    @Test
    public void counts_distinctCounter_countsTheDifferentFirstGroups() {
        // arrange
        final String source = "@Inject Clock a;\n@Inject Clock b;\n@Inject Bus c;\n";

        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(
                        distinct("injected types", "@Inject (\\w+) \\w+"),
                        counter("injection points", "@Inject (\\w+) \\w+")),
                source);

        // assert
        assertThat(counts.get("injected types"), is(2));
        assertThat(counts.get("injection points"), is(3));
    }

    @Test
    public void counts_distinctCounterWithoutAGroup_countsTheDifferentMatches() {
        // act
        final Map<String, Integer> counts =
                FactExtractor.counts(Collections.singletonList(distinct("types", "\\b[A-Z]\\w*")), "Foo Foo Bar a");

        // assert
        assertThat(counts.get("types"), is(2));
    }

    @Test
    public void counts_distinctCounterWhoseGroupDidNotTakePart_countsTheWholeMatch() {
        // act
        final Map<String, Integer> counts =
                FactExtractor.counts(Collections.singletonList(distinct("names", "a(b)?")), "a ab ab");

        // assert
        // "a" (the group did not take part) and "b"
        assertThat(counts.get("names"), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="time limit">
    /** An unclosed comment opener per line: the lazy pattern rescans to the end from each one. */
    private static final String UNCLOSED_COMMENTS = repeat("<!-- x\n", 16_000);

    /** Quadratic over {@link #UNCLOSED_COMMENTS}. */
    private static final String QUADRATIC = "(?s)<!--.*?-->";

    private static String repeat(final String line, final int times) {
        final StringBuilder text = new StringBuilder(line.length() * times);
        for (int i = 0; i < times; i++) {
            text.append(line);
        }
        return text.toString();
    }

    @Test
    public void counts_counterPastItsTimeLimit_isLeftOut() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(counter("comments", QUADRATIC), counter("lines", "(?m)^<")), UNCLOSED_COMMENTS);

        // assert
        assertThat(counts.containsKey("comments"), is(false));
        assertThat(counts.get("lines"), is(16_000));
    }

    @Test
    public void counts_distinctCounterPastItsTimeLimit_isLeftOut() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(distinct("comments", QUADRATIC), counter("lines", "(?m)^<")), UNCLOSED_COMMENTS);

        // assert
        assertThat(counts.containsKey("comments"), is(false));
    }

    @Test
    public void counts_ignoreCounterPastItsTimeLimit_blanksNothing() {
        // act
        final Map<String, Integer> counts = FactExtractor.counts(
                Arrays.asList(ignore("comments", QUADRATIC), counter("x", "\\bx\\b")), UNCLOSED_COMMENTS);

        // assert
        assertThat(counts.get("x"), is(16_000));
    }

    @Test
    public void counts_patternPastItsTimeLimit_warnsNamingTheCounterAndWhatItDoes() {
        // arrange
        final Logger logger = (Logger) LoggerFactory.getLogger(FactExtractor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        // act
        try {
            FactExtractor.counts(
                    Arrays.asList(ignore("comments", QUADRATIC), counter("lazy", QUADRATIC), counter("x", "x")),
                    UNCLOSED_COMMENTS);
        } finally {
            logger.detachAppender(appender);
        }

        // assert
        final List<String> warnings = new ArrayList<>();
        for (final ILoggingEvent event : appender.list) {
            if (event.getLevel() == Level.WARN) {
                warnings.add(event.getFormattedMessage());
            }
        }
        final String characters = UNCLOSED_COMMENTS.length() + "-character source";
        final String reads = String.valueOf(ReadBudget.limit(UNCLOSED_COMMENTS.length()));
        assertThat(
                warnings,
                is(Arrays.asList(
                        "Fact counter 'comments' stopped after reading " + reads + " characters of a " + characters
                                + ": its pattern backtracks too far, so it is not blanked",
                        "Fact counter 'lazy' stopped after reading " + reads + " characters of a " + characters
                                + ": its pattern backtracks too far, so it is left out of the facts")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factsBlock: time limit">
    @Test
    public void factsBlock_counterPastItsTimeLimit_isLeftOutOfTheLine() {
        // act
        final String block = FactExtractor.factsBlock(
                Arrays.asList(counter("comments", QUADRATIC), counter("lines", "(?m)^<")), UNCLOSED_COMMENTS);

        // assert
        assertThat(block, is(FactExtractor.FACTS_HEADER + "lines: 16000" + FactExtractor.FACTS_SUFFIX));
    }

    @Test
    public void factsBlock_everyCounterPastItsTimeLimit_returnsEmpty() {
        // act, assert
        assertThat(
                FactExtractor.factsBlock(Collections.singletonList(counter("comments", QUADRATIC)), UNCLOSED_COMMENTS),
                is(""));
    }
    // </editor-fold>
}
