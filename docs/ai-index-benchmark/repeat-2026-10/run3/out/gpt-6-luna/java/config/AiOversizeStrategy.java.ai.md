### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:19:56Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed model’s context window are handled.

#### Purpose
- Defines per-rule `<onOversize>` strategies for oversized source files.
- Defaults to aborting the build unless another strategy is configured.

#### Type
- Public enum `AiOversizeStrategy`.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; public default `DEFAULT = FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts a configured strategy token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates each strategy with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Parses tokens case-insensitively after trimming whitespace.
- `FAIL` aborts; `SAMPLE` summarizes the trimmed file head; `MAP_REDUCE` summarizes line-boundary chunks and combines results; `DETERMINISTIC` emits metadata and head/tail samples without a model.

#### Public API
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configured strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for a non-blank value that matches no strategy.
