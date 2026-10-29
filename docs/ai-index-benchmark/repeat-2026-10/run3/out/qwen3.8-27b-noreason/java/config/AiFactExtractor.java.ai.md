### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:46:04Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, whole-file regex match counts from configured `AiFactCounter` definitions to produce an exact "facts" block for AI-assisted source summarization.

#### Purpose
- Generates a deterministic, model-free facts block (label: count pairs) from a list of `AiFactCounter` regex definitions applied to full source text.
- Validates counter configurations (non-null labels, compilable patterns) to fail fast on misconfigured `<facts>` lists.

#### Type
- `public final class AiFactExtractor`
- Utility class with private no-arg constructor; not instantiable.
- No extends/implements; no generics.

#### Input
- `List<AiFactCounter>` — configured fact counters (nullable, may be empty).
- `String source` — full source text over which regexes are counted.
- Each `AiFactCounter` provides `getLabel()` (String) and `getPattern()` (String regex).

#### Output
- `String` — the facts block (e.g. `"**Facts (exact, whole file):** rows: 12; tables: 3\n\n"`) or `""` when no counters or no valid entries.
- `void` from `validate` — throws on invalid config; no return value.

#### Core logic
- `factsBlock`: iterates counters, skips null entries/labels/patterns; for each valid counter, counts non-overlapping regex matches in `source` via `countMatches`; joins entries with `"; "`; prefixes with `FACTS_HEADER` and suffixes with `FACTS_SUFFIX` (`"\n\n"`).
- `validate`: iterates counters; throws `IllegalArgumentException` if label is null, pattern is null, or `Pattern.compile(pattern)` raises `PatternSyntaxException`.
- `countMatches`: compiles pattern, iterates `Matcher.find()` over source, increments counter per match; zero-width matches count once per position.

#### Public API
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` — builds facts block from counters.
- `validate(@Nullable List<AiFactCounter>) -> void` — checks counter config validity.
- `FACTS_HEADER` (public static final String) — header prefix for facts line.
- `LABEL_COUNT_SEPARATOR` (public static final String) — separator between label and count.
- `ENTRY_SEPARATOR` (public static final String) — separator between entries.
- `FACTS_SUFFIX` (public static final String) — trailing separator before AI body.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for null label, null pattern, or invalid regex.
- `PatternSyntaxException` caught internally in `validate` and re-wrapped as `IllegalArgumentException`.
- Null/empty `counters` list handled gracefully (returns `""` or no-op).

#### Concurrency
- Stateless utility class; all methods are static and operate on immutable inputs; thread-safe.
