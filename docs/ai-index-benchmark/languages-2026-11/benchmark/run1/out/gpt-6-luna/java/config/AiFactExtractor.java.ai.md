### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:13:53Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact whole-source regex counts for configured code facts, formatted for inclusion alongside AI-generated summaries.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured labels and regex patterns.
- Validates fact-counter configuration so missing values or invalid regexes fail fast.

#### Type
- `AiFactExtractor` — `final` class; utility class with a private constructor.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes nullable counters and the full source text.
- `validate(List<AiFactCounter>)` consumes nullable counters and their labels and patterns.

#### Output
- `factsBlock` returns formatted facts ending in a blank line, or `""` when no entries are reportable.
- `validate` returns no value; throws for invalid counter configuration.
- Public formatting constants define the header and separators.

#### Core logic
- `factsBlock` returns `""` for a null or empty counter list.
- Skips null counters and counters with null labels or patterns.
- Counts each counter’s non-overlapping regex matches across the source using `Matcher.find()`.
- Joins `label: count` entries with `; ` and prefixes the facts header.
- `validate` skips null lists and null counters; rejects counters with null labels or patterns.
- Compiles each pattern to detect invalid regex syntax.

#### Public API
- `FACTS_HEADER` — exact-count facts header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — facts/body separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats whole-source fact counts.
- `validate(List<AiFactCounter>) -> void` — checks counter configuration.

#### Dependencies
- `AiFactCounter` — configured labels and regex patterns.
- `List` — counter collection.
- `Pattern`, `Matcher`, `PatternSyntaxException` — regex compilation and matching.
- `Nullable` — nullable counter-list annotations.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips null counters, labels, and patterns.
- A null source is not handled explicitly.

#### Concurrency
- Pure utility operations with no I/O or mutable instance state.
