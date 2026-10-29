### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:43:39Z
- G: 
- A: 0.0.0
- X: file
---
> Builds a deterministic, whole-file facts summary for AI prompts from regex-based fact counters.

#### Purpose
- Produces a compact `label: count` facts block for whole-file analysis.
- Validates and counts regex-based facts without I/O or model estimation.

#### Type
- `public final class AiFactExtractor`
- No extends; no implements
- No generics or type bounds
- No notable annotations on the type

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`: consumes the configured fact counters and the full source text.
- `validate(List<AiFactCounter> counters)`: consumes the configured counter list for compile-time validation.
- `countMatches(String pattern, String source)`: consumes a regex pattern and source text.
- Reads no files, streams, or external config; all inputs are in-memory strings/lists.
- `AiFactExtractor()` private constructor is not instantiable.

#### Output
- `factsBlock(...) -> String`: returns a `FACTS_HEADER + "label: count; ..." + FACTS_SUFFIX` string, or `""` when nothing valid remains.
- `validate(...) -> void`: produces no value, but enforces configuration correctness via exceptions.
- `countMatches(...) -> int`: returns the number of non-overlapping regex matches.
- Mutates no object state; class is effectively static and stateless.

#### Core logic
- `factsBlock` returns empty string for `null`/empty `counters`.
- Iterates `AiFactCounter` entries, skipping `null` counters and any counter whose label or pattern is `null`.
- Joins each valid `label: count` entry with `ENTRY_SEPARATOR` (`"; "`).
- Counts matches with `Pattern.compile(pattern).matcher(source)` and `Matcher.find()`, which counts non-overlapping matches and counts zero-width matches once per position.
- If no valid entries remain, returns empty string.
- `validate` ensures every counter has a non-null label and pattern, then compiles each regex with `Pattern.compile`.
- Invalid regexes are rethrown as `IllegalArgumentException` with context.

#### Public API
- `FACTS_HEADER -> String` — exact facts header
- `LABEL_COUNT_SEPARATOR -> String` — label/count separator
- `ENTRY_SEPARATOR -> String` — entry delimiter
- `FACTS_SUFFIX -> String` — facts tail
- `factsBlock(List<AiFactCounter> counters, String source) -> String` — builds facts block
- `validate(List<AiFactCounter> counters) -> void` — validates counters

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`
- `IllegalArgumentException`
- `StringBuilder`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` if a counter label is `null`.
- `validate` throws `IllegalArgumentException` if a counter pattern is `null`.
- `validate` catches `PatternSyntaxException` and rethrows it as `IllegalArgumentException` with label context.
- `factsBlock` silently skips invalid entries rather than failing.
- `factsBlock` returns `""` for `null`/empty counters and for lists that end up with no valid entries.

#### Concurrency
- No threading, synchronization, or shared mutable state.
- Immutable constant strings; stateless static methods.
