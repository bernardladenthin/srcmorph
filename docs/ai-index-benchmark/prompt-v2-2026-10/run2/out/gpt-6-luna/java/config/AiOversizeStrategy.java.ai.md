### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:48Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed AI model’s context window are handled.

#### Purpose
- Defines per-routing-rule oversize behavior: fail, sample the file head, map-reduce chunks, or emit deterministic output.
- Supplies the default strategy and parses `<onOversize>` configuration values.

#### Type
- `AiOversizeStrategy` — enum; no superclass or interfaces declared.

#### Input
- Enum constructor receives a configuration token.
- `fromConfig(@Nullable String value)` receives the configured strategy token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Defines `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC`, mapped to `fail`, `sample`, `mapReduce`, and `deterministic`.
- Sets `DEFAULT` to `FAIL`.
- Trims configuration input and compares tokens case-insensitively.
- Rejects nonblank values that match no strategy with `IllegalArgumentException`.

#### Public API
- `configValue()` -> `String` — return configuration token.
- `fromConfig(@Nullable String)` -> `AiOversizeStrategy` — parse configuration token.
- `DEFAULT` — default strategy constant.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for an unknown nonblank token.
- Null and blank values are handled by returning `DEFAULT`.

#### Concurrency
- Enum constants and their configuration tokens are immutable.
