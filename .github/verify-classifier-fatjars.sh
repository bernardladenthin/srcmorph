#!/usr/bin/env bash

# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Structural check of the srcmorph-cli fat jars before they are attached to a GitHub release.
#
# WHY THIS IS STRUCTURAL AND NOT A LAUNCH. The cross-repo rule is "no release asset is attached that
# CI has not run" (workspace/policies/fat-jar-release-assets.md), and the `smoke-fatjar` job does
# exactly that -- for the DEFAULT jar. The GPU-classifier jars cannot be covered the same
# way: a GitHub-hosted runner has no CUDA/ROCm/SYCL/OpenVINO device, and the only command that would
# load the native library is a real generation. `Plan` with the mock provider -- the one thing that
# does run on a GPU-less runner -- never touches it, so "launching" a classifier jar would assert
# nothing about the classifier. Pretending otherwise is worse than not checking: it reads as covered.
#
# What CAN be asserted without a device is that each jar is the artifact it claims to be:
#
#   1. exactly one jar per requested classifier, and no unexpected classifier jar;
#   2. every jar carries at least one native library (a jar with none is a broken assembly that
#      would fail at runtime on every platform);
#   3. every classifier jar carries the backend directory its NAME promises: net.ladenthin:llama
#      natives jars (5.2.0 on) are named <backend>-<os>-<arch> and hold <OS>/<ARCH>/<backend>/ --
#      this is the one that catches the failure the whole loop is exposed to. If
#      `-Dllama.classifier=` ever stops being wired through (a renamed property, a pom refactor),
#      Maven still resolves the default natives and the loop happily produces byte-similar jars
#      under GPU names. No other check in this pipeline would notice;
#   4. every classifier jar still carries every native of the default jar: the GPU backend is added
#      next to the CPU natives, which are the loader's fallback when the GPU runtime is missing;
#   5. the default jar carries natives for more than one OS (it is the all-platform CPU variant).
#
# A new classifier SHAPE fails this script until its expected native path is declared, rather than
# being skipped silently.
#
# Usage: verify-classifier-fatjars.sh <asset-dir> <classifier>...

set -euo pipefail

ASSET_DIR="${1:?usage: verify-classifier-fatjars.sh <asset-dir> <classifier>...}"
shift
CLASSIFIERS=("$@")

# Native libraries live under this prefix inside the jar, as <OS>/<ARCH>/<backend>/<lib>.
NATIVE_PREFIX="net/ladenthin/llama/"

fail() {
    echo "::error::$*" >&2
    exit 1
}

[ -d "$ASSET_DIR" ] || fail "asset directory '$ASSET_DIR' does not exist"
[ "${#CLASSIFIERS[@]}" -gt 0 ] || fail "no classifiers given -- pass the same list the build loop used"

# Sorted list of native-library entries in a jar, one per line.
natives_of() {
    unzip -Z1 "$1" | grep -E "^${NATIVE_PREFIX}.*\.(so|dll|dylib)$" | sort
}

# "<OS>/<ARCH>/<backend>/" directory a classifier's name promises (<backend>-<os>-<arch>). The
# catch-all is deliberate: a classifier shape nobody has mapped must red the job, not pass unchecked.
expected_path_of() {
    local c="$1" arch os_arch
    case "$c" in
        *-linux-x86-64) os_arch="Linux/x86_64" ;;
        *-linux-aarch64) os_arch="Linux/aarch64" ;;
        *-windows-x86-64) os_arch="Windows/x86_64" ;;
        *-windows-x86) os_arch="Windows/x86" ;;
        *-windows-aarch64) os_arch="Windows/aarch64" ;;
        *-android-aarch64) os_arch="Linux-Android/aarch64" ;;
        *) fail "classifier '$c' has no expected native path -- add its shape to expected_path_of()" ;;
    esac
    case "$c" in
        *-x86-64) arch="x86-64" ;;
        *) arch="${c##*-}" ;;
    esac
    local rest="${c%-"$arch"}"
    echo "$os_arch/${rest%-*}/"
}

default_jars=()
while IFS= read -r j; do default_jars+=("$j"); done < <(
    find "$ASSET_DIR" -maxdepth 1 -type f -name '*-jar-with-dependencies.jar' | sort
)
[ "${#default_jars[@]}" -eq 1 ] \
    || fail "expected exactly 1 default fat jar in '$ASSET_DIR', got ${#default_jars[@]}: ${default_jars[*]:-none}"
DEFAULT_JAR="${default_jars[0]}"

default_natives="$(natives_of "$DEFAULT_JAR")"
[ -n "$default_natives" ] || fail "$(basename "$DEFAULT_JAR") carries no native library at all"

# The default jar is the all-platform CPU variant: more than one OS directory under the prefix.
default_os_count=$(echo "$default_natives" | sed "s|^${NATIVE_PREFIX}||" | cut -d/ -f1 | sort -u | wc -l)
[ "$default_os_count" -gt 1 ] \
    || fail "$(basename "$DEFAULT_JAR") carries natives for only $default_os_count OS -- it is supposed to be the all-platform CPU jar"
echo "default: $(basename "$DEFAULT_JAR") -- $(echo "$default_natives" | wc -l) native(s) across $default_os_count OS"

# Every classifier jar present must be one we asked for; an extra one means the loop and this check
# disagree about what is being shipped.
for jar in "$ASSET_DIR"/*-jar-with-dependencies-*.jar; do
    [ -e "$jar" ] || continue
    name="$(basename "$jar")"
    suffix="${name##*-jar-with-dependencies-}"
    found="${suffix%.jar}"
    matched=0
    for c in "${CLASSIFIERS[@]}"; do [ "$c" = "$found" ] && matched=1 && break; done
    [ "$matched" -eq 1 ] || fail "unexpected classifier jar '$name' -- '$found' is not in the requested list"
done

for c in "${CLASSIFIERS[@]}"; do
    jars=()
    while IFS= read -r j; do jars+=("$j"); done < <(
        find "$ASSET_DIR" -maxdepth 1 -type f -name "*-jar-with-dependencies-${c}.jar" | sort
    )
    [ "${#jars[@]}" -eq 1 ] \
        || fail "expected exactly 1 fat jar for classifier '$c', got ${#jars[@]}: ${jars[*]:-none}"
    jar="${jars[0]}"

    natives="$(natives_of "$jar")"
    [ -n "$natives" ] || fail "$(basename "$jar") carries no native library at all"

    want="$(expected_path_of "$c")"
    echo "$natives" | grep -qF -- "${NATIVE_PREFIX}${want}" \
        || fail "$(basename "$jar") carries no native under ${NATIVE_PREFIX}${want} -- it does not contain what its name promises: $(echo "$natives" | tr '\n' ' ')"

    # Every CPU native of the default jar must still be there: it is the loader's fallback.
    missing="$(comm -23 <(echo "$default_natives") <(echo "$natives"))"
    [ -z "$missing" ] \
        || fail "$(basename "$jar") lost natives of the default jar (the CPU fallback): $(echo "$missing" | tr '\n' ' ')"

    echo "ok: $(basename "$jar") -- $(echo "$natives" | wc -l) native(s), matches ${want}"
done

echo "classifier fat jars verified: ${#CLASSIFIERS[@]} classifier(s) + the default"
