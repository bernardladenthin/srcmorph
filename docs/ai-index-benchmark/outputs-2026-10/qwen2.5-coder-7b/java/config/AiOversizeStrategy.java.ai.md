### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:57Z
- G: 
- A: 0.0.0
- X: file
---
> Configures handling strategies for source files larger than their routed model's context window.

#### Purpose
- Defines an enumeration of strategies for handling oversized source files.
- Provides a default strategy and a method to parse configuration values into corresponding strategies.

#### Type
- Enum (`AiOversizeStrategy`)
- Public

#### Input
- `value` (String): Configured value for the strategy, parsed by `fromConfig`.

#### Output
- `configValue` (String): Returns the configuration token for the strategy.
- `DEFAULT` (AiOversizeStrategy): The default strategy when no valid configuration is provided.

#### Core logic
- Enumerates possible strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- Parses a configuration value to determine the corresponding strategy.
- Returns the default strategy if the input value is null or blank.
- Throws an `IllegalArgumentException` if the input value does not match any strategy.

#### Public API
- `configValue() -> String`: Returns the configuration token for the strategy.
- `fromConfig(value: String) -> AiOversizeStrategy`: Parses a configuration value into the corresponding strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown when an unknown strategy is provided in the configuration.

#### Concurrency
- None
