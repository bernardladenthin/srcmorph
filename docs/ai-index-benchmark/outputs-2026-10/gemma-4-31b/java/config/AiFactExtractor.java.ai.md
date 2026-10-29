### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:25:15Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic summary of exact counts for specific patterns (e.g., function or table counts) within a source file to augment AI-generated summaries.

#### Purpose
* Provides a utility to extract and format "facts" (regex-based counts) from source text.
* Validates the configuration of fact counters to ensure regexes are compilable.

#### Type
Final class. Utility class (private constructor).

#### Input
* `List<AiFactCounter>`: A list of counters containing labels and regex patterns.
* `String source`: The full source text to be analyzed.

#### Output
* `String`: A formatted facts block (e.g., "**Facts (exact, whole file):** Label1: 5; Label2: 10\n\n") or an empty string if no counters are provided/valid.

#### Core logic
* **Fact Block Generation**:
    * Iterates through `AiFactCounter` entries, skipping nulls or those with missing labels/patterns.
    * For each valid counter, calculates the number of non-overlapping regex matches in the `source` text.
    * Joins these `label: count` pairs using `ENTRY_SEPARATOR`.
    * Wraps the resulting string with `FACTS_HEADER` and `FACTS_SUFFIX`.
* **Validation**:
    * Verifies that every `AiFactCounter` has a non-null label and pattern.
    * Attempts to compile each pattern via `Pattern.compile` to catch `PatternSyntaxException` early.
* **Counting**:
    * Uses `java.util.regex.Matcher.find()` in a loop to determine the total number of occurrences of a pattern.

#### Public API
* `factsBlock(List<AiFactCounter>, String) -> String`: Builds the formatted facts string from source.
* `validate(List<AiFactCounter>) -> void`: Validates counters for nulls and regex syntax.
* `FACTS_HEADER` (constant)
* `LABEL_COUNT_SEPARATOR` (constant)
* `ENTRY_SEPARATOR` (constant)
* `FACTS_SUFFIX` (constant)

#### Dependencies
* `AiFactCounter`
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate` if a counter is missing a label, missing a pattern, or contains an invalid regular expression.

#### Concurrency
* Thread-safe: The class is stateless and contains only pure static methods.
