### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:32Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled, from aborting the build to sampling, chunked summarization, or deterministic output.

#### Purpose
- Defines per-routing-rule `<onOversize>` strategies for source files exceeding a model context window.
- Defaults to failing rather than automatically choosing another model.

#### Type
- `AiOversizeStrategy` — public enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.
- Notable annotation: `@Nullable` on `fromConfig`’s parameter.

#### Input
- Enum constructor receives each strategy’s config token.
- `fromConfig(@Nullable String value)` accepts a configured token; null and blank are allowed.

#### Output
- `configValue()` returns the token for a strategy.
- `fromConfig(...)` returns the matching strategy or `DEFAULT`.
- Strategy choices describe failure, head sampling, line-boundary map-reduce, or model-free deterministic output; this enum does not perform those operations.

#### Core logic
- Associates `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` with `fail`, `sample`, `mapReduce`, and `deterministic`.
- `fromConfig` returns `DEFAULT` for null or trimmed blank input.
- Matches tokens case-insensitively after trimming; throws for unrecognized nonblank input.

#### Public API
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse config token.
- `DEFAULT` — default strategy constant.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank values that match no strategy.

#### Concurrency
- Enum constants and `configValue` are immutable; no mutable shared state.
