### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:34Z
- G: 
- A: 0.0.0
- X: file
---
> Parses per-rule oversize handling strategy (fail, sample, map-reduce, deterministic) for files exceeding an AI model's context window.

#### Purpose
- Defines how a routing rule handles source files larger than the routed model's context window.
- Maps the `<onOversize>` config string to a strategy constant.

#### Type
- `enum` `AiOversizeStrategy`, public.
- Implements none; no `extends`.
- Annotations: `@Nullable` (org.jspecify) on `fromConfig` parameter.

#### Input
- Constant constructor argument: `configValue` (String).
- `fromConfig(@Nullable String value)`: config string from `<onOversize>`, may be `null` or blank.

#### Output
- `configValue()` -> `String`: config token for the strategy.
- `fromConfig(String)` -> `AiOversizeStrategy`: matching enum constant or `DEFAULT`.
- Constant `DEFAULT` = `FAIL`.

#### Core logic
- Four constants: `FAIL("fail")`, `SAMPLE("sample")`, `MAP_REDUCE("mapReduce")`, `DETERMINISTIC("deterministic")`.
- `FAIL` aborts the build; user must route to a larger-context model.
- `SAMPLE` trims to window, summarizes head in one call.
- `MAP_REDUCE` chunks at line boundaries, summarizes each chunk, combines partial summaries; bounded by a `maxChunks` cap (referenced in docs, not in this file).
- `DETERMINISTIC` emits model-free body (metadata, head/tail sample).
- `fromConfig`: null or trimmed-empty input returns `DEFAULT`; otherwise case-insensitive match against `configValue`; no match throws.

#### Public API
- `configValue() -> String`: returns config token.
- `fromConfig(String value) -> AiOversizeStrategy`: parses config token, case-insensitive.
- `DEFAULT: AiOversizeStrategy`: default strategy constant (`FAIL`).
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`: strategy constants (4 total).

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `IllegalArgumentException` (java.lang)
- `AiOversizeStrategy.values()`

#### Exceptions / Errors
- `IllegalArgumentException` when value is non-blank and matches no strategy; message lists valid tokens.
- `null` and blank input are accepted and yield `DEFAULT`.

#### Concurrency
- Immutable: `configValue` is `private final`, set in constructor.
- Enum constants are inherently thread-safe; no shared mutable state.
