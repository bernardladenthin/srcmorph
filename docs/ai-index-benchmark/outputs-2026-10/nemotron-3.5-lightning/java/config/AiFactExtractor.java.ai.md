### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T22:57:58Z
- G: 
- A: 0.0.0
- X: file
---
> Generates exact whole-file regex counts as a facts block for AI documentation.

#### Purpose
- Computes exact, whole-file regex-based fact counts for AI-integrated source documentation.

#### Type
- public final class AiFactExtractor
- No extends/impls beyond Object; utility class with private constructor
- Notable annotation: @Nullable on method parameters

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`: counters may be null or empty; source is full text to search
- `validate(List<AiFactCounter> counters)`: counters may be null (valid, nothing to check)
- Private utility constructor not instantiable
- Consumed parameters are the only inputs

#### Output
- `factsBlock` returns `String`: facts block ending with blank line, or `""` when no counters/reportable matches
- `validate` returns `void`, throws `IllegalArgumentException` on misconfigured counters
- `countMatches` returns `int`: match count (zero-width matches counted once per position)

#### Core logic
- Iterate `counters`; skip entries where counter, label, or pattern is null
- For each valid counter, compile its pattern and count non-overlapping matches across entire `source` via `countMatches`
- Append `label: count` entries separated by `"; "`, prefixed by `FACTS_HEADER`, suffixed by `FACTS_SUFFIX`
- Return empty string if no valid entries generated

#### Public API
- `factsBlock(final @Nullable List<AiFactCounter> counters, final String source) -> String` generates facts block from counters over source
- `validate(final @Nullable List<AiFactCounter> counters) -> void` validates counter labels and patterns; throws IllegalArgumentException on failure
- `FACTS_HEADER -> String` header prefix for facts line
- `LABEL_COUNT_SEPARATOR -> String` separator between label and its count
- `ENTRY_SEPARATOR -> String` separator between consecutive label: count entries
- `FACTS_SUFFIX -> String` suffix blank line after facts block

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter` (referenced type)

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` if a counter is missing a label, pattern, or has an invalid regex
- Null-handling: null counters, labels, or patterns are skipped in `factsBlock`; null yields empty string
- Invalid regex patterns cause build-fast failure via `IllegalArgumentException`

#### Concurrency
- No threading or synchronization; pure function with no shared mutable state; immutable constants
