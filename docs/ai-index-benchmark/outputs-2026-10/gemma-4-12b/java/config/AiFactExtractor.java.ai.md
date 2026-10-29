### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:20:35Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic, language-agnostic "facts" block of exact counts for specific patterns found in source code.

#### Purpose
* Provides exact, model-free counts of code elements (e.g., rows, tables, functions) to be prepended to AI-generated summaries.
* Ensures counts are based on the whole source file rather than sampled chunks.

#### Type
final class; utility class (private constructor).

#### Input
* `List<AiFactCounter> counters`: List of labels and regex patterns to count.
* `String source`: The full source text to be analyzed.

#### Output
* `String`: A formatted facts block (e.g., "**Facts (exact, whole file):** label1: count1; label2: count2\n\n") or an empty string.
* `void`: Validation method produces no return but may throw exceptions.

#### Core logic
* Iterates through a list of `AiFactCounter` objects.
* Skips counters with null labels or patterns.
* Uses `java.util.regex.Pattern` and `Matcher` to find non-overlapping matches in the source string.
* Aggregates counts into a single string separated by `; ` and prefixed with a specific header.
* Validates that all provided counters have non-null labels and valid, compilable regex patterns.

#### Public API
* `factsBlock(List<AiFactCounter> counters, String source) -> String` builds the formatted facts block.
* `validate(List<AiFactCounter> counters) -> void` ensures all counters are valid and patterns are compilable.

#### Dependencies
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`
* `AiFactCounter` (referenced type)

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate` if a counter is missing a label/pattern or contains an invalid regex.
* `PatternSyntaxException`: Caught during validation and wrapped in `IllegalArgumentException`.

#### Concurrency
* Thread-safe: The class is stateless and uses local variables/stateless regex matching.
