### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:11:36Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed model context windows during AI processing.

#### Purpose
- Specifies behavior for oversized source files in AI routing rules.
- Provides configuration parsing for oversize handling strategies.

#### Type
- Enum `AiOversizeStrategy`
- Implements no interfaces
- No generics or type bounds
- Notable annotations: `@Nullable` from `org.jspecify.annotations`

#### Input
- Constructor parameter: `final String configValue`
- Static method `fromConfig`: `final @Nullable String value`
- Method `configValue()`: no parameters

#### Output
- Constructor: initializes `configValue` field
- Method `configValue()`: returns `String configValue`
- Static method `fromConfig()`: returns `AiOversizeStrategy`

#### Core logic
- Enum defines four strategies for oversized files: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC
- `fromConfig` method performs case-insensitive lookup of strategy by config string
- Default strategy is FAIL when input is null or blank
- Throws `IllegalArgumentException` for unrecognized config values

#### Public API
- `configValue() -> String`: returns configuration token
- `fromConfig(value) -> AiOversizeStrategy`: parses configuration string

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `AiOversizeStrategy` (self-reference in `values()`)

#### Exceptions / Errors
- `IllegalArgumentException` thrown when config value doesn't match any strategy
- Null input handled gracefully with default strategy

#### Concurrency
- Enum is immutable and thread-safe
- No synchronization needed for public methods
