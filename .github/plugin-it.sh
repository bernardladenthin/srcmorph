#!/usr/bin/env bash

# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Integration test for srcmorph-maven-plugin: runs the plugin as a PLUGIN, from the local
# repository, through a real Maven lifecycle, against the fixture project in .github/plugin-it/.
#
# WHY IT EXISTS. Every other test of this module calls Java methods directly --
# PluginArchitectureTest, MojoPhaseSkipTest, MojoGoalTest, PIT. None of them involves Maven, so none of
# them covers the parts only Maven does:
#
#   * what Maven injects: the project directory and its source and resource directories (the
#     subtrees when the configuration file names none), the project's name and properties
#     (a ${...} in the file), the command line's -D properties;
#   * goal-prefix resolution: that `mvn srcmorph:generate` finds this plugin at all;
#   * the srcmorph.* @Parameter property strings, which nothing else asserts -- rename one and
#     every unit test still passes because they set the field, not the property;
#   * lifecycle-phase binding of the goals;
#   * the descriptor maven-plugin-plugin generates, which is what carries all of the above.
#
# The fixture is configured by .github/plugin-it/srcmorph.yaml on the `mock` provider, so no GGUF, no
# GPU and no network are needed, and it mirrors the worked example in srcmorph-maven-plugin/README.md
# -- so this also fails when the documented configuration stops being the one that works.
#
# NO `-s settings.xml`, AND THAT IS LOAD-BEARING. An earlier version passed a settings.xml whose
# only content was <pluginGroups><pluginGroup>net.ladenthin</pluginGroup></pluginGroups>, on the
# assumption that `mvn srcmorph:generate` needs it. It does not: the fixture declares the plugin in
# its own <build><plugins>, and Maven resolves the prefix by comparing the request against THAT
# plugin's descriptor -- which is exactly the check we want. Adding the pluginGroup opens a second
# resolution path, through the group's repository metadata, which maps srcmorph -> the plugin by
# artifactId regardless of what the descriptor says. Measured: with a mutated <goalPrefix>, the run
# succeeded WITH settings.xml (logging `morphsrc:1.2.0:generate`) and failed without it. The masking
# is not a stale-local-repo artifact either -- Central serves net/ladenthin/maven-metadata.xml
# carrying <prefix>srcmorph</prefix>, so a pristine runner would be masked too. Do not reintroduce
# that file.
#
# Usage: plugin-it.sh <reactor-version>
#   The reactor must already be installed into the local repository, e.g.
#   mvn -B -pl srcmorph-maven-plugin -am -Dmaven.test.skip=true install

set -euo pipefail

VERSION="${1:?usage: plugin-it.sh <reactor-version>}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PLUGIN_JAR="$REPO_ROOT/srcmorph-maven-plugin/target/srcmorph-maven-plugin-$VERSION.jar"
IT_DIR="$SCRIPT_DIR/plugin-it"
OUT="$IT_DIR/target/ai"
# The default output directory when the file's outputDirectory does not apply. Checked as well as
# OUT, so a run that writes to the wrong place cannot satisfy a "nothing was written" assertion.
DEFAULT_OUT="$IT_DIR/src/site/ai"
# Logs live under target/ so .gitignore already covers them and the CI job can upload them on
# failure; reset() therefore clears only the output trees, never the whole directory.
LOGS="$IT_DIR/target/logs"
MVN=(mvn -B --no-transfer-progress -f "$IT_DIR/pom.xml" "-Dsrcmorph.it.version=$VERSION")

fail() {
    echo "::error::$*" >&2
    exit 1
}

# Every .ai.md under a directory, one per line (empty when the directory is absent).
written() {
    [ -d "$1" ] || return 0
    find "$1" -type f -name '*.ai.md' | sort
}

assert_written() {
    [ -e "$1" ] || fail "expected $1 to be written -- $2"
}

assert_nothing_written() {
    local found
    found="$(written "$OUT")"
    [ -z "$found" ] || fail "$1 -- but these were written: $(echo "$found" | tr '\n' ' ')"
    # A run that wrote to the DEFAULT tree instead would otherwise satisfy the check above while
    # proving the opposite of what it claims.
    found="$(written "$DEFAULT_OUT")"
    [ -z "$found" ] || fail "$1 -- output landed in the default directory, so the file's outputDirectory did not apply: $(echo "$found" | tr '\n' ' ')"
}

