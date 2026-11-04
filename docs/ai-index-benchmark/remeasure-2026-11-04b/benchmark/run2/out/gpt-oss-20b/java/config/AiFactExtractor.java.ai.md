### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:01Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Provides exact, whole‑file fact counts for AI summaries.
- Validates fact counter configurations.

#### Type
- `public final class AiFactExtractor` – utility class, no instances.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts block string or empty string.
- `validate` throws `IllegalArgumentException` on invalid counters.
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`:
  - Skip null counters or those with null label/pattern.
  - Append `ENTRY_SEPARATOR` between entries.
  - Append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.
  - Prepend `FACTS_HEADER`, append `FACTS_SUFFIX` if any entries.
- `validate`:
  - For each counter, check non‑null label and pattern.
  - Compile pattern with `Pattern.compile`; catch `PatternSyntaxException` and wrap in `IllegalArgumentException`.
- `countMatches`:
  - Compile pattern, create `Matcher`, iterate `matcher.find()` counting matches.

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
- `IllegalArgumentException` thrown by `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` caught and rethrown as `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; no shared mutable state.
