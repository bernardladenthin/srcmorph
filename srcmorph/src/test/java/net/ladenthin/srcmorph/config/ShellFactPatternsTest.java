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
 * Validates the shell fact counters of the current profile ({@code profiles/v3/shell-facts.tsv}) on Bash,
 * POSIX sh and ksh93 shapes, counted the way the facts line counts them: the {@code [ignore]} counters
 * blank comment lines and heredoc bodies first, so code quoted there does not count. The last tests pin
 * the counts of the benchmark's language corpus, which its expected facts rely on.
 */
public class ShellFactPatternsTest {

    private static final List<AiFactCounter> COUNTERS = ProfileFactPatterns.counters("shell-facts");

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
                        "option case arms",
                        "named positional parameters",
                        "exported variables",
                        "readonly variables",
                        "defaulted variables",
                        "script-level assignments",
                        "sourced files",
                        "traps",
                        "set -e",
                        "exit statements with a status",
                        "typeset declarations",
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
        assertThat(count("functions", "arr=()\nfoo\necho \"bar() {\"\n"), is(0));
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
    public void functions_hereString_isNoHeredoc() {
        assertThat(count("functions", "read x <<< \"$y\"\nf() {\n}\n"), is(1));
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
    public void optionCaseArms_dashOptions_countAndBareWordsAndCatchAllsDoNot() {
        assertThat(
                count(
                        "option case arms",
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
    public void exportedVariables_exportAndDeclareX_count() {
        assertThat(
                count(
                        "exported variables",
                        "export PATH=\"$PATH:/opt\"\nexport JAVA_HOME\ndeclare -x LANG=C\nexport -f myfn\n"),
                is(4));
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
    public void exitStatementsWithAStatus_onlyLiteralStatuses() {
        assertThat(count("exit statements with a status", "exit 0\n[ -f x ] || exit 2\nexit \"$rc\"\nexit\n"), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ksh">
    @Test
    public void typesetDeclarations_atAnyIndent() {
        assertThat(count("typeset declarations", "typeset -i n=0\ntypeset -A map\n  typeset -n ref=$1\n"), is(3));
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the benchmark's language corpus">
    @Test
    public void corpus_deployReleaseSh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/deploy_release.sh");
        assertThat(count("functions", source), is(8));
        assertThat(count("getopts loops", source), is(1));
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
    }

    @Test
    public void corpus_nightlyExtractKsh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/nightly_extract.ksh");
        assertThat(count("functions", source), is(5));
        assertThat(count("getopts loops", source), is(1));
        assertThat(count("named positional parameters", source), is(2));
        assertThat(count("exported variables", source), is(2));
        assertThat(count("readonly variables", source), is(2));
        assertThat(count("defaulted variables", source), is(1));
        assertThat(count("script-level assignments", source), is(1));
        assertThat(count("exit statements with a status", source), is(1));
        assertThat(count("typeset declarations", source), is(10));
    }

    @Test
    public void corpus_healthcheckSh() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/shell/healthcheck.sh");
        assertThat(count("functions", source), is(4));
        assertThat(count("defaulted variables", source), is(2));
        assertThat(count("script-level assignments", source), is(4));
        assertThat(count("exit statements with a status", source), is(4));
        assertThat(count("set -e", source), is(0));
    }
    // </editor-fold>
}
