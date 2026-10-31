### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:29Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source regex counts for configured facts and formats them for inclusion with an AI-generated summary.

#### Purpose
- `AiFactExtractor` produces deterministic fact counts from source text, independent of AI sampling or chunking.
- Formats nonempty counts as a facts header followed by a blank line.

#### Type
- `public final class AiFactExtractor` — final utility class; not instantiable.

#### Input
- `factsBlock(counters, source)` accepts a nullable list of `AiFactCounter` values and full source text.
- `validate(counters)` accepts a nullable list of `AiFactCounter` values.
- Reads each counter’s label and regex pattern.

#### Output
- `factsBlock` returns formatted `label: count` entries, or `""` when counters are null, empty, or yield no valid entries.
- `validate` returns no value; throws for invalid counter configuration.
- Exposes formatting constants for the facts header, separators, and suffix.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- Counts each counter’s non-overlapping regex matches across the full source using `Matcher.find()`.
- Joins entries with `"; "`, prefixes `**Facts (exact, whole file):** `, and appends `"\n\n"`.
- `validate` skips null lists and null counter elements; rejects missing labels or patterns and compiles each regex to check syntax.
- `countMatches` counts zero-width matches once per position, as provided by Java regex matching.

#### Public API
- `FACTS_HEADER` — facts-line header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — trailing blank-line separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — build formatted fact block.
- `validate(List<AiFactCounter>) -> void` — validate counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a counter with a null label, null pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips counters with null labels or patterns; a null `source` is not handled and can cause a `NullPointerException` during matching.

#### Concurrency
- Stateless utility with no mutable instance state or I/O.
