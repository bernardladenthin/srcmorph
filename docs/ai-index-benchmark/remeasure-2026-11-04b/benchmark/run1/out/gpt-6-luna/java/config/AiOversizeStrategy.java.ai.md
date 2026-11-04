### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures how source files exceeding a routed model’s context window are handled: fail, sample, summarize chunks, or emit deterministic metadata.

#### Purpose
- Maps `<onOversize>` configuration tokens to oversized-source handling strategies.
- Provides `FAIL` as the default when the configuration is absent or blank.

#### Type
- `public enum AiOversizeStrategy` — enum; constants `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.

#### Input
- `fromConfig(@Nullable String value)` — configured strategy token; accepts `null`, blank, and case-insensitive tokens.

#### Output
- `configValue()` returns the strategy’s configuration token.
- `fromConfig(...)` returns the matching enum constant or `DEFAULT`; no mutable state or external resource writes.

#### Core logic
- Each enum constant stores its token in private final field `configValue`; `DEFAULT` is `FAIL`.
- `fromConfig` returns `DEFAULT` for `null` or trimmed-empty input.
- Otherwise, iterates over `values()` and matches `configValue` using `equalsIgnoreCase(trimmed)`.
- If no token matches, throws `IllegalArgumentException` containing the original input and expected tokens.

#### Public API
- `FAIL` — abort build; requires a larger-context model.
- `SAMPLE` — summarize the trimmed file head in one call.
- `MAP_REDUCE` — summarize line-boundary chunks, then combine summaries.
- `DETERMINISTIC` — produce model-free metadata and head/tail sample.
- `DEFAULT: AiOversizeStrategy` — default strategy (`FAIL`).
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.IllegalArgumentException`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values not matching `fail`, `sample`, `mapReduce`, or `deterministic`.
- `null` and blank values resolve to `DEFAULT`.

#### Concurrency
- Enum constants and their `configValue` values are immutable; no synchronization or concurrent operations.
