### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:38:19Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact, whole-file regex match counts for AI fact counters and prepends a deterministic facts block to AI summaries.

#### Purpose
- Produces a deterministic facts block of `label: count` entries from `AiFactCounter` regex matches over entire source text.
- Supplies exact, model-free counts (rows, tables, functions, etc.) that chunked or sampled AI summaries cannot reliably generate.

#### Type
- `public final class AiFactExtractor` in package `net.ladenthin.srcmorph.config`
- Utility class; private constructor; not instantiable or extensible.
- No `extends` or `implements`.

#### Input
- `factsBlock` accepts `@Nullable List<AiFactCounter> counters` and `String source` (full source text).
- `validate` accepts `@Nullable List<AiFactCounter> counters`.
- `countMatches` accepts `String pattern` and `String source`.
- Consumes only in-memory strings and counter objects; no files, streams, or I/O.

#### Output
- `factsBlock` returns `String` facts block ending with `\n\n`, or `""` when no counters or entries exist.
- `validate` returns `void` and throws on misconfiguration; produces no output otherwise.
- `countMatches` returns `int` number of non-overlapping matches.
- Pure methods; no mutated fields or written resources.

#### Core logic
- `factsBlock` returns `""` immediately for null or empty counter lists.
- Iterates counters, skipping null counters or those with null label or pattern.
- Appends each entry as `label: count`, joined by `; `.
- Prefixes result with `**Facts (exact, whole file):** ` and suffixes with a blank line.
- `validate` iterates counters and throws `IllegalArgumentException` for null label, null pattern, or invalid regex syntax.
- `countMatches` compiles the pattern and counts matches via `Matcher.find()` across the whole source.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` builds exact facts block from counters.
- `validate(List<AiFactCounter>) -> void` fails fast on invalid counter config.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` when a counter has a null label, null pattern, or uncompilable regex.
- `factsBlock` skips null or incomplete counters rather than throwing.
- `countMatches` can throw `PatternSyntaxException` if given an invalid pattern, though `validate` is intended to prevent this.

#### Concurrency
- Stateless pure static methods with no shared mutable state.
- Immutable `String` constants.
- Thread-safe for concurrent invocation.
