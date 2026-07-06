import sys, json, re
model, ctx_target, load_s, expect_fn = sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4]
raw = sys.stdin.read()
total = "NA"
m = re.search(r"TOTAL:([0-9.]+)", raw)
if m: total = m.group(1)
raw = re.sub(r"\nTOTAL:[0-9.]+", "", raw)

def out(pn, ttft, ptps, dtps, ok, notes):
    print(f"{model}\t{ctx_target}\t{pn}\t{load_s}\t{ttft}\t{ptps}\t{dtps}\t{ok}\t{notes}")

try:
    d = json.loads(raw)
except Exception:
    out("NA","NA","NA","NA","ERR", "bad-json/"+raw[:40].replace(chr(9)," ").replace(chr(10)," ")); sys.exit()
if isinstance(d, dict) and d.get("error"):
    out("NA","NA","NA","NA","ERR", str(d["error"])[:60]); sys.exit()

msg = (d.get("choices") or [{}])[0].get("message", {})
tcs = msg.get("tool_calls") or []
fn = tcs[0]["function"]["name"] if tcs else None
content = msg.get("content") or ""
if fn == expect_fn: ok = "YES"
elif fn: ok = "WRONG:" + fn
elif expect_fn in content: ok = "TEXT"
else: ok = "NO"

t = d.get("timings") or {}
pn = t.get("prompt_n", "NA")
ttft = round(t["prompt_ms"]) if "prompt_ms" in t else (round(float(total)*1000) if total != "NA" else "NA")
ptps = round(t["prompt_per_second"], 1) if t.get("prompt_per_second") else "NA"
dtps = round(t["predicted_per_second"], 1) if t.get("predicted_per_second") else "NA"
out(pn, ttft, ptps, dtps, ok, f"total={total}s")
