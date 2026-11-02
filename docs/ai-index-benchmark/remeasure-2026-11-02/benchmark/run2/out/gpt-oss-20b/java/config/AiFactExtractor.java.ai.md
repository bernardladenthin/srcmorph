### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:11Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates a deterministic “facts” block summarizing exact regex match counts for AI summaries.

#### Purpose
- Provides exact, language‑agnostic counts of source elements.
- Prepends counts to AI output on oversize paths.

#### Type
`final class AiFactExtractor` – utility class, no instances.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, full source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted string or empty string.
- `validate` throws `IllegalArgumentException` on misconfiguration.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip nulls, build `label: count` entries separated by `; `, prepend header, append suffix.
- `validate`: ensure non‑null label/pattern, compile pattern to detect syntax errors, throw detailed `IllegalArgumentException`.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counter list.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless utility; thread‑safe.
