### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:01:56Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic, language-agnostic "facts" block containing exact regex match counts from a source file, prepended to AI summaries.

#### Purpose
*   Constructs a formatted facts block listing exact counts of regex matches for configured counters over a source string.
*   Validates counter configurations to ensure non-null labels and compilable patterns before usage.

#### Type
*   **Kind:** `public final class`
*   **Modifiers:** `final`, `utility class` (not instantiable)
*   **Annotations:** `@Nullable` (jspecify)

#### Input
*   `List<AiFactCounter>`: List of configured fact counters (can be null or empty).
*   `String source`: The full source text to analyze for regex matches.

#### Output
*   `String`: A formatted facts block string (e.g., `**Facts (exact, whole file):** label: count; label: count`) or empty string.
*   `void`: Side effect of throwing `IllegalArgumentException` upon validation failure.

#### Core logic
*   Validates that all counters have non-null labels and compilable regex patterns.
*   Iterates through counters, skipping null entries or those with missing labels/patterns.
*   Counts non-overlapping regex matches for each valid pattern against the source text.
*   Assembles results into a string with a specific header, separator, and suffix format.
*   Returns an empty string if no valid counters exist or no matches are found.

#### Public API
*   `factsBlock(List<AiFactCounter>, String) -> String`: Generates the facts block string.
*   `validate(List<AiFactCounter>) -> void`: Validates counter configuration and throws on error.

#### Dependencies
*   `java.util.List`
*   `java.util.regex.Matcher`
*   `java.util.regex.Pattern`
*   `java.util.regex.PatternSyntaxException`
*   `org.jspecify.annotations.Nullable`
*   `AiFactCounter`

#### Exceptions / Errors
*   `IllegalArgumentException`: Thrown if a counter lacks a label, lacks a pattern, or has an invalid regex pattern.
*   `PatternSyntaxException`: Caught internally during pattern compilation and re-thrown as `IllegalArgumentException`.

#### Concurrency
*   No concurrency concerns; operates on immutable strings and uses thread-safe `StringBuilder`.