reset() {
    rm -rf "$OUT" "$DEFAULT_OUT"
    mkdir -p "$LOGS"
}

# ---------------------------------------------------------------------------
# Preconditions -- checked before any assertion, so a missing install is reported as a missing
# install rather than as a failed check.
# ---------------------------------------------------------------------------
[ -e "$PLUGIN_JAR" ] || fail "plugin jar not found at $PLUGIN_JAR -- install the reactor first"

LOCAL_REPO="$(mvn -B -q --no-transfer-progress -N -DforceStdout help:evaluate -Dexpression=settings.localRepository -f "$REPO_ROOT/pom.xml" | tail -n1)"
RESOLVED_JAR="$LOCAL_REPO/net/ladenthin/srcmorph-maven-plugin/$VERSION/srcmorph-maven-plugin-$VERSION.jar"
[ -e "$RESOLVED_JAR" ] || fail "the plugin is not in the local repository at $RESOLVED_JAR -- install the reactor first"
# The descriptor check below reads the jar in target/, while every Maven run below resolves the one
# in the local repository. With a non-SNAPSHOT version and a restored build cache those can be
# different files, and then the descriptor assertions would be about an artifact nothing executes.
cmp -s "$PLUGIN_JAR" "$RESOLVED_JAR" \
    || fail "the plugin resolved from $LOCAL_REPO is NOT the jar just built -- stale local repository"

echo "== 1/7: full lifecycle, all three goals, configured by srcmorph.yaml =="
reset
"${MVN[@]}" verify > "$LOGS/run1.log" 2>&1 || { cat "$LOGS/run1.log"; fail "the fixture build failed"; }

GENERATED="$OUT/main/java/com/example/app/Greeter.java.ai.md"
# generate: one .ai.md per source file, under the package path the source lives in.
assert_written "$GENERATED" "the generate goal did not index the fixture sources"
# aggregate-packages: one per package directory.
assert_written "$OUT/main/java/com/example/app/package.ai.md" "the aggregate-packages goal did not run or wrote nothing"
# aggregate-project: the index on top.
assert_written "$OUT/project.ai.md" "the aggregate-project goal did not run or wrote nothing"

# The subtrees Maven fills: Extra.java is reachable ONLY through the resource directory the fixture
# pom adds (src/extra/java); the file names no subtrees.
assert_written "$OUT/extra/java/com/example/extra/Extra.java.ai.md" \
    "the project's resource directory was not a subtree -- src/extra/java was not indexed"
# excludes -- package-info.java exists for exactly this assertion.
[ ! -e "$OUT/main/java/com/example/app/package-info.java.ai.md" ] \
    || fail "the excludes did not apply -- package-info.java was indexed although the pattern excludes it"
# The skip rule: an and-group of a path glob and a size range, ranked above the java rule.
[ ! -e "$OUT/main/java/com/example/util/Names.java.ai.md" ] \
    || fail "the skip rule (and-group, priority) did not route Names.java -- it was summarized"

# The mock provider's marker: proves the generated body came through the provider abstraction.
grep -q "Mock summary" "$GENERATED" || fail "the generated file summary carries no mock-provider marker"
# The java rule's factsKey names profiles/v3/java-facts.tsv, which only profileDirectory reaches.
grep -q "^\*\*Facts (exact, whole file):\*\* type declarations: 1; " "$GENERATED" \
    || fail "the java-facts counters of the profile are not in the file summary -- got: $(grep -m1 'Facts' "$GENERATED" || echo none)"
# The extra rule's inline facts: the comment counter is ignored, so it is not reported, and the
# "class" of the line comment is not counted -- an unbound ignore would print both and count 2.
EXTRA="$OUT/extra/java/com/example/extra/Extra.java.ai.md"
grep -qx '\*\*Facts (exact, whole file):\*\* class keywords: 1' "$EXTRA" \
    || fail "the inline facts with ignore did not apply -- got: $(grep -m1 'Facts' "$EXTRA" || echo none)"
# The deterministic header the codec writes -- pins that a real AiMdDocument was written, not a stub.
grep -q "^- X: file$" "$GENERATED" || fail "the generated file summary has no 'X: file' header field"
grep -q "^- X: project$" "$OUT/project.ai.md" || fail "the project index has no 'X: project' header field"
# The project rule's templateFile: the overview was generated, so the prompt file was read.
grep -q "^#### Overview" "$OUT/project.ai.md" || fail "the project index has no overview -- the project rule did not run"

