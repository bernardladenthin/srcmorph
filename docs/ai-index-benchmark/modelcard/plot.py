# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Draws the model-card charts from runs/*.json and scores/files.tsv.

The quality score of a model is the mean of four shares, each 0..1:
Java recall, SQL recall, other-file recall (all three as in score.py, an empty body scoring 0),
and the share of its 37 .ai.md that hold the format: a body with the '>' lead, no '#'-'###'
heading, no code fence and no leaked reasoning marker.

Every chart is written twice, for a light and a dark page (charts/<name>-light.svg and
charts/<name>-dark.svg), so MODEL-CARD.md can pick one with <picture>.

Usage: python plot.py
"""
import csv
import json
import textwrap
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402
from matplotlib.transforms import Bbox  # noqa: E402

HERE = Path(__file__).resolve().parent
SOURCE_BYTES = 159594 + 66780 + 8231  # Java + SQL + other files of the corpus (MODEL-CARD.md, Method)
OUT = HERE / "charts"

# Validated categorical slots 1-3 (all-pairs, light and dark) with a marker per backend as the
# second channel, so the backend never rests on color alone.
BACKENDS = {
    "openrouter": ("OpenRouter", "o"),
    "huggingface": ("Hugging Face router", "s"),
    "local": ("Local GGUF, RTX 3070 Laptop", "^"),
}
THEMES = {
    "light": {"surface": "#fcfcfb", "ink": "#0b0b0b", "ink2": "#52514e", "muted": "#898781",
              "grid": "#e1e0d9", "axis": "#c3c2b7",
              "series": {"openrouter": "#2a78d6", "huggingface": "#eb6834", "local": "#1baf7a"},
              "ramp": ["#cde2fb", "#9ec5f4", "#6da7ec", "#3987e5", "#256abf", "#184f95", "#0d366b"]},
    "dark": {"surface": "#1a1a19", "ink": "#f0efec", "ink2": "#c3c2b7", "muted": "#898781",
             "grid": "#2c2c2a", "axis": "#383835",
             "series": {"openrouter": "#3987e5", "huggingface": "#d95926", "local": "#199e70"},
             "ramp": ["#0d366b", "#104281", "#184f95", "#1c5cab", "#256abf", "#2a78d6", "#3987e5"]},
}
FONT = ["Segoe UI", "DejaVu Sans", "sans-serif"]
GAP = 0.25  # space before the quality-score column, which is derived from the columns left of it


def short_name(model):
    """The model name without the quantization, which the backend marker already implies."""
    name = model["name"].replace(" it", "").replace("-Instruct", "")
    for cut in (" (Q4", " (UD-"):
        if cut in name:
            name = name[:name.index(cut)] + (", thinking off" if "thinking off" in name else "")
            break
    return name.replace(" (reasoning off)", ", reasoning off").replace(" 30B-A3B", "")


def load():
    models = json.loads((HERE / "models.json").read_text(encoding="utf-8"))["models"]
    runs = {p.stem: json.loads(p.read_text(encoding="utf-8")) for p in (HERE / "runs").glob("*.json")}
    rows = {}
    with open(HERE / "scores" / "files.tsv", encoding="utf-8") as f:
        for row in csv.DictReader(f, delimiter="\t"):
            rows.setdefault(row["model"], []).append(row)
    data = []
    for model in models:
        key = model["key"]
        if key not in runs or key not in rows:
            continue
        files = rows[key]
        recall = {}
        for kind in ("java", "sql", "other"):
            per_file = [int(r["idsFound"]) / int(r["idsExpected"]) if int(r["bodyBytes"] or 0) else 0.0
                        for r in files if r["kind"] == kind and r["missing"] == "False"
                        and int(r["idsExpected"] or 0)]
            recall[kind] = sum(per_file) / len(per_file)
        clean = sum(1 for r in files if r["missing"] == "False" and int(r["bodyBytes"] or 0)
                    and r["lead"] == "True" and r["badHeadings"] == "0" and r["fences"] == "0"
                    and r["leaks"] == "0")
        fmt_share = clean / len(files)
        seed = next(r for r in files if r["file"].endswith("05_seed_data.sql"))
        run = runs[key]
        source_files = sum(1 for r in files if r["kind"] in ("java", "sql", "other"))
        data.append({
            "key": key, "name": short_name(model), "backend": model["backend"],
            "recall": recall, "format": fmt_share,
            "seed": int(seed["numbersStated"] or 0) / 4,
            "quality": (recall["java"] + recall["sql"] + recall["other"] + fmt_share) / 4,
            "secondsPerFile": run["phases"]["GenerateFileIndex"]["seconds"] / source_files,
            "cost": run.get("costUsd"),
        })
    return data


def style(ax, theme):
    t = THEMES[theme]
    ax.set_facecolor(t["surface"])
    ax.figure.set_facecolor(t["surface"])
    for side in ("top", "right"):
        ax.spines[side].set_visible(False)
    for side in ("left", "bottom"):
        ax.spines[side].set_color(t["axis"])
    ax.tick_params(colors=t["muted"], labelcolor=t["ink2"], labelsize=9)
    ax.grid(True, color=t["grid"], linewidth=0.6)
    ax.set_axisbelow(True)


def pareto(points, lower_x_is_better=True):
    """The points no other point beats on both x (lower) and quality (higher)."""
    front = []
    for p in sorted(points, key=lambda p: (p[0], -p[1])):
        if not front or p[1] > front[-1][1]:
            front.append(p)
    return front


def place_labels(ax, items, theme):
    """Greedy label placement: try offsets around each point, keep the first that overlaps no
    earlier label, no marker and stays inside the axes."""
    t = THEMES[theme]
    fig = ax.figure
    fig.canvas.draw()
    renderer = fig.canvas.get_renderer()
    inside = ax.get_window_extent(renderer)
    taken = []
    marks = [ax.transData.transform((x, y)) for x, y, _ in items]
    offsets = [(8, 4), (8, -12), (-8, 4), (-8, -12), (0, 10), (0, -16), (14, -4), (-14, -4),
               (10, 16), (-10, 16), (10, -24), (-10, -24), (24, 4), (-24, 4), (0, 24), (0, -30)]
    for (x, y, text), mark in sorted(zip(items, marks), key=lambda z: -z[0][1]):
        for dx, dy in offsets:
            ha = "left" if dx > 0 else ("right" if dx < 0 else "center")
            label = ax.annotate(text, (x, y), xytext=(dx, dy), textcoords="offset points", ha=ha,
                                va="bottom", fontsize=8, color=t["ink2"], fontfamily=FONT)
            box = label.get_window_extent(renderer).expanded(1.04, 1.12)
            hits_mark = any(box.overlaps(Bbox.from_extents(mx - 7, my - 7, mx + 7, my + 7)) for mx, my in marks)
            fits = (box.x0 >= inside.x0 and box.x1 <= inside.x1 and box.y0 >= inside.y0
                    and box.y1 <= inside.y1)
            if fits and not hits_mark and not any(box.overlaps(o) for o in taken):
                taken.append(box)
                break
            label.remove()
        else:
            label = ax.annotate(text, (x, y), xytext=(8, 4), textcoords="offset points", fontsize=8,
                                color=t["ink2"], fontfamily=FONT)
            taken.append(label.get_window_extent(renderer))


def legend(ax, theme, backends):
    t = THEMES[theme]
    handles = [ax.scatter([], [], s=60, marker=BACKENDS[b][1], color=t["series"][b],
                          edgecolors=t["surface"], linewidths=1.5, label=BACKENDS[b][0]) for b in backends]
    leg = ax.legend(handles=handles, loc="lower right", frameon=False, fontsize=9)
    for text in leg.get_texts():
        text.set_color(t["ink2"])


def scatter_chart(data, theme, x_of, xlabel, title, name, note, fronts):
    t = THEMES[theme]
    plt.rcParams["font.family"] = FONT
    fig, ax = plt.subplots(figsize=(10, 6.2))
    style(ax, theme)
    ax.set_xscale("log")
    shown = [d for d in data if x_of(d) is not None]
    for group, dash, text in fronts:
        front = pareto([(x_of(d), d["quality"]) for d in shown if d["backend"] in group])
        if len(front) < 2:
            continue
        ax.step([p[0] for p in front], [p[1] for p in front], where="post", color=t["muted"],
                linewidth=1.2, linestyle=dash, zorder=1)
        ax.annotate(text, front[0], xytext=(0, -14), textcoords="offset points", ha="center",
                    fontsize=8, color=t["muted"], fontstyle="italic")
    for backend in BACKENDS:
        group = [d for d in shown if d["backend"] == backend]
        if group:
            ax.scatter([x_of(d) for d in group], [d["quality"] for d in group], s=70,
                       marker=BACKENDS[backend][1], color=t["series"][backend], edgecolors=t["surface"],
                       linewidths=2, zorder=3)
    xs = [x_of(d) for d in shown]
    ax.set_xlim(min(xs) / 1.6, max(xs) * 2.6)
    ys = [d["quality"] for d in shown]
    ax.set_ylim(max(0.0, min(ys) - 0.06), 1.0)
    ax.yaxis.set_major_formatter(matplotlib.ticker.PercentFormatter(1.0, decimals=0))
    ax.xaxis.set_major_formatter(matplotlib.ticker.FuncFormatter(
        lambda v, _: f"{v:g}" if v >= 1 else f"{v:.3g}"))
    ax.set_xlabel(xlabel, color=t["ink2"], fontsize=10)
    ax.set_ylabel("Quality score (higher is better)", color=t["ink2"], fontsize=10)
    ax.set_title(title, color=t["ink"], fontsize=13, loc="left", pad=14)
    backends = sorted({d["backend"] for d in shown}, key=list(BACKENDS).index)
    if len(backends) > 1:
        legend(ax, theme, backends)
    note = textwrap.fill(note, 150)
    fig.text(0.01, 0.01, note, color=t["muted"], fontsize=8)
    fig.tight_layout(rect=(0, 0.025 + 0.022 * (note.count("\n") + 1), 1, 1))
    place_labels(ax, [(x_of(d), d["quality"], d["name"]) for d in shown], theme)
    fig.savefig(OUT / f"{name}-{theme}.svg", facecolor=t["surface"], metadata={"Date": None})
    plt.close(fig)


def heatmap(data, theme):
    t = THEMES[theme]
    plt.rcParams["font.family"] = FONT
    columns = [("Java recall", lambda d: d["recall"]["java"]), ("SQL recall", lambda d: d["recall"]["sql"]),
               ("Other recall", lambda d: d["recall"]["other"]), ("Format held", lambda d: d["format"]),
               ("Seed counts", lambda d: d["seed"]), ("Quality score", lambda d: d["quality"])]
    ordered = sorted(data, key=lambda d: -d["quality"])
    fig, ax = plt.subplots(figsize=(10, 0.42 * len(ordered) + 1.6))
    fig.set_facecolor(t["surface"])
    ax.set_facecolor(t["surface"])
    ramp = t["ramp"]
    for row, d in enumerate(ordered):
        for col, (_, value_of) in enumerate(columns):
            value = value_of(d)
            # The ramp spans 40-100 %, where the models differ; the cell text carries the value.
            shade = max(0.0, (value - 0.4) / 0.6)
            step = min(len(ramp) - 1, int(shade * (len(ramp) - 1) + 0.5))
            x0 = col + (GAP if col == len(columns) - 1 else 0)
            ax.add_patch(plt.Rectangle((x0 + 0.03, row + 0.06), 0.94, 0.88, color=ramp[step], linewidth=0))
            light_cell = step < 3 if theme == "light" else step > 4
            ax.text(x0 + 0.5, row + 0.5, f"{value:.0%}", ha="center", va="center", fontsize=9,
                    color=t["ink"] if light_cell else "#ffffff")
    ax.set_xlim(0, len(columns) + GAP)
    ax.set_ylim(len(ordered), 0)
    ax.set_xticks([c + 0.5 + (GAP if c == len(columns) - 1 else 0) for c in range(len(columns))])
    ax.set_xticklabels([c[0] for c in columns], color=t["ink2"], fontsize=9)
    ax.xaxis.tick_top()
    ax.set_yticks([r + 0.5 for r in range(len(ordered))])
    ax.set_yticklabels([d["name"] for d in ordered], color=t["ink2"], fontsize=9)
    # A marker before each name repeats the backend of the scatter charts.
    for row, d in enumerate(ordered):
        ax.scatter([-0.09], [row + 0.5], marker=BACKENDS[d["backend"]][1], s=36, color=t["series"][d["backend"]],
                   clip_on=False, zorder=3)
    ax.tick_params(axis="y", length=0, pad=18)
    ax.tick_params(axis="x", length=0, pad=6)
    for spine in ax.spines.values():
        spine.set_visible(False)
    ax.set_title("Quality profile per model, sorted by quality score", color=t["ink"], fontsize=13,
                 loc="left", pad=34)
    present = sorted({d["backend"] for d in ordered}, key=list(BACKENDS).index)
    handles = [ax.scatter([], [], marker=BACKENDS[b][1], s=36, color=t["series"][b], label=BACKENDS[b][0])
               for b in present]
    leg = fig.legend(handles=handles, loc="lower left", ncol=len(handles), frameon=False, fontsize=9)
    for text in leg.get_texts():
        text.set_color(t["ink2"])
    fig.tight_layout(rect=(0, 0.035, 1, 1))
    fig.savefig(OUT / f"quality-profile-{theme}.svg", facecolor=t["surface"], metadata={"Date": None})
    plt.close(fig)


def main():
    OUT.mkdir(exist_ok=True)
    plt.rcParams["svg.hashsalt"] = "srcmorph-modelcard"
    data = load()
    mb = SOURCE_BYTES / 1e6
    for theme in THEMES:
        scatter_chart(data, theme, lambda d: d["secondsPerFile"], "Seconds per source file (log scale, lower is faster)",
                      "Quality against speed", "quality-vs-speed",
                      "Lines: per group, no other model is both faster and better. Hosted times depend on the "
                      "provider's load; local times are one RTX 3070 Laptop (8 GB).",
                      [(("openrouter", "huggingface"), (0, (4, 3)), "hosted"), (("local",), (0, (1, 2)), "local")])
        scatter_chart(data, theme, lambda d: d["cost"] / mb if d["backend"] == "openrouter" else None,
                      "USD per MB of source code (log scale, lower is cheaper)",
                      "Quality against cost (OpenRouter)", "quality-vs-cost",
                      "Dashed line: no other model is both cheaper and better. The Hugging Face models "
                      f"cost under 0.01 USD together; the router reports no per-run usage. The corpus is "
                      f"{mb:.2f} MB; the cost includes its package and project summaries.",
                      [(("openrouter",), (0, (4, 3)), "")])
        heatmap(data, theme)
    for d in sorted(data, key=lambda d: -d["quality"]):
        print(f"{d['quality']:.3f} {d['secondsPerFile']:7.1f} s {d['cost'] if d['cost'] is not None else '-':>9} {d['name']}")


if __name__ == "__main__":
    main()
