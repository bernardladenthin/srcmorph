### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:05Z
- G: 
- A: 0.0.0
- X: file
---
> Counts configured source-code facts across the full file and formats them as an exact-count block for AI summaries.

#### Purpose
- `AiFactExtractor` produces deterministic, regex-based counts for configured facts.
- Formats nonempty results as a facts header, separated `label: count` entries, and trailing blank line.

#### Type
- `AiFactExtractor` — `final` class; utility class with a private constructor.
- `@Nullable` annotates nullable counter-list parameters.

#### Input
- `factsBlock(counters, source)` consumes a nullable list of `AiFactCounter` and source text.
- `validate(counters)` consumes a nullable list of `AiFactCounter`.
- `countMatches(pattern, source)` consumes a regex and source text.

#### Output
- `factsBlock` returns `""` for null/empty counters or when all entries are skipped; otherwise returns formatted facts ending in `\n\n`.
- `validate` returns normally when valid; otherwise throws `IllegalArgumentException`.
- Public formatting constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- Counts each counter’s regex matches over the entire source and joins entries with `; `.
- `countMatches` uses `Matcher.find()`, counting non-overlapping matches, including zero-width matches.
- `validate` skips null lists and null counter entries; rejects missing labels or patterns.
- `validate` compiles each pattern and wraps `PatternSyntaxException` in `IllegalArgumentException`.

#### Public API
- `FACTS_HEADER` — exact-count facts header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — facts-entry separator.
- `FACTS_SUFFIX` — facts/body separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats whole-source counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter` — counter label and regex pattern.
- `List`, `Pattern`, `Matcher`, `PatternSyntaxException` — counter collection and regex matching/validation.
- `org.jspecify.annotations.Nullable` — nullable parameter annotations.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels or patterns and invalid regex syntax.
- `factsBlock` skips null counters and counters with null labels or patterns.
- Null `source` is not handled explicitly; regex matching may throw `NullPointerException`.

#### Concurrency
- Stateless utility with no I/O or mutable shared state.
