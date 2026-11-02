### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:42:53Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Produces exact whole-source counts for configured regex-based facts and formats them for inclusion with AI-generated summaries.

#### Purpose
- `AiFactExtractor` turns `AiFactCounter` labels and patterns into a deterministic facts block.
- Validates configured fact counters and regex patterns before use.

#### Type
- `AiFactExtractor` — `final class`; utility class with a private constructor.

#### Input
- `factsBlock(List<AiFactCounter>, String)` reads nullable counters and the full source text.
- `validate(List<AiFactCounter>)` reads nullable counters, labels, and regex patterns.

#### Output
- `factsBlock` returns a formatted facts block ending in a blank line, or `""` when there are no reportable counters.
- Public constants define the facts header, label/count separator, entry separator, and trailing suffix.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` returns `""` for null or empty counter lists; skips null counters and counters with null labels or patterns.
- For each remaining counter, compiles its regex and counts non-overlapping matches across the source using `Matcher.find()`.
- Formats entries as `label: count`, joined by `; `, after the exact-whole-file header.
- `validate` allows a null list and skips null elements; rejects missing labels or patterns and wraps invalid regex syntax in `IllegalArgumentException`.

#### Public API
- `FACTS_HEADER` — `String`; exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR` — `String`; separates label and count.
- `ENTRY_SEPARATOR` — `String`; separates fact entries.
- `FACTS_SUFFIX` — `String`; separates facts from following body.
- `factsBlock(List<AiFactCounter>, String)` -> `String` — formats regex fact counts.
- `validate(List<AiFactCounter>)` -> `void` — checks labels and regex patterns.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels or patterns, or invalid regex syntax; invalid-pattern errors retain the `PatternSyntaxException` cause.
- `factsBlock` skips counters with null labels or patterns.
- A null source is not explicitly handled.

#### Concurrency
- Stateless utility; methods use local state and perform no I/O.
