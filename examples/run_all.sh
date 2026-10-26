#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Runs the srcmorph CLI against config_All.json: all three phases in order (GenerateFileIndex,
# AggregatePackages, AggregateProject). Uses the mock provider, so no GGUF model is required -
# point generationProvider at "llamacpp-jni" and set a real modelPath to run a model.
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

java -jar "$JAR" config_All.json
