# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Scores the model-card outputs: compaction, fact recall, invented names and format per file kind.

For every model under <out> (one directory per model key) and every source file of the corpus:
- compaction = source bytes / body bytes (the .ai.md without its header), summed per kind over
  the files that got a body (a failed body is counted under "failed", and against recall);
- recall = per file the share of the expected identifiers (facts.json; for Java the public types and
  the public/protected methods taken from the source, annotated and multi-line signatures included,
  and the methods of a public interface, static or nested, and the elements of an annotation, but not
  an interface's private methods) the body names as a whole word, averaged over the files of a kind.
  An id that is no identifier (a path, a JNDI or property name, an option: `/orders`, `jdbc/ShopDS`,
  `min-revenue`) counts as a whole name too: not where it goes on with an identifier or path character
  (`/orders/{orderId}` names no `/orders`, `java:/jdbc/ShopDSX` no `jdbc/ShopDS`; whole_name).
  A file known to expect no name (a Java file without a public one, such as the package-private
  OpenAiCompatibleProviderSupport, or a facts entry without ids) is not measured when it got a
  summary, but scores 0 when it failed or is missing, so a failed body costs recall wherever it
  is; a file the facts do not list and that is no Java file is not measured at all. A name the facts
  list under "middle" may name an area (`LEDGER_LOCK`): it counts as the prefix of a name too
  (`LEDGER_LOCK_02701`), in recall and in the middle check alike.
  A code block that copies the source (at least half of its lines that hold an identifier are lines
  of the source, whatever the whitespace in them; a line of braces alone counts on neither side)
  does not count: a body that pastes the source names every identifier
  without summarizing it. A block is a ``` or ~~~ fence, opened and closed at the start of a line or
  indented under a list item, as CommonMark reads it (fenced_blocks; a ``` inside a line of prose opens
  none), or a run of lines indented by four
  spaces or a tab after a blank line. A fence around the model's own text (a signature list, a summary wrapped
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
  identifiers or a run of their word parts, split at an underscore, a capital or the end of an
  acronym (`free_space` and `used` of `ringbuf_free_space` and `ringbuf_used`, `get` of `getName`,
  `Parser` of `XMLParser`, the prefix `DEFAULT_`), or
  their plural; a piece inside a word part (`nam` of `getName`) is none (for SQL the name is matched
  without regard to case, the word-part boundaries are not). A qualified name counts as
  its last part, and keywords, standard-library types (for SQL in any case: `varchar` is VARCHAR),
  the names of languages, tools and products
  (`JavaBean`, `PostgreSQL`), file names (`pom.xml`, `docs/srcmorph.yaml`; with an extension that is a
  member name too, `.c`, `.h`, `.ts`, `.tag`, `.cmd`, `.cfg`, `.conf`, only with a directory or a stem no
  identifier has: `item.tag` is the member `tag`, `views/order.tag` a file; MEMBER_EXTENSIONS), escapes
  and format placeholders (`\\t`, `YYYY-MM-DD`, `getX`) are no names;
- numbers = the exact counts of facts.json the body states (the seed-data row counts), not stuck to a
  letter (`v3`, `S3`) and no part of a date (`2026-11-06`); a missing summary expects its numbers too;
