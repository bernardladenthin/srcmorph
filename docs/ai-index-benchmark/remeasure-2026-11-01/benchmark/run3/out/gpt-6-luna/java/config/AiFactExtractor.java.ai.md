### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:58:49Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact whole-source counts for configured regex facts, formatted for inclusion alongside AI-generated summaries.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured `AiFactCounter` entries.
- Validates fact-counter labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — final utility class; no superclass or implemented interfaces.
- No annotations.

#### Input
- `factsBlock(counters, source)` accepts a nullable list of counters and source text.
- `validate(counters)` accepts a nullable list of counters.
- Each counter supplies a label and regex pattern.

#### Output
- `factsBlock` returns formatted `label: count` entries, or `""` if none apply.
- Four public string constants define the header, separators, and trailing blank line.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` returns empty output for null or empty counter lists.
- Skips null counters and counters with null labels or patterns.
- Compiles each pattern and counts non-overlapping matches across the full source using `Matcher.find()`.
- Joins entries with `"; "` and wraps them with the facts header and `"\n\n"`.
- `validate` skips null lists and null entries; rejects missing labels or patterns and un-compilable regexes.

#### Public API
- `FACTS_HEADER` — facts-line header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — trailing blank line.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats exact counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter` — counter label and pattern.
- `List` — counter collection.
- `Pattern`, `Matcher`, `PatternSyntaxException` — regex compilation and matching.
- `Nullable` — nullable counter-list annotation.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips counters with null labels or patterns; a null source can fail during matching.

#### Concurrency
- Stateless utility with pure counting and formatting logic; no synchronization or shared mutable state.
