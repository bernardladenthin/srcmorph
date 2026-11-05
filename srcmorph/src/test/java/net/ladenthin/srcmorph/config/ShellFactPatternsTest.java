// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the shell fact counters of the current profile ({@code profiles/v3/shell-facts.tsv}) on Bash,
 * POSIX sh and ksh93 shapes, counted the way the facts line counts them: the {@code [ignore]} counters
 * blank comment lines and heredoc bodies first, so code quoted there does not count. The last tests pin
 * the counts of the benchmark's language corpus, which its expected facts rely on.
 */
public class ShellFactPatternsTest {

    private static final List<AiFactCounter> COUNTERS = ProfileFactPatterns.counters("shell-facts");

    /** Copies of the deploy script in the run-time test, about 1.5 MB. */
    private static final int LONG_INPUT_COPIES = 300;

    /**
     * What a long input may take; the lookbehind getopts counter alone took about a second, a heredoc
     * opened behind each of 20 000 shifts about 25 s.
     */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    /** Lines of the generated inputs of the heredoc run-time tests. */
    private static final int LONG_INPUT_LINES = 20_000;

    /** Repeats {@code line} {@value #LONG_INPUT_LINES} times behind a function definition. */
    private static String functionThenLines(final String line) {
        final StringBuilder source = new StringBuilder("f() {\n}\n");
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

    @Test
    public void profile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(COUNTERS, "").keySet()),
                is(Arrays.asList(
                        "functions",
                        "getopts loops",
                        "getopts option letters",
                        "dash-option case arms",
                        "named positional parameters",
                        "exported variables",
                        "readonly variables",
                        "defaulted variables",
                        "script-level assignments",
                        "sourced files",
                        "traps",
                        "set -e",
                        "exit statements with a status",
                        "typeset and declare declarations",
                        "ksh discipline functions")));
    }

    // <editor-fold defaultstate="collapsed" desc="functions">
    @Test
    public void functions_everyDefinitionForm_counts() {
        assertThat(
                count(
                        "functions",
                        "foo() {\n}\nbar ()\n{\n}\nfunction baz {\n}\nfunction qux() {\n}\n"
                                + "lib::util() { :; }\nsub() ( cd /tmp )\n"),
                is(6));
    }

    @Test
    public void functions_emptyArrayAndAQuotedPair_doNotCount() {
        assertThat(count("functions", "arr=()\nfoo\necho \"bar() {\"\nfoo() ifx\n"), is(0));
    }

    @Test
    public void functions_insideAHeredocOrACommentLine_doNotCount() {
        assertThat(count("functions", "cat <<EOF\nfake() {\nEOF\n# old() {\nreal() {\n}\n"), is(1));
    }

    @Test
    public void functions_quotedAndTabIndentedHeredoc_isBlankedToItsEndMarker() {
        assertThat(count("functions", "cat <<-'END'\n\tfake() {\n\tEND\nreal() {\n}\n"), is(1));
    }

    @Test
    public void functions_heredocWithAnyEndWord_isBlanked() {
        assertThat(count("functions", "cat <<!\nfake() {\nexit 1\n!\nreal() {\n}\n"), is(1));
        assertThat(count("functions", "psql <<'END-OF-SQL'\nfake() {\nEND-OF-SQL\nreal() {\n}\n"), is(1));
        assertThat(count("functions", "cat <<\\EOF\nfake() {\nEOF\nreal() {\n}\n"), is(1));
    }

    @Test
    public void functions_heredocEndMarker_standsAloneAfterTabsOnlyForDash() {
        // an indented marker does not end a plain heredoc, nor a space-indented one a <<- heredoc
        assertThat(count("functions", "cat <<EOF\nfake() {\n  EOF\nother() {\nEOF\nreal() {\n}\n"), is(1));
        assertThat(count("functions", "cat <<-EOF\n\tfake() {\n  EOF\n\tother() {\n\tEOF\nreal() {\n}\n"), is(1));
    }

    @Test
    public void functions_heredocMarkerInsideAComment_opensNothing() {
        assertThat(count("functions", "# see cat <<EOF below\na() {\n}\ncat <<EOF\nfake() {\nEOF\nb() {\n}\n"), is(2));
    }

    @Test
    public void functions_heredocWithCrLfLineEnds_isBlanked() {
        assertThat(count("functions", "cat <<EOF\r\nfake() {\r\nEOF\r\nreal() {\r\n}\r\n"), is(1));
    }

    @Test
    public void functions_hereString_isNoHeredoc() {
        assertThat(count("functions", "read x <<< \"$y\"\nf() {\n}\n"), is(1));
    }

    @Test
    public void functions_arithmeticShift_opensNoHeredoc() {
        // each shift would otherwise open a heredoc ending at the line "4", "n", "2" or "="
        assertThat(count("functions", "x=$((1 << 4))\nf() {\n}\n4\ng() {\n}\n"), is(2));
        assertThat(
                count(
                        "functions",
                        "(( mask <<= 2 ))\ny=$(( (a + b) << n ))\nz=$[1<<2]\nfor (( i = 1; i << 1; )); do :; done\n"
                                + "f() {\n}\nn\n=\n2\n1\ng() {\n}\n"),
                is(2));
    }

    /** Several groups, a nested ((a)) and a group with one of its own before the shift: still arithmetic. */
    @Test
    public void functions_arithmeticShiftAfterSeveralOrNestedGroups_opensNoHeredoc() {
        assertThat(
                count(
                        "functions",
                        "y=$(( (a) + (b) << n ))\nz=$(( ((a)) << m ))\nw=$(( (a * (b + 1)) << k ))\n"
                                + "f() {\n}\nn\nm\nk\ng() {\n}\n"),
                is(2));
    }

    @Test
    public void functions_heredocMarkerInsideAString_opensNothing() {
        assertThat(count("functions", "echo \"usage: cat <<EOF\"\nf() {\n}\nEOF\ng() {\n}\n"), is(2));
        assertThat(count("functions", "grep -c '<<EOF' x\nf() {\n}\nEOF\ng() {\n}\n"), is(2));
        assertThat(count("functions", "echo \"a <<EOF\" \"b\"\nf() {\n}\nEOF\ng() {\n}\n"), is(2));
    }

    @Test
    public void functions_heredocAfterQuotedWordsAndBeforeAComment_isBlanked() {
        assertThat(
                count(
                        "functions",
                        "ssh \"$h\" bash -s <<EOF # it's remote\nfake() {\nEOF\n"
                                + "cat <<\"END\" > \"$out\"\nfake2() {\nEND\nreal() {\n}\n"),
                is(1));
    }

    @Test
    public void functions_unclosedHeredoc_isBlankedToTheEnd() {
        // bash reads the rest of the file as the heredoc's body ("delimited by end-of-file")
        assertThat(count("functions", "f() {\n}\ncat <<EOF\ng() {\n}\n"), is(1));
    }

    @Test
    public void ignore_manyArithmeticShifts_stayLinear() {
        final String source = functionThenLines("x=$((1 << 4))");
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("functions", source)), is(1));
    }

    @Test
    public void ignore_manyHeredocMarkersInStrings_stayLinear() {
        final String source = functionThenLines("echo \"cat <<EOF here\"");
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("functions", source)), is(1));
    }

    @Test
    public void ignore_manyUnclosedHeredocs_stayLinear() {
        final String source = functionThenLines("cat <<EOF >> out.txt");
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("functions", source)), is(1));
    }

    @Test
    public void ignore_trailingComment_isBlanked() {
        final String source = "do_it # formerly exit 1 and getopts here\nfoo # bar() {\n";
        assertThat(count("getopts loops", source), is(0));
        assertThat(count("getopts option letters", source), is(0));
        assertThat(count("exit statements with a status", source), is(0));
        assertThat(count("functions", source), is(0));
    }

    @Test
    public void ignore_hashOfParameterCountLengthAndQuotedPatterns_isNoComment() {
        assertThat(count("exit statements with a status", "[ $# -eq 0 ] && exit 2\nn=${#arr[@]}\n"), is(1));
        assertThat(
                count(
                        "exit statements with a status",
                        "grep -v '^#' file\nsed 's/#.*//' f\necho \"# header\" > f\nexit 3\n"),
                is(1));
    }

    @Test
    public void ignore_blankAndHashInsideAString_readsAsAComment() {
        // the accepted limit: the shell knows the quotes, the regex does not, so OTHER is lost
        assertThat(count("exported variables", "export MSG=\"a # b\" OTHER=1\n"), is(1));
    }

    @Test
    public void functions_compoundCommandBodies_count() {
        assertThat(
                count(
                        "functions",
                        "f() if true; then :; fi\ng() [[ -n $1 ]]\nh() for x in a; do :; done\n"
                                + "j() while :; do break; done\nk() case $1 in a) ;; esac\n"
                                + "l() until false; do break; done\nm() (( x++ ))\n"),
                is(7));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="options and arguments">
    @Test
    public void getoptsLoops_quotedAndBareOptionStrings_count() {
        assertThat(
                count("getopts loops", "while getopts \":hv:o:\" opt; do\ndone\nwhile getopts ab: o; do :; done\n"),
                is(2));
    }

    @Test
    public void getoptsOptionLetters_everyLetterOfTheOptionString_counts() {
        assertThat(
                count(
                        "getopts option letters",
                        "while getopts \":hv:o:\" opt; do\ndone\nwhile getopts ab: o; do :; done\n"
                                + "getopts \"$OPTS\" opt\n"),
                is(5));
    }

    @Test
    public void dashOptionCaseArms_dashOptions_countAndBareWordsAndCatchAllsDoNot() {
        assertThat(
                count(
                        "dash-option case arms",
                        "case \"$1\" in\n  -h|--help) usage ;;\n  -o|--output) OUT=\"$2\"; shift ;;\n"
                                + "  --dry-run=*) DRY=1 ;;\n  -*) die ;;\n  *) break ;;\n  start) go ;;\nesac\n"),
                is(3));
    }

    @Test
    public void namedPositionalParameters_assignmentsWithAndWithoutExpansionFlags_count() {
        assertThat(
                count(
                        "named positional parameters",
                        "SRC=\"$1\"\nDEST=\"${2:?usage: dest}\"\nlocal mode=${3:-fast}\nreadonly N=$4\n"
                                + "X=$HOME\nY=\"${10}\"\n"),
                is(5));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="variables">
    @Test
    public void exportedVariables_exportAndDeclareX_countAndFunctionsDoNot() {
        assertThat(
                count(
                        "exported variables",
                        "export PATH=\"$PATH:/opt\"\nexport JAVA_HOME\ndeclare -x LANG=C\nexport -f myfn\n"
                                + "export -n GONE\nexport -p\n"),
                is(3));
    }

    @Test
    public void exportedVariables_everyNameOfALine_counts() {
        assertThat(
                count(
                        "exported variables",
                        "export A=1 B=2\nexport PATH=\"$PATH:/opt\" X\nexport X=$(cat f) Y=\"a b\"\n"),
                is(6));
    }

    @Test
    public void readonlyVariables_readonlyDeclareRAndTypesetR_count() {
        assertThat(
                count(
                        "readonly variables",
                        "readonly VERSION=1.2\ndeclare -r MAX=5\ndeclare -ir N=3\ntypeset -r K=1\n"
                                + "readonly -a ARR=(1 2)\n"),
                is(5));
    }

    @Test
    public void readonlyVariables_everyNameAndLocalR_countAndFunctionsDoNot() {
        assertThat(count("readonly variables", "readonly A B C\nreadonly -f fn\nlocal -r x=1\n"), is(4));
    }

    @Test
    public void defaultedVariables_namedDefaultsCountPositionalOnesDoNot() {
        assertThat(
                count(
                        "defaulted variables",
                        ": \"${PORT:=8080}\"\nHOST=\"${HOST:-localhost}\"\nX=${UNSET-none}\necho ${1:-x}\n"),
                is(3));
    }

    @Test
    public void scriptLevelAssignments_onlyAtColumnZero() {
        assertThat(count("script-level assignments", "LOG_DIR=/var/log\nverbose=0\nf() {\n  inner=1\n}\n"), is(2));
    }

    @Test
    public void scriptLevelAssignments_environmentPrefixesOfACommand_doNotCount() {
        assertThat(
                count(
                        "script-level assignments",
                        "LC_ALL=C sort file\nIFS= read -r line\nPATH+=:/opt/bin\nX=1 Y=2\nX=1 Y=2 cmd\n"
                                + "FOO=bar; export FOO\nIFS=$'\\n'\nname=\"$(basename \"$SOURCE_DIR\")\"\narr=(a b c)\n"
                                + "x=$(( 1 + 2 ))\nHOST_FILES=([staging]=\"hosts.staging\" [production]=\"hosts.production\")\n"
                                + "DRY_RUN=0\nX=\"a b\" cmd\n"),
                is(9));
    }

    /** A ${...} with a blank inside is one word of the value, not the end of an environment prefix. */
    @Test
    public void scriptLevelAssignments_aDefaultWithABlank_countsAndAPrefixStaysOne() {
        assertThat(count("script-level assignments", "MSG=${MSG:-a b}\nN=${N:-$(date)}\n"), is(2));
        assertThat(count("script-level assignments", "MSG=${MSG:-a b} cmd\nA=${X:-1} B=${Y:-c d} make\n"), is(0));
    }

    @Test
    public void scriptLevelAssignments_aContinuedLine_counts() {
        assertThat(
                count(
                        "script-level assignments",
                        "DESCRIPTOR=\"$(unzip -p \"$JAR\" plugin.xml)\" \\\n    || fail \"no descriptor\"\n"),
                is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="control">
    @Test
    public void sourcedFiles_sourceAndDot_countAndARelativeCommandDoesNot() {
        assertThat(
                count(
                        "sourced files",
                        ". ./lib.sh\nsource \"$DIR/common.sh\"\n  . /etc/os-release\n./run.sh\n"
                                + "# shellcheck source=lib/common.sh\n"),
                is(3));
    }

    @Test
    public void traps_settingOnesCountAndListingDoesNot() {
        assertThat(count("traps", "trap cleanup EXIT\ntrap 'rm -f \"$tmp\"' INT TERM\ntrap -p\n"), is(2));
    }

    @Test
    public void setE_shortAndLongForms_countAndOtherFlagsDoNot() {
        assertThat(count("set -e", "set -euo pipefail\nset -o errexit\nset -x\nset +e\n"), is(2));
    }

    @Test
    public void setE_afterOtherOptionWords_counts() {
        assertThat(
                count(
                        "set -e",
                        "set -x -e\nset -u -e\nset -o pipefail -o errexit\nset -o nounset -o errexit\n"
                                + "set -eo pipefail\nset -o pipefail\nset -- -e\n"),
                is(5));
    }

    @Test
    public void exitStatementsWithAStatus_onlyLiteralStatuses() {
        assertThat(count("exit statements with a status", "exit 0\n[ -f x ] || exit 2\nexit \"$rc\"\nexit\n"), is(2));
    }

    @Test
    public void sourcedFiles_afterAndThenSemicolonElseDoAndABrace_count() {
        assertThat(
                count(
                        "sourced files",
                        "[ -f ~/.env ] && . ~/.env\nif [ -f /etc/default/foo ]; then . /etc/default/foo; fi\n"
                                + ". lib.sh; source other.sh\nsudo . x\nfind . -name x\n{ . a; }\n"
                                + "if false; then :; else source alt.sh; fi\nfor f in conf.d/*.sh; do . \"$f\"; done\n"
                                + "./run.sh\n"),
                is(7));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ksh">
    @Test
    public void typesetAndDeclareDeclarations_atAnyIndent() {
        assertThat(
                count(
                        "typeset and declare declarations",
                        "declare -a HOSTS=()\ndeclare -A MAP\ntypeset -i n=0\n  declare -r X=1\n"),
                is(4));
    }

    @Test
    public void kshDisciplineFunctions_getSetUnsetAppend() {
        assertThat(
                count(
                        "ksh discipline functions",
                        "function counter.get {\n  .sh.value=$((n+1))\n}\nfunction x.set { :; }\n"
                                + "function get {\n}\n"),
                is(2));
    }

    @Test
    public void kshDisciplineFunctions_getnCountsAndOtherDottedNamesDoNot() {
        assertThat(
                count(
                        "ksh discipline functions",
                        "function n.getn {\n}\nfunction n.get {\n}\nfunction n.getx {\n}\nfunction get {\n}\n"),
                is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the benchmark's language corpus">
    @Test
    public void corpus_deployReleaseSh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/deploy_release.sh");
        assertThat(count("functions", source), is(8));
        assertThat(count("getopts loops", source), is(1));
        // e, t, n, p and h; the options are getopts letters, so no case arm starts with a dash
        assertThat(count("getopts option letters", source), is(5));
        assertThat(count("dash-option case arms", source), is(0));
        assertThat(count("named positional parameters", source), is(2));
        assertThat(count("exported variables", source), is(1));
        assertThat(count("readonly variables", source), is(1));
        assertThat(count("defaulted variables", source), is(2));
        assertThat(count("script-level assignments", source), is(7));
        assertThat(count("sourced files", source), is(1));
        assertThat(count("traps", source), is(1));
        // the remote script's "set -e" sits in a heredoc and is blanked
        assertThat(count("set -e", source), is(1));
        assertThat(count("exit statements with a status", source), is(3));
        // declare -a HOSTS and declare -A HOST_FILES
        assertThat(count("typeset and declare declarations", source), is(2));
        assertThat(count("ksh discipline functions", source), is(0));
    }

    @Test
    public void corpus_nightlyExtractKsh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/nightly_extract.ksh");
        assertThat(count("functions", source), is(5));
        assertThat(count("getopts loops", source), is(1));
        assertThat(count("getopts option letters", source), is(2));
        assertThat(count("dash-option case arms", source), is(0));
        assertThat(count("named positional parameters", source), is(2));
        assertThat(count("exported variables", source), is(2));
        assertThat(count("readonly variables", source), is(2));
        assertThat(count("defaulted variables", source), is(1));
        assertThat(count("script-level assignments", source), is(1));
        assertThat(count("exit statements with a status", source), is(1));
        assertThat(count("typeset and declare declarations", source), is(10));
        assertThat(count("traps", source), is(0));
        assertThat(count("sourced files", source), is(0));
        assertThat(count("set -e", source), is(0));
    }

    @Test
    public void corpus_healthcheckSh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/healthcheck.sh");
        assertThat(count("functions", source), is(4));
        assertThat(count("defaulted variables", source), is(2));
        assertThat(count("script-level assignments", source), is(4));
        assertThat(count("exit statements with a status", source), is(4));
        assertThat(count("set -e", source), is(0));
        assertThat(count("getopts loops", source), is(0));
        assertThat(count("dash-option case arms", source), is(0));
    }

    @Test
    public void counts_aLongScript_stayLinear() {
        final String script = ProfileFactPatterns.languageCorpusFile("other/shell/deploy_release.sh");
        final StringBuilder source = new StringBuilder();
        for (int i = 0; i < LONG_INPUT_COPIES; i++) {
            source.append(script);
        }
        assertThat(
                assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("getopts option letters", source.toString())),
                is(5 * LONG_INPUT_COPIES));
    }
    // </editor-fold>
}
