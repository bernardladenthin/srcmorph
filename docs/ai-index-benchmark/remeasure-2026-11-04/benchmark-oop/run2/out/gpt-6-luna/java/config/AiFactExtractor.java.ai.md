### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:35:33Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Produces exact whole-source counts for configured regex-based facts and formats them for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` counts configured source-code facts independently of AI summarization.
- Formats nonempty counts as a facts line followed by a blank line.

#### Type
- `public final class AiFactExtractor` — final utility class; no supertypes declared.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` — nullable counter list and complete source text.
- `validate(List<AiFactCounter> counters)` — nullable list of `AiFactCounter` configuration entries.
- `countMatches(String pattern, String source)` — regex and text to search.

#### Output
- `factsBlock` returns `""` if counters are null, empty, or yield no valid entries; otherwise returns the header, `label: count` entries, and trailing blank line.
- `validate` returns no value; rejects counters with missing labels or patterns and invalid regexes.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- For each remaining counter, compiles its pattern and counts non-overlapping matches across the full source using repeated `Matcher.find()`.
- Joins entries with `"; "` and formats them using the fixed header, `": "` label/count separator, and `"\n\n"` suffix.
- `validate` skips null lists and null counters; requires non-null labels and patterns, then compiles each pattern to check syntax.

#### Public API
- `FACTS_HEADER: String` — exact whole-file facts prefix.
- `LABEL_COUNT_SEPARATOR: String` — label/count separator.
- `ENTRY_SEPARATOR: String` — consecutive-entry separator.
- `FACTS_SUFFIX: String` — facts/body separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — build formatted facts block.
- `validate(List<AiFactCounter>) -> void` — validate counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a non-null counter missing its label or pattern, or for an invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips null counters and counters with null labels or patterns.
- `countMatches` compiles each pattern; it does not catch regex compilation errors.
- `counters` is nullable in both public methods; `source` is not annotated nullable.
