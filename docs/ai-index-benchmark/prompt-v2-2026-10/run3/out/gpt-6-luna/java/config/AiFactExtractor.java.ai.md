### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:57:39Z
- G: 
- A: 0.0.0
- X: file
---
> Counts configured regex-based facts across complete source text and formats exact counts for inclusion in AI summaries.

#### Purpose
- Produces a deterministic facts block from configured `AiFactCounter` entries.
- Validates counter labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — final utility class; not instantiable.

#### Input
- `factsBlock(counters, source)` receives a nullable list of fact counters and full source text.
- `validate(counters)` receives a nullable list of fact counters.
- Each counter supplies a label and regex pattern.

#### Output
- `factsBlock` returns an empty string when no entries can be reported; otherwise returns a formatted facts header, `label: count` entries, and trailing blank line.
- `validate` returns no value; throws for missing labels, missing patterns, or invalid regexes.

#### Core logic
- `factsBlock` skips null lists, empty lists, null counters, and counters with null labels or patterns.
- Counts non-overlapping regex matches over the entire source using `Matcher.find()`.
- Joins entries with `"; "` and prefixes `**Facts (exact, whole file):** `.
- `validate` skips null lists and null counters; checks each remaining label and pattern, then compiles the pattern to detect syntax errors.
- `countMatches` compiles each regex and increments once per match, including zero-width matches.

#### Public API
- `FACTS_HEADER` — facts-line header constant.
- `LABEL_COUNT_SEPARATOR` — label/count separator constant.
- `ENTRY_SEPARATOR` — entry separator constant.
- `FACTS_SUFFIX` — trailing blank-line constant.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds formatted facts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a null label, null pattern, or invalid regex; invalid-pattern errors retain the `PatternSyntaxException` cause.
- `countMatches` assumes non-null source; null source causes a runtime null failure when matching.

#### Concurrency
- Stateless utility with no mutable instance state or I/O.
