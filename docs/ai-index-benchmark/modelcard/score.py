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
  A file known to expect no name (a Java file without a public one, such as the package-private
  OpenAiCompatibleProviderSupport, or a facts entry without ids) is not measured when it got a
  summary, but scores 0 when it failed or is missing, so a failed body costs recall wherever it
  is; a file the facts do not list and that is no Java file is not measured at all. A name the facts
  list under "middle" may name an area (`LEDGER_LOCK`): it counts as the prefix of a name too
  (`LEDGER_LOCK_02701`), in recall and in the middle check alike.
  A code block that copies the source (at least half of its lines that hold an identifier are lines
  of the source, whatever the whitespace in them; a line of braces alone counts on neither side)
  does not count: a body that pastes the source names every identifier
  without summarizing it. A block is a ``` or ~~~ fence or a run of lines indented by four spaces or
  a tab after a blank line. A fence around the model's own text (a signature list, a summary wrapped
  in ```markdown) counts; the format column holds it against the body. A name the facts list under "words" is an everyday
  word too (`author`, `status`, `main`): it counts only where the body marks it as code (a backtick
  span, a call `main(`, bold, qualified, an option, the label of a list item, a column with its type
  `email VARCHAR(254)`, `status: integer`, `stock int`; a type that is an English word too, `date`,
  `number`, `text`, in small letters only after a colon or a parenthesis, since "the loan date" is
  prose), because prose uses the same
  word for something else. The Java names read from the source have no such list, deliberately: a
  Java summary that writes "close releases the model" means the method `close`, and requiring code
  marks there cost more right names than it saved wrong ones. SQL names are matched without regard
  to case;
- invented = the names outside a copied block that are no identifier of the source, per 100 names (a
  proxy for hallucinated names, not a full fact check). A name is an identifier in a backtick span,
  or a word of the prose shaped like code: camelCase, PascalCase with a second capital, or with an
  underscore (`orderTotal`, `OrderService`, `order_total`). A name of the source is one of its
  identifiers or a run of their word parts, split at an underscore or a capital (`free_space` and
  `used` of `ringbuf_free_space` and `ringbuf_used`, `get` of `getName`, the prefix `DEFAULT_`), or
  their plural; a piece inside a word part (`nam` of `getName`) is none (for SQL the name is matched
  without regard to case, the word-part boundaries are not). A qualified name counts as
  its last part, and keywords, standard-library types (for SQL in any case: `varchar` is VARCHAR),
  the names of languages, tools and products
  (`JavaBean`, `PostgreSQL`), file names (`pom.xml`, `docs/srcmorph.yaml`), escapes and format
  placeholders (`\\t`, `YYYY-MM-DD`, `getX`) are no names;
- numbers = the exact counts of facts.json the body states (the seed-data row counts); a missing
  summary expects its numbers too;
