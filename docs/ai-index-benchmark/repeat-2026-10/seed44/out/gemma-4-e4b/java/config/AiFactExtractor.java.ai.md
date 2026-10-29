### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:02:17Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language-agnostic counts of specific structural elements within a source text based on configured regular expressions.

#### Purpose
* Provides utility methods to generate a structured "facts" block from regex counters.
* Ensures configuration validity for `AiFactCounter` lists before fact extraction.

#### Type
final class AiFactExtractor

#### Input
* `factsBlock(List<AiFactCounter> counters, String source)`: List of `AiFactCounter`s, source text string.
* `validate(List<AiFactCounter> counters)`: List of `AiFactCounter`s.

#### Output
* `factsBlock`: String (the facts block, or empty string).
* `validate`: void (throws `IllegalArgumentException` on failure).

#### Core logic
* `factsBlock`: Iterates through `AiFactCounter`s, skips null/unlabeled/unpatterned counters. Appends label, separator, and count (from `countMatches`) to a `StringBuilder`. Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX`.
* `countMatches`: Compiles the provided regex `pattern` and iterates over all non-overlapping matches in the `source` string using `Matcher.find()`, returning the total count.
* `validate`: Iterates through `AiFactCounter`s, checks for null label/pattern, and attempts to compile the pattern using `Pattern.compile()`, throwing `IllegalArgumentException` on failure.

#### Public API
factsBlock(List<AiFactCounter>, String) -> String: Builds the facts block from counters.
validate(List<AiFactCounter>) -> void: Validates counter labels and regex patterns.

#### Dependencies
List, AiFactCounter, Pattern, Matcher, PatternSyntaxException

#### Exceptions / Errors
* `factsBlock`: Returns "" if input list is null or empty.
* `validate`: Throws IllegalArgumentException if counter lacks label/pattern or pattern is invalid.

#### Concurrency
No explicit concurrency mechanisms present; methods are static and operate on provided inputs.

#### Dependencies
List, AiFactCounter, Pattern, Matcher, PatternSyntaxException
