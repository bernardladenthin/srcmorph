# Prompts

The prompts srcmorph's own configurations use, one file per prompt, so each text exists once in the
repository. A file is the **system prompt** of one prompt key, sent verbatim: no placeholders, no
license header (it would reach the model). srcmorph sends the file or package name and the source
(or the child summaries) as a separate user message.

[`v1/`](v1/) is the current set, measured by the October 2026
[model card](../docs/ai-index-benchmark/MODEL-CARD.md) over all its runs:

| File | Prompt key | For |
|---|---|---|
| [`v1/file-body-java.md`](v1/file-body-java.md) | `file-body-java` | one Java source file |
| [`v1/file-body-sql.md`](v1/file-body-sql.md) | `file-body-sql` | one SQL file, as schema |
| [`v1/file-body-fallback.md`](v1/file-body-fallback.md) | `file-body-fallback` | any other source file |
| [`v1/package-body.md`](v1/package-body.md) | `package-body` | a package, from its file summaries |
| [`v1/project-body.md`](v1/project-body.md) | `project-body` | the project overview, from the package leads |

Each file starts with a domain lead line, then fixed `####` sections (Purpose, Type, Input, Output,
Core logic, Public API, Dependencies, Exceptions, Concurrency for Java; Schema, Reads, Writes,
Relationships, Routine logic for SQL), with the rules against code fences, `###` headings and
invented names, and the "index only what is literally present" guard for truncated sources.

## Who reads them

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml)
  (`<promptDirectory>${project.basedir}/../prompts</promptDirectory>`);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--prompts v1` by default);
- the examples under [`examples/`](../examples/) (`promptDirectory: "../prompts"`, or `"../../prompts"`
  one level deeper), so the CI smoke of the user-built jar reads them too;
- the plugin's integration-test fixture, [`.github/plugin-it/pom.xml`](../.github/plugin-it/pom.xml).

A configuration names a file with `templateFile` instead of `template`; a relative path resolves
against `promptDirectory` (without one, against the working directory), an absolute one is always
used as it is. To change a prompt for your own project, copy the files and point `promptDirectory`
at the copy.

## A new version

The next set goes into `v2/` with a file for each of the five keys, is measured with
`run.py --prompts v2`, and replaces `v1/` when it wins; the configurations above then switch to it,
and `v1/` goes. Only the current set is kept here, the earlier ones are in the history:

- Until these files existed (commit `cd2e15f`) the v1 texts were inline in the plugin pom and in the
  benchmark's `prompts.json`, and the October 2026 runs got them with what the pom's CDATA added
  around them: a leading line break, and a trailing line break plus the 36 spaces before `]]>`. The
  files drop that whitespace; the text is otherwise byte-identical.
- The June 2026 experiment, then also called v2 (`file-body-java-v2`, `package-body-v2`: trivial-file
  omission, JavaBean accessor grouping, a "no invented keywords" ban, single-child package
  pass-through, more brevity), was not adopted: code-specialized models started wrapping sections in
  code fences ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2"). It is
  `prompts/v2/` in commit `cd2e15f`.
- v1 as the June 2026 benchmark ran it (`4743e77`), before the system/user split, with the
  `File: %s` / `Source: %s` placeholders at its end, is `prompts/archive/2026-06/v1/` in commit
  `cd2e15f`.

`git show cd2e15f:prompts/v2/file-body-java.md` prints one of them.
