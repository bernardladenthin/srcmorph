### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:06Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed model’s context window are handled.

#### Purpose
- Defines per-routing-rule `<onOversize>` strategies, including failure, sampling, map-reduce, and model-free deterministic output.
- Uses `FAIL` when no strategy is configured.

#### Type
- `AiOversizeStrategy` — enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `DEFAULT` is a public static final alias for `FAIL`.
- `@Nullable` annotates the configuration parser input.

#### Input
- `fromConfig(@Nullable String value)` accepts a configuration token; null and blank values select the default.
- Each enum constant is initialized with its configuration token.

#### Output
- `configValue()` returns the enum constant’s configuration token.
- `fromConfig(...)` returns the matching `AiOversizeStrategy`.

#### Core logic
- Associates each strategy with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims configured values and matches tokens case-insensitively.
- Returns `DEFAULT` for null or blank input; otherwise returns the matching strategy.
- Documents strategy behavior: abort, summarize the file head, chunk and combine summaries, or emit a deterministic metadata/head-tail body.

#### Public API
- `FAIL` — abort oversized-file processing.
- `SAMPLE` — summarize the trimmed file head.
- `MAP_REDUCE` — summarize chunks and combine results.
- `DETERMINISTIC` — emit a model-free deterministic body.
- `DEFAULT` — default strategy (`FAIL`).
- `configValue() -> String` — return the configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse a configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for non-blank values that match no strategy.
