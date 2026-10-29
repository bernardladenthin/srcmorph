### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:34:03Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic facts block from a list of AiFactCounter configurations, enabling exact whole-file counts prepended to AI-generated content.

#### Purpose
- Computes a deterministic, language-agnostic "facts" block from a list of {@link AiFactCounter}s, providing exact whole-file counts that prepend to the AI body on the oversize path.

#### Type
- Class: `public final class AiFactExtractor`
- Utility class; not instantiable (private constructor)
- No extends / implements / generics

#### Input
- `counters`: configured fact counters (`List<AiFactCounter>`), may be null or empty
- `source`: full source text the regexes are counted over (`String`)
- Each counter's label and pattern are consumed internally

#### Output
- Returns the facts block ending in a blank line (`String`), or empty string when there are no counters or all counters are skipped
- Side effect: prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the assembled entries

#### Core logic
- Iterates over provided counters, skipping null entries or those with null label/pattern
- Appends `label: count` entries separated by `ENTRY_SEPARATOR`
- Calls `countMatches` to compute non-overlapping regex matches of each counter's pattern across the entire source
- Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the result
- Returns empty string if no valid entries remain

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String`
- `validate(List<AiFactCounter> counters) -> void` (throws IllegalArgumentException)
- `countMatches(String pattern, String source) -> int`

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- Referenced type: `AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` thrown from `validate` when a counter is missing its label or pattern, or when a pattern has invalid regex syntax
- Null counters are silently skipped in `factsBlock`; null label/pattern in `validate` triggers exception

#### Concurrency
- No threading or synchronization concerns; all methods are pure and stateless
