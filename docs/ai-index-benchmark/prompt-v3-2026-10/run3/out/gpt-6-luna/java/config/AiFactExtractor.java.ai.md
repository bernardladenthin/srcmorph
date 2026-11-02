### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:07Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source counts for configured regex-based facts and formats them for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` produces deterministic `label: count` facts from source text.
- Validates configured fact counters and regex patterns.

#### Type
- `AiFactExtractor` — `final` class; utility class with a private constructor.

#### Input
- `factsBlock(counters, source)` consumes a nullable list of `AiFactCounter` values and the full source text.
- `validate(counters)` consumes a nullable list of `AiFactCounter` values.

#### Output
- `factsBlock` returns a formatted facts block ending with a blank line, or `""` when there are no counters to report.
- `validate` returns no value; throws for invalid counter configuration.

#### Core logic
- `factsBlock` returns `""` for null or empty lists; skips null counters and counters with null labels or patterns.
- For each remaining counter, compiles its regex and counts non-overlapping matches across the source using `Matcher.find()`.
- Formats entries as `label: count`, separated by `; `, prefixed with `**Facts (exact, whole file):** `, and suffixed with two newlines.
- `validate` skips null lists and null counter entries; rejects counters with null labels or patterns and wraps regex syntax errors in `IllegalArgumentException`.

#### Public API
- `FACTS_HEADER` — facts block prefix.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — facts/body separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats exact fact counts.
- `validate(List<AiFactCounter>) -> void` — checks counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for null labels, null patterns, or invalid regex syntax; includes the `PatternSyntaxException` as cause for invalid patterns.
- `factsBlock` does not validate regexes; invalid patterns can throw `PatternSyntaxException`.
- Null source is not handled and can cause `NullPointerException` during matching.

#### Concurrency
- Stateless utility; no mutable shared state or synchronization.
