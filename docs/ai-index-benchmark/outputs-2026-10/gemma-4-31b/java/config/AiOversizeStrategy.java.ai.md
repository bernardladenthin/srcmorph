### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:27:00Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed an AI model's context window.

#### Purpose
* Provide a set of behaviors (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) to resolve oversized source files during AI processing.
* Map configuration string tokens to specific oversize handling strategies.

#### Type
enum.

#### Input
* `fromConfig(String value)`: A nullable string representing the `<onOversize>` configuration token.

#### Output
* `configValue()`: The string token associated with the strategy.
* `fromConfig(String value)`: The matching `AiOversizeStrategy` instance.

#### Core logic
* `FAIL`: Default strategy; aborts the build to force the user to route the file to a larger-context model.
* `SAMPLE`: Trims the source to the context window and processes only the head of the file in one call.
* `MAP_REDUCE`: Splits the file into window-sized chunks at line boundaries, summarizes each chunk, and combines partial summaries in a final call.
* `DETERMINISTIC`: Bypasses the AI model to emit a deterministic body containing file size, line count, and head/tail samples.
* `fromConfig` parsing:
    * Returns `DEFAULT` (`FAIL`) if the input is `null` or blank.
    * Performs a case-insensitive match against the `configValue` of all defined enum constants.
    * Throws `IllegalArgumentException` if the non-blank input does not match any known strategy.

#### Public API
* `configValue() -> String`: Returns the config token.
* `fromConfig(String) -> AiOversizeStrategy`: Parses a config value into a strategy.
* `DEFAULT`: Static reference to `FAIL`.

#### Dependencies
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `fromConfig` when the provided string does not match any available `AiOversizeStrategy`.
