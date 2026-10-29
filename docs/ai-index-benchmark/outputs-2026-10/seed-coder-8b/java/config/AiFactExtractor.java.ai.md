### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:11:51Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic "facts" block from a list of AI fact counters.

#### Purpose
- Generates a summary of code structure elements
- Provides exact counts for AI-assisted code analysis

#### Type
- `public final class`
- Implements pure logic without I/O

#### Input
- List of `AiFactCounter` objects
- Source text to analyze

#### Output
- Formatted facts block as a string
- Empty string if no counters or matches

#### Core logic
- Iterates through counters
- Compiles regex patterns
- Counts non-overlapping matches
- Formats results into a facts block

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String`
- `validate(List<AiFactCounter>) -> void`

#### Dependencies
- `java.util.List`
- `java.util.regex`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for invalid counters
- `PatternSyntaxException` for invalid regex patterns

#### Concurrency
- Thread-safe due to immutable operations
