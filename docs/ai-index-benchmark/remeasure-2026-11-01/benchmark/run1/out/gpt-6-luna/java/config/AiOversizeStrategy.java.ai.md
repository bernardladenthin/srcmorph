### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:21Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed model’s context window are handled.

#### Purpose
- `AiOversizeStrategy` defines per-routing-rule policies for oversized source files.
- Defaults to aborting rather than automatically choosing another model.

#### Type
- `AiOversizeStrategy` — public enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.

#### Input
- `fromConfig(String)` accepts an optional `<onOversize>` token.

#### Output
- Each strategy exposes its configuration token through `configValue()`.
- `fromConfig` returns the matching strategy, or `DEFAULT` for null or blank input.

#### Core logic
- Associates each strategy with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Parses tokens case-insensitively after trimming whitespace.
- Represents four policies: fail the build, summarize the file head, summarize line-boundary chunks then combine, or emit deterministic metadata and samples.

#### Public API
- `FAIL` — abort on oversized input.
- `SAMPLE` — summarize the file head.
- `MAP_REDUCE` — summarize chunks and combine.
- `DETERMINISTIC` — emit a model-free body.
- `DEFAULT` — default strategy (`FAIL`).
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank, unrecognized values.
