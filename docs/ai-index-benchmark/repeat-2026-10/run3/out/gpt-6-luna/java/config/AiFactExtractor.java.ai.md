### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:19:32Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact whole-source counts for configured regex-based facts and formats them for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` produces deterministic counts for configured `AiFactCounter` patterns.
- Formats nonempty counts as a facts header followed by a blank line.

#### Type
- `final` utility class; private constructor; no inheritance or implemented interfaces.
- Uses `@Nullable` for the counter-list parameter.

#### Input
- `factsBlock(counters, source)` reads a nullable list of `AiFactCounter` values and the full source string.
- `validate(counters)` reads a nullable list and checks each non-null counter’s label and pattern.

#### Output
- `factsBlock` returns `""` when there are no counters or no valid entries; otherwise returns formatted `label: count` entries.
- `validate` returns no value; throws `IllegalArgumentException` for invalid counter configuration.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` skips null counters and counters with null labels or patterns.
- Counts non-overlapping regex matches over the entire source using `Matcher.find()`.
- Joins entries with `"; "`, prefixes `**Facts (exact, whole file):** `, and appends `"\n\n"`.
- `validate` skips null lists and null counter entries; rejects missing labels, missing patterns, and patterns that fail compilation.

#### Public API
- `FACTS_HEADER` → `String` — facts-line prefix.
- `LABEL_COUNT_SEPARATOR` → `String` — separates label and count.
- `ENTRY_SEPARATOR` → `String` — separates fact entries.
- `FACTS_SUFFIX` → `String` — separates facts from following body.
- `factsBlock(List<AiFactCounter>, String)` → `String` — builds whole-source facts block.
- `validate(List<AiFactCounter>)` → `void` — validates configured counters.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for a null label or pattern, or wraps `PatternSyntaxException` for invalid regex syntax.
- `factsBlock` compiles patterns during counting; invalid patterns can throw `PatternSyntaxException`.
- `factsBlock` skips null counter values and counters with null labels or patterns.

#### Concurrency
- Stateless utility with no shared mutable state; operations use local builders, matchers, and counters.
