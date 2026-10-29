### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:52Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed the context window of a routed AI model.

#### Purpose
* Provide a set of predefined behaviors (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for oversized files.
* Map configuration string tokens to specific AI processing strategies.

#### Type
Enum.

#### Input
* `fromConfig(String)`: A nullable configuration string.
* `AiOversizeStrategy(String)`: Internal constructor for mapping config values.

#### Output
* `fromConfig(String)`: An `AiOversizeStrategy` instance.
* `configValue()`: The string token associated with the strategy.

#### Core logic
* **Strategy Definitions**:
    * `FAIL`: Aborts the build (default).
    * `SAMPLE`: Truncates file to window size and processes the head.
    * `MAP_REDUCE`: Splits file into line-boundary chunks, summarizes each, and aggregates results.
    * `DETERMINISTIC`: Generates a non-AI summary containing metadata and head/tail samples.
* **Parsing Logic**:
    * Returns `DEFAULT` (FAIL) if the input string is null or blank.
    * Performs a case-insensitive match against the `configValue` of all enum constants.
    * Throws `IllegalArgumentException` if a non-blank string does not match any known strategy.

#### Public API
* `configValue() -> String`: Returns the configuration token.
* `fromConfig(String) -> AiOversizeStrategy`: Parses config token to strategy.
* `DEFAULT`: Static constant referencing `FAIL`.

#### Dependencies
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `fromConfig` when the provided value is not a recognized strategy.
