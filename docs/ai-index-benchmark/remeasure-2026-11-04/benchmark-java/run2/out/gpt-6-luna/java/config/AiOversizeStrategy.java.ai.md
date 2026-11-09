### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:25Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures how source files exceeding a routed model’s context window are handled: fail, sample the head, summarize chunks, or emit deterministic metadata.

#### Purpose
- Provides the `<onOversize>` strategy for oversized source files.
- Defaults to aborting the build unless a routing rule selects another strategy.

#### Type
- `public enum AiOversizeStrategy`; four strategies: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `public static final AiOversizeStrategy DEFAULT = FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts a configured strategy token.
- Each enum constant receives its config token through a package-private constructor.

#### Output
- `configValue()` returns the token associated with a strategy.
- `fromConfig(...)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates strategies with tokens: `fail`, `sample`, `mapReduce`, and `deterministic`.
- Trims configured input and matches tokens case-insensitively.
- Strategy behavior: abort; summarize trimmed file head; summarize line-boundary chunks then combine; or produce a model-free metadata and head/tail body.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` -> oversized-file handling options
- `DEFAULT` -> default strategy (`FAIL`)
- `configValue() -> String` — return config token
- `fromConfig(String) -> AiOversizeStrategy` — parse configured strategy

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values that match no strategy.
