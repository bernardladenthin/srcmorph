### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:02:23Z
- G: 
- A: 0.0.0
- X: file
---
> Defines an enumeration of strategies for handling source files that exceed the context window of a routed AI model, including options to abort, sample the head, map-reduce chunks, or emit deterministic metadata.

#### Purpose
*   Enumerates strategies for processing oversized source files in the `AiOversizeStrategy` domain.
*   Provides configuration parsing and default behavior for routing rules.

#### Type
*   Enum `AiOversizeStrategy` with four constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
*   Contains a static final field `DEFAULT` initialized to `FAIL`.
*   Uses `org.jspecify.annotations.Nullable` for parameter typing.

#### Input
*   `fromConfig(String value)`: accepts a null or blank string, or a case-insensitive strategy token.
*   Constructor `AiOversizeStrategy(String configValue)`: accepts a strategy name string.

#### Output
*   `fromConfig(String value)`: returns an `AiOversizeStrategy` enum constant or throws `IllegalArgumentException`.
*   `configValue()`: returns the lowercase string identifier for the strategy.
*   `DEFAULT`: returns the `FAIL` strategy constant.

#### Core logic
*   `fromConfig` checks for null or empty input, defaulting to `FAIL`.
*   Iterates through enum values to find a case-insensitive match for the input string.
*   Throws `IllegalArgumentException` if the input matches no known strategy.
*   `SAMPLE` strategy trims source to the window and summarizes the head in a single call.
*   `MAP_REDUCE` strategy splits the file into line-boundary chunks, summarizes each, and combines partial summaries.
*   `DETERMINISTIC` strategy emits metadata and head/tail samples without using an AI model.

#### Public API
*   `configValue() -> String` (returns the strategy identifier)
*   `fromConfig(String value) -> AiOversizeStrategy` (parses config token)
*   `DEFAULT -> AiOversizeStrategy` (returns default strategy)

#### Dependencies
*   `org.jspecify.annotations.Nullable`
*   `IllegalArgumentException`

#### Exceptions / Errors
*   `IllegalArgumentException` thrown by `fromConfig` when the input string is non-blank and invalid.

#### Concurrency
*   Thread-safe due to immutable enum values and static final fields.
