# Profiles

A profile is one directory holding everything srcmorph's recognition depends on besides the model:
the prompts and the exact fact counters, one file per key. A configuration names the directory once
(`profileDirectory`), and its rules name only keys: a rule's `promptKey` reads
`<profileDirectory>/<key>.md`, its `factsKey` reads `<profileDirectory>/<key>.tsv`. No prompt text and
no regular expression is repeated in a configuration, so a profile can be copied, versioned and
handed on as a whole.

[`v2/`](v2/) is the current profile:

| File | Key | For |
|---|---|---|
| [`v2/file-body-java.md`](v2/file-body-java.md) | `promptKey: file-body-java` | one Java source file |
| [`v2/file-body-sql.md`](v2/file-body-sql.md) | `promptKey: file-body-sql` | one SQL file, as schema |
| [`v2/file-body-fallback.md`](v2/file-body-fallback.md) | `promptKey: file-body-fallback` | any other source file |
| [`v2/package-body.md`](v2/package-body.md) | `promptKey: package-body` | a package, from its file summaries |
| [`v2/project-body.md`](v2/project-body.md) | `promptKey: project-body` | the project overview, from the package leads |
| [`v2/java-facts.tsv`](v2/java-facts.tsv) | `factsKey: java-facts` | exact counts in front of every Java summary |
| [`v2/sql-facts.tsv`](v2/sql-facts.tsv) | `factsKey: sql-facts` | exact counts in front of every SQL summary |

## Prompt files

A `.md` file is the **system prompt** of its key, sent verbatim: no placeholders, no license header
(it would reach the model). srcmorph sends the file or package name and the source (or the child
summaries) as a separate user message. Each file starts with a domain lead line, then fixed `####`
sections (Purpose, Type, Input, Output, Core logic, Public API, Dependencies, Exceptions, Concurrency
for Java; Schema, Reads, Writes, Relationships, Routine logic for SQL; Constants and configuration for
other files), with the rules against code fences, `###` headings and invented names, and the "index
only what is literally present" guard for truncated sources.

## Fact files

A `.tsv` file holds one counter per line, its label and a Java regular expression separated by a tab;
lines starting with `#` are comments. srcmorph counts each pattern's matches over the whole source
and writes them as one line before the AI summary, for example
`**Facts (exact, whole file):** INSERT statements: 5; VALUES rows (approx): 33; tables: 2; ...`. A
sampled or chunked summary cannot count reliably, these lines can. Every pattern is compiled when the
run starts, so a broken one fails it with the file and line.

The counters are regex heuristics, and their labels say so where they are approximate. The tests read
these files, not copies: `SqlFactPatternsTest` pins every SQL counter on PostgreSQL, MySQL and T-SQL
forms (including the known over- and undercount of `VALUES rows (approx)`), and
`JavaStructureFactPatternsTest` the boundary of the Java method, constructor and field counts.

| `sql-facts` | Counts |
|---|---|
| `INSERT statements` | `INSERT INTO` at the start of a line |
| `VALUES rows (approx)` | the row tuples of `VALUES` lists, so a multi-row `INSERT` counts each row; rows an `INSERT ... SELECT` reads from a table are not seen |
| `tables`, `views`, `indexes` | `CREATE` statements, also `TEMP`/`UNLOGGED` tables, `MATERIALIZED` and `OR REPLACE`/`OR ALTER` views, `UNIQUE`/`CLUSTERED` indexes |
| `functions/procedures`, `triggers` | `CREATE [OR REPLACE / OR ALTER] FUNCTION / PROCEDURE / PROC / TRIGGER`, also MySQL's `DEFINER=` |
| `types` | `CREATE TYPE` (enums, composite types) |
| `ALTER TABLE` | every `ALTER TABLE` statement |

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

Who reads `v2/`:

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml)
  (`<ai.profileDir>`, overridable with `-Dai.profileDir=`);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--profile v2` by default);
- the examples under [`examples/`](../examples/) (`profileDirectory: "../profiles/v2"`, or
  `"../../profiles/v2"` one level deeper), so the CI smoke of the user-built jar reads it too;
- the plugin's integration-test fixture, [`.github/plugin-it/pom.xml`](../.github/plugin-it/pom.xml),
  which also checks that the `java-facts` counts reach the summaries.

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

The next profile goes into `v3/` with a file for each key, is measured with `run.py --profile v3`,
and replaces `v2/` when it wins; the configurations above then switch to it, and `v2/` goes. Only the
current profile is kept here, the earlier prompt sets are in the history (`git show <commit>:<path>`
prints one; the directory was called `prompts/` then):

| Set | Where | What |
|---|---|---|
| v1 | `prompts/v1/` in commit `6fe8335` | the set every run of the October 2026 model card used; before these files existed (commit `cd2e15f`) it was inline in the plugin pom and in the benchmark's `prompts.json`, where the pom's CDATA added a leading line break and a trailing line break plus 36 spaces |
| the June 2026 experiment, then also called v2 | `prompts/v2/` in commit `cd2e15f` | trivial-file omission, JavaBean accessor grouping, a "no invented keywords" ban, single-child package pass-through, more brevity; not adopted, because code-specialized models started wrapping sections in code fences ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2") |
| v1 as the June 2026 benchmark ran it | `prompts/archive/2026-06/v1/` in commit `cd2e15f` | before the system/user split (`4743e77`), with the `File: %s` / `Source: %s` placeholders at its end |
