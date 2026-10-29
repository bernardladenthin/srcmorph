### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T00:34:22Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a deterministic, language-agnostic "facts" block from a list of {@link AiFactCounter}s.

#### Purpose
- **Purpose**: Generates a facts block from a list of fact counters, providing exact, whole-file counts of regex matches.

#### Type
- **Kind**: class
- **Modifiers**: final

#### Input
- **Parameters**: 
  - `counters`: List of {@link AiFactCounter} objects, potentially null or empty.
  - `source`: Full source text to be searched.

#### Output
- **Return**: String representing the facts block, or an empty string if no counters are provided.

#### Core logic
- **Validate**: Ensure each counter has a non-null label and a compilable pattern.
- **Build Facts Block**: 
  - Iterate through counters, skipping null or invalid ones.
  - Append each counter's label and count of matches to a StringBuilder.
  - Return the formatted facts block string.

#### Public API
- **factsBlock(List<AiFactCounter> counters, String source) -> String**: Builds the facts block for given counters over the source text.
- **validate(List<AiFactCounter> counters) -> void**: Validates the counters' labels and patterns.

#### Dependencies
- **Imports**: 
  - `java.util.List`
  - `java.util.regex.Matcher`
  - `java.util.regex.Pattern`
  - `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- **Throws**: 
  - `IllegalArgumentException` if a counter is missing its label or pattern, or has an invalid regex.

#### Concurrency
- **Thread Safety**: The class is final and does not maintain state, making it inherently thread-safe.
