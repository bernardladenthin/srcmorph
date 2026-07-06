#!/usr/bin/env bash
# CPU agentic reaction-time vs context-fill, for a fixed model set. Loads each model ONCE,
# then measures a tool-call at several context sizes.
# Usage: agentic_cpu_ctx.sh <out.tsv> <model.gguf:maxctx> [<model.gguf:maxctx> ...]
set -uo pipefail
LS="/c/Users/berna/AppData/Local/Microsoft/WinGet/Packages/ggml.llamacpp_Microsoft.Winget.Source_8wekyb3d8bbwe/llama-server.exe"
MODELDIR="X:/Modelle"; PORT=8090; HERE="$(dirname "$0")"
OUT="$1"; shift
EXPECT="run_tests"
FILL_LEVELS="${FILL_LEVELS:-512 3072 16384}"   # override via env for finer sweeps
printf "model\tctx_target\tprompt_n\tload_s\tttft_ms\tprefill_tps\tdecode_tps\ttool_ok\tnotes\n" > "$OUT"

build_req() {  # target_tokens -> writes $HERE/req.json (filler code ~target tokens, then the tool task)
  python - "$1" <<'PY' > "$HERE/req.json"
import sys, json
target = int(sys.argv[1])
line = "    public final int f%d = compute(a%d, b%d) + offset; // context filler line %d\n"
buf, i, s, approx = [], 0, 0, int(target * 3.4)
while s < approx:
    ln = line % (i, i, i, i); buf.append(ln); s += len(ln); i += 1
user = ("// Large Java source under review:\n" + "".join(buf) +
        "\n\nNow: run the unit tests for the module named auth, then say you are done. Use the available tool.")
req = {"messages":[{"role":"user","content":user}],
 "tools":[{"type":"function","function":{"name":"run_tests","description":"Run unit tests for a given module","parameters":{"type":"object","properties":{"module":{"type":"string"}},"required":["module"]}}}],
 "tool_choice":"auto","temperature":0,"max_tokens":120,"cache_prompt":False}
print(json.dumps(req))
PY
}

for ENTRY in "$@"; do
  MODEL="${ENTRY%%:*}"; MAXC="${ENTRY##*:}"
  MPATH="$MODELDIR/$MODEL"
  [ -f "$MPATH" ] || { printf "%s\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tmissing\n" "$MODEL" >> "$OUT"; continue; }
  CTX=$MAXC; [ "$CTX" -gt 17408 ] && CTX=17408          # cap allocation
  taskkill //F //IM llama-server.exe >/dev/null 2>&1; sleep 1
  t0=$(date +%s); ready=0
  "$LS" -m "$MPATH" --host 127.0.0.1 --port $PORT -c $CTX -ngl 0 --jinja --no-webui > "$HERE/srv-$MODEL.log" 2>&1 &
  for i in $(seq 1 300); do
    curl -s "http://127.0.0.1:$PORT/health" 2>/dev/null | grep -qi '"status":"ok"' && { ready=1; break; }
    grep -qiE "error loading|failed to fit|out of memory|abort" "$HERE/srv-$MODEL.log" 2>/dev/null && break
    sleep 1
  done
  LOAD_S=$(( $(date +%s) - t0 ))
  if [ "$ready" != 1 ]; then printf "%s\tNA\tNA\t%s\tNA\tNA\tNA\tNA\tload-failed\n" "$MODEL" "$LOAD_S" >> "$OUT"; taskkill //F //IM llama-server.exe >/dev/null 2>&1; continue; fi
  for LVL in $FILL_LEVELS; do
    [ "$LVL" -gt $((CTX - 300)) ] && continue
    build_req "$LVL"
    resp=$(curl -s -w $'\nTOTAL:%{time_total}' "http://127.0.0.1:$PORT/v1/chat/completions" -H "Content-Type: application/json" -d @"$HERE/req.json" 2>/dev/null)
    printf '%s' "$resp" | python "$HERE/parse_ctx.py" "$MODEL" "$LVL" "$LOAD_S" "$EXPECT" >> "$OUT"
  done
  taskkill //F //IM llama-server.exe >/dev/null 2>&1; sleep 2
done
echo "CPU CTX BENCH DONE" >> "$OUT"
