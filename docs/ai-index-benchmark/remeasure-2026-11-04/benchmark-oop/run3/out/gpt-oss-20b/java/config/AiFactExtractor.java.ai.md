### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:37Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Generates a facts block summarizing exact counts of language constructs.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` – utility class, no instances.

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
- `countMatches`: compile pattern, use `Matcher.find()` loop to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` – build facts block.
- `validate(List<AiFactCounter> counters) -> void` – validate counters.
- `FACTS_HEADER: String` – header prefix.
- `LABEL_COUNT_SEPARATOR: String` – label‑count separator.
- `ENTRY_SEPARATOR: String` – entry separator.
- `FACTS_SUFFIX: String` – suffix after facts block.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; no shared mutable state.
