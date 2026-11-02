### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:01Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from regex counters for a source file.

#### Purpose
- Provides exact, whole‑file fact counts for AI summaries.
- Validates fact counter configurations.

#### Type
`public final class AiFactExtractor` – final class, no inheritance, no interfaces.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts block string or empty string.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip null or missing label/pattern, append `label: count` entries separated by `; `. Prepend header and suffix.
- `validate`: for each counter, ensure non‑null label/pattern, compile pattern, throw `IllegalArgumentException` on failure.
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables only.
