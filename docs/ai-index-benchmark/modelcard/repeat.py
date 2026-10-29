# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Repeatability: how far quality, speed and cost move between runs of the same model.

Takes the card's own run (../outputs-2026-10 with runs/) plus the repeat roots (by default every
directory under ../repeat-2026-10), each made by run.py with --out <root>/out --meta <root>/meta, and
scores every run with score.py's code. Quality is
the card's: the mean of Java, SQL and other-file recall and the share of files that hold the format
(plot.py computes the same from scores/files.tsv; main() checks the two agree on the card's runs).

Writes <scores>/repeat.json: per model every run, then mean, min, max and spread (max - min).

Usage: python repeat.py --work <prepared corpus dir> [--repeat <root>[,<root>...]] [--scores ...]
"""
import argparse
import json
from pathlib import Path

from score import score_model

HERE = Path(__file__).resolve().parent


def quality(rows):
    """Recall per kind (an empty body counts 0), format share and the card's quality score."""
    recall = {}
    for kind in ("java", "sql", "other"):
        per_file = [r["idsFound"] / r["idsExpected"] if r["bodyBytes"] else 0.0
                    for r in rows if r["kind"] == kind and not r["missing"] and r.get("idsExpected")]
        recall[kind] = sum(per_file) / len(per_file)
    clean = sum(1 for r in rows if not r["missing"] and r["bodyBytes"] and r["lead"]
                and not r["badHeadings"] and not r["fences"] and not r["leaks"])
    fmt = clean / len(rows)
    return {"quality": round((recall["java"] + recall["sql"] + recall["other"] + fmt) / 4, 4),
            "recall": {k: round(v, 4) for k, v in recall.items()}, "format": round(fmt, 4)}


def run_entry(label, model_dir, meta_path, work, facts):
    rows = score_model(model_dir, work, facts)
    entry = {"run": label, **quality(rows)}
    if meta_path.exists():
        meta = json.loads(meta_path.read_text(encoding="utf-8"))
        sources = sum(1 for r in rows if r["kind"] in ("java", "sql", "other"))
        entry["secondsPerFile"] = round(meta["phases"]["GenerateFileIndex"]["seconds"] / sources, 2)
        entry["costUsd"] = meta.get("costUsd")
        entry["seed"] = meta["definition"].get("seed")
    return entry


def spread(values):
    values = [v for v in values if v is not None]
    if not values:
        return None
    return {"mean": round(sum(values) / len(values), 4), "min": min(values), "max": max(values),
            "spread": round(max(values) - min(values), 4)}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work", required=True)
    parser.add_argument("--repeat", default=",".join(
        str(p) for p in sorted((HERE.parent / "repeat-2026-10").iterdir()) if p.is_dir()))
    parser.add_argument("--scores", default=str(HERE / "scores"))
    args = parser.parse_args()
    work = Path(args.work)
    facts = json.loads((HERE / "facts.json").read_text(encoding="utf-8"))
    roots = [Path(r) for r in args.repeat.split(",")]
    keys = sorted({p.name for root in roots for p in (root / "out").iterdir() if p.is_dir()})

    result = {}
    for key in keys:
        runs = [run_entry("card", HERE.parent / "outputs-2026-10" / key, HERE / "runs" / f"{key}.json", work, facts)]
        for root in roots:
            if (root / "out" / key).is_dir():
                runs.append(run_entry(root.name, root / "out" / key, root / "meta" / f"{key}.json", work, facts))
        result[key] = {"runs": runs,
                       **{field: spread([r.get(field) for r in runs])
                          for field in ("quality", "secondsPerFile", "costUsd")}}
        q = result[key]["quality"]
        print(f"{key}: quality {[r['quality'] for r in runs]} spread {q['spread']}", flush=True)

    # The card's quality from plot.py must match this file's: the same metric, two implementations.
    from plot import load
    card = {d["key"]: d["quality"] for d in load()}
    for key, entry in result.items():
        assert abs(card[key] - entry["runs"][0]["quality"]) < 1e-3, (key, card[key], entry["runs"][0]["quality"])

    scores = Path(args.scores)
    scores.mkdir(parents=True, exist_ok=True)
    (scores / "repeat.json").write_text(json.dumps(result, indent=1) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
