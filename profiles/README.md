# Profiles

A profile is one directory holding everything srcmorph's recognition depends on besides the model:
the prompts and the exact fact counters, one file per key. A configuration names the directory once
(`profileDirectory`), and its rules name only keys: a rule's `promptKey` reads
`<profileDirectory>/<key>.md`, its `factsKey` reads `<profileDirectory>/<key>.tsv`. No prompt text and
no regular expression is repeated in a configuration, so a profile can be copied, versioned and
handed on as a whole.

[`v3/`](v3/) is the current profile:

| File | Key | For |
|---|---|---|
| [`v3/file-body-java.md`](v3/file-body-java.md) | `promptKey: file-body-java` | one Java source file |
| [`v3/file-body-sql.md`](v3/file-body-sql.md) | `promptKey: file-body-sql` | one SQL file, as schema |
| [`v3/file-body-shell.md`](v3/file-body-shell.md) | `promptKey: file-body-shell` | one Bash, POSIX sh or ksh93 script |
| [`v3/file-body-perl.md`](v3/file-body-perl.md) | `promptKey: file-body-perl` | one Perl script or module |
| [`v3/file-body-xml.md`](v3/file-body-xml.md) | `promptKey: file-body-xml` | one XML file: a POM, a Spring context, an XSD, an XSLT |
| [`v3/file-body-fallback.md`](v3/file-body-fallback.md) | `promptKey: file-body-fallback` | any other source file |
| [`v3/package-body.md`](v3/package-body.md) | `promptKey: package-body` | a package, from its file summaries |
| [`v3/project-body.md`](v3/project-body.md) | `promptKey: project-body` | the project overview, from the package leads |
| [`v3/java-facts.tsv`](v3/java-facts.tsv) | `factsKey: java-facts` | exact counts in front of every Java summary |
| [`v3/sql-facts.tsv`](v3/sql-facts.tsv) | `factsKey: sql-facts` | exact counts in front of every SQL summary |
| [`v3/shell-facts.tsv`](v3/shell-facts.tsv) | `factsKey: shell-facts` | the same for Bash, POSIX sh and ksh93 scripts |
| [`v3/perl-facts.tsv`](v3/perl-facts.tsv) | `factsKey: perl-facts` | the same for Perl scripts and modules |
| [`v3/xml-facts.tsv`](v3/xml-facts.tsv) | `factsKey: xml-facts` | the same for XML, XSD and XSLT files |
| [`v3/pom-facts.tsv`](v3/pom-facts.tsv) | `factsKey: pom-facts` | the same for Maven POMs (a rule matching `**/*pom.xml`) |

## Prompt files

A `.md` file is the **system prompt** of its key, sent verbatim: no placeholders, no license header
(it would reach the model). srcmorph sends the file or package name and the source (or the child
summaries) as a separate user message. Each file starts with a domain lead line, then fixed `####`
sections (Purpose, Type, Input, Output, Core logic, Public API, Dependencies, Exceptions, Concurrency
for Java; Schema, Reads, Writes, Relationships, Routine logic for SQL; Constants and configuration for
shell, Perl and other files; Definitions, Configuration and Dependencies for XML), with the rules against code fences, `###` headings and invented names, and the "index
only what is literally present" guard for truncated sources.

## Fact files

A `.tsv` file holds one counter per line, its label and a Java regular expression separated by a tab;
lines starting with `#` are comments. srcmorph counts each pattern's matches over the whole source
and writes them as one line before the AI summary, for example
`**Facts (exact, whole file):** INSERT statements: 5; VALUES rows (approx): 33; tables: 2; ...`. A
sampled or chunked summary cannot count reliably, these lines can. Every pattern is compiled when the
run starts, so a broken one fails it with the file and line.

A label starting with `[ignore]` marks a region instead of a counter: its matches are blanked out
before the other counters count, every character except the line breaks, so `^` anchors and line
counts see the same lines as before. That is how a sub named in Perl's POD, a function or a `set -e`
in a shell heredoc, a commented-out POM dependency or an `INSERT` inside a SQL block comment stays
out of the counts. An ignore line is not reported, and a file of ignore lines only is refused. In a
configuration the same is a counter with `ignore: true`.

The counters are regex heuristics, and their labels say so where they are approximate. The tests read
these files, not copies, and count through the production extractor, so the ignore lines apply:
`SqlFactPatternsTest` pins every SQL counter on PostgreSQL, MySQL, T-SQL and Oracle forms (including
the known over- and undercount of `VALUES rows (approx)`), `JavaStructureFactPatternsTest` the
boundary of the Java method, constructor and field counts, and `ShellFactPatternsTest`,
`PerlFactPatternsTest` and `XmlFactPatternsTest` each counter's forms and near misses plus the
counts of the benchmark's language corpus ([`corpus-languages/`](../docs/ai-index-benchmark/corpus-languages/)).

