### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:16Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Routing rules can choose how oversized source files are handled, from aborting or sampling to chunked summarization or deterministic output.

#### Purpose
- Defines the strategies and configuration tokens for handling source files larger than a routed model’s context window.
- Parses the `<onOversize>` setting, defaulting to `FAIL`.

#### Type
- `AiOversizeStrategy` — public enum.
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
  - Notable annotation: `@Nullable` on `fromConfig`’s parameter.

#### Input
- Enum constructor receives a `String configValue` for each strategy.
- `fromConfig(String value)` accepts a nullable configuration value.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(String)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- `DEFAULT` is `FAIL`.
- `configValue()` returns the instance’s `configValue`.
- `fromConfig` returns `DEFAULT` for null or trimmed-empty input.
- Otherwise, iterates over `values()` and compares each `strategy.configValue` to the trimmed input using `equalsIgnoreCase`.
- Throws `IllegalArgumentException` when no strategy matches.

#### Public API
- `FAIL` — abort oversized-file processing.
- `SAMPLE` — use a trimmed head sample.
- `MAP_REDUCE` — summarize chunks and combine summaries.
- `DETERMINISTIC` — emit model-free output.
- `DEFAULT: AiOversizeStrategy` — default strategy.
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank unknown values.
