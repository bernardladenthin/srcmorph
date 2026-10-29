### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:26Z
- G: 
- A: 0.0.0
- X: file
---
> Generates exact, whole-file regex-based counts of code elements (rows, tables, functions) to prepend as a deterministic facts block for AI summaries.

#### Purpose
- Computes deterministic, language-agnostic statistics from source code using configured regular expressions.
- Formats these counts into a structured header string to augment AI-generated summaries with precise metrics.

#### Type
`public final class AiFactExtractor`
- Static utility class; private constructor prevents instantiation.
- No inheritance or interface implementation.
- Key constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Input
- `counters`: `List<AiFactCounter>` containing label and pattern definitions.
- `source`: `String` representing the full source text to analyze.
- Internal dependency: `AiFactCounter` objects providing `getLabel()` and `getPattern()`.

#### Output
- Returns `String`: A formatted facts block starting with `**Facts (exact, whole file):** `, followed by `label: count` pairs separated by `; `, ending with `\n\n`.
- Returns empty `String` if no valid counters exist.
- Throws `IllegalArgumentException` during validation for missing labels, patterns, or invalid regex syntax.

#### Core logic
- **factsBlock**:
  - Validates input list is not null/empty; returns empty string immediately if so.
  - Iterates through `AiFactCounter` list, skipping null entries or those with null labels/patterns.
  - Accumulates results in `StringBuilder`, appending `ENTRY_SEPARATOR` between items.
  - Calls `countMatches` for each counter's pattern against the full `source` string.
  - Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the accumulated string.
- **validate**:
  - Iterates through `AiFactCounter` list to ensure configuration integrity.
  - Checks for null `label` or `pattern` fields on each counter.
  - Attempts to compile each pattern using `Pattern.compile`; catches `PatternSyntaxException` to report invalid regex.
- **countMatches**:
  - Compiles the provided pattern string into a `Pattern` object.
  - Uses `Matcher.find()` loop to count non-overlapping occurrences in the source text.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` generates formatted fact summary.
- `validate(List<AiFactCounter>) -> void` ensures counter configuration validity.
- Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown by `validate` if a counter lacks a label, lacks a pattern, or contains an invalid regex syntax.
- `PatternSyntaxException`: Caught internally within `validate` to wrap invalid regex errors in `IllegalArgumentException`.
- Null handling: Explicit checks for null `counters` list, null `counter` items, and null `label`/`pattern` fields prevent crashes.

#### Concurrency
- Pure static methods with no shared mutable state; thread-safe by design.
- No synchronization required.
