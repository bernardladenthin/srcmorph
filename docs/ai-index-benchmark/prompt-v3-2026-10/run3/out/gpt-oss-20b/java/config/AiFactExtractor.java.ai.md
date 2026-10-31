### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:41Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from regex counters for a source file.

#### Purpose
- Generates a facts block for AI summaries.
- Validates fact counter configurations.

#### Type
`public final class AiFactExtractor` – utility class, no instances.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty string.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`: iterate counters, skip nulls, build `label: count` entries, prepend header, append suffix.
- `validate`: check non‑null label/pattern, compile pattern, throw on syntax errors.
- `countMatches`: compile pattern, iterate matcher, count `find()` occurrences.

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
- `IllegalArgumentException` from `validate` on missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables.
