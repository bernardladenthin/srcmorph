// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the Perl fact counters of the current profile ({@code profiles/v3/perl-facts.tsv}), counted
 * the way the facts line counts them: the {@code [ignore]} counters blank POD, the {@code __END__} tail,
 * heredoc bodies and comment lines first. The last tests pin the counts of the benchmark's language
 * corpus.
 */
public class PerlFactPatternsTest {

    private static final List<FactCounter> COUNTERS = ProfileFactPatterns.counters("perl-facts");

    /** Lines of the generated inputs of the heredoc run-time tests. */
    private static final int LONG_INPUT_LINES = 20_000;

    /** What a long input may take; a heredoc opened behind each of 20 000 shifts took 209 s. */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    /** Repeats {@code line} {@value #LONG_INPUT_LINES} times behind a sub definition. */
    private static String subThenLines(final String line) {
        final StringBuilder source = new StringBuilder("sub f {\n}\n");
        for (int i = 0; i < LONG_INPUT_LINES; i++) {
            source.append(line).append('\n');
        }
        return source.toString();
    }

    private static int count(final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(COUNTERS, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="profile">
    @Test
    public void profile_counterLabels_areTheseInThisOrder() {
        // act, assert
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(COUNTERS, "").keySet()),
                is(Arrays.asList(
                        "packages",
                        "subs",
                        "modules used",
                        "modules required",
                        "use constant statements",
                        "package variables (our)",
                        "file-scope lexicals (my at column 0)",
                        "environment variables read",
                        "GetOptions calls",
                        "die, croak and confess")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="packages and subs">
    @Test
    public void packages_statementAndBlockForms_countAndAStringDoesNot() {
        // act, assert
        assertThat(count("packages", "package Foo::Bar;\npackage Baz 1.2 {\n}\nmy $s = 'package Foo';\n"), is(2));
    }

    @Test
    public void subs_namedLexicalSignatureAndAttributeForms_count() {
        // act, assert
        assertThat(
                count(
                        "subs",
                        "sub foo {\n}\nsub Bar::baz\n{\n}\nmy sub lex { }\nsub sig ($x, $y) {\n}\nsub lv :lvalue {\n}\n"),
                is(5));
    }

    @Test
    public void subs_anonymousSubsForwardDeclarationsAndHashKeys_doNotCount() {
        // act, assert
        assertThat(count("subs", "my $cb = sub { 1 };\nsub fwd;\n$h{sub} = 1;\n"), is(0));
    }

    @Test
    public void subs_inPodAHeredocACommentOrAfterEnd_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "subs",
                        "sub real {}\n=head1 NAME\n\nsub in_pod {}\n\n=cut\nprint <<\"EOT\";\nsub in_heredoc {\nEOT\n"
                                + "# sub in_comment {\n__END__\nsub after_end {}\n"),
                is(1));
    }

    @Test
    public void subs_heredocMarkerInsideAComment_opensNothing() {
        // act, assert
        assertThat(count("subs", "# usage: print <<EOF\nsub a {}\nprint <<EOF;\nx\nEOF\nsub b {}\n"), is(2));
    }

    @Test
    public void subs_podAndEndInsideAHeredoc_areHeredocText() {
        // act, assert
        assertThat(count("subs", "print <<'EOP';\n=head1 NAME\n\nfoo\n\nEOP\nsub real {}\nsub real2 {}\n"), is(2));
        assertThat(count("subs", "print <<'EOT';\n__END__\nEOT\nsub after {}\n"), is(1));
    }

    @Test
    public void subs_backslashHeredoc_isBlanked() {
        // act, assert
        assertThat(count("subs", "print <<\\EOT;\nsub nope {\nEOT\nsub real {}\n"), is(1));
    }

    @Test
    public void subs_shiftWithASpaceBeforeAWord_isNoHeredoc() {
        // act, assert
        assertThat(count("subs", "my $x = 1 << SHIFT;\nsub a {}\nSHIFT\nsub b {}\n"), is(2));
    }

    @Test
    public void subs_attributesBeforeASignatureAndNestedParentheses_count() {
        // act, assert
        assertThat(
                count("subs", "sub f :prototype($$) ($x, $y) {\nsub g ($x = default()) {\nsub h ($x, $y) {\n"), is(3));
    }

    @Test
    public void subs_podWithoutCut_isBlankedToTheEnd() {
        // act, assert
        assertThat(count("subs", "sub real {}\n=pod\n\nsub in_pod {}\n"), is(1));
    }

    @Test
    public void subs_shiftOperator_isNoHeredoc() {
        // act, assert
        assertThat(count("subs", "my $x = 1 << 3;\nsub after {\n}\nmy $y = $a<<2;\n"), is(1));
    }

