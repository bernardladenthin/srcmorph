#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0

# Runs the srcmorph CLI against config_Plan.json: builds and logs the routing plan only (the
# GenerateFileIndex phase with planOnly forced true by the Plan command) - no model is loaded and
# nothing is written. Safe to run with no GGUF model on disk (generationProvider is "mock").
#
# The fat jar's file name is version-qualified (e.g.
# srcmorph-cli-1.2.0-SNAPSHOT-jar-with-dependencies.jar) and changes on every version bump, so the
# glob below picks whichever one was last built by `mvn package` in ../srcmorph-cli.
set -euo pipefail
cd "$(dirname "$0")"

JAR=$(ls ../srcmorph-cli/target/srcmorph-cli-*-jar-with-dependencies.jar 2>/dev/null | head -n 1)
if [ -z "$JAR" ]; then
  echo "srcmorph-cli fat jar not found under ../srcmorph-cli/target/ - run 'mvn package' first." >&2
  exit 1
fi

java -jar "$JAR" config_Plan.json
