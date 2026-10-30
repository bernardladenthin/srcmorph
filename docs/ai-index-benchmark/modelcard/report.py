# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Renders the generated tables of MODEL-CARD.md from runs/*.json and scores/summary.json.

Replaces the text between '<!-- generated:begin -->' and '<!-- generated:end -->' in
../MODEL-CARD.md, so the hand-written assessment around the tables stays untouched.

Usage: python report.py
"""
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
CARD = HERE.parent / "MODEL-CARD.md"
BEGIN, END = "<!-- generated:begin -->", "<!-- generated:end -->"
BACKEND = {"openrouter": "OpenRouter", "huggingface": "HF / nscale", "copilot": "Copilot API Gateway",
           "local": "local GGUF (RTX 3070)"}


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
    fmt_rows = ["| Model | No lead | `###` headings | Code fences | Reasoning leaks | Empty |",
                "|---|---|---|---|---|---|"]
    for model in models:
        key = model["key"]
        run, kinds_of = runs.get(key), summary.get(key)
        name = model["name"] + (" ¹" if model.get("origin") else "")
        if run is None or kinds_of is None:
            overview.append(f"| {name} | {BACKEND[model['backend']]} | not run | | | | |")
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
            f"| {name} | {BACKEND[run['backend']]} | {run['files']}{' (failed: ' + ', '.join(failed) + ')' if failed else ''}"
            f" | {total / 60:.1f} | {files_phase.get('seconds', 0) / max(file_count, 1):.1f} | {cost_text}"
            f" | {len(cut)}{' (' + ', '.join('`' + c + '`' for c in cut) + ')' if cut else ''} |")
        java, sql, other = kinds_of.get("java", {}), kinds_of.get("sql", {}), kinds_of.get("other", {})
        ticked = [kinds_of[k] for k in ("java", "sql", "other") if k in kinds_of]
        invented = [k["inventedPer100"] for k in ticked if k["inventedPer100"] is not None]
        kinds.append(
            f"| {name} | {fmt(java.get('compaction'))} | {fmt(java.get('recall'), '{:.0%}')}"
            f" | {fmt(sql.get('compaction'))} | {fmt(sql.get('recall'), '{:.0%}')} | {sql.get('numbers', '–')}"
            f" | {fmt(other.get('compaction'))} | {fmt(other.get('recall'), '{:.0%}')}"
            f" | {fmt(sum(invented) / len(invented) if invented else None, '{:.1f}')} |")
        all_kinds = list(kinds_of.values())
        fmt_rows.append(
            f"| {name} | {sum(k['noLead'] for k in all_kinds)} | {sum(k['badHeadings'] for k in all_kinds)}"
            f" | {sum(k['fences'] for k in all_kinds)} | {sum(k['leaks'] for k in all_kinds)}"
            f" | {sum(k['empty'] + k['missing'] for k in all_kinds)} |")

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
        "### Format discipline (all 37 `.ai.md` per model)",
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
