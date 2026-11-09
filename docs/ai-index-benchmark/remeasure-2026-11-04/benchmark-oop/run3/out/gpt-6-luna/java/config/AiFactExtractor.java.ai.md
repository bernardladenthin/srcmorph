### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:36Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Computes exact whole-source counts for configured regex facts, producing a facts block to accompany AI-generated summaries.

#### Purpose
- `AiFactExtractor` counts configured regex matches across source text and formats the counts as a facts block.
- Validates fact-counter labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — class; utility class with a private constructor.

#### Input
- `factsBlock(@Nullable List<AiFactCounter> counters, String source)` — configured counters and full source text.
- `validate(@Nullable List<AiFactCounter> counters)` — counters to validate.
- Reads each counter’s label and pattern.

#### Output
- `factsBlock(...)` returns `String`: formatted facts block ending in a blank line, or `""` when no entries are available.
- `validate(...)` returns `void`; no state is mutated.

#### Core logic
- `factsBlock` returns `""` for null or empty counter lists.
- Skips null counters or counters with null labels or patterns.
- Counts non-overlapping regex matches over the full source using `Matcher.find()`.
- Formats entries as `label: count`, separated by `; `, prefixed by `**Facts (exact, whole file):** ` and suffixed by `\n\n`.
- `validate` skips null lists and null counters; rejects missing labels or patterns and patterns that fail compilation.

#### Public API
- `FACTS_HEADER: String` — facts block prefix.
- `LABEL_COUNT_SEPARATOR: String` — label/count separator.
- `ENTRY_SEPARATOR: String` — entry separator.
- `FACTS_SUFFIX: String` — facts/body separator.
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` — build exact facts block.
- `validate(@Nullable List<AiFactCounter>) -> void` — validate counter configuration.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label or pattern, or an invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips null counters and counters with null labels or patterns.
- `countMatches` compiles each pattern; invalid regexes can therefore throw `PatternSyntaxException` if validation was not performed first.

#### Concurrency
- Stateless and performs no I/O; no shared mutable state.
