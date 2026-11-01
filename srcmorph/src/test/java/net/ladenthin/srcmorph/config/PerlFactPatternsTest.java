// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the Perl fact counters of the current profile ({@code profiles/v3/perl-facts.tsv}), counted
 * the way the facts line counts them: the {@code [ignore]} counters blank POD, the {@code __END__} tail,
 * heredoc bodies and comment lines first. The last tests pin the counts of the benchmark's language
 * corpus.
 */
public class PerlFactPatternsTest {

    private static final List<AiFactCounter> COUNTERS = ProfileFactPatterns.counters("perl-facts");

    private static int count(final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(COUNTERS, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    @Test
    public void profile_hasTheseCountersInThisOrder() {
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

    // <editor-fold defaultstate="collapsed" desc="packages and subs">
    @Test
    public void packages_statementAndBlockForms_countAndAStringDoesNot() {
        assertThat(count("packages", "package Foo::Bar;\npackage Baz 1.2 {\n}\nmy $s = 'package Foo';\n"), is(2));
    }

    @Test
    public void subs_namedLexicalSignatureAndAttributeForms_count() {
        assertThat(
                count(
                        "subs",
                        "sub foo {\n}\nsub Bar::baz\n{\n}\nmy sub lex { }\nsub sig ($x, $y) {\n}\nsub lv :lvalue {\n}\n"),
                is(5));
    }

    @Test
    public void subs_anonymousSubsForwardDeclarationsAndHashKeys_doNotCount() {
        assertThat(count("subs", "my $cb = sub { 1 };\nsub fwd;\n$h{sub} = 1;\n"), is(0));
    }

    @Test
    public void subs_inPodAHeredocACommentOrAfterEnd_doNotCount() {
        assertThat(
                count(
                        "subs",
                        "sub real {}\n=head1 NAME\n\nsub in_pod {}\n\n=cut\nprint <<\"EOT\";\nsub in_heredoc {\nEOT\n"
                                + "# sub in_comment {\n__END__\nsub after_end {}\n"),
                is(1));
    }

    @Test
    public void subs_heredocMarkerInsideAComment_opensNothing() {
        assertThat(count("subs", "# usage: print <<EOF\nsub a {}\nprint <<EOF;\nx\nEOF\nsub b {}\n"), is(2));
    }

    @Test
    public void subs_podAndEndInsideAHeredoc_areHeredocText() {
        assertThat(count("subs", "print <<'EOP';\n=head1 NAME\n\nfoo\n\nEOP\nsub real {}\nsub real2 {}\n"), is(2));
        assertThat(count("subs", "print <<'EOT';\n__END__\nEOT\nsub after {}\n"), is(1));
    }

    @Test
    public void subs_backslashHeredoc_isBlanked() {
        assertThat(count("subs", "print <<\\EOT;\nsub nope {\nEOT\nsub real {}\n"), is(1));
    }

    @Test
    public void subs_shiftWithASpaceBeforeAWord_isNoHeredoc() {
        assertThat(count("subs", "my $x = 1 << SHIFT;\nsub a {}\nSHIFT\nsub b {}\n"), is(2));
    }

    @Test
    public void subs_attributesBeforeASignatureAndNestedParentheses_count() {
        assertThat(
                count("subs", "sub f :prototype($$) ($x, $y) {\nsub g ($x = default()) {\nsub h ($x, $y) {\n"), is(3));
    }

    @Test
    public void subs_podWithoutCut_isBlankedToTheEnd() {
        assertThat(count("subs", "sub real {}\n=pod\n\nsub in_pod {}\n"), is(1));
    }

    @Test
    public void subs_shiftOperator_isNoHeredoc() {
        assertThat(count("subs", "my $x = 1 << 3;\nsub after {\n}\nmy $y = $a<<2;\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="modules, constants and variables">
    @Test
    public void modulesUsed_capitalizedModulesCountPragmasAndVersionsDoNot() {
        assertThat(
                count(
                        "modules used",
                        "use strict;\nuse warnings;\nuse Getopt::Long qw(GetOptions);\nuse File::Spec;\nuse 5.010;\n"
                                + "use v5.36;\n"),
                is(2));
    }

    @Test
    public void modulesRequired_moduleAndFileFormsCountAVersionDoesNot() {
        assertThat(
                count(
                        "modules required",
                        "require Exporter;\nrequire 'lib/conf.pl';\nrequire 5.006;\nrequire v5.10;\n"),
                is(2));
    }

    @Test
    public void useConstantStatements_scalarAndHashForms_countOnceEach() {
        assertThat(
                count(
                        "use constant statements",
                        "use constant PI => 3.14159;\nuse constant {\n  E => 2.718,\n  DEBUG => 0,\n};\n"),
                is(2));
    }

    @Test
    public void packageVariables_scalarArrayAndList_count() {
        assertThat(
                count("package variables (our)", "our $VERSION = '1.0';\nour @EXPORT_OK = qw(a b);\nour ($x, $y);\n"),
                is(3));
    }

    @Test
    public void fileScopeLexicals_onlyAtColumnZero() {
        assertThat(
                count(
                        "file-scope lexicals (my at column 0)",
                        "my $config = 'x';\nmy ($a, $b);\nsub f {\n    my $local = 1;\n}\n"),
                is(2));
    }

    @Test
    public void environmentVariablesRead_bareAndQuotedKeys_count() {
        assertThat(
                count(
                        "environment variables read",
                        "my $h = $ENV{HOME};\nmy $p = $ENV{'PATH'} // '';\nlocal $ENV{\"LANG\"} = 'C';\n"),
                is(3));
    }

    @Test
    public void getOptionsCalls_allVariantsCountAndTheImportDoesNot() {
        assertThat(
                count(
                        "GetOptions calls",
                        "use Getopt::Long qw(GetOptions);\nGetOptions(\n 'verbose!' => \\$v,\n);\n"
                                + "GetOptionsFromArray(\\@a, 'x=s' => \\$x);\n"),
                is(2));
    }

    @Test
    public void dieCroakAndConfess_callsCountAndTheImportDoesNot() {
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
        assertThat(
                count(
                        "die, croak and confess",
                        "print \"Bitte die Datei pruefen\\n\";\nmy %h = (die => 1);\n$obj->die;\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the benchmark's language corpus">
    @Test
    public void corpus_logDigestPm() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/perl/LogDigest.pm");
        assertThat(count("packages", source), is(1));
        // the sub named in the POD after __END__ is not one
        assertThat(count("subs", source), is(6));
        assertThat(count("modules used", source), is(3));
        assertThat(count("use constant statements", source), is(2));
        assertThat(count("package variables (our)", source), is(2));
        assertThat(count("file-scope lexicals (my at column 0)", source), is(1));
        // two croak calls; the croak of the import does not count
        assertThat(count("die, croak and confess", source), is(2));
    }

    @Test
    public void corpus_logDigestPl() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/perl/log_digest.pl");
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