- format: missing lead line, '#' to '###' headings outside code fences (indented by up to three spaces,
  as CommonMark reads one), code fence markers (``` and ~~~
  at the start of a line or indented under a list item, as fenced_blocks reads them), leaked reasoning
  markers (`<think>`, gpt-oss's harmony and Gemma's channel
  markers; one in backticks or right after a quote is the summary writing about it, no leak);
- failed = why a body is not a summary: missing, empty, leak-only (a leak and nothing after the leading
  reasoning block, or one that never closes; a complete summary after a reasoning block, `<think>`,
  harmony's analysis channel or Gemma's thought channel, with or without its lead, is a leak, not a
  failure, and so is one after reasoning a chat template opened, which ends in a `</think>` alone on its
  line or glued to a line's end, as CompletionParser strips it), refusal, too-short (under 80 bytes); a
  failed body scores 0 in recall.
  The package and project summaries a run writes are expected for every directory that holds an
  indexed source (and the root), so a run whose package or project phase failed has missing rows
  there too. cutOff = the generation hit maxOutputTokens (from the
  run's --meta file, for a summary written and for one missing alike); such a body may still be
  complete, so it is reported, not failed.
A file is a source of the corpus as run.py configures srcmorph: by its extension or by a name glob,
both in any case (run.indexes), unless the secrets rule keeps it from every model (run.skipped), so a
file srcmorph skips is no missing summary (indexed).
The facts and coverage lines srcmorph puts in front of a body are srcmorph's, not the model's: every
measure is taken on the body without them. A reasoning block in front of the answer (`<think>`,
harmony's analysis channel, Gemma's thought channel, or reasoning up to a `</think>` without its
`<think>`, answer_of) is no part of the summary either: every measure
but the leaks (recall, invented names, numbers, headings, fences, the lead, the body bytes and the
too-short and refusal checks) reads the answer after it.

Writes <scores>/files.tsv (one row per model and file) and <scores>/summary.json (per model and kind),
which MODEL-CARD.md is built from.

Usage: python score.py --work <prepared corpus dir> [--out ...] [--scores ...] [--facts <json>]
       [--meta <runs dir>] (--meta and --scores are required with an --out other than the card's)
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
# The lines srcmorph writes in front of a body (FactExtractor.FACTS_HEADER,
# AiMdLeadExtractor.COVERAGE_HEADER, which MapReduceMath.COVERAGE_HEADER names), in this order.
MACHINE_LINES = ("**Facts (exact, whole file):** ", "**Coverage:** ")
# The opening line of a fence (fenced_blocks): three or more backticks or tildes, an info string after
# backticks without a backtick in it.
FENCE_OPEN = re.compile(r"(?P<marker>`{3,}(?=[^`]*$)|~{3,})")
# A list item's marker line: its content starts after the marker and one to four spaces (one when more
# follow, or nothing), CommonMark's continuation indent of the item.
LIST_ITEM = re.compile(r"( *)([-*+]|\d{1,9}[.)])( +|$)")
# An indented code block: lines indented by four spaces or a tab, after a blank line or at the start.
INDENTED = re.compile(r"(?:\A|(?<=\n\n))(?P<code>(?:(?: {4}|\t)[^\n]*(?:\n|\Z))+)")


def list_content_column(line):
    """The column a list item's content starts at, or None for a line that is no list item's marker line."""
    match = LIST_ITEM.match(line)
    if not match:
        return None
    spaces = len(match.group(3))
    return match.end(2) + (spaces if 1 <= spaces <= 4 else 1)


def fenced_blocks(text):
    """The fenced blocks of a text as CommonMark reads them, as (start, end, code, markers): opened at the
    start of a line by three or more backticks or tildes, up to three spaces right of its container's
    content column (0 at the top; in a list item the item's content column, so a fence indented under
    `  - item` opens at four spaces), and closed by a run of the same character at least as long, at most
    three spaces right of that column, with nothing but spaces after it; an unclosed one runs to the end
    of the text, or of its list item (markers 1). A ``` inside a line of prose ("answers in ```json
    blocks") opens nothing. A non-blank line left of an item's content column ends the item, and a fence
    opened in it: a `## Heading` after an unclosed fence of an item is read again. start and end span the
    opening line to the closing one, or to the last line of the block (without its line break), code the
    lines between them."""
    blocks, columns = [], []
    lines = text.split("\n")
    offsets = [0]
    for line in lines:
        offsets.append(offsets[-1] + len(line) + 1)
    i = 0
    while i < len(lines):
        line = lines[i]
        if not line.strip():
            i += 1
            continue
        indent = len(line) - len(line.lstrip(" "))
        columns = [c for c in columns if c <= indent]
        item = list_content_column(line)
        container, rest = (columns[-1] if columns else 0), line[indent:]
        if item is not None:
            columns.append(item)
            # a fence may open on the item's own line ("- ```sql")
            container, rest = item, line[item:]
            indent = item
        opener = FENCE_OPEN.match(rest) if indent - container <= 3 else None
        if opener is None:
            i += 1
            continue
        char, length = opener.group("marker")[0], len(opener.group("marker"))
        closer = re.compile(r"( *)" + re.escape(char) + "{" + str(length) + r",}[ \t]*")
        j, closed = i + 1, False
        while j < len(lines):
            match = closer.fullmatch(lines[j])
            if match and len(match.group(1)) <= container + 3:
                closed = True
                break
            # a non-blank line left of the item's content column ends the item, and the fence with it
            if container and lines[j].strip() and len(lines[j]) - len(lines[j].lstrip(" ")) < container:
                break
            j += 1
        start = offsets[i]
        code_start = min(offsets[i + 1], len(text))
        if closed:
            blocks.append((start, offsets[j] + len(lines[j]), text[code_start:offsets[j]], 2))
            i = j + 1
        elif j < len(lines):
            # unclosed, up to the end of its item: the line that ended the item is read again
            blocks.append((start, offsets[j] - 1, text[code_start:offsets[j]], 1))
            i = j
        else:
            blocks.append((start, len(text), text[code_start:], 1))
            i = j
    return blocks


def sub_fences(text, replace):
    """The text with every fenced block (fenced_blocks) replaced by replace(code)."""
    out, last = [], 0
    for start, end, code, _ in fenced_blocks(text):
        out += [text[last:start], replace(code)]
        last = end
    return "".join(out) + text[last:]


def fence_markers(text):
    """The opening and closing lines of the fences of a text, as fenced_blocks reads them."""
    return sum(markers for _, _, _, markers in fenced_blocks(text))
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
# A name before a parenthesis is a call (`response.json()`), whatever it ends with.
FILE_NAME = re.compile(r"(?<![\w.$/-])(?:[\w.-]+/)*[\w-]+(?:\.[\w-]+)*\.(?i:" + "|".join(FILE_EXTENSIONS)
                       + r")(?![\w.$/(-])")
# Extensions that are member names too (`order.c`, `event.ts`, `item.tag`, `self.cfg`): a name ending in one is
# a file name only with a directory part (`views/order.tag`) or a stem no identifier has (`my-order.tag`,
# `05_seed.cfg`); otherwise it is the qualified name it reads as and counts as its last part. A short one
# (`c`, `ts`) is below the three letters a name needs either way; `tag`, `cmd`, `cfg` and `conf` are checked
# against the source. Extensions a member is rarely called (`json`, `properties`, `txt`, `xml`) stay file
# names: in the card's outputs every such span was a file (`ringbuf.h`, `05_seed_data.sql`), and a summary of
# `application.properties` that names its file must not invent `properties`.
MEMBER_EXTENSIONS = {"c", "h", "ts", "tag", "cmd", "cfg", "conf"}


def file_name_or_member(match):
    """A FILE_NAME match blanked, or kept when it is the qualified name of a member (MEMBER_EXTENSIONS)."""
    name = match.group(0)
    if name.rsplit(".", 1)[1].lower() in MEMBER_EXTENSIONS and QUALIFIED.fullmatch(name):
        return name
    return " "
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
# An interface's methods and an annotation's elements are public without the modifier; a private one
# (Java 9) is not.
INTERFACE_METHOD = re.compile(r"(?![^(]*\bprivate\s)" + ANNOTATIONS + r"(?:(?:static|default|abstract|@[\w.]+)\s+)*"
                              r"(?:" + GENERICS + r"\s+)?[\w<>\[\],.?@ ]+?\s+(\w+)\s*\(")
JAVA_INTERFACE = re.compile(ANNOTATIONS + r"public\s+(?:(?:static|abstract|strictfp|sealed|non-sealed)\s+)*"
                            r"(?:interface|@interface)\s+\w+")
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
# where a capital follows a small letter or a digit, or before the last capital of an acronym that a word
# goes on from (`XML` and `Parser` of `XMLParser`, as search.py splits it).
ACRONYM_END = r"(?<=[A-Z])(?=[A-Z][a-z])"
PART_START = r"(?:(?<![A-Za-z0-9_])|(?<=_)|(?<=[a-z0-9])(?=[A-Z])|" + ACRONYM_END + ")"
PART_END = r"(?:(?![A-Za-z0-9_])|(?=_)|(?<=_)|(?<=[a-z0-9])(?=[A-Z])|" + ACRONYM_END + ")"


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
        span = QUALIFIED.sub(lambda m: m.group(0).rsplit(".", 1)[1], FILE_NAME.sub(file_name_or_member, span))
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


def whole_name(name, case_insensitive=False):
    """A path- or colon-like id (`/orders`, `jdbc/ShopDS`, `orders.incoming.queue`, `min-revenue`) as a
    whole name: not after an identifier character when it starts with one, and not going on with an
    identifier or path character (`/`, `\\`, `@`, `{`), nor with a hyphen, a dot before an identifier
    character or a colon before a letter or a slash (`min-revenue-cap`, `urn:example:statement:v2`), so
    `/orders/{orderId}/cancel` names neither `/orders` nor `/orders/{orderId}`, and `java:/jdbc/ShopDSX` not
    `jdbc/ShopDS`; a sentence's period, a label's colon ("erp.host: the ERP"), a port (`erp.host:8080`), a
    Maven version (`jackson-databind:${jackson.version}`), a comma, a query string (`/orders?page=2`) or
    Perl's arrow (`JSON::PP->new`) may follow. What comes in front of one that starts with a path character
    is left open: `java:/jdbc/ShopDS` names `jdbc/ShopDS`, `--min-revenue` names `min-revenue`."""
    head = r"(?<![A-Za-z0-9_])" if re.match(r"\w", name) else ""
    return re.compile(head + re.escape(name) + r"(?![\w/\\@{])(?!-(?!>))(?!\.\w)(?!:[A-Za-z_/])",
                      re.I if case_insensitive else 0)


def names_found(ids, prose, case_insensitive, everyday=(), prefixes=()):
    """The expected ids the prose names: an everyday word only where marked as code, another
    identifier as a whole word, one of prefixes also as the prefix of a name (LEDGER_LOCK names the
    area of LEDGER_LOCK_02701 and LEDGER_LOCK_*), a path- or colon-like id as a whole name (whole_name)."""
    fold = str.lower if case_insensitive else (lambda s: s)
    words = {fold(w) for w in IDENT.findall(prose)}
    found = []
    for i in ids:
        if not IDENT.fullmatch(i):
            hit = whole_name(i, case_insensitive).search(prose) is not None
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
    before a sentence's period. Not stuck to a letter or an underscore either (`v3`, `S3`, `3rd`, `100k`,
    `x_5`), and not a part of a date or a code joined by hyphens (`2026-11-06`, `ID-12`); a hyphen to a
    word after it is prose ("a 4000-row table"), and one in front of nothing but the number a sign."""
    digits = str(n)
    head = len(digits) % 3 or 3
    groups = [digits[:head]] + [digits[i:i + 3] for i in range(head, len(digits), 3)]
    return re.compile(r"(?<![\d.,])(?<!\d[ \u00a0\u2009\u202f'])(?<![^\W\d])(?<!\w-)" + THOUSANDS.join(groups)
                      + r"(?![\d])(?![^\W\d])(?!-\d)(?![.,][\d])(?![ \u00a0\u2009\u202f'][\d]{3}(?![\d]))")


def middle_found(middle, prose, case_insensitive=False, everyday=()):
    """The middle names of a large-corpus file the prose names: a name, or the prefix of one
    (LEDGER_LOCK names the area of LEDGER_LOCK_02701), by the rule recall reads them with
    (names_found), so the two never disagree."""
    return names_found(middle, prose, case_insensitive, everyday, prefixes=middle)


def failure_of(missing, body, leaks, answer=None):
    """Why a body is no summary, or "" when it is one: empty on the whole body, leak-only when it leaks
    and nothing is left after a leading reasoning block (a complete answer after one, with or without a
    lead, is a summary that leaks), refusal and too-short on the answer after that block (answer_of; the
    body when not given)."""
    answer = body if answer is None else answer
    if missing:
        return "missing"
    if not body.strip():
        return "empty"
    if leaks and not answer.strip():
        return "leak-only"
    if REFUSAL.match(answer):
        return "refusal"
    if len(answer.strip().encode("utf-8")) < MIN_BODY_BYTES:
        return "too-short"
    return ""


# A reasoning block that opens the body and never closes (cut off while reasoning): no answer follows it.
UNCLOSED_REASONING = re.compile(r"^\s*(?:<think>(?!.*</think>)|<\|channel>thought(?!.*<channel\|>)"
                                r"|(?:<\|start\|>\w*)?<\|channel\|>analysis<\|message\|>(?!.*<\|end\|>))", re.S)


# The tag pair of the reasoning a chat template opens itself (Qwen3, DeepSeek-R1): the body holds only the
# closing tag (CompletionParser.THINK_TAG_START_MARKER / THINK_TAG_END_MARKER).
THINK_START, THINK_END = "<think>", "</think>"
# A closing tag after one of these is text about the tag ("`</think>`"), not the end of a line of reasoning.
NOT_GLUING = "\"'`"


def answer_before(before):
    """Whether a line of the text before a glued closing tag starts like an answer: the lead's '>' or a
    code fence (CompletionParser.answerBefore). Reasoning has neither."""
    return any(line.strip().startswith((">", "```", "~~~")) for line in before.split("\n"))


def injected_end(text, alone):
    """Where the closing tag that ends a template-opened reasoning starts, or -1: the first one with no
    <think> before it that stands alone on its line (alone), or else that ends its line glued to the word
    before it, after no line that starts like an answer (CompletionParser.injectedEnd)."""
    index = text.find(THINK_END)
    while index >= 0 and THINK_START not in text[:index]:
        line_end = text.find("\n", index)
        rest = text[index + len(THINK_END):len(text) if line_end < 0 else line_end]
        before = text[text.rfind("\n", 0, index) + 1:index]
        if not rest.strip():
            if alone and not before.strip():
                return index
            if (not alone and before and not before[-1].isspace() and before[-1] not in NOT_GLUING
                    and not answer_before(text[:index])):
                return index
        index = text.find(THINK_END, index + 1)
    return -1


def answer_of(text):
    """The model's answer: its text (model_text) without the reasoning in front of it, which is no part of
    the summary (a leak, counted on the whole text); nothing when that block never closes. The reasoning
    is a block that opens the text, or, as CompletionParser strips it, the text up to a </think> with no
    <think> before it that stands alone on its line or ends a line glued to its last word (a template
    opened the block itself)."""
    if UNCLOSED_REASONING.match(text):
        return ""
    if REASONING_BLOCK.match(text):
        return REASONING_BLOCK.sub("", text, count=1)
    end = injected_end(text, True)
    if end < 0:
        end = injected_end(text, False)
    return text if end < 0 else text[end + len(THINK_END):]


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

    def fence(code):
        return " " if copies(code) else "\n" + code + "\n"

    def indented(match):
        return " \n" if copies(match.group("code")) else match.group("code")

    return INDENTED.sub(indented, sub_fences(body, fence))


def score_file(source, body, ids, numbers, case_insensitive=False, everyday=(), prefixes=()):
    text = model_text(body)
    # every measure but the leaks reads the answer: a reasoning block in front of it is no part of the
    # summary, so its names, numbers, headings and bytes count for nothing (and it is no missing lead)
    answer = answer_of(text)
    prose = without_copies(answer, source)
    found = names_found(ids, prose, case_insensitive, everyday, prefixes)
    ticked = ticked_names(prose, case_insensitive)
    # SQL names and keywords are case-insensitive: `integer` names the source's INTEGER.
    identifiers = " ".join(sorted(set(IDENT.findall(source))))
    invented = [t for t in ticked if not names_the_source(t, identifiers, case_insensitive)]
    stated = [n for n in numbers if number_pattern(n).search(prose)]
    # a '#' line inside a fence is the fence's content (a shell comment), not a heading of the body
    outside_fences = sub_fences(answer, lambda code: "\n").strip().splitlines()
    lines = answer.strip().splitlines()
    lead = bool(lines) and lines[0].startswith(">")
    # a marker in backticks is the summary writing about it (a file that strips them), not a leak
    leaks = len(LEAK.findall(INLINE_CODE.sub("", text)))
    return {
        "srcBytes": len(source.encode("utf-8")),
        "bodyBytes": len(answer.strip().encode("utf-8")),
        "idsExpected": len(ids),
        "idsFound": len(found),
        "ticked": len(ticked),
        "invented": len(invented),
        "inventedNames": sorted(set(invented))[:8],
        "numbersExpected": len(numbers),
        "numbersStated": len(stated),
        "lead": lead,
        # CommonMark reads a heading indented by up to three spaces (four make an indented code block)
        "badHeadings": sum(1 for line in outside_fences if re.match(r"^ {0,3}#{1,3}\s", line)),
        "fences": fence_markers(answer),
        "leaks": leaks,
        "empty": not text.strip(),
        "failed": failure_of(False, text, leaks, answer),
    }


CARD_OUT = HERE.parent / "outputs-2026-10"
CARD_SCORES = HERE / "scores"


def scores_directory(out, scores):
    """Where the scores of an --out go: --scores, or for the card's own outputs the card's scores/;
    another output tree names a directory of its own, so the card's files.tsv and summary.json, which
    plot.py and report.py build the card from, are not overwritten."""
    card = Path(out).resolve() == CARD_OUT.resolve()
    if scores is None:
        if card:
            return str(CARD_SCORES)
        raise ValueError(f"--out {out} is not the card's outputs: --scores names a directory of its own")
    if not card and Path(scores).resolve() == CARD_SCORES.resolve():
        raise ValueError(f"--scores {scores} is the card's: the scores of --out {out} go to a directory of their own")
    return scores


def meta_directory(out, meta):
    """The runs' meta files whose cut-off paths an --out is scored with: --meta, or for the card's own
    outputs the card's runs/; another output tree names its meta, so the card's cut-off lists are not
    applied to it."""
    if meta is not None:
        return meta
    if Path(out).resolve() == CARD_OUT.resolve():
        return str(HERE / "runs")
    raise ValueError(f"--out {out} is not the card's outputs: --meta names its runs' meta files")


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
    """A source as srcmorph reads it (SourceLines.decode): UTF-8, or UTF-16 after its byte order
    mark, without the mark; a byte that is not valid in the encoding reads as U+FFFD."""
    data = path.read_bytes()
    for mark, encoding in ((codecs.BOM_UTF8, "utf-8"), (codecs.BOM_UTF16_LE, "utf-16-le"),
                           (codecs.BOM_UTF16_BE, "utf-16-be")):
        if data.startswith(mark):
            return data[len(mark):].decode(encoding, errors="replace")
    return data.decode("utf-8", errors="replace")


def indexed(path, root):
    """Whether srcmorph summarizes a file of the corpus (root) as run.py configures it: the walk collects
    it by its extension or a name glob, in any case (run.indexes), and the secrets rule does not keep it
    from every model (run.skipped: by its name, its path or its head). A file it leaves out is no missing
    summary."""
    return run.indexes(path.name) and not run.skipped(path.relative_to(root).as_posix(), source_text(path))


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
    sources = sorted(p for p in work.rglob("*") if p.is_file() and indexed(p, work))
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
            row["middleFound"] = len(middle_found(middle, without_copies(answer_of(model_text(body)), source),
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


def card_metrics(rows):
    """The card's quality of one model from its files.tsv rows (strings, as csv reads them): the recall
    of each kind (java, sql, other: the mean of the per-file recall, a file without a measured recall
    left out, 0.0 for a kind without any), the share of clean files (a body, a lead, no bad heading,
    fence or leak), the share of the four seed counts the seed data's summary states, and the quality,
    the mean of the three recalls and the clean share."""
    recall = {}
    for kind in ("java", "sql", "other"):
        per_file = [float(r["recall"]) for r in rows if r["kind"] == kind and r["recall"] != ""]
        recall[kind] = sum(per_file) / len(per_file) if per_file else 0.0
    clean = sum(1 for r in rows if not r["failed"] and r["lead"] == "True" and r["badHeadings"] == "0"
                and r["fences"] == "0" and r["leaks"] == "0")
    fmt_share = clean / len(rows)
    seed = next((r for r in rows if r["file"].endswith("05_seed_data.sql")), {})
    return {"recall": recall, "format": fmt_share, "seed": int(seed.get("numbersStated") or 0) / 4,
            "quality": (recall["java"] + recall["sql"] + recall["other"] + fmt_share) / 4}


def cost_per_mb(run, mb):
    """The USD an OpenRouter run cost per MB of source code, or None: another backend reports no cost,
    and a run whose cost lookup failed records costUsd null (run.py's cost_failed); a chart leaves it
    out."""
    if run["backend"] != "openrouter" or run["cost"] is None:
        return None
    return run["cost"] / mb


def tsv_line(row):
    return "\t".join(tsv_cell(row, c) for c in TSV_COLUMNS)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work", required=True)
    parser.add_argument("--out", default=str(CARD_OUT))
    parser.add_argument("--scores", help="where files.tsv and summary.json go (default: the card's scores/,"
                        " only with the card's --out)")
    parser.add_argument("--facts", default=str(HERE / "facts.json"))
    parser.add_argument("--meta", help="the runs' meta files, for the cut-off paths (default: the card's"
                        " runs/, only with the card's --out)")
    args = parser.parse_args()
    try:
        meta = meta_directory(args.out, args.meta)
        scores_dir = scores_directory(args.out, args.scores)
    except ValueError as e:
        parser.error(str(e))
    facts = json.loads(Path(args.facts).read_text(encoding="utf-8"))
    work = Path(args.work)
    scores = Path(scores_dir)
    scores.mkdir(parents=True, exist_ok=True)
    lines = ["\t".join(TSV_COLUMNS)]
    summary = {}
    for model_dir in sorted(p for p in Path(args.out).iterdir() if p.is_dir()):
        rows = score_model(model_dir, work, facts, cut_off_of(meta, model_dir.name))
        summary[model_dir.name] = summarize(rows)
        lines += [tsv_line({**row, "model": model_dir.name}) for row in rows]
    (scores / "files.tsv").write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    (scores / "summary.json").write_text(json.dumps(summary, indent=1) + "\n", encoding="utf-8", newline="\n")
    for model, kinds in summary.items():
        print(model, {k: (v["compaction"], v["recall"], v["inventedPer100"]) for k, v in sorted(kinds.items())})


if __name__ == "__main__":
    main()
