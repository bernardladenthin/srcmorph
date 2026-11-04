# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Scores the model-card outputs: compaction, fact recall, invented names and format per file kind.

For every model under <out> (one directory per model key) and every source file of the corpus:
- compaction = source bytes / body bytes (the .ai.md without its header), summed per kind over
  the files that got a body (a failed body is counted under "failed", and against recall);
- recall = per file the share of the expected identifiers (facts.json; for Java the public types and
  the public/protected methods taken from the source, annotated and multi-line signatures included,
  and an interface's methods) the body names as a whole word, averaged over the files of a kind.
  A code fence that copies the source (at least half of its lines are lines of the source) does
  not count: a body that pastes the source names every identifier without summarizing it. A fence
  around the model's own text (a signature list, a summary wrapped in ```markdown) counts; the
  format column holds it against the body. A name the facts list under "words" is an everyday
  word too (`author`, `status`, `main`): it counts only where the body marks it as code (backticks,
  a call `main(`, bold, qualified, an option, the label of a list item), because prose uses the same
  word for something else. The Java names read from the source have no such list, deliberately: a
  Java summary that writes "close releases the model" means the method `close`, and requiring code
  marks there cost more right names than it saved wrong ones. SQL names are matched without regard
  to case;
- invented = backticked identifiers outside a copied fence that occur nowhere in the source, per 100
  backticked identifiers (a proxy for hallucinated names, not a full fact check). A qualified name
  counts as its last part, and keywords, standard-library types, escapes and format placeholders
  (`\\t`, `YYYY-MM-DD`, `getX`) are no names;
- numbers = the exact counts of facts.json the body states (the seed-data row counts);
- format: missing lead line, '#' to '###' headings, code fences, leaked reasoning markers;
- failed = why a body is not a summary: missing, empty, leak-only, refusal, too-short (under 80
  bytes); a failed body scores 0 in recall. cutOff = the generation hit maxOutputTokens (from the
  run's --meta file); such a body may still be complete, so it is reported, not failed.
The facts and coverage lines srcmorph puts in front of a body are srcmorph's, not the model's: every
measure is taken on the body without them.

Writes <scores>/files.tsv (one row per model and file) and <scores>/summary.json (per model and kind),
which MODEL-CARD.md is built from.

Usage: python score.py --work <prepared corpus dir> [--out ...] [--scores ...] [--facts <json>]
       [--meta <runs dir>]
"""
import argparse
import json
import re
from collections import Counter, defaultdict
from pathlib import Path

HERE = Path(__file__).resolve().parent
KINDS = {".java": "java", ".sql": "sql"}
IDENT = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
# The lines srcmorph writes in front of a body (AiFactExtractor.FACTS_HEADER,
# AiMapReduceSupport.COVERAGE_HEADER), in this order.
MACHINE_LINES = ("**Facts (exact, whole file):** ", "**Coverage:** ")
# A fenced block (its opening line with the language, its content); an unclosed one runs to the end.
FENCED = re.compile(r"```[^\n]*\n?(.*?)(?:```|\Z)", re.S)
# A fence is a copy of the source when at least this share of its lines are lines of the source.
COPIED_SHARE = 0.5
BACKTICK = re.compile(r"`([^`\n]+)`")
QUALIFIED = re.compile(r"[A-Za-z_][\w$]*(?:\.[A-Za-z_][\w$]*)+")
# Escapes and the placeholders of a format or an accessor pattern, not names.
PLACEHOLDER = re.compile(r"\\|YYYY|\b[YMDHSTZ_]{4,}\b|\b(?:get|set|is|with)X(?:xx)?\b")
LEAK = re.compile(r"</?think>|<\|start\|>|<\|channel\|>|<\|message\|>|<\|end\|>")
REFUSAL = re.compile(r"(?i)^\s*(?:i['’]m sorry|i am sorry|i cannot|i can['’]t|as an ai\b)")
MIN_BODY_BYTES = 80
# Java comments, strings and char literals are blanked before the declarations are read.
JAVA_NOISE = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\\n])*"|\'(?:\\.|[^\'\\\n])+\'|/\*[\s\S]*?\*/|//[^\n]*')
ANNOTATION_ARGS = re.compile(r"(@[\w.]+)\s*\((?:[^()]|\([^()]*\))*\)")
GENERICS = r"<[^<>]*(?:<[^<>]*(?:<[^<>]*>[^<>]*)*>[^<>]*)*>"
ANNOTATIONS = r"(?:@[\w.]+\s+)*"
JAVA_TYPE = re.compile(ANNOTATIONS + r"public\s+(?:(?:static|final|abstract|sealed|non-sealed|strictfp)\s+)*"
                       r"(?:class|interface|enum|record|@interface)\s+(\w+)")
JAVA_METHOD = re.compile(ANNOTATIONS + r"(?:public|protected)\s+"
                         r"(?:(?:static|final|abstract|synchronized|default|native|strictfp|@[\w.]+)\s+)*"
                         r"(?:" + GENERICS + r"\s+)?[\w<>\[\],.?@ ]+?\s+(\w+)\s*\(")
# An interface's methods are public without the modifier.
INTERFACE_METHOD = re.compile(ANNOTATIONS + r"(?:(?:static|default|abstract|@[\w.]+)\s+)*"
                              r"(?:" + GENERICS + r"\s+)?[\w<>\[\],.?@ ]+?\s+(\w+)\s*\(")
JAVA_INTERFACE = re.compile(ANNOTATIONS + r"public\s+(?:sealed\s+|non-sealed\s+)?interface\s+\w+")
NOT_A_NAME = {"if", "for", "while", "switch", "return", "catch", "synchronized", "new", "throw", "else",
              "private", "this", "super"}
# Words a summary may backtick that are neither invented names nor in the source text: keywords,
# SQL keywords and types, and the standard-library types a summary of Java names.
GENERIC = set("""
null true false void int long short byte char float double boolean var this new return static final
public private protected class interface enum record abstract extends implements throws throw import
package super default switch case try catch finally volatile transient synchronized sealed permits
instanceof native strictfp assert break continue
String Object Integer Long Short Byte Character Float Double Boolean Number Void Math System Thread
Runnable Callable Iterable Iterator Comparable Comparator AutoCloseable Closeable Override Deprecated
FunctionalInterface SuppressWarnings Exception RuntimeException Error Throwable IOException
UncheckedIOException IllegalArgumentException IllegalStateException NullPointerException
UnsupportedOperationException IndexOutOfBoundsException InterruptedException List Map Set Collection
Optional Objects Arrays Collections HashMap LinkedHashMap TreeMap ArrayList LinkedList HashSet
LinkedHashSet TreeSet Deque ArrayDeque Queue ConcurrentHashMap Stream Collectors Function BiFunction
Supplier Consumer BiConsumer Predicate StringBuilder CharSequence Path Paths Files File Duration
Instant LocalDate LocalDateTime UUID Pattern Matcher Charset StandardCharsets BigDecimal BigInteger
CompletableFuture Future ExecutorService TimeUnit AtomicInteger AtomicLong AtomicBoolean
NULL TRUE FALSE INTEGER INT BIGINT SMALLINT DECIMAL NUMERIC VARCHAR CHAR TEXT DATE TIME TIMESTAMP
BOOLEAN SERIAL SELECT INSERT INTO VALUES UPDATE DELETE FROM WHERE JOIN LEFT RIGHT INNER OUTER GROUP
ORDER HAVING PRIMARY FOREIGN KEY REFERENCES UNIQUE CHECK INDEX VIEW TRIGGER FUNCTION PROCEDURE BEGIN
END CREATE ALTER DROP TABLE CONSTRAINT DEFAULT CASCADE COMMIT ROLLBACK
""".split())
TSV_COLUMNS = ["model", "file", "kind", "missing", "failed", "cutOff", "srcBytes", "bodyBytes", "idsExpected",
               "idsFound", "ticked", "invented", "numbersExpected", "numbersStated", "lead", "badHeadings",
               "fences", "leaks", "inventedNames"]


def body_of(text):
    """The .ai.md body: everything after the header's '---' line. A file written without that line
    holds only the header: the generation came back empty (e.g. all reasoning, no answer)."""
    parts = text.split("\n---\n", 1)
    return parts[1] if len(parts) == 2 else ""


def model_text(body):
    """The body without the facts and coverage lines srcmorph writes in front of the model's text."""
    for header in MACHINE_LINES:
        stripped = body.lstrip()
        if stripped.startswith(header):
            body = stripped.split("\n", 1)[1] if "\n" in stripped else ""
    return body


def kind_of(path):
    return KINDS.get(path.suffix, "other")


def java_names(source):
    """The public types and the public/protected methods of a Java source, and an interface's methods:
    the declarations between the braces and semicolons, with comments, strings and annotation
    arguments blanked, so an annotated or multi-line signature reads like any other."""
    text = ANNOTATION_ARGS.sub(r"\1", JAVA_NOISE.sub(lambda m: '""' if m.group(0)[0] in "\"'" else " ", source))
    names, depth, interface_depths = set(), 0, []
    for token in re.split(r"([;{}])", text):
        if token == "{":
            depth += 1
            continue
        if token == "}":
            depth -= 1
            while interface_depths and interface_depths[-1] > depth:
                interface_depths.pop()
            continue
        if token == ";":
            continue
        declaration = " ".join(token.split())
        for regex in (JAVA_TYPE, JAVA_METHOD):
            match = regex.match(declaration)
            if match:
                names.add(match.group(1))
                break
        else:
            if interface_depths and interface_depths[-1] == depth:
                match = INTERFACE_METHOD.match(declaration)
                if match:
                    names.add(match.group(1))
        if JAVA_INTERFACE.match(declaration):
            interface_depths.append(depth + 1)
    return sorted(n for n in names if n not in NOT_A_NAME)


def expected_ids(rel, source, facts):
    """The ids a summary of the file names, the numbers it states, and the ids that are everyday words."""
    if rel in facts:
        return facts[rel].get("ids", []), facts[rel].get("numbers", []), facts[rel].get("words", [])
    if rel.endswith(".java"):
        return java_names(source), [], []
    return [], [], []


def ticked_names(prose):
    """The identifiers of the backticked spans, a qualified name as its last part, without keywords,
    standard-library types, escapes and placeholders."""
    names = []
    for span in BACKTICK.findall(prose):
        if PLACEHOLDER.search(span):
            continue
        span = QUALIFIED.sub(lambda m: m.group(0).rsplit(".", 1)[1], span)
        names += [t for t in IDENT.findall(span) if len(t) >= 3 and t not in GENERIC]
    return names


def marked_as_code(word, prose, case_insensitive):
    """Whether the prose marks the word as code: in backticks, as a call, in bold, qualified
    (`loan.due_on`, `.partial`), as an option or a variable (`--currency`, `$status`), or as the label
    of a list item ("- author: ...")."""
    w = re.escape(word)
    pattern = (r"`[^`\n]*(?<![\w])" + w + r"(?![\w])[^`\n]*`|(?<![\w])" + w + r"\s*\(|\*\*" + w + r"\*\*"
               r"|(?<![\w])" + w + r"\.\w|(?:\.|--|\$\{?)" + w + r"(?![\w])"
               r"|^[ \t]*(?:[-*+]|\d+\.)[ \t]+(?:\*\*)?" + w + r"(?:\*\*)?[ \t]*[:(\u2013\u2014]")
    return re.search(pattern, prose, re.M | (re.I if case_insensitive else 0)) is not None


def names_found(ids, prose, case_insensitive, everyday=()):
    """The expected ids the prose names: an everyday word only where marked as code, another
    identifier as a whole word, a path- or colon-like id as a substring."""
    fold = str.lower if case_insensitive else (lambda s: s)
    words = {fold(w) for w in IDENT.findall(prose)}
    found = []
    for i in ids:
        if not IDENT.fullmatch(i):
            hit = fold(i) in fold(prose)
        elif i in everyday:
            hit = marked_as_code(i, prose, case_insensitive)
        else:
            hit = fold(i) in words
        if hit:
            found.append(i)
    return found


# Between two digit groups of a number: a comma, a space, a thin or narrow no-break space, an apostrophe.
THOUSANDS = "[,\u00a0\u2009\u202f' ]?"


def number_pattern(n):
    """A number as prose writes it: 20000, 20,000, 20 000; not inside a longer number or a decimal, but
    before a sentence's period."""
    digits = str(n)
    head = len(digits) % 3 or 3
    groups = [digits[:head]] + [digits[i:i + 3] for i in range(head, len(digits), 3)]
    return re.compile(r"(?<![\d.,])(?<!\d[ \u00a0\u2009\u202f'])" + THOUSANDS.join(groups)
                      + r"(?![\d])(?![.,][\d])(?![ \u00a0\u2009\u202f'][\d]{3}(?![\d]))")


def middle_found(middle, prose):
    """The middle names of a large-corpus file the prose names: a name, or the prefix of one
    (LEDGER_LOCK names the area of LEDGER_LOCK_02701), case as written."""
    words = IDENT.findall(prose)
    return [m for m in middle if any(w == m or w.startswith(m + "_") for w in words) or
            (not IDENT.fullmatch(m) and m in prose)]


def failure_of(missing, body, lead, leaks):
    """Why a body is no summary, or "" when it is one."""
    if missing:
        return "missing"
    if not body.strip():
        return "empty"
    if leaks and not lead:
        return "leak-only"
    if REFUSAL.match(body):
        return "refusal"
    if len(body.strip().encode("utf-8")) < MIN_BODY_BYTES:
        return "too-short"
    return ""


def without_copies(body, source):
    """The body with every fence that copies the source blanked, and the other fences' markers dropped."""
    source_lines = {line.strip() for line in source.splitlines() if line.strip()}

    def replace(match):
        lines = [line.strip() for line in match.group(1).splitlines() if line.strip()]
        copied = sum(1 for line in lines if line in source_lines)
        return " " if lines and copied >= COPIED_SHARE * len(lines) else "\n" + match.group(1) + "\n"

    return FENCED.sub(replace, body)


def score_file(source, body, ids, numbers, case_insensitive=False, everyday=()):
    body = model_text(body)
    prose = without_copies(body, source)
    found = names_found(ids, prose, case_insensitive, everyday)
    ticked = ticked_names(prose)
    # SQL names and keywords are case-insensitive: `integer` names the source's INTEGER.
    haystack = source.lower() if case_insensitive else source
    invented = [t for t in ticked if (t.lower() if case_insensitive else t) not in haystack]
    stated = [n for n in numbers if number_pattern(n).search(prose)]
    lines = body.strip().splitlines()
    lead = bool(lines) and lines[0].startswith(">")
    leaks = len(LEAK.findall(body))
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
        "lead": lead,
        "badHeadings": sum(1 for line in lines if re.match(r"^#{1,3}\s", line)),
        "fences": body.count("```"),
        "leaks": leaks,
        "empty": not body.strip(),
        "failed": failure_of(False, body, lead, leaks),
    }


def cut_off_of(meta_dir, model):
    """The paths a run's provider reported as cut off at maxOutputTokens (runs/<model>.json)."""
    if meta_dir is None:
        return set()
    path = Path(meta_dir) / f"{model}.json"
    if not path.exists():
        return set()
    phases = json.loads(path.read_text(encoding="utf-8")).get("phases", {})
    return {f for phase in phases.values() for f in phase.get("cutOff", [])}


def score_model(model_dir, work, facts, cut_off=frozenset()):
    rows = []
    for source_path in sorted(p for p in work.rglob("*") if p.is_file()):
        rel = source_path.relative_to(work).as_posix()
        out = model_dir / (rel + ".ai.md")
        source = source_path.read_text(encoding="utf-8")
        ids, numbers, everyday = expected_ids(rel, source, facts)
        if not out.exists():
            rows.append({"file": rel, "kind": kind_of(source_path), "missing": True, "failed": "missing",
                         "cutOff": False, "srcBytes": len(source.encode("utf-8")), "idsExpected": len(ids)})
            continue
        body = body_of(out.read_text(encoding="utf-8"))
        row = score_file(source, body, ids, numbers,
                         case_insensitive=kind_of(source_path) == "sql", everyday=everyday)
        secret = (facts.get(rel) or {}).get("secret")
        if secret:
            # a value the facts mark as a secret: the summary may name it, never copy it
            row["secretLeaked"] = secret in body
        middle = (facts.get(rel) or {}).get("middle", [])
        if middle:
            row["middleExpected"] = len(middle)
            row["middleFound"] = len(middle_found(middle, without_copies(model_text(body), source)))
        row.update({"file": rel, "kind": kind_of(source_path), "missing": False, "cutOff": rel in cut_off})
        rows.append(row)
    for agg in sorted(model_dir.rglob("package.ai.md")) + sorted(model_dir.glob("project.ai.md")):
        body = body_of(agg.read_text(encoding="utf-8"))
        rel = agg.relative_to(model_dir).as_posix()
        kind = "project" if agg.name == "project.ai.md" else "package"
        row = score_file("", body, [], [])
        row.update({"file": rel, "kind": kind, "missing": False, "cutOff": rel in cut_off, "srcBytes": 0,
                    "invented": 0, "ticked": 0, "inventedNames": []})
        rows.append(row)
    return rows


def summarize(rows):
    by_kind = defaultdict(list)
    for row in rows:
        by_kind[row["kind"]].append(row)
    summary = {}
    for kind, items in by_kind.items():
        present = [r for r in items if not r["missing"]]
        # Compaction and the format counts only over files with a summary; the others are "failed".
        answered = [r for r in present if not r["failed"]]
        src = sum(r["srcBytes"] for r in answered)
        body = sum(r["bodyBytes"] for r in answered)
        # Recall is the mean of the per-file recalls, so one file with 88 getters does not outweigh
        # eleven small ones; a failed body scores 0 there.
        per_file = [(0 if r["failed"] else r["idsFound"] / r["idsExpected"])
                    for r in items if r.get("idsExpected")]
        ticked = sum(r.get("ticked", 0) for r in answered)
        summary[kind] = {
            "files": len(items),
            "missing": len(items) - len(present),
            "failed": dict(sorted(Counter(r["failed"] for r in items if r["failed"]).items())),
            "cutOff": sum(1 for r in items if r.get("cutOff")),
            "srcBytes": src,
            "bodyBytes": body,
            "compaction": round(src / body, 2) if body and src else None,
            "recall": round(sum(per_file) / len(per_file), 3) if per_file else None,
            "inventedPer100": round(100 * sum(r.get("invented", 0) for r in answered) / ticked, 1) if ticked else None,
            "numbers": f"{sum(r.get('numbersStated', 0) for r in answered)}/{sum(r.get('numbersExpected', 0) for r in present)}",
            "noLead": sum(1 for r in answered if not r["lead"]),
            "badHeadings": sum(r["badHeadings"] for r in answered),
            "fences": sum(r["fences"] for r in answered),
            "leaks": sum(r["leaks"] for r in present),
            "secretsLeaked": sum(1 for r in present if r.get("secretLeaked")),
        }
    return summary


def tsv_line(row):
    return "\t".join(" ".join(row.get(c, [])) if c == "inventedNames" else str(row.get(c, "")) for c in TSV_COLUMNS)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work", required=True)
    parser.add_argument("--out", default=str(HERE.parent / "outputs-2026-10"))
    parser.add_argument("--scores", default=str(HERE / "scores"))
    parser.add_argument("--facts", default=str(HERE / "facts.json"))
    parser.add_argument("--meta", default=str(HERE / "runs"), help="the runs' meta files, for the cut-off paths")
    args = parser.parse_args()
    facts = json.loads(Path(args.facts).read_text(encoding="utf-8"))
    work = Path(args.work)
    scores = Path(args.scores)
    scores.mkdir(parents=True, exist_ok=True)
    lines = ["\t".join(TSV_COLUMNS)]
    summary = {}
    for model_dir in sorted(p for p in Path(args.out).iterdir() if p.is_dir()):
        rows = score_model(model_dir, work, facts, cut_off_of(args.meta, model_dir.name))
        summary[model_dir.name] = summarize(rows)
        lines += [tsv_line({**row, "model": model_dir.name}) for row in rows]
    (scores / "files.tsv").write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    (scores / "summary.json").write_text(json.dumps(summary, indent=1) + "\n", encoding="utf-8", newline="\n")
    for model, kinds in summary.items():
        print(model, {k: (v["compaction"], v["recall"], v["inventedPer100"]) for k, v in sorted(kinds.items())})


if __name__ == "__main__":
    main()
