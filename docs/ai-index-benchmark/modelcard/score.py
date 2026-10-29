# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Scores the model-card outputs: compaction, fact recall, invented names and format per file kind.

For every model under <out> (one directory per model key) and every source file of the corpus:
- compaction = source bytes / body bytes (the .ai.md without its header), summed per kind over
  the files that got a body (an empty body counts under "empty", and against recall);
- recall = per file the share of the expected identifiers (facts.json; for Java the type name plus the
  public/protected method names taken from the source) the body names as a whole word, averaged
  over the files of a kind;
- invented = backticked identifiers in the body that occur nowhere in the source, per 100
  backticked identifiers (a proxy for hallucinated names, not a full fact check);
- numbers = the exact counts of facts.json the body states (the seed-data row counts);
- format: missing lead line, '###' or higher headings, code fences, leaked reasoning markers.

Writes <scores>/files.tsv (one row per model and file) and <scores>/summary.json (per model and kind),
which MODEL-CARD.md is built from.

Usage: python score.py --work <prepared corpus dir> [--out ...] [--scores ...]
"""
import argparse
import json
import re
from collections import defaultdict
from pathlib import Path

HERE = Path(__file__).resolve().parent
KINDS = {".java": "java", ".sql": "sql"}
IDENT = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
BACKTICK = re.compile(r"`([^`\n]+)`")
JAVA_METHOD = re.compile(
    r"^\s*(?:public|protected)\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|default\s+|native\s+)*"
    r"(?:<[^>]+>\s+)?[\w<>\[\],.? ]+?\s+(\w+)\s*\(", re.M)
JAVA_TYPE = re.compile(r"^\s*public\s+(?:final\s+|abstract\s+|sealed\s+)*(?:class|interface|enum|record)\s+(\w+)", re.M)
LEAK = re.compile(r"</?think>|<\|start\|>|<\|channel\|>|<\|message\|>|<\|end\|>")
# Words a summary may backtick that are neither invented names nor in the source text.
GENERIC = {"null", "true", "false", "void", "int", "long", "boolean", "String", "Object", "this", "new",
           "return", "static", "final", "public", "private", "protected", "class", "interface", "enum",
           "record", "abstract", "NULL", "TRUE", "FALSE", "INTEGER", "BIGINT", "VARCHAR", "TEXT", "DATE"}


def body_of(text):
    """The .ai.md body: everything after the header's '---' line. A file written without that line
    holds only the header: the generation came back empty (e.g. all reasoning, no answer)."""
    parts = text.split("\n---\n", 1)
    return parts[1] if len(parts) == 2 else ""


def kind_of(path):
    return KINDS.get(path.suffix, "other")


def expected_ids(rel, source, facts):
    if rel in facts:
        return facts[rel].get("ids", []), facts[rel].get("numbers", [])
    if rel.endswith(".java"):
        names = set(JAVA_TYPE.findall(source)) | set(JAVA_METHOD.findall(source))
        return sorted(n for n in names if n not in ("if", "for", "while", "switch", "return")), []
    return [], []


def score_file(source, body, ids, numbers, case_insensitive=False):
    words = set(IDENT.findall(body))
    found = [i for i in ids if (i in words if IDENT.fullmatch(i) else i in body)]
    ticked = [t for span in BACKTICK.findall(body) for t in IDENT.findall(span)
              if len(t) >= 3 and t not in GENERIC]
    # SQL names and keywords are case-insensitive: `integer` names the source's INTEGER.
    haystack = source.lower() if case_insensitive else source
    invented = [t for t in ticked if (t.lower() if case_insensitive else t) not in haystack]
    stated = [n for n in numbers if re.search(r"(?<![\d.])" + str(n) + r"(?![\d.])", body)]
    lines = body.strip().splitlines()
    return {
        "srcBytes": len(source.encode("utf-8")),
        "bodyBytes": len(body.strip().encode("utf-8")),
        "idsExpected": len(ids),
        "idsFound": len(found),
        "ticked": len(ticked),
        "invented": len(invented),
        "inventedNames": sorted(set(invented))[:8],
        "numbersExpected": len(numbers),
        "numbersStated": len(stated),
        "lead": bool(lines) and lines[0].startswith(">"),
        "badHeadings": sum(1 for line in lines if re.match(r"^#{1,3}\s", line)),
        "fences": body.count("```"),
        "leaks": len(LEAK.findall(body)),
        "empty": not body.strip(),
    }


def score_model(model_dir, work, facts):
    rows = []
    for source_path in sorted(p for p in work.rglob("*") if p.is_file()):
        rel = source_path.relative_to(work).as_posix()
        out = model_dir / (rel + ".ai.md")
        source = source_path.read_text(encoding="utf-8")
        ids, numbers = expected_ids(rel, source, facts)
        if not out.exists():
            rows.append({"file": rel, "kind": kind_of(source_path), "missing": True,
                         "srcBytes": len(source.encode("utf-8"))})
            continue
        row = score_file(source, body_of(out.read_text(encoding="utf-8")), ids, numbers,
                         case_insensitive=kind_of(source_path) == "sql")
        row.update({"file": rel, "kind": kind_of(source_path), "missing": False})
        rows.append(row)
    for agg in sorted(model_dir.rglob("package.ai.md")) + sorted(model_dir.glob("project.ai.md")):
        body = body_of(agg.read_text(encoding="utf-8"))
        rel = agg.relative_to(model_dir).as_posix()
        kind = "project" if agg.name == "project.ai.md" else "package"
        row = score_file("", body, [], [])
        row.update({"file": rel, "kind": kind, "missing": False, "srcBytes": 0, "invented": 0, "ticked": 0})
        rows.append(row)
    return rows


def summarize(rows):
    by_kind = defaultdict(list)
    for row in rows:
        by_kind[row["kind"]].append(row)
    summary = {}
    for kind, items in by_kind.items():
        present = [r for r in items if not r["missing"]]
        # Compaction only over files that got a body; the empty ones are counted under "empty".
        answered = [r for r in present if not r["empty"]]
        src = sum(r["srcBytes"] for r in answered)
        body = sum(r["bodyBytes"] for r in answered)
        # Recall is the mean of the per-file recalls, so one file with 88 getters does not outweigh
        # eleven small ones; an empty body scores 0 there.
        per_file = [r["idsFound"] / r["idsExpected"] for r in present if r.get("idsExpected")]
        ticked = sum(r.get("ticked", 0) for r in present)
        summary[kind] = {
            "files": len(items),
            "missing": len(items) - len(present),
            "empty": sum(1 for r in present if r["empty"]),
            "srcBytes": src,
            "bodyBytes": body,
            "compaction": round(src / body, 2) if body and src else None,
            "recall": round(sum(per_file) / len(per_file), 3) if per_file else None,
            "inventedPer100": round(100 * sum(r.get("invented", 0) for r in present) / ticked, 1) if ticked else None,
            "numbers": f"{sum(r.get('numbersStated', 0) for r in present)}/{sum(r.get('numbersExpected', 0) for r in present)}",
            "noLead": sum(1 for r in answered if not r["lead"]),
            "badHeadings": sum(r["badHeadings"] for r in present),
            "fences": sum(r["fences"] for r in present),
            "leaks": sum(r["leaks"] for r in present),
        }
    return summary


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work", required=True)
    parser.add_argument("--out", default=str(HERE.parent / "outputs-2026-10"))
    parser.add_argument("--scores", default=str(HERE / "scores"))
    args = parser.parse_args()
    facts = json.loads((HERE / "facts.json").read_text(encoding="utf-8"))
    work = Path(args.work)
    scores = Path(args.scores)
    scores.mkdir(parents=True, exist_ok=True)
    columns = ["model", "file", "kind", "missing", "srcBytes", "bodyBytes", "idsExpected", "idsFound", "ticked",
               "invented", "numbersExpected", "numbersStated", "lead", "badHeadings", "fences", "leaks", "inventedNames"]
    lines = ["\t".join(columns)]
    summary = {}
    for model_dir in sorted(p for p in Path(args.out).iterdir() if p.is_dir()):
        rows = score_model(model_dir, work, facts)
        summary[model_dir.name] = summarize(rows)
        for row in rows:
            row["model"] = model_dir.name
            lines.append("\t".join(str(row.get(c, "")) if c != "inventedNames" else " ".join(row.get(c, []))
                                   for c in columns))
    (scores / "files.tsv").write_text("\n".join(lines) + "\n", encoding="utf-8")
    (scores / "summary.json").write_text(json.dumps(summary, indent=1) + "\n", encoding="utf-8")
    for model, kinds in summary.items():
        print(model, {k: (v["compaction"], v["recall"], v["inventedPer100"]) for k, v in sorted(kinds.items())})


if __name__ == "__main__":
    main()
