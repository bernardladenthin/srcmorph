### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:50:51Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic summary block of exact regex-based counts (facts) from source code to supplement AI-generated summaries.

#### Purpose
* Provides a mechanism to extract precise, language-agnostic metrics (e.g., function counts) from source text.
* Validates configuration of fact counters to ensure regex patterns are compilable.

#### Type
Final class. Utility class (private constructor).

#### Input
* `List<AiFactCounter>`: Configuration containing labels and regex patterns.
* `String source`: The full source code text to be analyzed.

#### Output
* `String`: A formatted facts block starting with `FACTS_HEADER` and ending with `FACTS_SUFFIX`, or an empty string if no counters are provided/valid.

#### Core logic
* Iterates through a list of `AiFactCounter` objects.
* For each valid counter, executes a regex search across the entire `source` string.
* Increments a counter for every non-overlapping match found by `java.util.regex.Matcher`.
* Joins results into a single string using `LABEL_COUNT_SEPARATOR` (": ") and `ENTRY_SEPARATOR` ("; ").
* Validates that all provided counters have non-null labels and syntactically correct regex patterns.

#### Public API
* `factsBlock(List<AiFactCounter>, String) -> String`: Produces the formatted facts summary string.
* `validate(List<AiFactCounter>) -> void`: Verifies counters for nulls and regex syntax.

#### Dependencies
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`
* `AiFactCounter`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate` if a counter lacks a label, lacks a pattern, or contains an invalid regular expression.
* `PatternSyntaxException`: Caught during validation and wrapped in `IllegalArgumentException`.
