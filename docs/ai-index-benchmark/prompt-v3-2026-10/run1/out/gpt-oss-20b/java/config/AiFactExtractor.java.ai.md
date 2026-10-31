### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:28Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Generates a facts block for AI summaries.
- Validates fact counter configurations.

#### Type
`final class AiFactExtractor` – utility class, no instances.

#### Input
- `factsBlock(List<AiFactCounter>, String)` – list of counters, source text.
- `validate(List<AiFactCounter>)` – list of counters.
- `countMatches(String, String)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- Skip null counters or those with null label/pattern.
- Append `label: count` entries separated by `; `.
- Prepend header and append suffix.
- Validate each counter’s label, pattern, and regex compilability.
- Count non‑overlapping regex matches using `Matcher.find()`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – check counter validity.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless; thread‑safe.
