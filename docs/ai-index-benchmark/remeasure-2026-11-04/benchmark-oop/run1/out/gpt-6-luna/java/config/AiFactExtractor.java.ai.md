### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:02Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Produces exact whole-source counts for configured regex-based facts and formats them for inclusion in AI summaries.

#### Purpose
- `AiFactExtractor` counts configured facts such as rows, tables, fields, or functions across complete source text.
- Formats counts as a facts block, or validates counter configuration before use.

#### Type
- `public final class AiFactExtractor` — class; utility class with a private constructor.

#### Input
- `factsBlock(@Nullable List<AiFactCounter> counters, String source)` consumes counters and the full source text.
- `validate(@Nullable List<AiFactCounter> counters)` consumes configured counters.

#### Output
- `factsBlock` returns a formatted `String` ending in a blank line, or `""` when counters are absent or no valid entries remain.
- `validate` produces no value; throws `IllegalArgumentException` for missing labels or patterns and invalid regexes.
- Public constants define the facts header and formatting separators.

#### Core logic
- `factsBlock` returns `""` for null or empty lists; skips null counters and counters with null labels or patterns.
- For each remaining counter, compiles its regex, counts non-overlapping matches across the source, and appends `label: count`.
- Joins entries with `; ` and wraps them with the exact-facts header and trailing blank line.
- `validate` permits a null list and skips null entries; rejects counters with null labels or patterns, and wraps `PatternSyntaxException` in `IllegalArgumentException`.
- `countMatches` repeatedly calls `Matcher.find()` and increments once per match.

#### Public API
- `FACTS_HEADER: String` — facts-line header.
- `LABEL_COUNT_SEPARATOR: String` — label and count separator.
- `ENTRY_SEPARATOR: String` — separator between entries.
- `FACTS_SUFFIX: String` — trailing blank line.
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` — format whole-source fact counts.
- `validate(@Nullable List<AiFactCounter>) -> void` — check counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` cause.
- `factsBlock` skips null counters and null labels or patterns.
- `source` is not declared nullable; no explicit null handling is present.

#### Concurrency
- Pure computation with no I/O or mutable shared state.
