### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:23Z
- G: 
- A: 0.0.0
- X: file
---
> Generates exact, whole-file regex-based statistics for source code to prepend as deterministic facts to AI summaries.

#### Purpose
- Computes deterministic counts of specific structural elements (e.g., rows, functions) in source code using regex patterns.
- Formats these counts into a structured header block for inclusion in AI-generated summaries.

#### Type
`public final class AiFactExtractor`
- Static utility class with private constructor.
- Key constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.
- No external annotations or inheritance.

#### Input
- `counters`: `List<AiFactCounter>` containing configured labels and regex patterns.
- `source`: `String` representing the full text of the source file to analyze.
- Internal dependency: `AiFactCounter` objects providing `getLabel()` and `getPattern()`.

#### Output
- Returns `String`: A formatted facts block (e.g., "**Facts (exact, whole file):** label: count; ...") or empty string if no valid counters exist.
- Throws `IllegalArgumentException` during validation for missing labels, null patterns, or invalid regex syntax.

#### Core logic
- **factsBlock**:
  - Validates input list is not null/empty; returns empty string immediately if so.
  - Iterates through `AiFactCounter` list, skipping null entries or those with null labels/patterns.
  - Accumulates "label: count" pairs separated by `ENTRY_SEPARATOR` in a `StringBuilder`.
  - Calls `countMatches` for each counter's pattern against the full `source` text.
  - Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the accumulated string.
- **validate**:
  - Iterates through provided counters to ensure non-null labels and patterns.
  - Attempts to compile each pattern using `Pattern.compile` to detect syntax errors.
  - Throws descriptive `IllegalArgumentException` on any validation failure.
- **countMatches**:
  - Compiles the pattern string into a `Pattern` object.
  - Uses `Matcher.find()` loop to count all non-overlapping matches in the source text.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String`
  - Builds formatted facts block from counters and source text.
- `validate(List<AiFactCounter>) -> void`
  - Ensures counters have valid labels and compilable regex patterns.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown by `validate` if a counter lacks a label, lacks a pattern, or contains an invalid regex syntax.
- `PatternSyntaxException`: Caught internally within `validate` to wrap invalid regex errors.
- Null handling: Explicitly checks for null lists, counters, labels, and patterns to prevent crashes.

#### Concurrency
- Pure static methods with no shared mutable state; thread-safe by design.
- No synchronization required.