| `sql-facts` | Counts |
|---|---|
| `[ignore] block comments` | `/* ... */`, but not MySQL's `/*! ... */` or Oracle's `/*+ ... */`, which hold SQL |
| `INSERT statements` | `INSERT INTO` at the start of a line, also `INSERT IGNORE` / `LOW_PRIORITY` and `REPLACE INTO` |
| `VALUES rows (approx)` | the row tuples of `VALUES` lists, so a multi-row `INSERT` counts each row; rows an `INSERT ... SELECT` reads from a table are not seen |
| `tables`, `views`, `indexes` | `CREATE` statements, also `TEMP`/`UNLOGGED` tables, `MATERIALIZED`, `OR REPLACE`/`OR ALTER` and Oracle's `FORCE EDITIONABLE` views, `UNIQUE`/`CLUSTERED`/`FULLTEXT`/`SPATIAL`/`BITMAP` indexes |
| `functions/procedures`, `triggers` | `CREATE [OR REPLACE / OR ALTER] [EDITIONABLE] FUNCTION / PROCEDURE / PROC / TRIGGER`, also MySQL's `DEFINER=` |
| `types` | `CREATE TYPE` (enums, composite types) |
| `ALTER TABLE` | every `ALTER TABLE` statement |
| `sequences`, `extensions`, `schemas and databases`, `packages and package bodies` | `CREATE [TEMP] SEQUENCE`, `CREATE EXTENSION`, `CREATE SCHEMA` / `DATABASE`, Oracle's `CREATE [OR REPLACE] PACKAGE [BODY]` |

| `shell-facts` (Bash, sh, ksh93) | Counts |
|---|---|
| `[ignore]` comment lines, heredoc bodies | a whole-line `#` comment; a heredoc from `<<` to its end marker, quoted or `<<-` indented |
| `functions` | `name()`, `name ()` and `function name` definitions |
| `getopts loops`, `option case arms` | `getopts` calls; `case` arms of `-x` / `--long` options |
| `named positional parameters` | an argument assigned to a name (`SRC="$1"`, `local mode=${3:-fast}`) |
| `exported variables`, `readonly variables`, `defaulted variables` | `export` / `declare -x`; `readonly` / `declare -r` / `typeset -r`; `${NAME:-default}` and `${NAME:=default}` |
| `script-level assignments` | an assignment at column 0 |
| `sourced files`, `traps`, `set -e`, `exit statements with a status` | `source` / `.`; `trap` (not `trap -p`); `set -e` / `-o errexit`; `exit` with a literal status |
| `typeset declarations`, `ksh discipline functions` | ksh93's `typeset`; `function name.get` / `.set` / `.unset` / `.append` |

| `perl-facts` | Counts |
|---|---|
| `[ignore]` POD, `__END__`/`__DATA__`, heredoc bodies, comment lines | from a `=word` line to `=cut`; from `__END__` to the end; `<<"END"`, `<<~END` bodies; whole-line `#` comments |
| `packages`, `subs` | `package` statements and blocks; named, lexical (`my sub`), signature and attribute subs, not anonymous subs or forward declarations |
| `modules used`, `modules required` | `use Some::Module` (not pragmas or versions); `require Module` / `require 'file'` |
| `use constant statements`, `package variables (our)`, `file-scope lexicals (my at column 0)` | each `use constant`; `our` declarations; a `my` at column 0 |
| `environment variables read`, `GetOptions calls`, `die, croak and confess` | `$ENV{KEY}`; `GetOptions(...)` and its variants; every mention of the three, an import included |

| `xml-facts` / `pom-facts` | Counts |
|---|---|
| `[ignore]` comments, CDATA sections | `<!-- ... -->`; `<![CDATA[ ... ]]>` (xml-facts only) |
| `elements`, `namespace declarations`, `id and name attributes` | start tags; `xmlns` / `xmlns:prefix`; `id=` and `name=` attributes |
| `XSD type definitions`, `XSLT templates`, `Spring beans` | named `complexType` / `simpleType`; `template` elements (not calls); `bean` elements |
| `dependencies`, `managed dependency sections`, `plugins`, `modules`, `profiles`, `test-scoped dependencies` | pom-facts: each `<dependency>` (managed and profile ones included), `<dependencyManagement>`, `<plugin>`, `<module>`, `<profile>`, `<scope>test</scope>` |

