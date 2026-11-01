### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:04Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact counts of regex‑matched elements in source code for AI summaries.

#### Purpose
- Computes exact, whole‑file counts of language‑agnostic facts.
- Prepends a facts header to AI body on oversize paths.

#### Type
`public final class AiFactExtractor` – class, final, no superclass, no interfaces.

#### Input
- `List<AiFactCounter> counters` – may be null/empty; each counter supplies `label` and `pattern`.
- `String source` – full source text to search.

#### Output
- `String` facts block ending with a blank line, or empty string if no counters or all skipped.
- Side effect: none (pure computation).

#### Core logic
- If counters null/empty → return `""`.
- For each counter:
  - Skip if counter, label, or pattern is null.
  - Append `label: count` to `StringBuilder`, separating entries with `"; "`.
  - Count matches via `countMatches(pattern, source)` (non‑overlapping, zero‑width counted).
- If no entries → return `""`.
- Prefix with `FACTS_HEADER` and suffix with `FACTS_SUFFIX`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – ensure non‑null label/pattern and compilable regex.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables only.
