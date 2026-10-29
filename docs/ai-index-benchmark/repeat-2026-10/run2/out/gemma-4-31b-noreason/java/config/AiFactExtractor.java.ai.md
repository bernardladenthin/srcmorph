### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:15Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic summary block of exact regex-based counts (e.g., number of functions or tables) from source code to supplement AI-generated summaries.

#### Purpose
* Provides a mechanism to extract precise, model-free "facts" from source text using regular expressions.
* Validates configuration of fact counters to ensure regexes are compilable.

#### Type
Final class. Utility class (private constructor).

#### Input
* `List<AiFactCounter>`: A list of counters containing labels and regex patterns.
* `String source`: The full source code text to be analyzed.

#### Output
* `String`: A formatted facts block starting with `FACTS_HEADER` and ending with `FACTS_SUFFIX`, or an empty string if no counters are provided/valid.
* Side effect: Throws `IllegalArgumentException` during validation of invalid patterns or missing labels.

#### Core logic
* **Fact Block Generation**:
    * Iterates through `AiFactCounter` list.
    * For each valid counter, executes a regex search across the entire `source` string.
    * Aggregates results into a semicolon-separated list of `label: count` pairs.
    * Prepends a standard header and appends a double-newline suffix.
* **Regex Counting**:
    * Uses `Pattern.compile` and `Matcher.find()` in a loop to count non-overlapping matches.
* **Validation**:
    * Checks for null labels or patterns.
    * Attempts to compile each regex pattern to catch `PatternSyntaxException` early.

#### Public API
* `factsBlock(List<AiFactCounter>, String) -> String`: Builds the formatted facts summary block.
* `validate(List<AiFactCounter>) -> void`: Ensures all counters have valid labels and compilable regexes.

#### Dependencies
* `java.util.List`
* `java.util.regex.Matcher`
* `java.util.regex.Pattern`
* `java.util.regex.PatternSyntaxException`
* `org.jspecify.annotations.Nullable`
* `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate` if a counter is missing a label, missing a pattern, or contains an invalid regular expression.
* `PatternSyntaxException`: Caught internally during validation and re-thrown as `IllegalArgumentException`.
