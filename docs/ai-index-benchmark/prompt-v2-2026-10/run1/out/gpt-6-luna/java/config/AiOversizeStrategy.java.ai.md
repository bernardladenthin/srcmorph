### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:06Z
- G: 
- A: 0.0.0
- X: file
---
> Configures how oversized source files are handled, from failing the build to sampling, map-reduce summarization, or deterministic output.

#### Purpose
- Defines per-routing-rule strategies for source files exceeding a model’s context window.
- Defaults to failing; supports explicit handling without automatic model selection.

#### Type
- `public enum AiOversizeStrategy` — enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts the configured `<onOversize>` token.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(...)` returns the matching strategy or the default for null/blank input.

#### Core logic
- Stores a config token per strategy: `fail`, `sample`, `mapReduce`, or `deterministic`.
- `fromConfig` trims input and matches tokens case-insensitively.
- `FAIL` aborts; `SAMPLE` summarizes the head; `MAP_REDUCE` summarizes line-boundary chunks and combines results; `DETERMINISTIC` emits metadata and head/tail samples without a model.

#### Public API
- `FAIL` — aborts oversized-file processing.
- `SAMPLE` — summarizes a trimmed file head.
- `MAP_REDUCE` — summarizes chunks and combines results.
- `DETERMINISTIC` — emits model-free deterministic output.
- `DEFAULT` — default strategy, `FAIL`.
- `configValue()` -> `String` — returns configuration token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parses configured token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank values that match no strategy.