# ${it.aiVersion} of the file, filled from the fixture pom's property.
grep -q "^- A: 0.0.0-it$" "$GENERATED" \
    || fail "the pom property did not reach \${it.aiVersion} of the file -- got: $(grep '^- A: ' "$GENERATED" || echo none)"
# G is srcmorph's own version (support.AiGeneratorVersion), the plugin's, not the fixture pom's 1.0.0.
grep -qxF -e "- G: $VERSION" "$GENERATED" \
    || fail "G is not the plugin's version $VERSION -- got: $(grep '^- G: ' "$GENERATED" || echo none)"
# ${project.name}, which Maven fills.
grep -q "^### srcmorph plugin integration-test fixture$" "$OUT/project.ai.md" \
    || fail "\${project.name} did not reach the project index"
echo "ok: $(written "$OUT" | wc -l) .ai.md file(s); subtrees, excludes, skip rule, facts, properties and \${project.*} all observable"

echo
echo "== 2/7: a second run is incremental, srcmorph.force regenerates =="
# No reset: the tree of run 1 is up to date, so nothing is generated again ...
"${MVN[@]}" srcmorph:generate > "$LOGS/run2.log" 2>&1 || { cat "$LOGS/run2.log"; fail "the second run failed"; }
grep -q "Unchanged AI index file: .*Greeter.java.ai.md" "$LOGS/run2.log" \
    || fail "the second run did not find Greeter.java up to date -- the incremental check did not hold"
grep -q "Wrote AI index file" "$LOGS/run2.log" && fail "the second run rewrote a summary although no source changed"
# ... and srcmorph.force (a run switch the file cannot see) regenerates every one.
"${MVN[@]}" srcmorph:generate -Dsrcmorph.force=true > "$LOGS/run2-force.log" 2>&1 \
    || { cat "$LOGS/run2-force.log"; fail "the forced run failed"; }
grep -q "Wrote AI index file: .*Greeter.java.ai.md" "$LOGS/run2-force.log" \
    || fail "srcmorph.force=true did not regenerate Greeter.java"
echo "ok: unchanged sources are left alone, srcmorph.force rewrites them"

echo
echo "== 3/7: the generated plugin descriptor =="
# maven-plugin-plugin generates it from the @Mojo/@Parameter annotations, and it carries the goal
# prefix, the goal names, the floor and every property string -- none of which any unit test can see.
DESCRIPTOR="$(unzip -p "$PLUGIN_JAR" META-INF/maven/plugin.xml)" \
    || fail "the plugin jar carries no META-INF/maven/plugin.xml -- maven-plugin-plugin did not run"

grep -q "<goalPrefix>srcmorph</goalPrefix>" <<<"$DESCRIPTOR" \
    || fail "descriptor goalPrefix is not 'srcmorph': $(grep -o '<goalPrefix>[^<]*' <<<"$DESCRIPTOR")"
for goal in generate aggregate-packages aggregate-project calibrate check help; do
    grep -q "<goal>$goal</goal>" <<<"$DESCRIPTOR" || fail "descriptor declares no goal '$goal'"
done
# The floor the plugin is built for (Java 8 bytecode, Maven 3.6.3), not the maven-plugin-api version
# maven-plugin-plugin takes when nothing is set.
grep -q "<requiredMavenVersion>3.6.3</requiredMavenVersion>" <<<"$DESCRIPTOR" \
    || fail "descriptor requiredMavenVersion is not 3.6.3: $(grep -o '<requiredMavenVersion>[^<]*' <<<"$DESCRIPTOR")"
grep -q "<requiredJavaVersion>1.8</requiredJavaVersion>" <<<"$DESCRIPTOR" \
    || fail "descriptor requiredJavaVersion is not 1.8: $(grep -o '<requiredJavaVersion>[^<]*' <<<"$DESCRIPTOR")"

# The property set is compared BOTH ways against the expected list, so a renamed property fails and
# a NEW property also fails until somebody lists it here deliberately.
ACTUAL_PROPS="$(grep -oE '\$\{srcmorph[^}]*\}' <<<"$DESCRIPTOR" | tr -d '${}' | sort -u)"
EXPECTED_PROPS="$(printf '%s\n' \
    srcmorph.configurationFile \
    srcmorph.file.skip \
    srcmorph.force \
    srcmorph.package.skip \
    srcmorph.planOnly \
    srcmorph.project.skip \
    srcmorph.skip | sort)"