    @Test
    public void subs_shiftByAConstantAfterATerm_isNoHeredoc() {
        // act, assert
        // each shift would otherwise open a heredoc ending at the line "SHIFT", "FLAG", "BITS" or "X"
        assertThat(
                count(
                        "subs",
                        "my $x = $y <<SHIFT;\nmy $f = 1<<FLAG;\nmy $b = $a[0] <<BITS;\nmy $c = f() <<X;\n"
                                + "sub a {}\nSHIFT\nFLAG\nBITS\nX\nsub b {}\n"),
                is(2));
    }

    /** A hash element or a dereference before << is a value, so it is a shift; a {$fh} block is not. */
    @Test
    public void subs_shiftAfterAHashElementOrABlockFilehandle_opensAHeredocOnlyAfterTheFilehandle() {
        // act, assert
        assertThat(
                count("subs", "my $b = $h{x} <<BITS;\nmy $c = $r->{k}{j} << K;\nsub a {}\nBITS\nK\nsub b {}\n"), is(2));
        assertThat(count("subs", "print {$fh} <<EOT;\nsub no {}\nEOT\nsub yes {}\n"), is(1));
    }

    /** A << inside a quote-like operator or a regex is text, not a heredoc. */
    @Test
    public void subs_heredocMarkerInsideAQuoteLikeOperatorOrARegex_opensNothing() {
        // act, assert
        assertThat(
                count(
                        "subs",
                        "my $u = q{cat <<EOF};\nmy $v = qq/a <<END/;\nsub a {}\nEOF\nEND\n"
                                + "if ($x =~ m/<<HERE/) {}\n$y =~ s/<<THERE/x/;\nsub b {}\nHERE\nTHERE\nsub c {}\n"),
                is(3));
    }

    @Test
    public void subs_heredocMarkerInsideAStringOrATrailingComment_opensNothing() {
        // act, assert
        assertThat(count("subs", "print \"cat <<EOF\\n\";\nsub a {}\nEOF\nsub b {}\n"), is(2));
        assertThat(count("subs", "foo(); # print <<EOF\nsub a {}\nEOF\nsub b {}\n"), is(2));
    }

    @Test
    public void subs_heredocToAFilehandleAndWithASpaceBeforeTheQuote_isBlanked() {
        // act, assert
        assertThat(
                count(
                        "subs",
                        "print $fh <<EOT;\nsub no1 {}\nEOT\nprint {$fh} <<EOT;\nsub no2 {}\nEOT\n"
                                + "my $t = << \"END\";\nsub no3 {}\nEND\nprint STDERR <<'X' . \"it's\"; # it's\nsub no4 {}\nX\n"
                                + "sub real {}\n"),
                is(1));
    }

    @Test
    public void subs_unclosedHeredoc_isBlankedToTheEnd() {
        // act, assert
        // perl refuses the file ("Can't find string terminator"); the rest counts as the heredoc's body
        assertThat(count("subs", "sub a {}\nprint <<EOF;\nsub b {}\n"), is(1));
    }

