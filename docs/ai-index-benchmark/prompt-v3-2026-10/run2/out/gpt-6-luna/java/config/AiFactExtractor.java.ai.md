### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:10:49Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source regex counts for configured facts and formats them for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` produces deterministic `label: count` facts from source text.
- Validates configured fact counters so invalid labels or regexes fail fast.

#### Type
- `AiFactExtractor` — `public final` utility class; no superclass or implemented interfaces.
- Private constructor prevents instantiation.

#### Input
- `factsBlock(counters, source)` consumes a nullable list of `AiFactCounter` and source text.
- `validate(counters)` consumes a nullable list of `AiFactCounter`.
- Each counter supplies a label and regex pattern.

#### Output
- `factsBlock` returns a formatted facts block ending with a blank line, or `""` when no entries are available.
- Public constants define the facts header and formatting separators.

#### Core logic
- `factsBlock` returns `""` for null or empty counter lists.
- Skips null counters and counters with null labels or patterns.
- Compiles each pattern and counts non-overlapping matches across the whole source using `Matcher.find()`.
- Joins entries with `"; "` and formats them as `**Facts (exact, whole file):** ...\n\n`.
- `validate` skips null lists and null counters; rejects missing labels or patterns and compiles each pattern to check syntax.

#### Public API
- `FACTS_HEADER` — `String`; exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR` — `String`; separates label and count.
- `ENTRY_SEPARATOR` — `String`; separates fact entries.
- `FACTS_SUFFIX` — `String`; separates facts from following content.
- `factsBlock(List<AiFactCounter>, String) -> String` — build formatted facts block.
- `validate(List<AiFactCounter>) -> void` — validate labels and regexes.

#### Dependencies
- `AiFactCounter` — counter label and regex source.
- `java.util.List` — counter collection.
- `java.util.regex.Matcher`, `Pattern` — regex matching and compilation.
- `java.util.regex.PatternSyntaxException` — invalid regex handling.
- `org.jspecify.annotations.Nullable` — nullable counter-list annotations.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a counter with a null label or pattern, or an invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` silently skips null counters and counters with null labels or patterns.
- `countMatches` assumes non-null pattern and source.

#### Concurrency
- Stateless utility with no mutable shared state; operations are pure and perform no I/O.
