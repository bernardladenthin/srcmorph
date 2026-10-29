### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:43Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic facts block by counting non-overlapping regex matches across a source text based on configured counters.

#### Purpose
* Provides utility methods to generate structured fact blocks from regex counters.
* Ensures configuration validity for `AiFactCounter` lists.

#### Type
final class AiFactExtractor;

#### Input
* `factsBlock(List<AiFactCounter> counters, String source)`: List of `AiFactCounter`s, source text string.
* `validate(List<AiFactCounter> counters)`: List of `AiFactCounter`s.

#### Output
* `factsBlock`: String (the facts block, or empty string).
* `validate`: void (throws exceptions on invalid configuration).

#### Core logic
* `factsBlock`: Iterates through `AiFactCounter`s, skips null/unlabeled/unpatterned counters. Appends label, separator, and match count for each counter to a `StringBuilder`. Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX`.
* `countMatches`: Compiles the provided regex `pattern` and uses `Matcher.find()` iteratively to count non-overlapping matches in the `source`.
* `validate`: Iterates through `AiFactCounter`s, checks for null labels/patterns, and attempts `Pattern.compile()` to catch `PatternSyntaxException`.

#### Public API
factsBlock(counters, source) -> String: Builds the facts block from counters.
validate(counters) -> void: Validates counter labels and regex patterns.

#### Dependencies
List, AiFactCounter, Pattern, Matcher, PatternSyntaxException

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `validate` if a counter lacks a label, lacks a pattern, or has an invalid regex pattern.
* Null handling: `factsBlock` handles null or empty `counters` list by returning "".

#### Concurrency
No explicit concurrency mechanisms present; methods are static and operate on local state or provided immutable inputs.
