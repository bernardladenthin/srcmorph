### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:38:38Z
- G: 
- A: 0.0.0
- X: file
---
Preparing a dense, structured markdown summary of the AiFactExtractor.java source file.

The class is final. It exposes four public static constants and two public methods: factsBlock and validate. The latter computes a deterministic facts block from AiFactCounter regex matches over source text.

The class also has a private constructor and a private countMatches helper. Six public members total: four constants and two methods.> Builds an exact, model-free facts line of regex match counts over a whole source file for AI summaries.

#### Purpose
- `AiFactExtractor` turns configured `AiFactCounter` regexes into a deterministic `label: count` facts block prepended to an oversize AI summary.
- Language-agnostic whole-file counts (rows, tables, boolean fields, functions, and similar) that chunked or sampled AI text cannot reliably produce.

#### Type
- `public final class AiFactExtractor`; no superclass or interfaces; not instantiable (private no-arg constructor).
- Static utility; no instance state. `@Nullable` on `List<AiFactCounter>` parameters.

#### Input
- `factsBlock`: `@Nullable List<AiFactCounter> counters` and full `String source`.
- `validate`: `@Nullable List<AiFactCounter> counters`.
- Each `AiFactCounter` supplies `getLabel()` and `getPattern()`; patterns are compiled with `Pattern.compile`.
- No files, streams, or config I/O.

#### Output
- `factsBlock` returns `""` when counters are null, empty, or all skipped; otherwise `FACTS_HEADER` + joined `label: count` entries + `FACTS_SUFFIX` (`\n\n`).
- `validate` returns void; fails the build via `IllegalArgumentException` on bad counters.
- `countMatches` returns non-overlapping match count as `int`. No mutated fields.

#### Core logic
- Skip a null or empty counter list in `factsBlock` and return `""`.
- For each `AiFactCounter`, skip null counter, null label, or null pattern; append `ENTRY_SEPARATOR` between entries.
- Append `label` + `LABEL_COUNT_SEPARATOR` + `countMatches(pattern, source)`.
- `countMatches` compiles the regex, walks `Matcher.find()` until exhausted, and increments once per match (including zero-width).
- If every counter was skipped, return `""`; else prefix `**Facts (exact, whole file):** ` and suffix a blank line.
- `validate` treats null list as valid; skips null counters; requires non-null label and pattern; compiles each pattern and wraps `PatternSyntaxException`.

#### Public API
- `FACTS_HEADER -> String` — exact whole-file facts prefix.
- `LABEL_COUNT_SEPARATOR -> String` — separator between label and count.
- `ENTRY_SEPARATOR -> String` — separator between fact entries.
- `FACTS_SUFFIX -> String` — blank line after facts block.
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` — builds facts block or empty.
- `validate(@Nullable List<AiFactCounter>) -> void` — fail-fast check of fact counters.

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`
- `IllegalArgumentException`, `StringBuilder`

#### Exceptions / Errors
- `IllegalArgumentException` if a non-null counter lacks a label or pattern, or if the pattern is not compilable.
- `PatternSyntaxException` is caught and rethrown as `IllegalArgumentException` with label and cause.
- Null list, empty list, null counter, null label, and null pattern are skipped in `factsBlock` (empty result if nothing remains).
- Null list and null counters are allowed in `validate`; missing label or pattern is not.

#### Concurrency
- Pure static methods; no shared mutable state; private constructor. Each count compiles a fresh `Pattern`/`Matcher`.
