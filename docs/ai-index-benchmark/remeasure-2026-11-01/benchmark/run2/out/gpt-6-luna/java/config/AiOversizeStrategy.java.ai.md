### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:02Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled during AI model routing, from failing the build to sampling, chunked summarization, or deterministic output.

#### Purpose
- Defines the per-routing-rule `<onOversize>` strategies and their configuration tokens.
- Defaults to failing when no strategy is configured.

#### Type
- `AiOversizeStrategy` — public enum.
- Four strategies: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.

#### Input
- Enum constructor receives each strategy’s configuration token.
- `fromConfig` receives a nullable string from `<onOversize>` configuration.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig` returns a matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates each enum constant with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Parses configuration by trimming whitespace and matching tokens case-insensitively.
- Uses `FAIL` as the default strategy.
- Strategies represent aborting, head sampling, line-boundary map-reduce, or deterministic metadata and sampling.

#### Public API
- `FAIL` — abort oversized-file processing.
- `SAMPLE` — summarize the trimmed file head.
- `MAP_REDUCE` — summarize chunks, then combine summaries.
- `DETERMINISTIC` — emit a model-free body.
- `DEFAULT` — default strategy, `FAIL`.
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configured strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank unknown tokens.

#### Concurrency
- Enum constants and `DEFAULT` are immutable; no mutable shared state.
