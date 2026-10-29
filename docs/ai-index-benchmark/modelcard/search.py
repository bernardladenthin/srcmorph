# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Measures whether a model's .ai.md summaries lead to the right file: the search use of the index.

Every question of search-questions.json names the corpus files that answer it. For each model's
output tree (one directory per model key under <out>) the 24 file summaries are searched two ways:
- bm25: BM25 over the summary bodies (the header, which names the file, is left out), with
  camelCase and snake_case identifiers split into words; deterministic and free;
- reader: one LLM call per question that sees every summary under its path, as an agent browsing
  the index would, and names the three files most likely to answer (OpenRouter, the key from
  OPENROUTER_API_KEY; answers are cached, so a rerun only pays for what is new).
Two baselines go through the same retrievers: "source" (the source files themselves) and "paths"
(nothing but the paths). hit@1 / hit@3 count a question as found when an answer file is ranked
first / among the first three; MRR is the mean reciprocal rank of the first answer file (0 when
the reader does not name one).

Writes <scores>/search.json (per run and retriever) and <scores>/search.tsv (per question).

Usage: python search.py --work <prepared corpus dir> [--reader <model>] [--no-reader]
       [--cache <jsonl>] [--out ...] [--scores ...]
"""
import argparse
import hashlib
import json
import math
import os
import re
import threading
import urllib.request
from collections import Counter
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

from score import body_of

HERE = Path(__file__).resolve().parent
READER = "mistralai/mistral-small-3.2-24b-instruct"
BASELINES = ("source", "paths")
STOP = set("""a an and are as at be by can do does for from has have how in into is it its of on or
that the their there this to was what when where which while who why with without""".split())
SYSTEM = ("You find files in a code base. You get one summary per file, each under its path, and a "
          "developer's question. Answer with the paths of the three files most likely to answer the "
          "question, best first, as JSON: {\"files\": [\"path1\", \"path2\", \"path3\"]}. Use only "
          "paths from the list. No other text.")


def words(text):
    """Lower-case words; identifiers count as themselves and as their camelCase/snake_case parts."""
    out = []
    for token in re.findall(r"[A-Za-z][A-Za-z0-9_]*|\d+", text):
        parts = [p for p in re.split(r"_|(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", token) if p]
        for word in {token.lower(), *(p.lower() for p in parts)}:
            if word not in STOP and len(word) > 1:
                out.append(word)
    return out


def bm25_rank(docs, query, k1=1.5, b=0.75):
    """The doc keys by BM25 score, best first; ties keep the doc order."""
    tokenized = {key: words(text) for key, text in docs.items()}
    avg = sum(len(t) for t in tokenized.values()) / len(tokenized) or 1
    df = Counter(w for t in tokenized.values() for w in set(t))
    n = len(tokenized)
    scores = {}
    for key, tokens in tokenized.items():
        tf = Counter(tokens)
        score = 0.0
        for w in set(words(query)):
            if tf[w]:
                idf = math.log(1 + (n - df[w] + 0.5) / (df[w] + 0.5))
                score += idf * tf[w] * (k1 + 1) / (tf[w] + k1 * (1 - b + b * len(tokens) / avg))
        scores[key] = score
    return sorted(scores, key=lambda k: -scores[k])


def documents(run, out, work, files):
    """Path -> searchable text of one run (or baseline)."""
    if run == "source":
        return {f: (work / f).read_text(encoding="utf-8") for f in files}
    if run == "paths":
        return {f: "" for f in files}
    docs = {}
    for f in files:
        md = out / run / (f + ".ai.md")
        docs[f] = body_of(md.read_text(encoding="utf-8")).strip() if md.exists() else ""
    return docs


class Reader:
    """The LLM retriever with a JSONL answer cache keyed by model, context and question."""

    def __init__(self, model, cache_path):
        self.model = model
        self.cache_path = Path(cache_path)
        self.lock = threading.Lock()
        self.cache = {}
        self.cost = 0.0
        if self.cache_path.exists():
            for line in self.cache_path.read_text(encoding="utf-8").splitlines():
                entry = json.loads(line)
                self.cache[entry["id"]] = entry

    def ask(self, context, question):
        key = hashlib.sha256((self.model + "\0" + context + "\0" + question).encode("utf-8")).hexdigest()
        if key in self.cache:
            return self.cache[key]["answer"]
        body = {"model": self.model, "temperature": 0, "seed": 1, "max_tokens": 300,
                "usage": {"include": True},
                "messages": [{"role": "system", "content": SYSTEM},
                             {"role": "user", "content": context + "\n\nQuestion: " + question}]}
        request = urllib.request.Request("https://openrouter.ai/api/v1/chat/completions",
                                         data=json.dumps(body).encode("utf-8"),
                                         headers={"Authorization": "Bearer " + os.environ["OPENROUTER_API_KEY"],
                                                  "Content-Type": "application/json"})
        for attempt in range(4):
            try:
                with urllib.request.urlopen(request, timeout=180) as response:
                    data = json.load(response)
                break
            except OSError:
                if attempt == 3:
                    raise
        answer = data["choices"][0]["message"].get("content") or ""
        with self.lock:
            self.cost += data.get("usage", {}).get("cost", 0.0)
            entry = {"id": key, "answer": answer}
            self.cache[key] = entry
            with open(self.cache_path, "a", encoding="utf-8") as f:
                f.write(json.dumps(entry) + "\n")
        return answer


def reader_rank(answer, files):
    """The listed paths the reader named, in its order (paths it made up are dropped)."""
    try:
        named = json.loads(re.search(r"\{.*\}", answer, re.S).group(0))["files"]
    except (AttributeError, ValueError, KeyError, TypeError):
        named = re.findall(r"[\w./-]+\.\w+", answer)
    ranked = []
    for path in named:
        path = str(path).strip().lstrip("./")
        if path in files and path not in ranked:
            ranked.append(path)
    return ranked


def context_of(docs, run):
    if run == "paths":
        return "Files:\n" + "\n".join(docs)
    return "\n\n".join(f"## FILE: {path}\n{text}" for path, text in docs.items())


def rank_of(ranked, answers):
    for i, path in enumerate(ranked, 1):
        if path in answers:
            return i
    return 0


def metrics(ranks):
    n = len(ranks)
    return {"hit1": round(sum(1 for r in ranks if r == 1) / n, 3),
            "hit3": round(sum(1 for r in ranks if 1 <= r <= 3) / n, 3),
            "mrr": round(sum(1 / r for r in ranks if r) / n, 3)}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work", required=True)
    parser.add_argument("--out", default=str(HERE.parent / "outputs-2026-10"))
    parser.add_argument("--scores", default=str(HERE / "scores"))
    parser.add_argument("--reader", default=READER)
    parser.add_argument("--no-reader", action="store_true")
    parser.add_argument("--cache", default=str(HERE / "search-cache.jsonl"))
    parser.add_argument("--threads", type=int, default=6)
    args = parser.parse_args()

    work, out = Path(args.work), Path(args.out)
    questions = json.loads((HERE / "search-questions.json").read_text(encoding="utf-8"))["questions"]
    files = sorted(p.relative_to(work).as_posix() for p in work.rglob("*") if p.is_file())
    for q in questions:
        missing = [a for a in q["answer"] if a not in files]
        assert not missing, f"{q['id']}: {missing} not in the corpus"
    runs = sorted(p.name for p in out.iterdir() if p.is_dir()) + list(BASELINES)
    reader = None if args.no_reader else Reader(args.reader, args.cache)

    results, rows = {}, ["run\tretriever\tquestion\trank\tranked"]
    for run in runs:
        docs = documents(run, out, work, files)
        retrievers = {}
        if run != "paths":
            retrievers["bm25"] = [bm25_rank(docs, q["q"]) for q in questions]
        if reader:
            context = context_of(docs, run)
            with ThreadPoolExecutor(args.threads) as pool:
                answers = list(pool.map(lambda q: reader.ask(context, q["q"]), questions))
            retrievers["reader"] = [reader_rank(a, files) for a in answers]
        results[run] = {}
        for name, rankings in retrievers.items():
            ranks = [rank_of(r, q["answer"]) for r, q in zip(rankings, questions)]
            results[run][name] = metrics(ranks)
            rows += [f"{run}\t{name}\t{q['id']}\t{rank}\t{' '.join(r[:3])}"
                     for q, rank, r in zip(questions, ranks, rankings)]
        print(run, results[run], flush=True)

    scores = Path(args.scores)
    scores.mkdir(parents=True, exist_ok=True)
    meta = {"questions": len(questions), "files": len(files), "reader": None if args.no_reader else args.reader}
    (scores / "search.json").write_text(json.dumps({"meta": meta, "runs": results}, indent=1) + "\n",
                                        encoding="utf-8")
    (scores / "search.tsv").write_text("\n".join(rows) + "\n", encoding="utf-8")
    if reader:
        print(f"reader cost this run: {reader.cost:.4f} USD")


if __name__ == "__main__":
    main()
