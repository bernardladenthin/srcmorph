### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:29Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Computes exact whole-source counts for configured regex facts and formats them for inclusion with an AI summary.

#### Purpose
- Produces a deterministic facts block from configured `AiFactCounter` labels and regex patterns.
- Validates fact-counter configuration so missing fields and invalid regexes fail fast.

#### Type
- `public final class AiFactExtractor` — utility class; private constructor; no superclass or implemented interfaces.
- Uses `@Nullable` for the counter-list parameters.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes optional counters and the full source text.
- `validate(List<AiFactCounter>)` consumes optional counters and their labels and regex patterns.

#### Output
- `factsBlock` returns formatted `label: count` entries, or `""` if no entries qualify.
- Counts matches across the full source; generated facts end with a blank line.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` returns empty for a null or empty list.
- Skips null counters and counters with null labels or patterns.
- Compiles each pattern and counts non-overlapping matches using repeated `Matcher.find()`.
- Joins entries with `; ` and wraps them with the exact-facts header and `\n\n` suffix.
- `validate` skips null lists and null counters; rejects missing labels or patterns and wraps invalid regex syntax in `IllegalArgumentException`.

#### Public API
- `FACTS_HEADER` — exact-facts header prefix.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — trailing blank-line separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — build exact facts block.
- `validate(List<AiFactCounter>) -> void` — validate counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a counter with a null label, null pattern, or invalid regex; invalid-pattern errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips counters with null labels or patterns.
- `countMatches` compiles patterns; invalid regex syntax can propagate as `PatternSyntaxException` when called through `factsBlock`.

#### Concurrency
- Stateless utility with no mutable shared state; each call creates its own matcher and builder.
