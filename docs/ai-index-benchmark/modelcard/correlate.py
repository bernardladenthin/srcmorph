# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Does a longer index search worse? The correlation the model card's Search section quotes.

Reads scores/summary.json (the bytes of every run's file summaries: Java, SQL and other files, the
package and project summaries left out) and scores/search.json (the reader's hit@1 and MRR) and prints
Pearson's r and Spearman's rank correlation of the two over the scored runs. By default the three
runs the card names as empty or broken are left out (Grok 4.7's reasoning before every lead, Granite
4.0 H-Tiny's fenced and headed summaries, Qwen3.5-35B-A3B's five empty Java summaries), as the card's
"over the 32 scored runs ..." does; --exclude "" takes all of them. Over the 32 runs it gives +0.13 with
hit@1 and +0.15 with the MRR since the reader run of 2026-11-07, which reads the answer without the
facts and coverage lines (the first one gave -0.18 and -0.08).

Writes nothing.

Usage: python correlate.py [--scores <dir>] [--exclude <run>[,<run>...]]
"""
import argparse
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent

# the runs the card leaves out of the correlation, by their key in scores/
CARD_EXCLUDED = ("grok-4.7", "granite-4.0-h-tiny", "qwen3.5-35b-a3b")

# the file kinds whose summaries make the index the reader searches
FILE_KINDS = ("java", "sql", "other")


def pearson(xs, ys):
    """Pearson's r of two equally long series; None when they are empty or one of them does not vary."""
    n = len(xs)
    if n == 0:
        return None
    mean_x = sum(xs) / n
    mean_y = sum(ys) / n
    sxy = sum((x - mean_x) * (y - mean_y) for x, y in zip(xs, ys))
    sx = sum((x - mean_x) ** 2 for x in xs) ** 0.5
    sy = sum((y - mean_y) ** 2 for y in ys) ** 0.5
    if sx == 0 or sy == 0:
        return None
    return sxy / (sx * sy)


def ranks(values):
    """The rank of every value from 1, ties sharing the mean of their ranks."""
    order = sorted(range(len(values)), key=lambda i: values[i])
    result = [0.0] * len(values)
    i = 0
    while i < len(order):
        j = i
        while j + 1 < len(order) and values[order[j + 1]] == values[order[i]]:
            j += 1
        for k in range(i, j + 1):
            result[order[k]] = (i + j) / 2 + 1
        i = j + 1
    return result


def spearman(xs, ys):
    """Spearman's rank correlation: Pearson's r of the ranks."""
    return pearson(ranks(xs), ranks(ys))


def rows(summary, search, excluded):
    """(run, index bytes, reader hit@1, reader MRR) of every run both files score, but the excluded ones."""
    result = []
    for run in sorted(summary):
        if run in excluded or run not in search:
            continue
        index_bytes = sum(summary[run][kind]["bodyBytes"] for kind in FILE_KINDS if kind in summary[run])
        reader = search[run]["reader"]
        result.append((run, index_bytes, reader["hit1"], reader["mrr"]))
    return result


def correlations(table):
    """Pearson and Spearman of the index bytes against hit@1 and against the MRR."""
    size = [r[1] for r in table]
    hit1 = [r[2] for r in table]
    mrr = [r[3] for r in table]
    return {
        "runs": len(table),
        "pearsonHit1": pearson(size, hit1),
        "pearsonMrr": pearson(size, mrr),
        "spearmanHit1": spearman(size, hit1),
        "spearmanMrr": spearman(size, mrr),
    }


def fmt_r(value):
    """A correlation with two decimals, or n/a where there is none (no run, or a series that does not vary)."""
    return "n/a" if value is None else f"{value:.2f}"


def result_line(result):
    """The line main prints under the table."""
    return (f"{result['runs']} runs: Pearson r {fmt_r(result['pearsonHit1'])} with hit@1, "
            f"{fmt_r(result['pearsonMrr'])} with the MRR; Spearman {fmt_r(result['spearmanHit1'])} and "
            f"{fmt_r(result['spearmanMrr'])}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--scores", type=Path, default=HERE / "scores",
                        help="the directory with summary.json and search.json")
    parser.add_argument("--exclude", default=",".join(CARD_EXCLUDED),
                        help="runs to leave out, comma-separated (default: the card's three)")
    args = parser.parse_args()
    summary = json.loads((args.scores / "summary.json").read_text(encoding="utf-8"))
    search = json.loads((args.scores / "search.json").read_text(encoding="utf-8"))["runs"]
    excluded = {name.strip() for name in args.exclude.split(",") if name.strip()}
    table = rows(summary, search, excluded)
    for run, index_bytes, hit1, mrr in table:
        print(f"{run:40} {index_bytes:8d} bytes  hit@1 {hit1:.3f}  MRR {mrr:.3f}")
    print("\n" + result_line(correlations(table)))


if __name__ == "__main__":
    main()
