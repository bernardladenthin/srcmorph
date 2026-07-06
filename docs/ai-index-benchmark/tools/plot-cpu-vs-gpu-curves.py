import sys, csv
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

cpu_tsv, htiny_vk, gptoss_vk, outpng = sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4]

def load(path, model_filter=None):
    pts = []
    try:
        for r in csv.DictReader(open(path), delimiter="\t"):
            if (r.get("prompt_n") or "").isdigit() and (r.get("ttft_ms") or "").lstrip("-").isdigit():
                if model_filter and r["model"] != model_filter:
                    continue
                pf = r.get("prefill_tps")
                pts.append((int(r["prompt_n"]), int(r["ttft_ms"]) / 1000.0,
                            float(pf) if pf not in ("NA", "", None) else None))
    except FileNotFoundError:
        pass
    return sorted(pts)

HT = "granite-4.0-h-tiny-Q4_K_M.gguf"
GO = "gpt-oss-20b-mxfp4.gguf"
series = [
    ("granite-h-tiny · GPU (Vulkan/3070)", load(htiny_vk, HT), "#2e7d32", "-", 2.4, "o"),
    ("granite-h-tiny · CPU",               load(cpu_tsv, HT),  "#2e7d32", "--", 1.6, "o"),
    ("gpt-oss-20b · GPU (Vulkan, partial)", load(gptoss_vk, GO), "#c62828", "-", 2.4, "s"),
    ("gpt-oss-20b · CPU",                  load(cpu_tsv, GO),  "#c62828", "--", 1.6, "s"),
]

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13.5, 5.4))
for name, pts, col, ls, lw, mk in series:
    if not pts: continue
    xs = [p[0] for p in pts]
    ax1.plot(xs, [p[1] for p in pts], marker=mk, ls=ls, color=col, lw=lw, ms=6, label=name)
    pf = [(p[0], p[2]) for p in pts if p[2] is not None]
    if pf:
        ax2.plot([p[0] for p in pf], [p[1] for p in pf], marker=mk, ls=ls, color=col, lw=lw, ms=6, label=name)

for ax in (ax1, ax2):
    ax.grid(True, alpha=0.3); ax.legend(fontsize=8.5)
    ax.set_xlabel("Kontext im Fenster (Tokens)  →  Agent lädt Dateien nach")
ax1.set_title("Reaktionszeit (TTFT) vs. Kontext", fontsize=12, weight="bold")
ax1.set_ylabel("Zeit bis zur ersten Aktion (Sekunden)")
ax1.axhline(y=1, color="gray", ls=":", alpha=0.6)
ax2.set_title("Prefill-Geschwindigkeit vs. Kontext", fontsize=12, weight="bold")
ax2.set_ylabel("Prefill (Tokens/Sekunde) — höher = besser")

fig.suptitle("granite-h-tiny vs gpt-oss-20b — CPU vs Vulkan-GPU (RTX 3070 8GB, kein CUDA)", fontsize=13, weight="bold")
fig.tight_layout(rect=[0, 0, 1, 0.95])
fig.savefig(outpng, dpi=130)
print("wrote", outpng)
