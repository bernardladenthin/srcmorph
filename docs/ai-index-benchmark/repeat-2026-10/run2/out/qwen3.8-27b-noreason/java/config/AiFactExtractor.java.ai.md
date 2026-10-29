### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:34Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact, whole-file regex match counts for configured source-code facts (rows, tables, functions) to prepend as a deterministic header to AI-generated summaries.

#### Purpose
- Generates a deterministic "facts" block from a list of `AiFactCounter` regex definitions to provide exact, model-free metrics for source code analysis.
- Validates counter configurations to fail fast on missing labels or invalid regular expressions.

#### Type
- `public final class AiFactExtractor`
- Utility class with private constructor (not instantiable).
- Uses `org.jspecify.annotations.Nullable` for null-safety annotations.

#### Input
- `List<AiFactCounter> counters`: Configured fact counters (label + regex pattern pairs).
- `String source`: The full source text to be analyzed.
- `AiFactCounter.getLabel()`: Returns the display label for the fact.
- `AiFactCounter.getPattern()`: Returns the regex pattern string.

#### Output
- `String`: The formatted facts block (e.g., `**Facts (exact, whole file):** label: count; label: count\n\n`) or empty string if no valid counters/matches.
- `void`: Validation method throws exceptions on invalid config.

#### Core logic
- `factsBlock`: Iterates counters, skips null/invalid entries, counts regex matches per counter, joins entries with `; `, prefixes with `FACTS_HEADER`, suffixes with `FACTS_SUFFIX`.
- `validate`: Checks each counter for non-null label and pattern; compiles pattern to verify syntax; throws `IllegalArgumentException` on failure.
- `countMatches`: Compiles pattern, creates `Matcher`, iterates `find()` to count non-overlapping matches (zero-width matches count per position).

#### Public API
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` - Builds formatted facts block.
- `validate(@Nullable List<AiFactCounter>) -> void` - Validates counter configurations.
- `FACTS_HEADER -> String` - Constant header prefix.
- `LABEL_COUNT_SEPARATOR -> String` - Constant label-count delimiter.
- `ENTRY_SEPARATOR -> String` - Constant entry delimiter.
- `FACTS_SUFFIX -> String` - Constant trailing separator.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown by `validate` when counter label/pattern is null or pattern is invalid regex.
- `PatternSyntaxException`: Caught in `validate` and wrapped in `IllegalArgumentException`.
- Null handling: `factsBlock` returns `""` for null/empty counters; skips null counters/labels/patterns.

#### Concurrency
- Stateless utility class; all methods are static and pure (no shared mutable state).
- Thread-safe by design (no instance fields, no side effects).
