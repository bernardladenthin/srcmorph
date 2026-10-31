# Prompts

The prompts srcmorph's own configurations use, one file per prompt, so each text exists once in the
repository. A file is the **system prompt** of one prompt key, sent verbatim: no placeholders, no
license header (it would reach the model). srcmorph sends the file or package name and the source
(or the child summaries) as a separate user message.

[`v2/`](v2/) is the current set:

| File | Prompt key | For |
|---|---|---|
| [`v2/file-body-java.md`](v2/file-body-java.md) | `file-body-java` | one Java source file |
| [`v2/file-body-sql.md`](v2/file-body-sql.md) | `file-body-sql` | one SQL file, as schema |
| [`v2/file-body-fallback.md`](v2/file-body-fallback.md) | `file-body-fallback` | any other source file |
| [`v2/package-body.md`](v2/package-body.md) | `package-body` | a package, from its file summaries |
| [`v2/project-body.md`](v2/project-body.md) | `project-body` | the project overview, from the package leads |

Each file starts with a domain lead line, then fixed `####` sections (Purpose, Type, Input, Output,
Core logic, Public API, Dependencies, Exceptions, Concurrency for Java; Schema, Reads, Writes,
Relationships, Routine logic for SQL; Constants and configuration for other files), with the rules
against code fences, `###` headings and invented names, and the "index only what is literally
present" guard for truncated sources.

## v2 against v1

v2 is v1 with targeted additions for the names v1 lost in the October 2026
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

## Who reads them

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml)
  (`<promptDirectory>${project.basedir}/../prompts</promptDirectory>`);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--prompts v2` by default);
- the examples under [`examples/`](../examples/) (`promptDirectory: "../prompts"`, or `"../../prompts"`
  one level deeper), so the CI smoke of the user-built jar reads them too;
- the plugin's integration-test fixture, [`.github/plugin-it/pom.xml`](../.github/plugin-it/pom.xml).

A configuration names a file with `templateFile` instead of `template`; a relative path resolves
against `promptDirectory` (without one, against the working directory), an absolute one is always
used as it is. To change a prompt for your own project, copy the files and point `promptDirectory`
at the copy.

## A new version, and the earlier ones

The next set goes into `v3/` with a file for each of the five keys, is measured with
`run.py --prompts v3`, and replaces `v2/` when it wins; the configurations above then switch to it,
and `v2/` goes. Only the current set is kept here, the earlier ones are in the history
(`git show <commit>:<path>` prints one):

| Set | Where | What |
|---|---|---|
| v1 | `prompts/v1/` in commit `6fe8335` | the set every run of the October 2026 model card used; before these files existed (commit `cd2e15f`) it was inline in the plugin pom and in the benchmark's `prompts.json`, where the pom's CDATA added a leading line break and a trailing line break plus 36 spaces |
| the June 2026 experiment, then also called v2 | `prompts/v2/` in commit `cd2e15f` | trivial-file omission, JavaBean accessor grouping, a "no invented keywords" ban, single-child package pass-through, more brevity; not adopted, because code-specialized models started wrapping sections in code fences ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2") |
| v1 as the June 2026 benchmark ran it | `prompts/archive/2026-06/v1/` in commit `cd2e15f` | before the system/user split (`4743e77`), with the `File: %s` / `Source: %s` placeholders at its end |