## Using a profile

- Point `profileDirectory` at it: relative to the working directory (the CLI) or to the module's
  `${basedir}` (the Maven plugin), or absolute.
- Name keys in the rules. A key that is neither defined in the configuration nor a file of the
  profile fails the run before any model loads, naming the file it looked for.
- To change a prompt or a counter for your own project, copy the directory and point
  `profileDirectory` at the copy.
- A `promptDefinition` or `factDefinition` in the configuration still works and wins over a file of
  the same key: inline (`template`, `facts`) or naming its own file (`templateFile`, `factsFile`,
  relative to `profileDirectory`).

Who reads `v3/`:

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml)
  (`<ai.profileDir>`, overridable with `-Dai.profileDir=`);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--profile v3` by default);
- the examples under [`examples/`](../examples/) (`profileDirectory: "../profiles/v3"`, or
  `"../../profiles/v3"` one level deeper), so the CI smoke of the user-built jar reads it too;
- the plugin's integration-test fixture, [`.github/plugin-it/pom.xml`](../.github/plugin-it/pom.xml),
  which also checks that the `java-facts` counts reach the summaries.

## Shell, Perl and XML prompts

Up to these three files, shell scripts, Perl and XML went through `file-body-fallback`. The
benchmark's language corpus ([`corpus-languages/`](../docs/ai-index-benchmark/corpus-languages/):
Perl, Bash, ksh93, POSIX sh, a POM, a Spring context, an XSD, an XSLT) showed what gpt-oss-20b lost
with it: the options and globals of a deploy script (`DRY_RUN`, `HOSTS`, the sourced library),
lower-case ksh variables, a POM's plugins, an XSLT's keys. The prompts ask for those by kind:

- **`file-body-shell`:** the dialect and its strict mode; every argument by the variable that holds
  it and every option by the letter a user types, with its parser; every top-level variable, also
  the lower-case ones, one bullet each as `name=value`; every function, the small helpers too;
  sourced files, external commands and the tables or hosts their heredocs use; exit statuses and
  traps. A name that occurs only inside a heredoc or a comment is not the script's own.
- **`file-body-perl`:** packages and subs, the private `_helper` subs too; `Getopt::Long` options as
  typed; every `%ENV` key; `use constant`, `our` and file-scope `my` configuration; modules used
  and what they import; `die` / `croak` / `confess`. POD and what follows `__END__` are
  documentation.
- **`file-body-xml`:** what the file defines, by kind: a POM's coordinates, dependencies, plugins,
  properties, modules and profiles; a Spring context's beans with class and references; an XSD's
  namespace, elements and types; an XSLT's templates, parameters and keys; otherwise the element
  structure and the repeated records.

The shell prompt took four drafts on gpt-oss: with the first two, it wrote `tmp` without the value it is
built from (the `.partial` file of the benchmark's backup script); with "each as `name=value`" it
copied the assignments into a code fence in two of three runs; "one bullet each" kept both. Three
runs each on OpenRouter, scored as the model card scores (`score.py --facts`):

| Corpus | Model | before (`file-body-fallback`) | with the language prompts |
|---|---|---|---|
| language corpus | gpt-oss-20b | 95.7 % (95.6 to 96.1) | 99.3 % (98.5 to 100) |
| language corpus | GPT-6 Luna | 99.6 % (99.3 to 100) | 100 % in all three |
| benchmark | gpt-oss-20b | 100 % in all three | 99.6 % (99.0 to 100) |
| benchmark | GPT-6 Luna | 100 % | 100 % |
| second | gpt-oss-20b | 99.8 % (99.3 to 100) | 99.5 % (98.5 to 100) |
| second | GPT-6 Luna | 100 % | 100 % |

On the language corpus gpt-oss's deploy script went from 70 to 100 %, the POM from 85 to 100 %,
the ksh script from 89 to 98 %. On the other two corpora every miss with the language prompts is
in a Java or SQL file, whose prompts did not change: gpt-oss's usual variation between runs. No
run wrote a code fence or a `###` heading in a shell, Perl or XML summary.

