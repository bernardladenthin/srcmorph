### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:50:34Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic facts block from a list of AiFactCounter configurations.

#### Purpose
- Computes a deterministic, language-agnostic facts block from a list of AiFactCounter configurations, providing exact whole-file counts for AI processing.

#### Type
- final class; utility class; not instantiable (private constructor).
- No extends / implements; no generics.
- Annotations: none declared.

#### Input
- `counters`: List of AiFactCounter (may be null or empty).
- `source`: String — the full source text the regexes are counted over.
- Consumed fields: each counter's label and pattern.

#### Output
- Returns a facts block string ending in a blank line, or empty string when there are no counters.
- Side effect: prepends FACTS_HEADER and appends FACTS_SUFFIX.

#### Core logic
- Iterates over counters; skips nulls or counters with null label/pattern.
- Appends `label: count` entries separated by `; `.
- Calls `countMatches` to compute non-overlapping regex matches of each counter's pattern across the whole source.
- Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the built entry string.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String`
- `validate(List<AiFactCounter> counters) -> void`
- `countMatches(String pattern, String source) -> int` (private)

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` thrown from `validate` if a counter is missing its label or pattern, or has an invalid regex.
- `PatternSyntaxException` caught and rethrown as `IllegalArgumentException`.

#### Concurrency
- Pure function; no shared mutable state; thread-safe.
