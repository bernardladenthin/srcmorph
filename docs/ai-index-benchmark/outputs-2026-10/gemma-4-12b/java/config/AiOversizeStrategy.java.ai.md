### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:32:54Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the behavior for handling source files that exceed the context window of a target AI model.

#### Purpose
*   Defines strategies for processing oversized source files during model routing.
*   Provides configuration mapping for the `onOversize` setting.

#### Type
enum

#### Input
*   `fromConfig(String value)`: Accepts a nullable, case-insensitive string representing the strategy name.

#### Output
*   `configValue()`: Returns the string identifier for the enum constant.
*   `fromConfig(String value)`: Returns the matching `AiOversizeStrategy` or `DEFAULT`.

#### Core logic
*   `FAIL`: Aborts the build if the file is too large.
*   `SAMPLE`: Truncates the file to the context window and summarizes the head.
*   `MAP_REDUCE`: Splits the file into line-boundary chunks, summarizes each, and aggregates results.
*   `DETERMINISTIC`: Skips AI processing and emits metadata (size, line count, samples).
*   `fromConfig`: Performs case-insensitive matching of input strings to enum constants, defaulting to `FAIL` on null/blank input.

#### Public API
*   `DEFAULT`: Constant for the default strategy (FAIL).
*   `configValue() -> String`: Returns the configuration token.
*   `fromConfig(String value) -> AiOversizeStrategy`: Parses a string into a strategy.

#### Dependencies
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `fromConfig`: Throws `IllegalArgumentException` if the provided non-blank string does not match any known strategy.