The size tiers for big XML files (`huge-xml`, `big-window-xml` in the plugin's self-test) stay on
`file-body-fallback`: with `file-body-xml`, both Granite models named fewer of an 850 KB catalog's
elements (86 to 14 % and 86 to 57 %, one run each), and H-1B took the prompt's example sentence
as its lead.

## v3 against v2

v2 was written from the misses on the benchmark corpus, so it ran three times on a second corpus as
well, which is kept outside this repository: Java with its SQL migrations, a shell script, C and
Python from other open-source projects. Its gain carried over (gpt-oss-20b 94.6 % with v1, 99.0 %
with v2; GPT-6 Luna 98.6 % and 100 %). The prompts of v3 are v2's with additions for what gpt-oss
still lost there:

- **SQL:** when several `INSERT`s write one table, each statement's count as an addition, then the
  total (the model counted every statement right and summed wrong); every `CREATE EXTENSION` and
  `CREATE TYPE` by name, an enum with its values; every index as its own bullet with its table,
  columns or expressions, method and operator class, also in a file that creates no table.
- **Other files:** the program entry point `main`; for a command-line tool every command,
  subcommand and option by the name a user types, also where the function behind it is named
  differently; a script's arguments by the variable that holds them.
- The Java, package and project prompts and both fact files are unchanged.

The last two additions came from the drafts in between: with the command-line sentence alone,
gpt-oss described a shell script's arguments as `$1` instead of naming `SOURCE_DIR`, and an
index-only migration lost its index list. Three runs of v3 each, both corpora:

| Corpus | Model | v2 | v3 |
|---|---|---|---|
| benchmark | gpt-oss-20b | 99.0 % (97.0 to 100) | 100 % in all three |
| benchmark | GPT-6 Luna | 100 % | 100 % |
| second | gpt-oss-20b | 99.0 % (98.2 to 100) | 99.8 % (99.3 to 100) |
| second | GPT-6 Luna | 100 % | 100 % |

No format slip, the index did not grow, and the search measurement stayed where it was (model
card, finding 6). gpt-oss's results move by a few points between runs of one unchanged prompt, so
these numbers separate a miss that recurs from one that does not, not a one-point difference.

## v2 against v1

The prompts of v2 are v1's with targeted additions for the names v1 lost in the October 2026
[model card](../docs/ai-index-benchmark/MODEL-CARD.md) (finding 5 there):

- **Java:** the type's own name first in the Type section (gpt-oss-20b left it out of the large
  files), and getters and setters are not a family: every accessor is named, a pair on one short
  line (GPT-6 Luna once named 3 of the 88 accessors of a configuration class).
- **SQL:** every constraint and index by its declared name, also those of `ALTER TABLE`; for data,
  the exact number of rows inserted per table.
- **Other files:** a Constants and configuration section (constants, macros, status codes,
  configuration and environment variables), the internal helper functions, the library functions
  called.
- `package-body` and `project-body` are unchanged.

The examples in the added instructions are neutral names that occur nowhere in the benchmark corpus.
Three runs each on OpenRouter: GPT-6 Luna scored 100 % in all three (v1: 93.6 to 99.0 %), gpt-oss-20b
99.0 % on average (v1: 96.8 %), with no format slip and a barely longer index. The search measurement
did not move beyond its noise (about one of 43 questions lower on average). v2 was written from the
misses on that same corpus, so part of the gain may be fitted to it; a corpus it was not written on
would tell.

The fact counters came with v2 into the profile; before, they were written out in the plugin pom and
the example configuration. Since then `INSERT rows` is `INSERT statements` (it always counted
statements, so a multi-row `INSERT` read as one row), `VALUES rows (approx)` counts the rows, and
materialized views, triggers, types and `ALTER TABLE` are counted.

## A new version, and the earlier ones

The next profile goes into `v4/` with a file for each key, is measured with `run.py --profile v4`,
and replaces `v3/` when it wins; the configurations above then switch to it, and `v3/` goes. Only the
current profile is kept here, the earlier prompt sets are in the history (`git show <commit>:<path>`
prints one; the directory was called `prompts/` then):

| Set | Where | What |
|---|---|---|
| v2 | `profiles/v2/` in commit `b89d028` | the set before v3; the model card's finding 5 measured it |
| v1 | `prompts/v1/` in commit `6fe8335` | the set every run of the October 2026 model card used; before these files existed (commit `cd2e15f`) it was inline in the plugin pom and in the benchmark's `prompts.json`, where the pom's CDATA added a leading line break and a trailing line break plus 36 spaces |
| the June 2026 experiment, then also called v2 | `prompts/v2/` in commit `cd2e15f` | trivial-file omission, JavaBean accessor grouping, a "no invented keywords" ban, single-child package pass-through, more brevity; not adopted, because code-specialized models started wrapping sections in code fences ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2") |
| v1 as the June 2026 benchmark ran it | `prompts/archive/2026-06/v1/` in commit `cd2e15f` | before the system/user split (`4743e77`), with the `File: %s` / `Source: %s` placeholders at its end |
