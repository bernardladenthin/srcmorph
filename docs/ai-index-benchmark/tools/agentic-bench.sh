#!/usr/bin/env bash
# Agentic reaction-time bench for local GGUF models via llama-server (tool-calling).
# Usage: agentic_bench.sh <out.tsv> <model1.gguf> [model2.gguf ...]
set -uo pipefail
LS="/c/Users/berna/AppData/Local/Microsoft/WinGet/Packages/ggml.llamacpp_Microsoft.Winget.Source_8wekyb3d8bbwe/llama-server.exe"
MODELDIR="X:/Modelle"
PORT=8090
HERE="$(dirname "$0")"
OUT="$1"; shift
export PATH="/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v13.3/bin:/c/Program Files/NVIDIA GPU Computing Toolkit/CUDA/v13.3/bin/x64:$PATH"
printf "model\tload_s\tttft_ms\tprefill_tps\tdecode_tps\ttool_ok\tnotes\n" > "$OUT"

EXPECT="run_tests"
REQ='{"messages":[{"role":"user","content":"Run the unit tests for the module named auth, then tell me you are done. Use the available tool."}],"tools":[{"type":"function","function":{"name":"run_tests","description":"Run unit tests for a given module and return pass/fail","parameters":{"type":"object","properties":{"module":{"type":"string","description":"the module name to test"}},"required":["module"]}}}],"tool_choice":"auto","temperature":0,"max_tokens":200,"cache_prompt":false}'

try_model() { # model ngl -> sets READY/LOAD_S, leaves server running if ready
  local mpath="$1" ngl="$2" log="$3"
  taskkill //F //IM llama-server.exe >/dev/null 2>&1; sleep 1
  "$LS" -m "$mpath" --host 127.0.0.1 --port $PORT -c 4096 -ngl $ngl --jinja --no-webui > "$log" 2>&1 &
  local t0=$(date +%s); READY=0
  for i in $(seq 1 240); do
    curl -s "http://127.0.0.1:$PORT/health" 2>/dev/null | grep -qi '"status":"ok"' && { READY=1; break; }
    grep -qiE "error loading|failed to fit|out of memory|unable to load|abort" "$log" 2>/dev/null && break
    sleep 1
  done
  LOAD_S=$(( $(date +%s) - t0 ))
}

for MODEL in "$@"; do
  MPATH="$MODELDIR/$MODEL"
  if [ ! -f "$MPATH" ]; then printf "%s\tNA\tNA\tNA\tNA\tNA\tmissing-file\n" "$MODEL" >> "$OUT"; continue; fi
  SZ=$(stat -c%s "$MPATH")
  USED_NGL=0
  if [ "$SZ" -lt 7000000000 ]; then
    try_model "$MPATH" 999 "$HERE/srv-$MODEL.log"; USED_NGL=999
    if [ "$READY" != 1 ]; then                       # GPU didn't fit -> CPU fallback
      try_model "$MPATH" 0 "$HERE/srv-$MODEL.log"; USED_NGL=0
    fi
  else
    try_model "$MPATH" 0 "$HERE/srv-$MODEL.log"; USED_NGL=0
  fi
  if [ "$READY" = 1 ]; then
    curl -s "http://127.0.0.1:$PORT/v1/chat/completions" -H "Content-Type: application/json" -d "$REQ" >/dev/null 2>&1  # warmup
    resp=$(curl -s -w $'\nTOTAL:%{time_total}' "http://127.0.0.1:$PORT/v1/chat/completions" -H "Content-Type: application/json" -d "$REQ" 2>/dev/null)
    printf '%s' "$resp" | python "$HERE/parse_agentic.py" "$MODEL(ngl=$USED_NGL)" "$LOAD_S" "$EXPECT" >> "$OUT"
  else
    printf "%s\t%s\tNA\tNA\tNA\tNA\tload-failed\n" "$MODEL(ngl=$USED_NGL)" "$LOAD_S" >> "$OUT"
  fi
  taskkill //F //IM llama-server.exe >/dev/null 2>&1; sleep 2
done
echo "AGENTIC BENCH DONE" >> "$OUT"
