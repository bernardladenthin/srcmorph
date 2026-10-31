### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:02:12Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from source text using configured regex counters.

#### Purpose
- Provides exact, whole‑file counts for AI fact blocks.
- Validates counter configurations.

#### Type
`public final class AiFactExtractor` – class, final, no superclass, no interfaces.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty string.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip nulls or missing label/pattern, append `label: count` entries separated by `; `, prepend header, append suffix.
- `validate`: for each counter, ensure non‑null label/pattern, compile pattern, throw `IllegalArgumentException` on failure.
- `countMatches`: compile pattern, iterate matcher, increment count for each non‑overlapping match.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` on null label, null pattern, or invalid regex.
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- All methods are static and stateless; thread‑safe.
