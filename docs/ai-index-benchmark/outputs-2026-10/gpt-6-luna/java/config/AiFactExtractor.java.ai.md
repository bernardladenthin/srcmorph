### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:19:42Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact, whole-source regex counts for configured facts and validates fact-counter configuration.

#### Purpose
- Builds a deterministic facts block for source text, suitable for adding to an AI summary.
- Checks fact counters for missing labels or patterns and invalid regex syntax.

#### Type
- `public final` class `AiFactExtractor`; utility class with a private constructor.
- No superclass or implemented interfaces declared.

#### Input
- `factsBlock(List<AiFactCounter>, String)` accepts nullable counters and full source text.
- `validate(List<AiFactCounter>)` accepts nullable counters.

#### Output
- `factsBlock` returns formatted `label: count` entries with a header and trailing blank line, or `""` if no entries are produced.
- Public format constants define the facts header, separators, and suffix.

#### Core logic
- `factsBlock` returns `""` for null or empty lists; skips null counters and counters with null labels or patterns.
- Counts each counter’s regex matches across the full source using repeated `Matcher.find()`; matches are non-overlapping.
- Joins entries with `"; "` and wraps them with `FACTS_HEADER` and `FACTS_SUFFIX`.
- `validate` accepts a null list and skips null counters; rejects missing labels or patterns, then compiles each pattern to check syntax.

#### Public API
- `FACTS_HEADER -> String` — facts-line header.
- `LABEL_COUNT_SEPARATOR -> String` — separates label and count.
- `ENTRY_SEPARATOR -> String` — separates fact entries.
- `FACTS_SUFFIX -> String` — separates facts from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds whole-source counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-pattern errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips null counters and null labels or patterns.
- `countMatches` compiles patterns during counting; it does not handle invalid patterns or null source.

#### Concurrency
- No mutable instance state or synchronization; counting uses local matcher and builder instances.
