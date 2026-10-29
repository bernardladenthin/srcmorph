### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:26:33Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed an AI model's context window during routing.

#### Purpose
* Provide a set of predefined behaviors (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for oversized source files.
* Map configuration string tokens to specific AI processing strategies.

#### Type
Enum.

#### Input
* `fromConfig(String)`: A nullable string representing the configuration token.

#### Output
* `fromConfig(String)`: An `AiOversizeStrategy` instance.
* `configValue()`: The string token associated with the enum constant.

#### Core logic
* Maintains a mapping between enum constants and case-insensitive configuration strings (e.g., `MAP_REDUCE` maps to `"mapReduce"`).
* Implements a lookup mechanism in `fromConfig` that:
    * Returns `DEFAULT` (FAIL) if the input is null or blank.
    * Iterates through all enum constants to find a case-insensitive match for the trimmed input.
    * Throws an `IllegalArgumentException` if a non-blank string does not match any known strategy.

#### Public API
* `configValue() -> String`: Returns the configuration token.
* `fromConfig(String) -> AiOversizeStrategy`: Parses a config string into a strategy.
* `DEFAULT -> AiOversizeStrategy`: Constant representing the default (FAIL) strategy.

#### Dependencies
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `fromConfig` when the provided string does not match any defined `AiOversizeStrategy`.
