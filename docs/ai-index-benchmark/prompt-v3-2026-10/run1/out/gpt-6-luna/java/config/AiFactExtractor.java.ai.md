### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:10Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source regex counts for configured code facts and formats them for inclusion with AI-generated summaries.

#### Purpose
- Produces a deterministic facts block with one `label: count` entry per usable `AiFactCounter`.
- Validates configured counters and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — utility class; not instantiable.
- No superclass or implemented interfaces.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes optional counters and full source text.
- `validate(List<AiFactCounter>)` consumes optional configured counters.

#### Output
- `factsBlock` returns a formatted facts block ending in a blank line, or `""` when no entries are reportable.
- `validate` returns no value; throws for invalid counter configuration.
- Public formatting constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Core logic
- `factsBlock` returns `""` for a null or empty list; skips null counters and counters with null labels or patterns.
- Counts non-overlapping regex matches over the entire source using `Matcher.find()`.
- Joins entries with `"; "` and wraps them with the facts header and `"\n\n"` suffix.
- `validate` allows a null list and skips null counters; requires each non-null counter to have a label and pattern, then compiles its regex.
- `countMatches` counts zero-width matches once per position, as reported by `Matcher.find()`.

#### Public API
- `FACTS_HEADER` — exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — facts/body separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats exact source counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` cause.
- `factsBlock` does not validate patterns; regex compilation errors can propagate from `Pattern.compile`.
- `countMatches` requires a non-null source for regex matching.

#### Concurrency
- Stateless utility with no mutable instance state; methods use local variables.
