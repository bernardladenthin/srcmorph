### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:05Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Produces exact whole-source regex counts for configured facts, formatted for inclusion alongside AI-generated summaries.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from `AiFactCounter` labels and regex patterns.
- Validates configured counters and patterns before use.

#### Type
- `public final class AiFactExtractor` — final utility class; not instantiable.
- No notable type annotations.

#### Input
- `factsBlock(counters, source)` receives a nullable list of `AiFactCounter` values and full source text.
- `validate(counters)` receives a nullable list of `AiFactCounter` values.

#### Output
- `factsBlock` returns formatted `String`, or `""` if counters are null, empty, or contribute no entries.
- Public constants define the facts header and output separators.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- For each remaining counter, counts non-overlapping regex matches across the full source.
- Formats entries as `label: count`, separated by `; `; wraps them with the facts header and trailing blank line.
- `validate` checks non-null counters for a label, pattern, and compilable regex.
- `countMatches` compiles each regex and increments a count for every `Matcher.find()` match.

#### Public API
- `FACTS_HEADER` — facts block header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — facts entry separator.
- `FACTS_SUFFIX` — trailing blank line.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds exact facts block.
- `validate(List<AiFactCounter>) -> void` — validates counters and regex patterns.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; wraps `PatternSyntaxException`.
- `factsBlock` skips null counters and null labels or patterns.
- A null `source` is not explicitly handled and can fail during regex matching.

#### Concurrency
- Stateless and performs no I/O; no mutable shared state or synchronization.
