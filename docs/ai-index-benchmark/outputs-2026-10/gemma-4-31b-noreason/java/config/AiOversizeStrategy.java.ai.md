### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:51:18Z
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
* `AiOversizeStrategy(String)`: Internal constructor for mapping config tokens.

#### Output
* `fromConfig(String)`: An `AiOversizeStrategy` instance.
* `configValue()`: The string token associated with the strategy.

#### Core logic
* **Strategy Definitions**:
    * `FAIL`: Aborts build; requires user to select a larger-context model.
    * `SAMPLE`: Truncates file to window size and processes the head.
    * `MAP_REDUCE`: Splits file into line-bounded chunks, summarizes each, and aggregates results.
    * `DETERMINISTIC`: Bypasses AI to emit metadata and head/tail samples.
* **Parsing Logic**:
    * Returns `DEFAULT` (FAIL) if input is null or blank.
    * Performs case-insensitive matching of trimmed input against `configValue` of all enum constants.
    * Throws `IllegalArgumentException` if a non-blank string does not match any known strategy.

#### Public API
* `configValue() -> String`: Returns the configuration token.
* `fromConfig(String) -> AiOversizeStrategy`: Parses config token to strategy.
* `DEFAULT`: Constant referencing the default `FAIL` strategy.

#### Dependencies
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `fromConfig` when the provided string does not match any defined strategy.
