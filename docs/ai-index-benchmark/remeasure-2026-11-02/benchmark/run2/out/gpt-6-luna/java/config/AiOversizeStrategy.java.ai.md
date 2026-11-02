### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:16Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Routes oversized source files through failure, head sampling, chunked summarization, or deterministic reporting.

#### Purpose
- Defines per-rule handling for source files exceeding a routed model’s context window.
- Uses `FAIL` when no `<onOversize>` strategy is configured.

#### Type
- `public enum AiOversizeStrategy`; no superclass or implemented interfaces declared.
- Enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `@Nullable` annotates the nullable configuration input.

#### Input
- `fromConfig(@Nullable String value)` accepts an `<onOversize>` token.
- Enum constructor receives each strategy’s configuration token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns a matching strategy or `DEFAULT`.

#### Core logic
- Associates each strategy with a token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- `fromConfig` returns `DEFAULT` for `null` or blank input.
- Trims input and matches tokens case-insensitively.
- `FAIL` aborts; `SAMPLE` summarizes the trimmed head; `MAP_REDUCE` summarizes line-boundary chunks and combines summaries; `DETERMINISTIC` emits metadata and head/tail samples without a model.

#### Public API
- `DEFAULT` — default strategy, `FAIL`.
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank input that matches no strategy.

#### Concurrency
- Enum constants and their token fields are immutable; safe to share across threads.
