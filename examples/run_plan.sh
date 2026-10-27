#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Runs the srcmorph CLI against config_Plan.json: builds and logs the routing plan only (the
# GenerateFileIndex phase with planOnly forced true by the Plan command) - no model is loaded and
# nothing is written. Safe to run with no GGUF model on disk (generationProvider is "mock").
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

java -jar "$JAR" config_Plan.json
