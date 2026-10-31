### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:29:01Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic text block of exact counts for specific patterns found within a source file to supplement AI-generated summaries.

#### Purpose
* Provides model-free, exact counts of structural elements (e.g., rows, tables, functions) using regex.
* Prepend exact "facts" to AI summaries to compensate for potential AI estimation errors in large files.

#### Type
final class; utility class (private constructor); non-instantiable.

#### Input
* `counters`: A `List<AiFactCounter>` containing labels and regex patterns.
* `source`: The full string content of the source file to be analyzed.

#### Output
* A formatted `String` containing a header, semicolon-separated `label: count` entries, and a trailing newline suffix.
* Returns an empty string if no counters are provided or if no valid counters exist.

#### Core logic
* Iterates through `AiFactCounter` objects to extract labels and regex patterns.
* Uses `Pattern.compile` and `Matcher.find()` to perform non-overlapping regex searches across the entire `source` string.
* Aggregates results into a single string using `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, and `FACTS_SUFFIX`.
* Validates all counters upfront to ensure labels are present and regex patterns are syntactically correct.

#### Public API
* `factsBlock(List<AiFactCounter>, String) -> String` Generates the formatted facts string.
* `validate(List<AiFactCounter>) -> void` Validates labels and regex syntax for all counters.

#### Dependencies
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`
* `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
* Throws `IllegalArgumentException` if a counter has a null label, a null pattern, or an invalid regex pattern.

#### Concurrency
* Stateless and thread-safe; performs pure computations without side effects or shared mutable state.