if ! diff <(echo "$ACTUAL_PROPS") <(echo "$EXPECTED_PROPS") > "$LOGS/props.diff" 2>&1; then
    cat "$LOGS/props.diff"
    fail "the descriptor's srcmorph.* property set drifted (< actual, > expected) -- a rename, or a new property that has to be listed in this script deliberately"
fi
echo "ok: prefix 'srcmorph', 6 goals, Maven 3.6.3 / Java 1.8, all $(echo "$EXPECTED_PROPS" | wc -l) property strings unchanged"

echo
echo "== 4/7: goal prefix, srcmorph.planOnly and a -D property into the file, from the command line =="
reset
# `srcmorph:generate` (not the fully-qualified coordinates) resolves through the descriptor of the
# plugin the fixture declares -- see the no-settings.xml note in the header for why that only holds
# without a <pluginGroups> entry.
"${MVN[@]}" srcmorph:generate -Dsrcmorph.planOnly=true > "$LOGS/run4.log" 2>&1 \
    || { cat "$LOGS/run4.log"; fail "mvn srcmorph:generate did not resolve or failed"; }
grep -q "AI index plan" "$LOGS/run4.log" || fail "the plan was not printed"
# The routing rule is matched BY ID here, which proves the condition tree routed rather than
# everything falling through to the fallback.
grep -q "| java | file-body-oop |" "$LOGS/run4.log" \
    || fail "the .java routing rule did not match -- the condition tree did not route"
# If srcmorph.planOnly is ever renamed, the property stops binding, the run generates for real, and
# this is the assertion that catches it.
assert_nothing_written "srcmorph.planOnly=true must stop before generating"

reset
# A -D beats the pom's property for a ${...} of the file.
"${MVN[@]}" srcmorph:generate -Dit.aiVersion=9.9.9 > "$LOGS/run4-property.log" 2>&1 \
    || { cat "$LOGS/run4-property.log"; fail "the property-configured run failed"; }
grep -q "^- A: 9.9.9$" "$GENERATED" \
    || fail "-Dit.aiVersion did not reach the file's \${it.aiVersion} -- got: $(grep '^- A: ' "$GENERATED" || echo none)"
echo "ok: goal prefix resolved, plan printed, nothing generated; a -D property reached the file"

echo
echo "== 5/7: the four skip properties, including one off-diagonal =="
# The global skip plus each phase's own. The off-diagonal case is the one that catches a
# copy-pasted property or field among the three per-phase flags -- the diagonal alone cannot.
reset
"${MVN[@]}" srcmorph:generate -Dsrcmorph.skip=true > "$LOGS/run5-global.log" 2>&1 \
    || { cat "$LOGS/run5-global.log"; fail "the globally skipped run failed"; }
grep -q "AI index generation skipped." "$LOGS/run5-global.log" \
    || fail "srcmorph.skip=true did not reach the skip branch"
assert_nothing_written "srcmorph.skip=true must write nothing"

reset
"${MVN[@]}" srcmorph:generate -Dsrcmorph.file.skip=true > "$LOGS/run5-file.log" 2>&1 \
    || { cat "$LOGS/run5-file.log"; fail "the file-skipped run failed"; }
grep -q "AI index generation skipped." "$LOGS/run5-file.log" \
    || fail "srcmorph.file.skip=true did not skip the generate goal"
assert_nothing_written "srcmorph.file.skip=true must write nothing"

reset
"${MVN[@]}" srcmorph:aggregate-packages -Dsrcmorph.package.skip=true > "$LOGS/run5-package.log" 2>&1 \
    || { cat "$LOGS/run5-package.log"; fail "the package-skipped run failed"; }
grep -q "AI package aggregation skipped." "$LOGS/run5-package.log" \
    || fail "srcmorph.package.skip=true did not skip the aggregate-packages goal"

reset
"${MVN[@]}" srcmorph:aggregate-project -Dsrcmorph.project.skip=true > "$LOGS/run5-project.log" 2>&1 \
    || { cat "$LOGS/run5-project.log"; fail "the project-skipped run failed"; }
