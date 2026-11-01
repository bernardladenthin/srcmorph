### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:16Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled by AI model routing rules.

#### Purpose
- Defines the configured policy for source files exceeding a routed model’s context window.
- Defaults to aborting; also supports head sampling, chunked map-reduce, and deterministic model-free output.

#### Type
- `public enum AiOversizeStrategy` — enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts the `<onOversize>` configuration token.

#### Output
- Each strategy exposes its configuration token through `configValue()`.
- `fromConfig` returns a matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates each strategy with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims configured input and matches tokens case-insensitively.
- Describes policies: abort; summarize the trimmed head; summarize line-boundary chunks then combine; or emit deterministic metadata and head/tail sample.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.
- `DEFAULT` — default strategy (`FAIL`).
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values that match no strategy.
