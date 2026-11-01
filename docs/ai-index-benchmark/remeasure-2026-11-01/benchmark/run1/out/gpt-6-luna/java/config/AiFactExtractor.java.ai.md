### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:19:56Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source regex counts for configured code facts and formats them for inclusion alongside an AI summary.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured `AiFactCounter` entries.
- Validates counter labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — final utility class; not instantiable.

#### Input
- `factsBlock(counters, source)` accepts a nullable list of fact counters and the full source text.
- `validate(counters)` accepts a nullable list of fact counters.

#### Output
- `factsBlock` returns formatted `String` output, or `""` when no facts can be reported.
- `validate` returns no value; throws for invalid counter configuration.
- Publishes formatting constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Core logic
- `factsBlock` returns `""` for a null or empty counter list.
- Skips null counters and counters with null labels or patterns.
- Counts each counter’s non-overlapping regex matches across the full source using `Matcher.find()`.
- Formats entries as `label: count`, joined by `; ` and prefixed with the facts header; appends a blank-line suffix.
- `validate` skips null lists and null counter entries; rejects counters missing labels or patterns.
- Compiles each configured pattern during validation to detect invalid regex syntax.

#### Public API
- `FACTS_HEADER` — exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — facts-entry separator.
- `FACTS_SUFFIX` — separator before following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats whole-source fact counts.
- `validate(List<AiFactCounter>) -> void` — checks counter configuration.

#### Dependencies
- `AiFactCounter` — supplies labels and regex patterns.
- `java.util.List` — counter collection.
- `java.util.regex.Pattern`, `Matcher`, `PatternSyntaxException` — compile and count regex matches.
- `org.jspecify.annotations.Nullable` — nullable counter-list annotations.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; wraps `PatternSyntaxException` as the cause for invalid patterns.
- `factsBlock` skips null counter entries and null labels or patterns.
- A null source is not handled explicitly and can fail during matcher creation.

#### Concurrency
- Stateless utility with no mutable instance state or I/O.