    @Test
    public void ignore_manyShiftsByAConstant_stayLinear() {
        // arrange
        final String source = subThenLines("my $x = $y <<SHIFT;");

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("subs", source)), is(1));
    }

    @Test
    public void ignore_manyHeredocMarkersInStringsAndTrailingComments_stayLinear() {
        // arrange
        final String strings = subThenLines("print \"cat <<EOF here\\n\";");

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("subs", strings)), is(1));

        // arrange
        final String comments = subThenLines("foo(); # see <<EOF");

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("subs", comments)), is(1));
    }

    @Test
    public void ignore_manyUnclosedHeredocs_stayLinear() {
        // arrange
        final String source = subThenLines("print <<EOF;");

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("subs", source)), is(1));
    }

    @Test
    public void packagesAndSubs_perl538ClassAndMethod_count() {
        // arrange
        final String source = "use v5.38;\nuse experimental 'class';\nclass Point {\n    field $x :param = 0;\n"
                + "    field $y :param = 0;\n    method coords { return ($x, $y) }\n    method move ($dx, $dy) {\n    }\n}\n"
                + "class Point3D :isa(Point) {\n}\nmy %h = (class => 'x', method => 'y');\n";

        // act, assert
        assertThat(count("packages", source), is(2));
        assertThat(count("subs", source), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="modules, constants and variables">
    @Test
    public void modulesUsed_modulesPragmasAndVersions_countOnlyTheCapitalizedModules() {
        // act, assert
        assertThat(
                count(
                        "modules used",
                        "use strict;\nuse warnings;\nuse Getopt::Long qw(GetOptions);\nuse File::Spec;\nuse 5.010;\n"
                                + "use v5.36;\n"),
                is(2));
    }

    @Test
    public void modulesRequired_moduleFileAndVersionForms_countTheModuleAndFileFormsOnly() {
        // act, assert
        assertThat(
                count(
                        "modules required",
                        "require Exporter;\nrequire 'lib/conf.pl';\nrequire 5.006;\nrequire v5.10;\n"),
                is(2));
    }

    @Test
    public void useConstantStatements_scalarAndHashForms_countOnceEach() {
        // act, assert
        assertThat(
                count(
                        "use constant statements",
                        "use constant PI => 3.14159;\nuse constant {\n  E => 2.718,\n  DEBUG => 0,\n};\n"),
                is(2));
    }

    @Test
    public void packageVariables_scalarArrayAndList_count() {
        // act, assert
        assertThat(
                count("package variables (our)", "our $VERSION = '1.0';\nour @EXPORT_OK = qw(a b);\nour ($x, $y);\n"),
                is(3));
    }

    @Test
    public void fileScopeLexicals_atColumnZeroAndIndented_countOnlyAtColumnZero() {
        // act, assert
        assertThat(
                count(
                        "file-scope lexicals (my at column 0)",
                        "my $config = 'x';\nmy ($a, $b);\nsub f {\n    my $local = 1;\n}\n"),
                is(2));
    }

    @Test
    public void environmentVariablesRead_bareQuotedAndDashedKeys_countAndALocalWriteDoesNot() {
        // act, assert
        assertThat(
                count(
                        "environment variables read",
                        "my $h = $ENV{HOME};\nmy $p = $ENV{'PATH'} // '';\nlocal $ENV{\"LANG\"} = 'C';\n"),
                is(2));
    }

    @Test
    public void environmentVariablesRead_readsAndWrites_countOnlyTheReads() {
        // act, assert
        assertThat(
                count(
                        "environment variables read",
                        "$ENV{PATH} = '/bin';\nlocal $ENV{PATH} = 1;\nprint $ENV{PATH};\n$ENV{'MY-VAR'};\ndelete $ENV{TMP};\n"
                                + "if ($ENV{DEBUG} == 1) {}\nif ($ENV{HOME} =~ /x/) {}\n$ENV{X} .= 'a';\n$ENV{Y} //= 'd';\n"
                                + "my $h = $ENV{HOME};\nmy $p = $ENV{'PATH'} // '';\nlocal $ENV{\"LANG\"} = 'C';\nexists $ENV{FOO}\n"),
                is(7));
    }

    @Test
    public void getOptionsCalls_variantsAndTheImport_countAllVariantsButNotTheImport() {
        // act, assert
        assertThat(
                count(
                        "GetOptions calls",
                        "use Getopt::Long qw(GetOptions);\nGetOptions(\n 'verbose!' => \\$v,\n);\n"
                                + "GetOptionsFromArray(\\@a, 'x=s' => \\$x);\n"),
                is(2));
    }

    @Test
    public void dieCroakAndConfess_callsAndTheImport_countTheCallsButNotTheImport() {
        // act, assert
        assertThat(
                count(
                        "die, croak and confess",
                        "use Carp qw(croak);\nopen my $fh, '<', $f or die \"no $f\";\ncroak 'bad';\n"
                                + "die \"x\" unless $ok;\neval { confess \"deep\" };\n$v // die \"undef\";\n"
                                + "foo(); die \"after\";\nmy $y = $z || croak('no');\n"),
                is(7));
    }

    @Test
    public void dieCroakAndConfess_theWordInAMessageAHashKeyAndAMethod_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "die, croak and confess",
                        "print \"Bitte die Datei pruefen\\n\";\nmy %h = (die => 1);\n$obj->die;\n"),
                is(0));
    }

    @Test
    public void modulesUsed_parentAndBase_countAndOtherPragmasDoNot() {
        // act, assert
        assertThat(
                count(
                        "modules used",
                        "use parent 'Exporter';\nuse base qw(Exporter);\nuse parent -norequire, 'Foo';\n"
                                + "use strict;\nuse lib \"$Bin\";\nuse Getopt::Long;\n"),
                is(4));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the benchmark's language corpus">
    @Test
    public void corpus_logDigestPm_countsItsFacts() {
        // arrange
        final String source = ProfileFactPatterns.languageCorpusFile("other/perl/LogDigest.pm");

        // act, assert
        assertThat(count("packages", source), is(1));
        // the sub named in the POD after __END__ is not one
        assertThat(count("subs", source), is(6));
        // POSIX, List::Util, Carp and use parent
        assertThat(count("modules used", source), is(4));
        assertThat(count("use constant statements", source), is(2));
        assertThat(count("package variables (our)", source), is(2));
        assertThat(count("file-scope lexicals (my at column 0)", source), is(1));
        // two croak calls; the croak of the import does not count
        assertThat(count("die, croak and confess", source), is(2));
    }

    @Test
    public void corpus_logDigestPl_countsItsFacts() {
        // arrange
        final String source = ProfileFactPatterns.languageCorpusFile("other/perl/log_digest.pl");

        // act, assert
        assertThat(count("packages", source), is(0));
        // the sub-like line of the usage heredoc is not one
        assertThat(count("subs", source), is(3));
        assertThat(count("modules used", source), is(4));
        assertThat(count("file-scope lexicals (my at column 0)", source), is(5));
        assertThat(count("environment variables read", source), is(1));
        assertThat(count("GetOptions calls", source), is(1));
        assertThat(count("die, croak and confess", source), is(3));
    }
    // </editor-fold>
}
