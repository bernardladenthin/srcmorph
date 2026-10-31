# Prompts

Every prompt srcmorph's own configurations use, one file per prompt, so each text exists once in
the repository. A file is the **system prompt** of one prompt key, sent verbatim: no placeholders,
no license header (it would reach the model). srcmorph sends the file or package name and the
source (or the child summaries) as a separate user message.

| Set | Keys | Status | Measured in |
|---|---|---|---|
| [`v1/`](v1/) | `file-body-java`, `file-body-sql`, `file-body-fallback`, `package-body`, `project-body` | **current**, used by every configuration below | the October 2026 [model card](../docs/ai-index-benchmark/MODEL-CARD.md) (all runs) |
| [`v2/`](v2/) | `file-body-java`, `package-body` | experimental | June 2026, [COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2" |
| [`archive/2026-06/v1/`](archive/2026-06/v1/) | the five v1 keys | historical | the June 2026 benchmark ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md)) |

## Who reads them

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml)
  (`<promptDirectory>${project.basedir}/../prompts</promptDirectory>`, v1 and v2);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--prompts v1` by default; any set with all five keys works);
- the examples under [`examples/`](../examples/) (`promptDirectory: "../prompts"`, or `"../../prompts"`
  one level deeper), so the CI smoke of the user-built jar reads them too;
- the plugin's integration-test fixture, [`.github/plugin-it/pom.xml`](../.github/plugin-it/pom.xml).

A configuration names a file with `templateFile` instead of `template`; a relative path resolves
against `promptDirectory` (without one, against the working directory), an absolute one is used as
it is. To change a prompt for your own project, copy the files and point `promptDirectory` at the
copy.

## The sets

**`v1/`** is the production prompt set: each file prioritizes a domain lead line, then fixed
`####` sections (Purpose, Type, Input, Output, Core logic, Public API, Dependencies, Exceptions,
Concurrency for Java; Schema, Reads, Writes, Relationships, Routine logic for SQL), with the rules
against code fences, `###` headings and invented names, and the "index only what is literally
present" guard for truncated sources. Until these files existed the texts were inline in the plugin
pom and in the benchmark's `prompts.json`, and the October 2026 runs got them with what the pom's
CDATA added around them: a leading line break, and a trailing line break plus the 36 spaces before
`]]>`. The files drop that whitespace; the text is otherwise byte-identical.

**`v2/`** is the experiment of June 2026 (branch `claude2026_06_26_00`): a trivial-file omission
rule, JavaBean accessor grouping, an explicit "no invented keywords" ban, single-child package
pass-through and a tighter brevity instruction. It tightened the output and cut accessor bloat, but
code-specialized models started wrapping sections in code fences, so it was not adopted
(COMPARISON.md). The June text itself was not kept; these files are v2 as it has been in the plugin
pom since the system/user prompt split (`6aaa6fd`).

**`archive/2026-06/v1/`** is v1 as the June 2026 benchmark ran it (`4743e77`), before the
system/user split: the prompt and the source went in one message, filled in through the
`File: %s` / `Source: %s` placeholders at the end. Current srcmorph sends a `%s` literally, so these
files are a record, not a set to run.

A new set goes into its own directory (`v3/`, …) with a file for each of the five keys, and is
measured with `run.py --prompts v3` before a configuration switches to it.
