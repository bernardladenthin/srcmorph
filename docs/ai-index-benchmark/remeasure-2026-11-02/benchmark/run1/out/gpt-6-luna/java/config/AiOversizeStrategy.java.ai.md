### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:49Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Routing rules can choose how oversized source files are handled: fail, sample the head, summarize chunks, or emit deterministic metadata.

#### Purpose
- Defines the `<onOversize>` strategies for source files exceeding a routed model’s context window.
- Defaults to `FAIL` when the setting is absent or blank.

#### Type
- `AiOversizeStrategy` — public enum; four constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `DEFAULT` is `FAIL`; each constant stores a private final `String configValue`.

#### Input
- Enum constructor receives each strategy’s config token.
- `fromConfig(@Nullable String value)` receives a configured token; trims whitespace and matches case-insensitively.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching strategy, or `DEFAULT` for null or blank input.

#### Core logic
- Associates `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` with tokens `fail`, `sample`, `mapReduce`, and `deterministic`.
- Parses nonblank input by comparing its trimmed value against each token, ignoring case.
- Throws `IllegalArgumentException` when no token matches.

#### Public API
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token.
- `DEFAULT` — default strategy (`FAIL`).

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for nonblank unknown values; error message includes the original value and expected tokens.

#### Concurrency
- Enum constants and their config tokens are immutable.
