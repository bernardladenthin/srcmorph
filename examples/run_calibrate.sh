#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Runs the srcmorph CLI against config_Calibrate.json: loads each distinct routed model once and
# prints a paste-ready <calibration> block per model. With the mock provider (as shipped) this is
# a no-op smoke check; swap generationProvider to "llamacpp-jni" and set a real modelPath to
# calibrate an actual GGUF model on this machine.
#
# Runs the jar of build-your-own-jar/ (srcmorph-cli plus the natives of this machine); build it
# once with build-your-own-jar/build.sh.
set -euo pipefail
cd "$(dirname "$0")"

JAR=build-your-own-jar/target/srcmorph.jar
if [ ! -f "$JAR" ]; then
  echo "$JAR not found - run build-your-own-jar/build.sh first." >&2
  exit 1
fi

java -jar "$JAR" config_Calibrate.json
