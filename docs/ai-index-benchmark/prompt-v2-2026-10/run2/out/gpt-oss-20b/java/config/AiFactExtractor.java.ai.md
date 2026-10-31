### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:48:59Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block listing exact counts of regex‑matched elements in a source file for AI summaries.

#### Purpose
- Provides exact, whole‑file counts of language‑agnostic facts (e.g., rows, tables, functions) for AI documentation.

#### Type
`final class AiFactExtractor` – public, final, no inheritance, no generics.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` – list of counters, source text.
- `validate(List<AiFactCounter> counters)` – list of counters.
- `countMatches(String pattern, String source)` – regex pattern, source text.

#### Output
- `factsBlock` returns a formatted facts string or empty.
- `validate` throws `IllegalArgumentException` on misconfiguration.
- `countMatches` returns integer match count.

#### Core logic
- Skip null counters or those with null label/pattern.
- Append each counter’s label and match count, separated by `ENTRY_SEPARATOR`.
- Prepend `FACTS_HEADER`, append `FACTS_SUFFIX`.
- Validate each counter’s label/pattern; compile pattern to detect syntax errors.
- Count matches using `Matcher.find()` loop.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for missing label/pattern or invalid regex.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables only.
