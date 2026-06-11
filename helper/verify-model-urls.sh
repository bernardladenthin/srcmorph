#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
# SPDX-License-Identifier: Apache-2.0
#
# verify-model-urls.sh
#
# Verifies that every AI model GGUF referenced by the <aiDefinitions> block in
# pom.xml is actually downloadable from HuggingFace.
#
# For each <aiDefinition> the download URL is derived from:
#   - the HuggingFace repo URL in the preceding XML comment, and
#   - the file name (basename of <modelPath>),
# normalised to the canonical  {repo}/resolve/main/{filename}  form.
# Duplicate URLs (e.g. one GGUF referenced by several context-size entries)
# are checked once.
#
# A model is considered OK when an HTTP HEAD (following redirects to the CDN)
# returns 200. The script exits non-zero if any URL fails, so it is usable as a
# CI gate. Requires: bash, python3, curl. Needs outbound access to huggingface.co.
#
# Usage:
#   helper/verify-model-urls.sh            # check every model in pom.xml
#   POM=/path/to/pom.xml helper/verify-model-urls.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POM="${POM:-$SCRIPT_DIR/../pom.xml}"

if [[ ! -f "$POM" ]]; then
  echo "pom.xml not found at: $POM" >&2
  exit 2
fi

# Extract unique, document-ordered download URLs from the <aiDefinitions> block.
mapfile -t URLS < <(python3 - "$POM" <<'PY'
import re, sys
text = open(sys.argv[1], encoding="utf-8").read()
start = text.index("<aiDefinitions>")
end = text.index("</aiDefinitions>")
region = text[start:end]
seen, out = set(), []
last = None
for m in re.finditer(r'(https://huggingface\.co/[^\s"<>]+)|<modelPath>([^<]+)</modelPath>', region):
    if m.group(1):
        last = m.group(1).rstrip(".")
    else:
        fname = m.group(2).strip().split("/")[-1]
        repo = re.sub(r"/(blob|resolve|tree)/.*$", "", last) if last else ""
        url = f"{repo}/resolve/main/{fname}"
        if url not in seen:
            seen.add(url)
            out.append(url)
print("\n".join(out))
PY
)

echo "Checking ${#URLS[@]} unique model URLs from $(basename "$POM")"
echo

fail=0
for url in "${URLS[@]}"; do
  code="$(curl -sIL -o /dev/null -w '%{http_code}' --max-time 30 "$url" || echo "000")"
  if [[ "$code" == "200" ]]; then
    printf '  OK   %s  %s\n' "$code" "$url"
  else
    printf '  FAIL %s  %s\n' "$code" "$url"
    fail=$((fail + 1))
  fi
done

echo
if [[ "$fail" -ne 0 ]]; then
  echo "$fail of ${#URLS[@]} model URL(s) FAILED" >&2
  exit 1
fi
echo "All ${#URLS[@]} model URLs OK"
