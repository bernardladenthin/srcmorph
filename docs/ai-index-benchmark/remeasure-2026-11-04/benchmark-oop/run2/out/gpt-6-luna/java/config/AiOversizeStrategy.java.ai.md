### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:35:54Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Routing rules choose whether oversized source files abort processing, use a head sample, use chunked summaries, or receive a deterministic model-free summary.

#### Purpose
- Defines oversized-file handling strategies and parses routing-rule `<onOversize>` values.
- Uses `FAIL` when no strategy is configured.

#### Type
- `public enum AiOversizeStrategy` — enum; no supertypes declared; `@Nullable` annotates the parser input.

#### Input
- Enum constructor receives a `String configValue` for each strategy.
- `fromConfig(@Nullable String value)` consumes the configured strategy token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns the matching strategy; `null` or blank input returns `DEFAULT`.
- `DEFAULT` is the public constant `FAIL`.

#### Core logic
- Associates `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` with `fail`, `sample`, `mapReduce`, and `deterministic`.
- Trims input; blank or `null` selects `DEFAULT`.
- Matches tokens case-insensitively against enum values.
- Throws `IllegalArgumentException` for an unknown non-blank token.

#### Public API
- `FAIL` — abort oversized-file processing.
- `SAMPLE` — summarize the trimmed file head in one call.
- `MAP_REDUCE` — summarize line-boundary chunks, then combine summaries.
- `DETERMINISTIC` — emit metadata and head/tail sample without a model.
- `DEFAULT: AiOversizeStrategy` — default strategy (`FAIL`).
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` when a non-blank value matches no strategy.
