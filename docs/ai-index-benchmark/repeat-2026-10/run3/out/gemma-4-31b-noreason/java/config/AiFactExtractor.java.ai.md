### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:25:39Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic summary of exact counts for specific patterns (e.g., functions, tables) within a source file to supplement AI-generated summaries.

#### Purpose
* Provide a language-agnostic "facts" block containing exact counts of regex matches.
* Validate configuration of fact counters to ensure build-time failure on invalid regexes.

#### Type
* final class.
* Utility class (private constructor).

#### Input
* `List<AiFactCounter>`: Configuration containing labels and regex patterns.
* `String source`: The full source text to be analyzed.

#### Output
* `String`: A formatted facts block starting with `FACTS_HEADER` and ending with `FACTS_SUFFIX`, or an empty string if no counters are provided/valid.

#### Core logic
* Iterates through a list of `AiFactCounter` objects.
* For each valid counter, executes a regex search across the entire `source` string.
* Increments a counter for every non-overlapping match found by `Matcher.find()`.
* Joins results into a single string using `LABEL_COUNT_SEPARATOR` (": ") and `ENTRY_SEPARATOR` ("; ").
* Validates counters by attempting to compile the regex patterns via `Pattern.compile()`.

#### Public API
* `factsBlock(List<AiFactCounter>, String) -> String`: Produces the formatted facts summary string.
* `validate(List<AiFactCounter>) -> void`: Verifies labels and regex syntax for all counters.
* `FACTS_HEADER` (constant)
* `LABEL_COUNT_SEPARATOR` (constant)
* `ENTRY_SEPARATOR` (constant)
* `FACTS_SUFFIX` (constant)

#### Dependencies
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`
* `AiFactCounter`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate()` if a counter lacks a label, lacks a pattern, or contains an invalid regular expression.
* `PatternSyntaxException`: Caught during validation and wrapped in `IllegalArgumentException`.
