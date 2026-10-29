### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:03Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled, from aborting the build to sampling, chunk summarization, or deterministic output.

#### Purpose
- Defines the per-routing-rule `<onOversize>` policy for files exceeding a model’s context window.
- Uses `FAIL` as the default when no strategy is configured.

#### Type
- Public enum `AiOversizeStrategy`.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; each has a configuration token.
- Public static final field: `DEFAULT`, set to `FAIL`.

#### Input
- `fromConfig(@Nullable String value)` consumes a configured strategy token.

#### Output
- `configValue()` returns the token associated with an enum constant.
- `fromConfig(...)` returns the matching strategy or `DEFAULT` for null or blank input.

#### Core logic
- Associates `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` with `fail`, `sample`, `mapReduce`, and `deterministic`.
- Trims input and matches tokens case-insensitively.
- Returns `DEFAULT` for null or blank input; rejects unknown non-blank tokens.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — oversize handling policies.
- `DEFAULT` — default policy (`FAIL`).
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for an unknown non-blank token.

#### Concurrency
- Enum constants and configuration tokens are immutable; no mutable shared state.
