### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:47Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures how source files exceeding an AI model’s context window are handled: fail, sample the head, summarize chunks, or emit deterministic metadata.

#### Purpose
- Defines per-routing-rule behavior for oversized source files.
- Uses `fail` as the default when no strategy is configured.

#### Type
- `public enum AiOversizeStrategy`; annotated input parameter with `@Nullable`.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Public default constant: `DEFAULT = FAIL`.

#### Input
- `fromConfig(String value)` accepts a nullable `<onOversize>` config token.

#### Output
- `configValue()` returns the config token for a strategy.
- `fromConfig(...)` returns the matching strategy or `DEFAULT` for null or blank input.

#### Core logic
- Stores each enum value’s config token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Parses config tokens case-insensitively after trimming whitespace.
- Represents oversized-file handling modes:
  - `FAIL`: abort and require a larger-context model.
  - `SAMPLE`: summarize the trimmed file head in one call.
  - `MAP_REDUCE`: summarize line-boundary chunks, then combine summaries.
  - `DETERMINISTIC`: produce model-free metadata and head/tail sample.

#### Public API
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token.
- `DEFAULT` — default strategy constant.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for a nonblank, unrecognized token.
