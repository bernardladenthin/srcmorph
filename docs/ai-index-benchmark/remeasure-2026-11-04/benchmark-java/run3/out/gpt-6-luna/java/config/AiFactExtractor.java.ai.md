### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:27Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Produces exact whole-source counts for configured regex-based facts, formatted for inclusion with AI-generated summaries.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured `AiFactCounter` labels and regex patterns.
- Validates fact-counter configuration and rejects invalid patterns before use.

#### Type
- `AiFactExtractor` — final class; utility class with a private constructor.
- `@Nullable` marks nullable counter-list parameters.

#### Input
- `factsBlock(counters, source)` receives nullable `List<AiFactCounter>` and full source text.
- `validate(counters)` receives a nullable list of `AiFactCounter` values.

#### Output
- `factsBlock` returns an empty string if no usable counters exist; otherwise returns formatted `label: count` entries followed by a blank line.
- Counts non-overlapping regex matches across the full source; emits no I/O or mutable state.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- Counts matches with `Matcher.find()` for each usable counter; joins entries with `; ` and prefixes `**Facts (exact, whole file):** `.
- `validate` skips null lists and null counter elements; rejects missing labels, missing patterns, and regex syntax errors.
- Compiles patterns during validation and again when counting matches.

#### Public API
- `FACTS_HEADER` — header identifying exact whole-file counts.
- `LABEL_COUNT_SEPARATOR` — separator between label and count.
- `ENTRY_SEPARATOR` — separator between entries.
- `FACTS_SUFFIX` — blank-line suffix after the facts block.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds formatted facts block.
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
- `factsBlock` does not handle a null `source`; regex matching may throw `NullPointerException`.
- Null counters and counters with null labels or patterns are skipped by `factsBlock`.

#### Concurrency
- Stateless and pure; no shared mutable state or synchronization.
