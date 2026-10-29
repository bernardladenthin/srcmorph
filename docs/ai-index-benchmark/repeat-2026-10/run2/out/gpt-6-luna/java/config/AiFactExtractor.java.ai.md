### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:26Z
- G: 
- A: 0.0.0
- X: file
---
> Counts configured source-code facts across the full file and formats exact totals for inclusion with an AI summary.

#### Purpose
- `AiFactExtractor` produces deterministic, regex-based `label: count` facts from source text.
- Supports validating configured `AiFactCounter` entries before extraction.

#### Type
- `public final` utility class; no superclass or implemented interfaces.
- Static-only API; private constructor prevents instantiation.
- No class-level annotations.

#### Input
- `factsBlock(List<AiFactCounter>, String)` accepts nullable counters and full source text.
- `validate(List<AiFactCounter>)` accepts nullable counter lists.
- Each counter supplies a label and regex pattern.

#### Output
- `factsBlock` returns a formatted facts block ending with two newlines, or `""` if there are no usable counters.
- `validate` returns no value; throws `IllegalArgumentException` for invalid counter configuration.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` returns `""` for a null or empty list; skips null counters and counters with null labels or patterns.
- Counts each pattern’s non-overlapping matches across the entire source using `Matcher.find()`.
- Formats entries with `": "` and joins them with `"; "`, prefixed by `**Facts (exact, whole file):** ` and suffixed by `\n\n`.
- `validate` skips null lists and null counter entries; rejects missing labels or patterns and compiles each pattern to check syntax.
- Wraps `PatternSyntaxException` in `IllegalArgumentException`, retaining the cause.
- Zero-width matches are counted once per matcher position.

#### Public API
- `FACTS_HEADER` — `String` constant; facts-line prefix.
- `LABEL_COUNT_SEPARATOR` — `String` constant; separates label and count.
- `ENTRY_SEPARATOR` — `String` constant; separates fact entries.
- `FACTS_SUFFIX` — `String` constant; separates facts from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds whole-source facts block.
- `validate(List<AiFactCounter>) -> void` — validates counter labels and regexes.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax.
- `factsBlock` skips null counters and null label/pattern values.
- Null `source` handling is not defined; regex matching may fail.

#### Concurrency
- Stateless static utility methods; no synchronization or shared mutable state.