- format: missing lead line, '#' to '###' headings outside code fences, code fence markers (``` and ~~~), leaked
  reasoning markers (`<think>`, gpt-oss's harmony and Gemma's channel markers; one in
  backticks or right after a quote is the summary writing about it, no leak);
- failed = why a body is not a summary: missing, empty, leak-only (reasoning and no lead; a complete
  summary after a reasoning block, `<think>`, harmony's analysis channel or Gemma's thought channel,
  is a leak, not a failure), refusal, too-short (under 80 bytes); a failed body scores 0 in recall.
  The package and project summaries a run writes are expected for every directory that holds an
  indexed source (and the root), so a run whose package or project phase failed has missing rows
  there too. cutOff = the generation hit maxOutputTokens (from the
  run's --meta file, for a summary written and for one missing alike); such a body may still be
  complete, so it is reported, not failed.
A file is a source of the corpus as run.py configures srcmorph: by its extension or by a name glob,
both in any case (run.indexes).
The facts and coverage lines srcmorph puts in front of a body are srcmorph's, not the model's: every
measure is taken on the body without them.

Writes <scores>/files.tsv (one row per model and file) and <scores>/summary.json (per model and kind),
which MODEL-CARD.md is built from.

Usage: python score.py --work <prepared corpus dir> [--out ...] [--scores ...] [--facts <json>]
       [--meta <runs dir>]
"""
import argparse
import codecs
import json
import re
from collections import Counter, defaultdict
from pathlib import Path

import run  # run.py's constants (the extensions and file names it indexes); run.py does not import this module

HERE = Path(__file__).resolve().parent
KINDS = {".java": "java", ".sql": "sql"}
IDENT = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
# The lines srcmorph writes in front of a body (AiFactExtractor.FACTS_HEADER,
# AiMapReduceSupport.COVERAGE_HEADER), in this order.
MACHINE_LINES = ("**Facts (exact, whole file):** ", "**Coverage:** ")
# A fenced block (its opening line with the language, its content), opened and closed by the same run of
# three or more backticks or tildes; an unclosed one runs to the end.
FENCED = re.compile(r"(?P<marker>`{3,}|~{3,})[^\n]*\n?(?P<code>.*?)(?:(?P=marker)|\Z)", re.S)
# An indented code block: lines indented by four spaces or a tab, after a blank line or at the start.
INDENTED = re.compile(r"(?:\A|(?<=\n\n))(?P<code>(?:(?: {4}|\t)[^\n]*(?:\n|\Z))+)")
# A fence marker the format column counts.
FENCE_MARKER = re.compile(r"`{3,}|~{3,}")
# A word of the prose shaped like code: camelCase, PascalCase with a second capital, or snake_case.
CODE_SHAPED = re.compile(r"(?<![\w`.$])(?:[a-z][a-z0-9]*[A-Z][A-Za-z0-9]*|[A-Z][a-z0-9]+[A-Z][A-Za-z0-9]*"
                         r"|[A-Za-z][A-Za-z0-9]*_[A-Za-z0-9_]*[A-Za-z0-9])(?![\w`$])")
# A fence is a copy of the source when at least this share of its lines are lines of the source.
COPIED_SHARE = 0.5
BACKTICK = re.compile(r"`([^`\n]+)`")
QUALIFIED = re.compile(r"[A-Za-z_][\w$]*(?:\.[A-Za-z_][\w$]*)+")
# Escapes and the placeholders of a format or an accessor pattern, not names.
PLACEHOLDER = re.compile(r"\\|YYYY|\b[YMDHSTZ_]{4,}\b|\b(?:get|set|is|with)X(?:xx)?\b")
# A file name, with or without its directory (`pom.xml`, `docs/srcmorph.yaml`): no identifier.
FILE_EXTENSIONS = sorted({e.lstrip(".") for e in run.EXTENSIONS} | {
    "md", "json", "txt", "csv", "tsv", "jar", "war", "ear", "html", "htm", "js", "css", "toml", "ini", "cfg",
    "conf", "bat", "cmd", "ps1", "gguf", "zip", "gz", "tar", "png", "svg", "pdf"}, key=lambda e: (-len(e), e))
FILE_NAME = re.compile(r"(?<![\w.$/-])(?:[\w.-]+/)*[\w-]+(?:\.[\w-]+)*\.(?i:" + "|".join(FILE_EXTENSIONS)
                       + r")(?![\w.$/-])")
# The reasoning markers: <think>, gpt-oss's harmony channels and Gemma 4's thought channel
# (srcmorph's CompletionParser strips the last one). A marker right after a quote is the summary quoting
# it ("<|channel>thought", the constant of a parser), as one in backticks is.
LEAK = re.compile(r"(?<![\"'“‘])(?:</?think>|<\|start\|>|<\|channel\|>|<\|message\|>|<\|end\|>"
                  r"|<\|channel>|<channel\|>)")
# A reasoning block that opens the body (with harmony's header of the final channel after it), and an
# inline code span.
REASONING_BLOCK = re.compile(r"^\s*(?:<think>.*?</think>|<\|channel>thought.*?<channel\|>"
                             r"|(?:<\|start\|>\w*)?<\|channel\|>analysis<\|message\|>.*?<\|end\|>"
                             r"(?:\s*(?:<\|start\|>\w*)?<\|channel\|>final<\|message\|>)?)", re.S)
