### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:39:42Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact, model-free regex match counts (rows, tables, boolean fields, functions, etc.) from a list of `AiFactCounter` configs and formats them as a deterministic "facts" block prepended to AI-generated source summaries.

#### Purpose
- Utility class producing a pure, I/O-free facts block that supplements AI summaries with exact whole-file regex counts.
- Validates `AiFactCounter` configurations at build time to fail fast on misconfigured `<facts>` lists.

#### Type
- `public final class AiFactExtractor`
- No extends/implements; utility class with private constructor (not instantiable).

#### Input
- `List<AiFactCounter>` — each counter carries a `label` (String) and a `pattern` (String regex).
- `String source` — the full source text over which all regexes are matched.

#### Output
- `factsBlock` returns a formatted string: `FACTS_HEADER` + `label: count; label: count` + `FACTS_SUFFIX`, or `""` when empty.
- `validate` has no return; throws on invalid config.
- No fields mutated; no I/O.

#### Core logic
- `factsBlock`: iterates counters, skips nulls/missing label/pattern, counts non-overlapping regex matches per counter, joins entries with `"; "`, wraps in header/suffix.
- `validate`: for each non-null counter, asserts label non-null, pattern non-null, and `Pattern.compile` succeeds; throws `IllegalArgumentException` on any failure.
- `countMatches`: compiles pattern, runs `Matcher.find()` in a loop, increments counter per match (zero-width matches count once per position).

#### Public API
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` — builds the facts block string.
- `validate(@Nullable List<AiFactCounter>) -> void` — fails fast on bad config.

#### Dependencies
- `AiFactCounter` (same package) — provides `getLabel()` and `getPattern()`.
- `java.util.List`
- `java.util.regex.Pattern`, `java.util.regex.Matcher`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for null label, null pattern, or `PatternSyntaxException` (wrapped with cause).
- `factsBlock` silently skips null counters or counters with null label/pattern (defensive; validation is expected to run first).

#### Concurrency
- Stateless utility; all methods static; thread-safe by construction.
