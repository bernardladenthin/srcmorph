### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:44Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic "facts" block from a list of `AiFactCounter`s.

#### Purpose
- Generates a facts block for AI summaries.
- Validates fact counters.
- Counts non-overlapping regex matches in source text.

#### Type
- `public final class AiFactExtractor`
- `@Nullable` annotations from `org.jspecify.annotations`

#### Input
- `counters`: `@Nullable List<AiFactCounter>`
- `source`: `String`

#### Output
- `factsBlock`: `String` (facts block ending in a blank line)
- `validate`: `void` (no output)

#### Core logic
- `factsBlock`: Iterates over counters, compiles patterns, counts matches, and constructs facts block.
- `validate`: Iterates over counters, checks for null label/pattern, compiles patterns, and throws exceptions on invalid regex.
- `countMatches`: Compiles pattern, matches against source, and counts non-overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String`: Builds facts block.
- `validate(List<AiFactCounter>) -> void`: Validates fact counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown when a counter is missing its label/pattern or has an invalid regex.

#### Concurrency
- Not thread-safe; uses mutable `StringBuilder`.
