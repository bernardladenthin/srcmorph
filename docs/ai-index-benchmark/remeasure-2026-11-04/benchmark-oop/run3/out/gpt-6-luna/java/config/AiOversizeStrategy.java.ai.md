### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:55Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Routing rules choose whether oversized source files fail, are sampled, are summarized in chunks, or receive a deterministic model-free summary.

#### Purpose
- Defines oversize handling strategies and parses routing-rule `<onOversize>` values.
- Defaults to aborting when no strategy is configured.

#### Type
- `AiOversizeStrategy` — enum; no supertypes explicitly declared.

#### Input
- `fromConfig(String value)` accepts a nullable configuration token.
- Enum constructor receives each strategy’s configuration token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(String)` returns the matching strategy or `DEFAULT`.

#### Core logic
- `FAIL` aborts processing; `SAMPLE` summarizes the trimmed file head in one call.
- `MAP_REDUCE` summarizes line-boundary chunks, then combines partial summaries.
- `DETERMINISTIC` emits metadata and head/tail samples without a model.
- `fromConfig` maps null, blank, or case-insensitive matching tokens to the default or corresponding strategy; unknown nonblank values cause an error.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.
- `DEFAULT: AiOversizeStrategy` — default strategy (`FAIL`).
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for an unknown nonblank token.
- Null and blank values resolve to `DEFAULT`.
