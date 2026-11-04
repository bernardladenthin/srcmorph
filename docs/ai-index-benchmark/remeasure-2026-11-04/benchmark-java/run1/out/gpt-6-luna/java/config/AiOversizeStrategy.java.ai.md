### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:33Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures how source files exceeding an AI model’s context window are handled, from aborting or sampling to chunked summarization or deterministic output.

#### Purpose
- Provides the per-routing-rule `<onOversize>` strategy for oversized source files.
- Uses `FAIL` when no strategy is configured.

#### Type
- `AiOversizeStrategy` — enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; public constant `DEFAULT = FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts the configured `<onOversize>` token.

#### Output
- `configValue()` returns a strategy’s configuration token.
- `fromConfig(...)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates strategies with tokens: `fail`, `sample`, `mapReduce`, `deterministic`.
- Parses tokens after trimming, using case-insensitive matching.
- Represents handling choices: abort; summarize the window-trimmed head; summarize line-boundary chunks then combine; or emit model-free deterministic metadata and samples.

#### Public API
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse strategy token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for non-blank, unrecognized tokens.
