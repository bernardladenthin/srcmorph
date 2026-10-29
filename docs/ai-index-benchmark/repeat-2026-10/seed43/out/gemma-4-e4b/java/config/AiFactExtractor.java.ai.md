### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:55:15Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language-agnostic counts of specific structural elements within a source text based on configured regular expressions.

#### Purpose
* Provides utility methods to generate a structured "facts" block from `AiFactCounter` instances.
* Offers validation logic to ensure `AiFactCounter` configurations are sound before processing.

#### Type
final class AiFactExtractor;

#### Input
* `factsBlock(List<AiFactCounter> counters, String source)`: `counters` (List<AiFactCounter>), `source` (String).
* `validate(List<AiFactCounter> counters)`: `counters` (List<AiFactCounter>).
* `countMatches(String pattern, String source)`: `pattern` (String), `source` (String).

#### Output
* `factsBlock`: String (the facts block, or "" if no counters).
* `validate`: void (throws IllegalArgumentException on failure).
* `countMatches`: int (the number of non-overlapping matches).

#### Core logic
* `factsBlock`: Iterates through `AiFactCounter` list, skipping nulls/unconfigured counters. For each valid counter, it calls `countMatches` to get the count, then constructs the string using `FACTS_HEADER`, `ENTRY_SEPARATOR`, and `FACTS_SUFFIX`.
* `validate`: Iterates through `AiFactCounter` list, checking for null labels/patterns and attempting `Pattern.compile()` to catch `PatternSyntaxException`.
* `countMatches`: Compiles the input `pattern` into a `Pattern`, creates a `Matcher` against the `source`, and iterates using `matcher.find()` to tally non-overlapping matches.

#### Public API
factsBlock(counters, source) -> String: Builds the facts block from counters.
validate(counters) -> void: Validates counter labels and regex patterns.
countMatches(pattern, source) -> int: Counts non-overlapping matches of pattern.

#### Dependencies
List, AiFactCounter, Pattern, Matcher, PatternSyntaxException

#### Exceptions / Errors
* `validate`: Throws IllegalArgumentException if a counter lacks a label/pattern or has an invalid regex pattern (wrapping PatternSyntaxException).
* `factsBlock`: Returns "" if input `counters` is null or empty, or if no valid counters are present.

#### Concurrency
No explicit concurrency mechanisms present; methods are static and operate on input parameters.
