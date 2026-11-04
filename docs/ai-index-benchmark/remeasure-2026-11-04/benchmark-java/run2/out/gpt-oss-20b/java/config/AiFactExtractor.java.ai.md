### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:49Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates a deterministic “facts” block summarizing exact regex match counts for AI summaries.

#### Purpose
- Provides exact, language‑agnostic counts of source elements.
- Prepends counts to AI output on oversize paths.

#### Type
`final class AiFactExtractor` – utility class, no instances.

#### Input
- `List<AiFactCounter> counters` (nullable, may contain nulls).
- `String source` – full source text.

#### Output
- `String` facts block ending with a blank line, or empty string if no counters.

#### Core logic
- `factsBlock`: iterate counters, skip null or missing label/pattern, append `label: count` separated by `; `. Count via `countMatches`. Prefix with header and suffix.
- `validate`: ensure each counter has non‑null label/pattern; compile pattern to catch syntax errors; throw `IllegalArgumentException` on failure.
- `countMatches`: compile regex, iterate `Matcher.find()` to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` on missing label/pattern or invalid regex.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables.
