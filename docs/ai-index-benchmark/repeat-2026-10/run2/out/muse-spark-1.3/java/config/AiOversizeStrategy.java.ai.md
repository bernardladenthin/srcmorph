### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:14:56Z
- G: 
- A: 0.0.0
- X: file
---
> Controls how oversized source files exceeding an AI model's context window are handled during summarization.
#### Purpose
- Defines oversize handling policy for routing rules via `<onOversize>` token.
- Parses config strings into strategies with FAIL default.
#### Type
- enum AiOversizeStrategy with 4 constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC.
- Modifiers: public enum, package-private constructor.
- Fields: 2 total — `DEFAULT`, `configValue`.
#### Input
- Constructor param: `configValue` String token (`fail`/`sample`/`mapReduce`/`deterministic`).
- `fromConfig(value: @Nullable String)`: null/blank config value, case-insensitive token.
- Consumed field: `strategy.configValue` during lookup loop over `values()`.
#### Output
- `configValue() -> String`: returns `<onOversize>` token.
- `fromConfig() -> AiOversizeStrategy`: returns matching strategy or DEFAULT.
- Produced state: immutable `configValue`, `DEFAULT=FAIL`.
#### Core logic
- FAIL aborts build requiring larger-context model routing.
- SAMPLE trims source head to window for single-call summary.
- MAP_REDUCE chunks at line boundaries, summarizes each, combines partials.
- DETERMINISTIC emits model-free metadata plus head/tail sample.
- fromConfig normalizes via trim, empty/null to DEFAULT, case-insensitive match, else reject.
#### Public API
- configValue() -> String — returns config token.
- fromConfig(String) -> AiOversizeStrategy — parses onOversize token.
#### Dependencies
- org.jspecify.annotations.Nullable
- String, IllegalArgumentException, AiOversizeStrategy enum values
#### Exceptions / Errors
- Throws IllegalArgumentException on unknown non-blank `onOversize` value with expected list.
- Null-handling: null/blank/whitespace returns DEFAULT without error.
#### Concurrency
- Immutable enum constants and final fields; thread-safe without synchronization.
