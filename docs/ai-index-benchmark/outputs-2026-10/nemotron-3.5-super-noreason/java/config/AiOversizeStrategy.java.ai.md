### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:01:42Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how the build handles source files that exceed an AI model's context window, with strategies to fail, sample, map-reduce, or emit deterministic output.

#### Purpose
- Provides an enum for configuring per-rule behavior when a source file is oversized relative to its routed model's context window.
- Parses the `<onOversize>` configuration token into a concrete strategy.

#### Type
- `public enum AiOversizeStrategy`
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`
- Public static final field: `DEFAULT = FAIL`

#### Input
- `fromConfig(String value)`: the configured `<onOversize>` string, may be `null` or blank.
- Constructor `AiOversizeStrategy(String configValue)`: the internal config token for each constant.

#### Output
- `configValue()`: returns the `String` config token.
- `fromConfig(String)`: returns the matching `AiOversizeStrategy` constant, or `DEFAULT` for `null`/blank input.
- Throws `IllegalArgumentException` for non-blank unrecognized values.

#### Core logic
- `AiOversizeStrategy` maps each strategy to a lowercase config token (`fail`, `sample`, `mapReduce`, `deterministic`).
- `fromConfig` trims the input, returns `DEFAULT` when `null` or empty, otherwise iterates `values()` comparing case-insensitively.
- No match results in an `IllegalArgumentException` listing the expected tokens.

#### Public API
- `configValue() -> String` returns the config token.
- `fromConfig(String) -> AiOversizeStrategy` parses config value case-insensitively.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` when a non-blank value matches no strategy.

#### Concurrency
- Enum is immutable and thread-safe; no synchronization needed.
