import sys, json, re

model = sys.argv[1]; load_s = sys.argv[2]; expect_fn = sys.argv[3]
raw = sys.stdin.read()
total = "NA"
m = re.search(r"TOTAL:([0-9.]+)", raw)
if m: total = m.group(1)
raw = re.sub(r"\nTOTAL:[0-9.]+", "", raw)

def out(ttft, ptps, dtps, ok, notes):
    print(f"{model}\t{load_s}\t{ttft}\t{ptps}\t{dtps}\t{ok}\t{notes}")

try:
    d = json.loads(raw)
except Exception:
    out("NA","NA","NA","ERR", "bad-json/"+raw[:40].replace("\t"," ").replace("\n"," "))
    sys.exit()

if isinstance(d, dict) and d.get("error"):
    out("NA","NA","NA","ERR", str(d["error"])[:50])
    sys.exit()

msg = (d.get("choices") or [{}])[0].get("message", {})
tcs = msg.get("tool_calls") or []
fn = tcs[0]["function"]["name"] if tcs else None
args = tcs[0]["function"].get("arguments") if tcs else None
# some models emit the call as text instead of native tool_calls -> detect fallback
content = msg.get("content") or ""
text_call = expect_fn in content if content else False
if fn == expect_fn:
    ok = "YES"
elif fn:
    ok = "WRONG-FN:" + fn
elif text_call:
    ok = "TEXT"          # emitted call as prose, not native tool_calls
else:
    ok = "NO"

t = d.get("timings") or {}
ttft = round(t["prompt_ms"]) if "prompt_ms" in t else (round(float(total)*1000) if total!="NA" else "NA")
ptps = round(t["prompt_per_second"],1) if t.get("prompt_per_second") else "NA"
dtps = round(t["predicted_per_second"],1) if t.get("predicted_per_second") else "NA"
notes = f"args={args}" if args else (f"total={total}s")
out(ttft, ptps, dtps, ok, notes[:60])
