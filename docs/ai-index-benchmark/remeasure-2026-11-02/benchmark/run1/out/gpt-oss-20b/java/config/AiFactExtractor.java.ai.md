### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:48Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Computes deterministic, language‑agnostic fact counts from regex counters for AI summaries.

#### Purpose
- Generates a facts block for AI output.
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
- `factsBlock`: iterate counters, skip nulls, append label and match count separated by constants; prepend header, append suffix.
- `validate`: for each counter, ensure non‑null label/pattern, compile pattern to detect syntax errors.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

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
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
