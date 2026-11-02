### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:27Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures how oversized source files are handled when they exceed a routed model’s context window.

#### Purpose
- Defines per-routing-rule strategies for oversized source files, from build failure to sampling, chunked summarization, or deterministic output.
- Parses `<onOversize>` configuration tokens, defaulting to `FAIL`.

#### Type
- `public enum AiOversizeStrategy`; no superclass or interfaces declared.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `public static final AiOversizeStrategy DEFAULT = FAIL`.

#### Input
- `fromConfig(@Nullable String value)` accepts an `<onOversize>` token; values are matched case-insensitively after trimming.
- Enum constructor accepts each strategy’s configuration token.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig()` returns the matching strategy or `DEFAULT` for null/blank input.

#### Core logic
- Maps strategies to tokens: `FAIL` → `fail`, `SAMPLE` → `sample`, `MAP_REDUCE` → `mapReduce`, `DETERMINISTIC` → `deterministic`.
- `FAIL` is the default; documented behaviors are aborting, sampling the file head, chunking and combining summaries, or producing model-free metadata and head/tail samples.
- `fromConfig()` trims input, returns the default for null or blank, searches tokens case-insensitively, and rejects unknown values.

#### Public API
- `DEFAULT` -> `AiOversizeStrategy` — default strategy.
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig()` throws `IllegalArgumentException` for non-blank values that match no strategy; the error includes the supplied value and expected tokens.

#### Concurrency
- Enum constants and their token fields are immutable; no mutable shared state.
