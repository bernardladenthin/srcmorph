### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:56:18Z
- G: 
- A: 0.0.0
- X: file
---
> Enum defining strategies for handling source files larger than the model's context window.

#### Purpose
- Enumerate strategies to handle oversized source files during routing.
- Provide default behavior and configurable options.

#### Type
- `public enum AiOversizeStrategy`
- Implements `java.lang.Enum<AiOversizeStrategy>`

#### Input
- `configValue`: String representing the configured strategy value.

#### Output
- `configValue()`: Returns the configured strategy value.
- `fromConfig(String)`: Parses a string to return the corresponding strategy.

#### Core logic
- Defines four strategies: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Each strategy has a corresponding config value.
- `fromConfig` method parses a string to return the matching strategy.

#### Public API
- `FAIL("fail")`: Aborts the build; user must configure a larger-context model.
- `SAMPLE("sample")`: Trims the source to the window and summarizes the head.
- `MAP_REDUCE("mapReduce")`: Chunks at line boundaries, summarizes each chunk, combines summaries.
- `DETERMINISTIC("deterministic")`: Emits a model-free deterministic body.
- `DEFAULT`: The default strategy when none is configured.
- `configValue()`: Returns the config token for the strategy.
- `fromConfig(String)`: Parses a config value to return the matching strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown when an unknown strategy is provided.

#### Concurrency
- No concurrency considerations; enum is thread-safe.
