### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact counts of regex‑matched elements in a source file for AI summaries.

#### Purpose
- Computes exact, whole‑file counts of language‑agnostic facts.
- Prepends a facts header to AI output on oversize paths.

#### Type
`public final class AiFactExtractor` – utility class, no instances.

#### Input
- `List<AiFactCounter> counters` – may be null/empty; each counter supplies `label` and `pattern`.
- `String source` – full source text to search.

#### Output
- `String` facts block ending with a blank line, or empty string if no counters or all skipped.

#### Core logic
- Iterate counters; skip null or missing label/pattern.
- Append `label: count` entries separated by `; `.
- Count matches via `countMatches`: compile pattern, iterate `Matcher.find()` for non‑overlapping matches.
- Prepend `FACTS_HEADER` and append `FACTS_SUFFIX` if any entries exist.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – ensure all counters have labels, patterns, and valid regexes.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` if a counter lacks label/pattern or regex fails to compile.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
