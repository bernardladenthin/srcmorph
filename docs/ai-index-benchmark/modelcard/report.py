# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Renders the generated tables of MODEL-CARD.md from runs/*.json and scores/summary.json.

Replaces the text between '<!-- generated:begin -->' and '<!-- generated:end -->' in
../MODEL-CARD.md, so the hand-written assessment around the tables stays untouched.

Usage: python report.py
"""
import json
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
CARD = HERE.parent / "MODEL-CARD.md"
BEGIN, END = "<!-- generated:begin -->", "<!-- generated:end -->"
BACKEND = {"openrouter": "OpenRouter", "huggingface": "HF / nscale", "copilot": "Copilot API Gateway",
           "local": "local GGUF (RTX 3070)"}



def invented_per_100(kinds):
    """The invented names per 100 backticked ones over every kind: their sums, so a kind with three
    backticked names weighs as much as its three, not as much as a kind with three thousand."""
    if any("ticked" not in k for k in kinds):
        sys.exit("a summary.json without the ticked and invented totals: run score.py again")
    ticked = sum(k["ticked"] for k in kinds)
    return 100 * sum(k["invented"] for k in kinds) / ticked if ticked else None

def fmt(value, pattern="{:.2f}", missing="–"):
    return missing if value is None else pattern.format(value)


def main():
    models = json.loads((HERE / "models.json").read_text(encoding="utf-8"))["models"]
    summary = json.loads((HERE / "scores" / "summary.json").read_text(encoding="utf-8"))
    runs = {p.stem: json.loads(p.read_text(encoding="utf-8")) for p in sorted((HERE / "runs").glob("*.json"))}

    overview = ["| Model | Backend | Files | Time (min) | s/file | Cost (USD) | Cut off |",
                "|---|---|---|---|---|---|---|"]
    kinds = ["| Model | Java comp. | Java recall | SQL comp. | SQL recall | Seed counts | Other comp. | Other recall | Invented /100 |",
             "|---|---|---|---|---|---|---|---|---|"]
    fmt_rows = ["| Model | No lead | `###` headings | Code fences | Reasoning leaks | Failed | Cut off |",
                "|---|---|---|---|---|---|---|"]
    per_model = set()
    for model in models:
        if model.get("corpus"):
            continue  # measured on its own corpus (large.py), not on the card's
        key = model["key"]
        run, kinds_of = runs.get(key), summary.get(key)
        name = model["name"] + (" ¹" if model.get("origin") else "")
        if run is None or kinds_of is None:
            overview.append(f"| {name} | {BACKEND.get(model['backend'], model['backend'])} | not run | | | | |")
            continue
        phases = run["phases"]
        total = sum(p["seconds"] for p in phases.values())
        files_phase = phases.get("GenerateFileIndex", {})
        file_count = sum(kinds_of[k]["files"] for k in ("java", "sql", "other") if k in kinds_of)
        cut = sorted({f for p in phases.values() for f in p.get("cutOff", [])})
        failed = [n for n, p in phases.items() if p["exit"] != 0]
        cost = run.get("costUsd")
        cost_text = fmt(cost, "{:.4f}") if run["backend"] == "openrouter" else (
            "0 (local)" if run["backend"] == "local" else
            "Copilot plan" if run["backend"] == "copilot" else "see text")
        overview.append(
            f"| {name} | {BACKEND.get(run['backend'], run['backend'])} | {run['files']}{' (failed: ' + ', '.join(failed) + ')' if failed else ''}"
            f" | {total / 60:.1f} | {files_phase.get('seconds', 0) / max(file_count, 1):.1f} | {cost_text}"
            f" | {len(cut)}{' (' + ', '.join('`' + c + '`' for c in cut) + ')' if cut else ''} |")
        java, sql, other = kinds_of.get("java", {}), kinds_of.get("sql", {}), kinds_of.get("other", {})
        invented = invented_per_100([kinds_of[k] for k in ("java", "sql", "other") if k in kinds_of])
        kinds.append(
            f"| {name} | {fmt(java.get('compaction'))} | {fmt(java.get('recall'), '{:.0%}')}"
            f" | {fmt(sql.get('compaction'))} | {fmt(sql.get('recall'), '{:.0%}')} | {sql.get('numbers', '–')}"
            f" | {fmt(other.get('compaction'))} | {fmt(other.get('recall'), '{:.0%}')}"
            f" | {fmt(invented, '{:.1f}')} |")
        all_kinds = list(kinds_of.values())
        per_model.add(sum(k["files"] for k in all_kinds))
        reasons = {}
        for k in all_kinds:
            for reason, count in k["failed"].items():
                reasons[reason] = reasons.get(reason, 0) + count
        failed_text = str(sum(reasons.values())) + (
            " (" + ", ".join(f"{r} {c}" for r, c in sorted(reasons.items())) + ")" if reasons else "")
        fmt_rows.append(
            f"| {name} | {sum(k['noLead'] for k in all_kinds)} | {sum(k['badHeadings'] for k in all_kinds)}"
            f" | {sum(k['fences'] for k in all_kinds)} | {sum(k['leaks'] for k in all_kinds)}"
            f" | {failed_text} | {sum(k['cutOff'] for k in all_kinds)} |")

    generated = "\n".join([
        BEGIN,
        "### Run overview",
        "",
        *overview,
        "",
        "### Compaction and faithfulness per file kind",
        "",
        *kinds,
        "",
        "### Format discipline (all " + "/".join(str(n) for n in sorted(per_model)) + " `.ai.md` per model)",
        "",
        *fmt_rows,
        "",
        "¹ also in the June 2026 benchmark ([COMPARISON.md](COMPARISON.md)).",
        END,
    ])
    text = CARD.read_text(encoding="utf-8")
    start, end = text.index(BEGIN), text.index(END) + len(END)
    CARD.write_text(text[:start] + generated + text[end:], encoding="utf-8", newline="\n")
    print(f"updated {CARD}")


if __name__ == "__main__":
    main()
