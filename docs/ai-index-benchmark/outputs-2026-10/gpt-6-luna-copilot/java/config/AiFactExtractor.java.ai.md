### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:07Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact whole-source regex counts for configured facts and formats them for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured `AiFactCounter` entries.
- Validates counter labels and regex patterns before use.

#### Type
- `final` utility class; no superclass or implemented interfaces.
- `@Nullable` annotates the counter-list parameters.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes counters and full source text.
- `validate(List<AiFactCounter>)` consumes counters; accepts a null list.

#### Output
- `factsBlock` returns a formatted string ending in a blank line, or `""` when no facts are reportable.
- Public constants define the header and formatting separators.

#### Core logic
- Skips null counters or counters with null labels or patterns when building facts.
- Counts non-overlapping regex matches over the full source with `Matcher.find()`.
- Formats each entry as `label: count`, joins entries with `; `, and adds the facts header and suffix.
- Validation checks labels and patterns, compiling each pattern to detect invalid regex syntax.

#### Public API
- `FACTS_HEADER` — facts-line header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — facts/body separator.
- `factsBlock(counters, source) -> String` — builds formatted facts.
- `validate(counters) -> void` — checks counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; wraps `PatternSyntaxException`.
- `factsBlock` compiles patterns during counting and may propagate `PatternSyntaxException`.
- Null source is not handled explicitly.
