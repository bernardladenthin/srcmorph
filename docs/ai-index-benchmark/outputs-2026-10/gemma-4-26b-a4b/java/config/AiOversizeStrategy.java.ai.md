### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:35:10Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the context window of a routed AI model.

#### Purpose
* Determines the processing behavior (abort, sample, chunk, or deterministic) when a file is too large for the target model.

#### Type
Enum; implements no interfaces.

#### Input
* `fromConfig(String)`: A nullable, potentially blank string from configuration.
* `AiOversizeStrategy` constructor: A string token representing the strategy.

#### Output
* `configValue()`: The string token associated with the strategy.
* `fromConfig(String)`: A matching `AiOversizeStrategy` instance or the `DEFAULT`.

#### Core logic
* `FAIL`: Aborts the build process.
* `SAMPLE`: Trims the file to the window size and processes the head.
* `MAP_REDUCE`: Splits file into line-boundary chunks, summarizes chunks, and combines summaries.
* `DETERMINISTIC`: Bypasses AI to emit metadata and head/tail samples.
* `fromConfig` parsing: Performs case-insensitive matching against `configValue` after trimming; defaults to `FAIL` if input is null or blank.

#### Public API
* `DEFAULT -> AiOversizeStrategy` The fallback strategy (FAIL).
* `configValue() -> String` Returns the strategy's configuration token.
* `fromConfig(String) -> AiOversizeStrategy` Parses string into a strategy.

#### Dependencies
* `org.jspecify.annotations.Nullable`
* `java.lang.IllegalArgumentException`

#### Exceptions / Errors
* Throws `IllegalArgumentException` if a non-blank string does not match any known strategy token.
