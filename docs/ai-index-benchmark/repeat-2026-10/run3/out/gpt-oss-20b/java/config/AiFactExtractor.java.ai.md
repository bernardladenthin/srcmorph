### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:51:57Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from regex counters for a source file.

#### Purpose
- Provides exact, whole‑file fact counts for AI summaries.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` (public, no inheritance, no interfaces).

#### Input
- `factsBlock(List<AiFactCounter>, String)` – list of counters, source text.
- `validate(List<AiFactCounter>)` – list of counters.
- `countMatches(String, String)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts block string or empty string.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip nulls, build `label: count` entries separated by `; `, prepend header, append suffix.
- `validate`: check non‑null label/pattern, compile pattern, throw on syntax error.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

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
- `IllegalArgumentException` in `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless utility class; thread‑safe.
