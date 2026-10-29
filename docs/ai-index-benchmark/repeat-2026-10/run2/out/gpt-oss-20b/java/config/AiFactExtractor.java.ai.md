### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:11:36Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language‑agnostic fact counts from source text for AI summaries.

#### Purpose
- Provides exact counts of language constructs via regex counters.
- Prepends a facts block to AI output on oversize paths.

#### Type
- `final class AiFactExtractor` (utility, no instances).

#### Input
- `List<AiFactCounter> counters` (nullable, may contain nulls).
- `String source` (full source text).

#### Output
- `String` facts block or empty string.
- No state mutation or I/O.

#### Core logic
- Iterate counters; skip null or missing label/pattern.
- Append `label: count` entries separated by `ENTRY_SEPARATOR`.
- Count matches with `countMatches` (non‑overlapping regex search).
- Prefix with `FACTS_HEADER`, suffix with `FACTS_SUFFIX`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – ensure counters are valid.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` in `validate` for missing label/pattern or invalid regex.
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless; thread‑safe.
