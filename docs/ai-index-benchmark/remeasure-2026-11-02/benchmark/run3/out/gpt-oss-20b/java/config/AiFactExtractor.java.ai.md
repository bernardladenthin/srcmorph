### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:06Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates a deterministic “facts” block summarizing exact regex‑based counts from a source file for AI summaries.

#### Purpose
- Compute exact, language‑agnostic fact counts for AI summary oversize handling.

#### Type
`final class AiFactExtractor` – utility class, no instances, static methods only.

#### Input
- `factsBlock(List<AiFactCounter>, String)` – list of counters, source text.
- `validate(List<AiFactCounter>)` – list of counters.
- `countMatches(String, String)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty string.
- `validate` throws `IllegalArgumentException` on misconfiguration.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip nulls, append `label: count` entries separated by `; `. Count via `countMatches`. Prepend header, append suffix.
- `validate`: for each counter, ensure non‑null label/pattern; compile pattern to detect syntax errors; throw detailed `IllegalArgumentException`.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counter list.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables only.
