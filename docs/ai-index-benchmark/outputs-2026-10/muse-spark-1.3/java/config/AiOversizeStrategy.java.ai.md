### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:14:28Z
- G: 
- A: 0.0.0
- X: file
---
> Handles oversized source files exceeding AI model context window via fail, sample, map-reduce, or deterministic fallback.
#### Purpose
- Defines `AiOversizeStrategy` handling for source files larger than routed model context window.
- Parses per-routing-rule `<onOversize>` config token via `fromConfig`.
#### Type
- enum `AiOversizeStrategy` with 4 constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Modifiers: public enum, package-private constructor.
- Fields: 2 total — 1 static `DEFAULT`, 1 instance `configValue`.
- Annotations: none on type; `@Nullable` on method parameter.
#### Input
- Constructor `AiOversizeStrategy(String configValue)`: config token `fail`/`sample`/`mapReduce`/`deterministic`.
- `fromConfig(@Nullable String value)`: `<onOversize>` string, null or blank allowed.
- Consumed field `configValue` for case-insensitive match in `fromConfig`.
#### Output
- `configValue() -> String`: returns `<onOversize>` token e.g. `mapReduce`.
- `fromConfig(String) -> AiOversizeStrategy`: returns matching strategy or `DEFAULT`.
- Side effects: none; no state mutation, no I/O.
#### Core logic
- `FAIL` default aborts build, requires larger-context model routing.
- `SAMPLE` trims source head to window for single-call summary.
- `MAP_REDUCE` splits file at line boundaries into window chunks, summarizes each, combines summaries.
- `DETERMINISTIC` emits model-free body with size, line count, head/tail sample.
- `fromConfig` trims input, returns `DEFAULT` on null/blank, case-insensitive `configValue` match, else throws.
#### Public API
- `configValue() -> String` — returns config token.
- `fromConfig(String value) -> AiOversizeStrategy` — parses onOversize token.
#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.String`, `java.lang.IllegalArgumentException`
- `net.ladenthin.srcmorph.config.AiOversizeStrategy` self-reference in Javadoc
#### Exceptions / Errors
- Throws `IllegalArgumentException` on non-blank unknown `onOversize` value with expected list.
- Null-handling: null returns `DEFAULT`; blank/whitespace-only returns `DEFAULT`.
#### Concurrency
- Immutable enum with final `configValue`; thread-safe without synchronization.
