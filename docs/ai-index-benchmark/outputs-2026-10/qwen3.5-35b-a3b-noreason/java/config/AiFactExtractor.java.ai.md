### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:55:45Z
- G: 
- A: 0.0.0
- X: file
---
> Generates exact, whole-file regex-based statistics for AI summaries by counting non-overlapping pattern matches in source code.

#### Purpose
- Computes deterministic "facts" blocks (label: count pairs) from source code using configured regex counters.
- Validates configuration of fact counters to ensure labels and patterns are present and syntactically correct before processing.

#### Type
`public final class AiFactExtractor`
- Utility class with private constructor; not instantiable.
- Static methods only.

#### Input
- `counters`: `List<AiFactCounter>` containing label and regex pattern definitions.
- `source`: `String` representing the full text content to be analyzed.
- Internal dependencies: `Pattern`, `Matcher`, `PatternSyntaxException`.

#### Output
- Returns `String` facts block prefixed with `**Facts (exact, whole file):**` and suffixed with newlines.
- Throws `IllegalArgumentException` on validation failure (missing label/pattern or invalid regex).
- Returns empty string if no valid counters exist.

#### Core logic
- **factsBlock**: Iterates through provided `AiFactCounter` list, skipping null entries.
- Compiles each counter's pattern and counts non-overlapping matches against the `source` text.
- Concatenates results as `label: count` pairs separated by `; `.
- Prepends header and appends suffix to form the final output string.
- **validate**: Checks each counter for null label or pattern.
- Attempts to compile each pattern string; catches `PatternSyntaxException` to report invalid regexes.
- **countMatches**: Uses `java.util.regex.Matcher.find()` loop to tally occurrences of a pattern in a string.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` generates formatted statistics block.
- `validate(List<AiFactCounter>) -> void` ensures configuration integrity.
- `FACTS_HEADER` constant defines output prefix.
- `LABEL_COUNT_SEPARATOR` constant defines label/count delimiter.
- `ENTRY_SEPARATOR` constant defines entry delimiter.
- `FACTS_SUFFIX` constant defines trailing newline separator.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if a counter lacks a label, lacks a pattern, or contains an invalid regex syntax.
- `PatternSyntaxException`: Caught internally during validation to wrap invalid regex errors.
- Null handling: Skips null counters in loops; returns empty string for null/empty input lists.

#### Concurrency
- No threading or synchronization mechanisms used.
- Methods are stateless and thread-safe due to lack of mutable static fields.
