#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0

# Runs the srcmorph CLI against config_All.json: all three phases in order (GenerateFileIndex,
# AggregatePackages, AggregateProject). Uses the mock provider, so no GGUF model is required -
# point generationProvider at "llamacpp-jni" and set a real modelPath to run a model.
#
# The fat jar's file name is version-qualified (e.g.
# srcmorph-cli-1.1.1-jar-with-dependencies.jar) and changes on every version bump, so the
# glob below picks whichever one was last built by `mvn package` in ../srcmorph-cli.
set -euo pipefail
cd "$(dirname "$0")"

JAR=$(ls ../srcmorph-cli/target/srcmorph-cli-*-jar-with-dependencies.jar 2>/dev/null | head -n 1)
if [ -z "$JAR" ]; then
  echo "srcmorph-cli fat jar not found under ../srcmorph-cli/target/ - run 'mvn package' first." >&2
  exit 1
fi

java -jar "$JAR" config_All.json
