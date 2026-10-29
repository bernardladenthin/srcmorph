### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:56:48Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, whole-file regex match counts for configured labels to produce an exact "facts" block that supplements AI-generated source summaries.

#### Purpose
- Generates a model-free, exact count summary (e.g., rows, tables, functions) from a list of `AiFactCounter` regex definitions.
- Prepends these exact counts to AI-generated summaries to compensate for sampling/chunking inaccuracies.

#### Type
- `final class` `AiFactExtractor`
- Utility class with private constructor; all methods are `static`.
- Uses `org.jspecify.annotations.Nullable` for null-safety annotations.

#### Input
- `List<AiFactCounter> counters`: Configured fact counters, each providing a `label` (String) and `pattern` (String regex).
- `String source`: The full source text to be searched for regex matches.

#### Output
- `String`: A formatted facts block (e.g., `**Facts (exact, whole file):** label1: 5; label2: 10\n\n`) or an empty string if no valid counters/matches exist.
- `void`: `validate` throws exceptions on misconfiguration but produces no return value.

#### Core logic
- `factsBlock`: Iterates counters, skips nulls, counts non-overlapping regex matches per counter, and concatenates `label: count` pairs with `; ` separators.
- `validate`: Checks each counter for non-null `label` and `pattern`; attempts `Pattern.compile` to catch `PatternSyntaxException` early.
- `countMatches`: Compiles the regex, creates a `Matcher`, and iterates `find()` to count non-overlapping matches (zero-width matches count once per position).
- Returns empty string if `counters` is null/empty or if all counters are invalid/skipped.

#### Public API
- `factsBlock(@Nullable List<AiFactCounter>, String) -> String` - Builds formatted facts block.
- `validate(@Nullable List<AiFactCounter>) -> void` - Validates counter configuration.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown by `validate` if a counter has null `label`, null `pattern`, or invalid regex syntax.
- `PatternSyntaxException`: Caught internally in `validate` and wrapped in `IllegalArgumentException`.
- Null-safe: `factsBlock` handles null `counters` list and null individual counter fields by skipping them.

#### Concurrency
- Stateless utility class; all methods are static and operate on immutable inputs.
- Thread-safe: No shared mutable state; `Pattern.compile` creates new instances per call.
