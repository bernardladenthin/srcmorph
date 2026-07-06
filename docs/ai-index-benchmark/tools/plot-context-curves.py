import sys, csv
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

tsv, outpng = sys.argv[1], sys.argv[2]
rows = [r for r in csv.DictReader(open(tsv), delimiter="\t")
        if (r.get("prompt_n") or "").isdigit() and (r.get("ttft_ms") or "").lstrip("-").isdigit()]

series = {}  # model -> list of (ctx, ttft_s, prefill_tps, decode_tps)
for r in rows:
    m = r["model"]
    series.setdefault(m, []).append((
        int(r["prompt_n"]),
        int(r["ttft_ms"]) / 1000.0,
        float(r["prefill_tps"]) if r["prefill_tps"] not in ("NA", "") else None,
        float(r["decode_tps"]) if r["decode_tps"] not in ("NA", "") else None,
    ))
for m in series:
    series[m].sort()

label = {
    "granite-4.0-h-tiny-Q4_K_M.gguf": "granite-4.0-h-tiny (1B-akt, HYBRID/Mamba)",
    "gpt-oss-20b-mxfp4.gguf": "gpt-oss-20b (3.6B-akt, Transformer)",
}
color = {"granite-4.0-h-tiny-Q4_K_M.gguf": "#2e7d32", "gpt-oss-20b-mxfp4.gguf": "#c62828"}

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5.2))

for m, pts in series.items():
    xs = [p[0] for p in pts]
    ax1.plot(xs, [p[1] for p in pts], "o-", color=color.get(m, None), label=label.get(m, m), linewidth=2, markersize=6)
    pf = [(p[0], p[2]) for p in pts if p[2] is not None]
    if pf:
        ax2.plot([p[0] for p in pf], [p[1] for p in pf], "o-", color=color.get(m, None), label=label.get(m, m), linewidth=2, markersize=6)

ax1.set_title("Reaktionszeit (TTFT) vs. Kontext  —  CPU (Ryzen 7 5800H)", fontsize=12, weight="bold")
ax1.set_xlabel("Kontext im Fenster (Tokens)  →  Agent lädt Dateien nach")
ax1.set_ylabel("Zeit bis zur ersten Aktion (Sekunden)")
ax1.grid(True, alpha=0.3); ax1.legend(fontsize=9)
ax1.axhline(y=5, color="gray", ls=":", alpha=0.6)
ax1.text(ax1.get_xlim()[1]*0.02, 5.3, "~5 s: Grenze noch erträglich", fontsize=8, color="gray")

ax2.set_title("Prefill-Geschwindigkeit vs. Kontext  —  CPU", fontsize=12, weight="bold")
ax2.set_xlabel("Kontext im Fenster (Tokens)")
ax2.set_ylabel("Prefill (Tokens/Sekunde)  —  höher = besser")
ax2.grid(True, alpha=0.3); ax2.legend(fontsize=9)

fig.suptitle("Agentisch mit wachsendem Kontext: wann überwiegt welches Modell? (CPU)", fontsize=13, weight="bold")
fig.tight_layout(rect=[0, 0, 1, 0.96])
fig.savefig(outpng, dpi=130)
print("wrote", outpng)

# also print a small crossover analysis
gt = dict((p[0], p[1]) for p in series.get("gpt-oss-20b-mxfp4.gguf", []))
ht = dict((p[0], p[1]) for p in series.get("granite-4.0-h-tiny-Q4_K_M.gguf", []))
print("ctx\th-tiny_ttft_s\tgptoss_ttft_s\twinner")
for c in sorted(set(list(gt) + list(ht))):
    a = ht.get(c); b = gt.get(c)
    w = "-" if a is None or b is None else ("h-tiny" if a < b else "gpt-oss")
    print(f"{c}\t{a}\t{b}\t{w}")
