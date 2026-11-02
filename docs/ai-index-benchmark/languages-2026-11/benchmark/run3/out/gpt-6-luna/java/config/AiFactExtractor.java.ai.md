### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:15Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact whole-source regex counts for configured facts and formats them for inclusion with AI-generated summaries.

#### Purpose
- Computes deterministic, language-agnostic counts from the full source text.
- Formats nonempty counts as a facts block; validates configured fact counters.

#### Type
- `public final class AiFactExtractor` — final utility class.
- Not instantiable; no inheritance or implemented interfaces.

#### Input
- `factsBlock(List<AiFactCounter>, String)` — nullable counter list and source text.
- `validate(List<AiFactCounter>)` — nullable list of configured `AiFactCounter` values.
- Reads each counter’s label and regex pattern.

#### Output
- Returns a formatted facts block ending in a blank line, or `""` when no entries are produced.
- Validation returns no value; invalid counter configuration throws `IllegalArgumentException`.
- Exposes four public formatting constants.

#### Core logic
- `factsBlock` returns `""` for a null or empty list; skips null counters and counters with null labels or patterns.
- Counts each pattern’s non-overlapping matches across the entire source using `Matcher.find()`.
- Formats entries as `label: count`, separated by `; `, with the exact whole-file header and trailing blank line.
- `validate` skips null lists and null counters; rejects missing labels or patterns and compiles each regex to detect syntax errors.

#### Public API
- `FACTS_HEADER` — exact-count facts block header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — facts entry separator.
- `FACTS_SUFFIX` — separator before following content.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats source counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter` — provides labels and regex patterns.
- `java.util.List` — counter collection.
- `java.util.regex.Pattern`, `Matcher`, `PatternSyntaxException` — regex compilation and matching.
- `org.jspecify.annotations.Nullable` — nullable counter-list annotations.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; wraps `PatternSyntaxException` as the cause.
- `factsBlock` skips null counters, labels, and patterns.
- A null source is not checked and fails during regex matching.

#### Concurrency
- Stateless utility; no mutable shared state or synchronization.
