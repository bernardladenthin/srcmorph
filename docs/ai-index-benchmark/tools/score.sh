#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Objective scorer for one AI-index benchmark cell.
# Usage: score.sh <ai_md_path> <run_log_path> <expected_bucket_count>
# Prints a single TSV row: sections\treachesConc\bytes\claimedMaxBucket\parenCount\countErr\truncated\promptTok\prefillMs\decodeTok\decodeMs
set -uo pipefail
ai="$1"; log="$2"; exp="$3"

if [ ! -f "$ai" ]; then
  echo -e "FAIL\tFAIL\t0\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA"
  exit 0
fi

# --- section completeness (9 expected) ---
secs=0
for h in "Purpose" "Type" "Input" "Output" "Core logic" "Public API" "Dependencies" "Exceptions" "Concurrency"; do
  if grep -qiE "^#+ +.*${h}" "$ai"; then secs=$((secs+1)); fi
done
reaches=0; grep -qiE "^#+ +.*Concurrency" "$ai" && reaches=1
bytes=$(wc -c < "$ai" | tr -d ' ')

# --- claimed counts (best effort) ---
claimed=$(grep -oE "adjustBucket[0-9]+" "$ai" | grep -oE "[0-9]+" | sort -n | tail -1)
[ -z "$claimed" ] && claimed=NA
paren=$(grep -oiE "[0-9]+[^0-9]{0,12}methods" "$ai" | grep -oE "^[0-9]+" | sort -n | tail -1)
[ -z "$paren" ] && paren=NA
cerr=NA
ref=$claimed; [ "$ref" = "NA" ] && ref=$paren
if [ "$ref" != "NA" ] && [ "$exp" != "NA" ]; then cerr=$(( ref > exp ? ref - exp : exp - ref )); fi

# --- log metrics (UTF-16 -> UTF-8) ---
txt=$(iconv -f UTF-16LE -t UTF-8 "$log" 2>/dev/null || cat "$log")
trunc=$(printf '%s' "$txt" | grep -oE "truncated = [0-9]+" | grep -oE "[0-9]+$" | sort -rn | head -1); [ -z "$trunc" ] && trunc=NA
pe=$(printf '%s' "$txt" | grep -oE "prompt eval time =[^/]*/ +[0-9]+ tokens" | tail -1)
ptok=$(printf '%s' "$pe" | grep -oE "/ +[0-9]+ tokens" | grep -oE "[0-9]+" | head -1); [ -z "$ptok" ] && ptok=NA
pms=$(printf '%s' "$pe" | grep -oE "= +[0-9.]+ ms" | grep -oE "[0-9.]+" | head -1); [ -z "$pms" ] && pms=NA
ev=$(printf '%s' "$txt" | grep -oE "[^t]eval time =[^/]*/ +[0-9]+ tokens" | tail -1)
dtok=$(printf '%s' "$ev" | grep -oE "/ +[0-9]+ tokens" | grep -oE "[0-9]+" | head -1); [ -z "$dtok" ] && dtok=NA
dms=$(printf '%s' "$ev" | grep -oE "= +[0-9.]+ ms" | grep -oE "[0-9.]+" | head -1); [ -z "$dms" ] && dms=NA

echo -e "${secs}\t${reaches}\t${bytes}\t${claimed}\t${paren}\t${cerr}\t${trunc}\t${ptok}\t${pms}\t${dtok}\t${dms}"
