### AiFactExtractor.java
- H: 1.0
- C: C5F253BD
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:45:15Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 1; field declarations (w/ modifier): 4

> Generates a deterministic, language‑agnostic facts block summarizing exact regex‑based counts from a source file.

#### Purpose
- Computes exact counts of patterns for AI summaries.
- Prepends facts to AI body for oversize paths.

#### Type
- `final class AiFactExtractor`

#### Input
- `List<AiFactCounter> counters` (may be null/empty).
- `String source` (full source text).

#### Output
- `String` facts block ending with a blank line, or empty string if no counters.

#### Core logic
- Iterate counters; skip null, missing label/pattern.
- For each valid counter, count non‑overlapping regex matches in `source`.
- Append `label: count` entries separated by `"; "`; prepend header and suffix.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – check counters for nulls and valid regexes.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` if counter label/pattern missing or regex invalid.

#### Concurrency
- Stateless; thread‑safe.