grep -q "AI project index aggregation skipped." "$LOGS/run5-project.log" \
    || fail "srcmorph.project.skip=true did not skip the aggregate-project goal"

# Off-diagonal: the generate goal's flag must NOT skip a different phase.
reset
"${MVN[@]}" srcmorph:aggregate-packages -Dsrcmorph.file.skip=true > "$LOGS/run5-cross.log" 2>&1 \
    || { cat "$LOGS/run5-cross.log"; fail "the cross-skip run failed"; }
grep -q "AI package aggregation skipped." "$LOGS/run5-cross.log" \
    && fail "srcmorph.file.skip leaked into the aggregate-packages goal -- the per-phase flags are not independent"
echo "ok: global skip, three per-phase skips, and one off-diagonal"

echo
echo "== 6/7: the calibrate, check and help goals, and a missing configuration file =="
reset
# calibrate loads no model with the mock provider and finishes in well under a second.
"${MVN[@]}" srcmorph:calibrate > "$LOGS/run6-calibrate.log" 2>&1 \
    || { cat "$LOGS/run6-calibrate.log"; fail "srcmorph:calibrate failed"; }
grep -q "Model 'fixture-model'" "$LOGS/run6-calibrate.log" \
    || fail "calibrate did not resolve the aiDefinition by key"
grep -q "calibration:" "$LOGS/run6-calibrate.log" || fail "calibrate printed no paste-ready calibration: block"

reset
"${MVN[@]}" srcmorph:check > "$LOGS/run6-check.log" 2>&1 || { cat "$LOGS/run6-check.log"; fail "srcmorph:check failed on a sound configuration"; }
grep -q "Check passed: the plan" "$LOGS/run6-check.log" || fail "check did not report the plan as in order"
grep -q "Check passed: the package rule" "$LOGS/run6-check.log" || fail "check did not check the package rule"
assert_nothing_written "srcmorph:check must write nothing"
# A rule on a model nobody defines fails the check, naming it.
"${MVN[@]}" srcmorph:check -Dit.model=no-such-model > "$LOGS/run6-check-fail.log" 2>&1 \
    && fail "srcmorph:check passed although every rule names an undefined model"
grep -q "names the model 'no-such-model', which no aiDefinition defines" "$LOGS/run6-check-fail.log" \
    || { cat "$LOGS/run6-check-fail.log"; fail "the failed check did not name the undefined model"; }

"${MVN[@]}" srcmorph:help -Ddetail=true -Dgoal=generate > "$LOGS/run6-help.log" 2>&1 \
    || { cat "$LOGS/run6-help.log"; fail "srcmorph:help failed"; }
grep -q "planOnly" "$LOGS/run6-help.log" || fail "srcmorph:help -Dgoal=generate did not describe planOnly"
grep -q "configurationFile" "$LOGS/run6-help.log" || fail "srcmorph:help -Dgoal=generate did not describe configurationFile"

"${MVN[@]}" srcmorph:generate -Dsrcmorph.configurationFile=does-not-exist.yaml > "$LOGS/run6-missing.log" 2>&1 \
    && fail "the run passed without a configuration file"
grep -q "No srcmorph configuration file at" "$LOGS/run6-missing.log" \
    || { cat "$LOGS/run6-missing.log"; fail "a missing configuration file was not reported as such"; }
echo "ok: calibrate, check (passing and failing), help, and a missing file"

echo
echo "== 7/7: the self-test profile of the plugin module plans with its YAML =="
# srcmorph-selftest.yaml with the module's ${ai.*} properties: planned only, so no GGUF is needed;
# the run proves the profile's file binds and routes its two core packages.
mvn -B --no-transfer-progress -f "$REPO_ROOT/srcmorph-maven-plugin/pom.xml" -P srcmorph-selftest \
    "net.ladenthin:srcmorph-maven-plugin:$VERSION:generate" -Dsrcmorph.planOnly=true \
    > "$LOGS/run7-selftest.log" 2>&1 \
    || { cat "$LOGS/run7-selftest.log"; fail "the srcmorph-selftest profile did not plan"; }
grep -q "| java | file-body-oop |" "$LOGS/run7-selftest.log" \
    || fail "the self-test profile routed no Java file to its java rule"
echo "ok: the self-test profile planned"

echo
echo "plugin integration test PASSED"
