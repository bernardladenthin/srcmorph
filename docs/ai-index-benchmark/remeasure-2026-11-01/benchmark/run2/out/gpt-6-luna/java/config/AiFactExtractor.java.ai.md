### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:41:35Z
- G: 
- A: 0.0.0
- X: file
---
> Counts configured source-code facts across the full text and formats them as an exact, deterministic facts block.

#### Purpose
- `AiFactExtractor` produces model-independent counts for configured regex-based facts.
- Formats non-empty results for inclusion before an AI-generated body.

#### Type
- `AiFactExtractor` — `final` class.
- Utility class with a private constructor; no inheritance or notable annotations.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes configured counters and full source text.
- `validate(List<AiFactCounter>)` consumes configured counters.
- Each counter supplies a label and regex pattern.

#### Output
- `factsBlock` returns a formatted facts block ending with a blank line, or `""` if there are no reportable counters.
- `validate` returns no value; rejects invalid counter configuration.
- Public constants define the header and separators.

#### Core logic
- Skips null or empty counter lists when building the facts block.
- For each counter with non-null label and pattern, counts regex matches across the entire source.
- Uses repeated `Matcher.find()`, counting non-overlapping matches, including zero-width matches.
- Joins entries as `label: count` with `; `, prefixes the facts header, and appends two newlines.
- Validation permits a null list and skips null counter elements; checks labels, patterns, and regex syntax.

#### Public API
- `FACTS_HEADER` — facts block header.
- `LABEL_COUNT_SEPARATOR` — label/count separator.
- `ENTRY_SEPARATOR` — entry separator.
- `FACTS_SUFFIX` — trailing blank-line separator.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds whole-source facts block.
- `validate(List<AiFactCounter>) -> void` — validates fact counter configuration.

#### Dependencies
- `AiFactCounter` — configured labels and regex patterns.
- `java.util.List` — counter collection.
- `java.util.regex.Matcher`, `Pattern`, `PatternSyntaxException` — regex matching and validation.
- `org.jspecify.annotations.Nullable` — nullable counter-list annotation.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a missing label, missing pattern, or invalid regex; wraps `PatternSyntaxException` as the cause.
- `factsBlock` skips null counters and counters with null labels or patterns.
- Null `source` is not handled explicitly.

#### Concurrency
- Stateless utility; no mutable shared state or synchronization.
