#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0

# Runs srcmorph with srcmorph.yaml from this directory (or the configuration named as argument).
set -euo pipefail
cd "$(dirname "$0")"
java -jar target/srcmorph.jar "${1:-srcmorph.yaml}"
