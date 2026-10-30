### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:36Z
- G: 
- A: 0.0.0
- X: file
---
> Configures how source files exceeding an AI model’s context window are handled.

#### Purpose
- `AiOversizeStrategy` defines routing-rule behavior for oversized source files.
- `DEFAULT` is `FAIL`; alternatives sample, map-reduce, or produce deterministic output.

#### Type
- Public enum: `AiOversizeStrategy`
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`
- `DEFAULT` is a public static final strategy constant.

#### Input
- `fromConfig(String value)` accepts an `@Nullable` configuration token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig()` returns a matching strategy or `DEFAULT` for null or blank input.

#### Core logic
- Associates each enum constant with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims input and matches tokens case-insensitively.
- Does not itself perform oversized-file handling.

#### Public API
- `configValue() -> String` — returns configuration token
- `fromConfig(String) -> AiOversizeStrategy` — parses configuration token
- `DEFAULT -> AiOversizeStrategy` — provides default strategy

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `String`

#### Exceptions / Errors
- `fromConfig()` throws `IllegalArgumentException` for nonblank values that match no strategy.
