### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:30Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed model’s context window are handled.

#### Purpose
- Defines the per-rule `<onOversize>` policy for oversized source files.
- Defaults to aborting; also supports head sampling, chunked summarization, or deterministic output.

#### Type
- `public enum AiOversizeStrategy` — enum.
- Values: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.

#### Input
- `fromConfig(@Nullable String value)` consumes a configured strategy token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns a matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Stores a token for each strategy: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims configuration input and matches tokens case-insensitively.
- Throws `IllegalArgumentException` when a nonblank value matches no strategy.

#### Public API
- `configValue()` -> `String` — return configuration token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse configuration token.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.
- `DEFAULT` — default strategy constant.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for unknown nonblank tokens.
