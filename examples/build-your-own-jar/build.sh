#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Builds target/srcmorph.jar from pom.xml (the CLI plus the natives of this OS/arch).
# Extra arguments go to Maven, e.g. ./build.sh -Dgpu=vulkan-linux-x86-64
set -euo pipefail
cd "$(dirname "$0")"
mvn -B package "$@"
