### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:13Z
- G: 
- A: 0.0.0
- X: file
---
> Configures how oversized source files are handled: abort, sample the head, summarize chunks, or emit a deterministic summary.

#### Purpose
- Defines per-routing-rule `<onOversize>` strategies for source files exceeding a model’s context window.
- Defaults to aborting rather than automatically selecting another model.

#### Type
- `AiOversizeStrategy` — public enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; each stores a config token.
- `public static final AiOversizeStrategy DEFAULT` is `FAIL`.

#### Input
- Enum constructor receives a config token.
- `fromConfig(@Nullable String value)` receives a configured `<onOversize>` value.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(...)` returns the matching strategy or `DEFAULT` for null or blank input.

#### Core logic
- Matches nonblank config values against enum tokens, ignoring case after trimming whitespace.
- Tokens: `fail`, `sample`, `mapReduce`, `deterministic`.
- Strategy meanings: abort; summarize trimmed file head; summarize line-boundary chunks and combine; emit model-free metadata and head/tail sample.

#### Public API
- `FAIL` — abort on oversized files.
- `SAMPLE` — summarize the file head.
- `MAP_REDUCE` — summarize chunks and combine.
- `DETERMINISTIC` — emit a model-free body.
- `DEFAULT` — return the default strategy.
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig(...)` throws `IllegalArgumentException` for nonblank values that match no strategy.