INLINE_CODE = re.compile(r"`[^`\n]*`")
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
Dict Tuple Any Callable Sequence Mapping DictReader DictWriter FileNotFoundError ValueError KeyError TypeError
OSError SystemExit
JavaBean JavaBeans JavaScript TypeScript JavaDoc Javadoc GitHub GitLab PostgreSQL MySQL SQLite MariaDB
MongoDB NoSQL GraphQL OAuth OpenAI OpenRouter OpenJDK GraalVM HotSpot IntelliJ NetBeans PowerShell
DevOps JUnit TestNG SpotBugs ArchUnit NullAway CMake WebSocket WebSockets YouTube LaTeX KaTeX macOS iOS
NULL TRUE FALSE INTEGER INT BIGINT SMALLINT DECIMAL NUMERIC VARCHAR CHAR TEXT DATE TIME TIMESTAMP
BOOLEAN SERIAL SELECT INSERT INTO VALUES UPDATE DELETE FROM WHERE JOIN LEFT RIGHT INNER OUTER GROUP
ORDER HAVING PRIMARY FOREIGN KEY REFERENCES UNIQUE CHECK INDEX VIEW TRIGGER FUNCTION PROCEDURE BEGIN
END CREATE ALTER DROP TABLE CONSTRAINT DEFAULT CASCADE COMMIT ROLLBACK
""".split())
TSV_COLUMNS = ["model", "file", "kind", "missing", "failed", "cutOff", "srcBytes", "bodyBytes", "idsExpected",
               "idsFound", "recall", "ticked", "invented", "numbersExpected", "numbersStated", "lead", "badHeadings",
               "fences", "leaks", "inventedNames"]
# The summaries srcmorph's package and project phases write below a model's output tree.
PACKAGE_FILE, PROJECT_FILE = "package.ai.md", "project.ai.md"


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
    """The kind of a source by its extension, in any case (srcmorph's SourcePaths.hasExtension)."""
    return KINDS.get(path.suffix.lower(), "other")


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
    """The ids a summary of the file names, the numbers it states, the ids that are everyday words, and
    whether the ids are known (a facts entry or a Java file; for any other file nobody listed them)."""
    if rel in facts:
        return facts[rel].get("ids", []), facts[rel].get("numbers", []), facts[rel].get("words", []), True
    if rel.lower().endswith(".java"):
        return java_names(source), [], [], True
    return [], [], [], False


def file_recall(failed, found, expected, known=True):
    """A file's recall: the share of its expected ids found, 0 for a failed (or missing) summary, also of a
    file known to expect none (a Java file without a public name), so a failure always costs recall;
    None (not measured) for the summary of a file that expects none and for a file nobody listed ids for."""
    if not expected:
        return 0.0 if failed and known else None
    return 0.0 if failed else found / expected


# Where a run of word parts of an identifier starts and ends: at the identifier's ends, at an underscore,
# or where a capital follows a small letter or a digit.
PART_START = r"(?:(?<![A-Za-z0-9_])|(?<=_)|(?<=[a-z0-9])(?=[A-Z]))"
PART_END = r"(?:(?![A-Za-z0-9_])|(?=_)|(?<=_)|(?<=[a-z0-9])(?=[A-Z]))"


def names_the_source(name, identifiers, case_insensitive=False):
    """Whether a name is one of the identifiers (joined by spaces), a run of their word parts, or the
    plural of either. With case_insensitive (SQL) the name is matched in any case, but the word-part
    boundaries stay case-sensitive: under a global re.I a capital after a small letter would be a
    boundary between any two letters, and `tome` would name `customer_order`."""
    for candidate in (name, name[:-1]) if len(name) > 3 and name.endswith("s") else (name,):
        text = "(?i:" + re.escape(candidate) + ")" if case_insensitive else re.escape(candidate)
        if re.search(PART_START + text + PART_END, identifiers):
            return True
    return False


def generic(name, case_insensitive=False):
    """Whether a name is a keyword, a standard type or a product (GENERIC); for SQL in any case."""
    return name in GENERIC or (case_insensitive and name.upper() in GENERIC)


def ticked_names(prose, case_insensitive=False):
    """The names of the prose: the identifiers of the backticked spans, a qualified name as its last part,
    and the code-shaped words outside them, without keywords, standard-library types, file names, escapes
    and placeholders."""
    names = []
    for span in BACKTICK.findall(prose):
        if PLACEHOLDER.search(span):
            continue
        span = QUALIFIED.sub(lambda m: m.group(0).rsplit(".", 1)[1], FILE_NAME.sub(" ", span))
        names += [t for t in IDENT.findall(span) if len(t) >= 3 and not generic(t, case_insensitive)]
    unticked = QUALIFIED.sub(lambda m: " " + m.group(0).rsplit(".", 1)[1], INLINE_CODE.sub(" ", prose))
    names += [t for t in CODE_SHAPED.findall(unticked)
              if not generic(t, case_insensitive) and not PLACEHOLDER.search(t)]
    return names


# The column types after which an everyday word is a column or a field ("email VARCHAR(254)"): after a
# colon or a parenthesis in any case ("status: integer"), after a space in capitals, or in small letters
# when the type is no English word too ("stock int not null"); "the loan date", "the book number" and
# "the author text" are prose.
ENGLISH_COLUMN_TYPES = r"CHAR|TEXT|NUMBER|REAL|FLOAT|DOUBLE|DATE|TIME|SERIAL|STRING"
SQL_COLUMN_TYPES = (r"VARCHAR2?|NVARCHAR|CLOB|BLOB|INT(?:EGER)?|BIGINT|SMALLINT|TINYINT|DECIMAL|NUMERIC"
                    r"|BOOL(?:EAN)?|DATETIME|TIMESTAMPTZ?|BIGSERIAL|UUID|JSONB?|BYTEA")
COLUMN_TYPE_NAMES = SQL_COLUMN_TYPES + "|" + ENGLISH_COLUMN_TYPES
COLUMN_TYPES = (r"(?:[ \t]*[:(][ \t]*(?i:" + COLUMN_TYPE_NAMES + r")|[ \t]+(?-i:" + COLUMN_TYPE_NAMES + r")"
                r"|[ \t]+(?i:" + SQL_COLUMN_TYPES + r"))")


def marked_as_code(word, prose, case_insensitive):
    """Whether the prose marks the word as code: in a backtick span (one span, not the prose between two),
    as a call, in bold, qualified (`loan.due_on`, `.partial`), as an option or a variable
    (`--currency`, `$status`), as the label of a list item ("- author: ...") or as a column or field
    with its type ("email VARCHAR(254)", "status: integer")."""
    w = re.escape(word)
    flags = re.M | (re.I if case_insensitive else 0)
    whole = r"(?<![\w])" + w + r"(?![\w])"
    if any(re.search(whole, span, flags) for span in BACKTICK.findall(prose)):
        return True
    pattern = (r"(?<![\w])" + w + r"\s*\(|\*\*" + w + r"\*\*"
               r"|(?<![\w])" + w + r"\.\w|(?:\.|--|\$\{?)" + w + r"(?![\w])"
               r"|^[ \t]*(?:[-*+]|\d+\.)[ \t]+(?:\*\*)?" + w + r"(?:\*\*)?[ \t]*[:(\u2013\u2014]"
               r"|" + whole + COLUMN_TYPES + r"\b")
    return re.search(pattern, prose, flags) is not None


def names_found(ids, prose, case_insensitive, everyday=(), prefixes=()):
    """The expected ids the prose names: an everyday word only where marked as code, another
    identifier as a whole word, one of prefixes also as the prefix of a name (LEDGER_LOCK names the
    area of LEDGER_LOCK_02701 and LEDGER_LOCK_*), a path- or colon-like id as a substring."""
    fold = str.lower if case_insensitive else (lambda s: s)
    words = {fold(w) for w in IDENT.findall(prose)}
    found = []
    for i in ids:
        if not IDENT.fullmatch(i):
            hit = fold(i) in fold(prose)
        elif i in everyday:
            hit = marked_as_code(i, prose, case_insensitive)
        else:
            hit = fold(i) in words or (i in prefixes and any(w.startswith(fold(i) + "_") for w in words))
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


def middle_found(middle, prose, case_insensitive=False, everyday=()):
    """The middle names of a large-corpus file the prose names: a name, or the prefix of one
    (LEDGER_LOCK names the area of LEDGER_LOCK_02701), by the rule recall reads them with
    (names_found), so the two never disagree."""
    return names_found(middle, prose, case_insensitive, everyday, prefixes=middle)


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
    """The body with every code block that copies the source blanked, and the other fences' markers
    dropped. A line of a block is a line of the source whatever the whitespace in it: a copy indented
    anew or with its spaces changed is still a copy. Only the lines that hold an identifier count, in
    the block and in the share: a `}` or `});` is in every source, and a skeleton of the model's own
    (a class line, invented signatures, its closing brace) is no copy."""
    source_lines = {"".join(line.split()) for line in source.splitlines() if IDENT.search(line)}

    def copies(code):
        lines = ["".join(line.split()) for line in code.splitlines() if IDENT.search(line)]
        return bool(lines) and sum(1 for line in lines if line in source_lines) >= COPIED_SHARE * len(lines)

    def fence(match):
        return " " if copies(match.group("code")) else "\n" + match.group("code") + "\n"

    def indented(match):
        return " \n" if copies(match.group("code")) else match.group("code")

    return INDENTED.sub(indented, FENCED.sub(fence, body))


def score_file(source, body, ids, numbers, case_insensitive=False, everyday=(), prefixes=()):
    body = model_text(body)
    prose = without_copies(body, source)
    found = names_found(ids, prose, case_insensitive, everyday, prefixes)
    ticked = ticked_names(prose, case_insensitive)
    # SQL names and keywords are case-insensitive: `integer` names the source's INTEGER.
    identifiers = " ".join(sorted(set(IDENT.findall(source))))
    invented = [t for t in ticked if not names_the_source(t, identifiers, case_insensitive)]
    stated = [n for n in numbers if number_pattern(n).search(prose)]
    # a '#' line inside a fence is the fence's content (a shell comment), not a heading of the body
    outside_fences = FENCED.sub("\n", body).strip().splitlines()
    # a reasoning block in front of a complete summary is a leak (counted below), not a missing lead
    answer = REASONING_BLOCK.sub("", body, count=1).strip().splitlines()
    lead = bool(answer) and answer[0].startswith(">")
    # a marker in backticks is the summary writing about it (a file that strips them), not a leak
    leaks = len(LEAK.findall(INLINE_CODE.sub("", body)))
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
        "badHeadings": sum(1 for line in outside_fences if re.match(r"^#{1,3}\s", line)),
        "fences": len(FENCE_MARKER.findall(body)),
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


def source_text(path):
    """A source as srcmorph reads it (AiSourceLines.decode): UTF-8, or UTF-16 after its byte order
    mark, without the mark; a byte that is not valid in the encoding reads as U+FFFD."""
    data = path.read_bytes()
    for mark, encoding in ((codecs.BOM_UTF8, "utf-8"), (codecs.BOM_UTF16_LE, "utf-16-le"),
                           (codecs.BOM_UTF16_BE, "utf-16-be")):
        if data.startswith(mark):
            return data[len(mark):].decode(encoding, errors="replace")
    return data.decode("utf-8", errors="replace")


def indexed(path):
    """Whether srcmorph indexes a file of the corpus as run.py configures it: by its extension or a
    name glob, in any case (run.indexes). A file it leaves out is no missing summary."""
    return run.indexes(path.name)


def missing_row(rel, kind, cut_off, src_bytes=0, ids=(), numbers=(), known=False):
    """The row of a summary the run did not write: it fails as "missing", expects what the file expects,
    and is cut off when the run's meta says so (under onIncomplete retryOnce every cut-off file is a
    missing one)."""
    return {"file": rel, "kind": kind, "missing": True, "failed": "missing", "cutOff": rel in cut_off,
            "srcBytes": src_bytes, "idsExpected": len(ids), "idsFound": 0,
            "recall": file_recall("missing", 0, len(ids), known), "numbersExpected": len(numbers),
            "numbersStated": 0, "lead": False, "badHeadings": 0, "fences": 0, "leaks": 0}


def expected_aggregates(sources):
    """The package summaries srcmorph's package phase writes for these sources (relative paths): one
    per directory that holds one, its parents up to the root included; and the project summary."""
    directories = set()
    for rel in sources:
        parts = rel.split("/")[:-1]
        directories |= {"/".join(parts[:i]) for i in range(len(parts) + 1)}
    packages = sorted(((d + "/" if d else "") + PACKAGE_FILE for d in directories), key=lambda r: r.split("/"))
    return packages + [PROJECT_FILE]


def score_model(model_dir, work, facts, cut_off=frozenset()):
    rows = []
    sources = sorted(p for p in work.rglob("*") if p.is_file() and indexed(p))
    for source_path in sources:
        rel = source_path.relative_to(work).as_posix()
        out = model_dir / (rel + ".ai.md")
        source = source_text(source_path)
        kind = kind_of(source_path)
        ids, numbers, everyday, known = expected_ids(rel, source, facts)
        if not out.exists():
            rows.append(missing_row(rel, kind, cut_off, len(source.encode("utf-8")), ids, numbers, known))
            continue
        body = body_of(out.read_text(encoding="utf-8"))
        middle = (facts.get(rel) or {}).get("middle", [])
        row = score_file(source, body, ids, numbers, case_insensitive=kind == "sql", everyday=everyday,
                         prefixes=middle)
        row["recall"] = file_recall(row["failed"], row["idsFound"], row["idsExpected"], known)
        secret = (facts.get(rel) or {}).get("secret")
        if secret:
            # a value the facts mark as a secret: the summary may name it, never copy it
            row["secretLeaked"] = secret in body
        if middle:
            row["middleExpected"] = len(middle)
            row["middleFound"] = len(middle_found(middle, without_copies(model_text(body), source),
                                                  kind == "sql", everyday))
        row.update({"file": rel, "kind": kind, "missing": False, "cutOff": rel in cut_off})
        rows.append(row)
    written = {p.relative_to(model_dir).as_posix() for p in model_dir.rglob(PACKAGE_FILE)}
    written |= {PROJECT_FILE} if (model_dir / PROJECT_FILE).is_file() else set()
    expected = expected_aggregates(p.relative_to(work).as_posix() for p in sources)
    # an aggregate the run wrote although no source asked for it is scored too
    for rel in expected + sorted(written - set(expected), key=lambda r: r.split("/")):
        kind = "project" if rel == PROJECT_FILE else "package"
        if rel not in written:
            rows.append(missing_row(rel, kind, cut_off))
            continue
        row = score_file("", body_of((model_dir / rel).read_text(encoding="utf-8")), [], [])
        row.update({"file": rel, "kind": kind, "missing": False, "cutOff": rel in cut_off, "srcBytes": 0,
                    "invented": 0, "ticked": 0, "inventedNames": [], "recall": None})
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
        # eleven small ones; a failed body scores 0 there (file_recall).
        per_file = [r["recall"] for r in items if r.get("recall") is not None]
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
            "ticked": ticked,
            "invented": sum(r.get("invented", 0) for r in answered),
            "inventedPer100": round(100 * sum(r.get("invented", 0) for r in answered) / ticked, 1) if ticked else None,
            # a missing or failed summary expects its numbers too: 0/4, not 0/0
            "numbers": f"{sum(r.get('numbersStated', 0) for r in answered)}/{sum(r.get('numbersExpected', 0) for r in items)}",
            "noLead": sum(1 for r in answered if not r["lead"]),
            "badHeadings": sum(r["badHeadings"] for r in answered),
            "fences": sum(r["fences"] for r in answered),
            "leaks": sum(r["leaks"] for r in present),
            "secretsLeaked": sum(1 for r in present if r.get("secretLeaked")),
        }
    return summary


def tsv_cell(row, column):
    """A row's value as files.tsv writes it: the invented names space-separated, an unmeasured recall
    empty."""
    if column == "inventedNames":
        return " ".join(row.get(column, []))
    value = row.get(column)
    return "" if value is None else str(value)


def tsv_line(row):
    return "\t".join(tsv_cell(row, c) for c in TSV_COLUMNS)


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
