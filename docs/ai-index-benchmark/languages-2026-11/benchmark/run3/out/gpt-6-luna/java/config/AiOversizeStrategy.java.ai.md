### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:42Z
- G: 
- A: 0.0.0
- X: file
---
> Configures how source files exceeding an AI model’s context window are handled during routing.

#### Purpose
- Defines oversize-file policies: fail, sample the file head, map-reduce chunks, or produce deterministic output.
- Provides config-token parsing and a default policy.

#### Type
- `AiOversizeStrategy` — public enum.
- Four constants; `DEFAULT` is `FAIL`.
- `@Nullable` marks the `fromConfig` parameter as nullable.

#### Input
- `fromConfig(String value)` consumes an `<onOversize>` token; accepts `null`, blank, and case-insensitive strategy names.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns a matching strategy, defaulting to `FAIL` for `null` or blank input.

#### Core logic
- Associates each strategy with a config token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims input and matches tokens case-insensitively.
- Throws `IllegalArgumentException` for nonblank, unknown tokens.

#### Public API
- `FAIL` — abort on oversized files.
- `SAMPLE` — process the file head.
- `MAP_REDUCE` — summarize chunks, then combine.
- `DETERMINISTIC` — emit model-free metadata and samples.
- `DEFAULT` — default strategy, `FAIL`.
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` when a nonblank value matches no strategy.
