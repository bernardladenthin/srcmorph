### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:21Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic "facts" block from a list of AiFactCounter entries, enabling exact whole-file counts prepended to AI summaries.

#### Purpose
- Computes a deterministic, language-agnostic "facts" block from a list of AiFactCounter entries, enabling exact whole-file counts prepended to AI summaries.

#### Type
- final class; utility class; not instantiable (private constructor)
- implements no interfaces; extends Object
- key types: AiFactCounter (consumed as List<AiFactCounter>)

#### Input
- List<AiFactCounter> counters: configured fact counters; null or empty yields empty string
- String source: the full source text the regexes are counted over

#### Output
- String: the facts block ending in a blank line, or "" when there is nothing to report

#### Core logic
- Iterates over counters; skips entries with null label or pattern
- Appends `label: count` entries separated by "; "
- Prepends `**Facts (exact, whole file):** ` header and `\n\n` suffix
- Delegates matching to `countMatches(String, String)` which compiles the pattern and counts non-overlapping matches via Matcher.find()
- Validates all counters up front via `validate(List)`; throws IllegalArgumentException on missing label/pattern or invalid regex

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String`
- `validate(List<AiFactCounter> counters) -> void` throws IllegalArgumentException
- `countMatches(String pattern, String source) -> int`

#### Dependencies
- java.util.List
- java.util.regex.Matcher
- java.util.regex.Pattern
- java.util.regex.PatternSyntaxException
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- IllegalArgumentException thrown from validate if a counter is missing its label or pattern, or has an invalid regex
- Null-handling: null counters or null label/pattern entries are skipped in factsBlock; null list is accepted by validate

#### Concurrency
- Pure function; no I/O; no shared mutable state; thread-safe
