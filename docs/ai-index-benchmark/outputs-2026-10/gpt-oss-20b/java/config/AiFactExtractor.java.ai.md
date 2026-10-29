### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:21Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact regex match counts for a source file.

#### Purpose
- Provides exact, language‑agnostic counts of code elements.
- Prepends these counts to AI summaries on oversize paths.

#### Type
- `final class AiFactExtractor` (utility, non‑instantiable).

#### Input
- `List<AiFactCounter> counters` (nullable, may contain null entries).
- `String source` (full source text).

#### Output
- `String` facts block ending with a blank line, or empty string if no counters.

#### Core logic
- Skip null counters or those with null label/pattern.
- For each valid counter, append `label: count` to a `StringBuilder`.
- Separate entries with `"; "`; prepend header and suffix newline.
- `countMatches` compiles the pattern and counts non‑overlapping matches via `Matcher.find()`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – ensure counters are well‑formed.

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if a counter lacks label/pattern or has an invalid regex.

#### Concurrency
- Stateless; thread‑safe.
