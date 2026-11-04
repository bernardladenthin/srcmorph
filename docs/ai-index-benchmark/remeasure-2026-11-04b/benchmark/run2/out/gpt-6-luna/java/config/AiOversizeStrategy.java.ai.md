### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:50Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures how source files exceeding a routed model’s context window are handled: fail, sample, map-reduce, or emit a deterministic summary.

#### Purpose
- Defines the `<onOversize>` strategy tokens and default for oversized source files.
- Parses configured tokens into an `AiOversizeStrategy`.

#### Type
- `public enum AiOversizeStrategy` — enum; no explicit supertypes.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `DEFAULT` — `public static final AiOversizeStrategy`.

#### Input
- `fromConfig(@Nullable String value)` consumes a configured `<onOversize>` token; accepts `null` and blank strings.
- Constructor accepts a `String configValue`.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(...)` returns the matching strategy or `DEFAULT`.
- Stores each strategy’s token in private final field `configValue`.

#### Core logic
- `fromConfig` returns `DEFAULT` when `value` is `null` or `value.trim().isEmpty()`.
- Iterates over `values()` and compares `strategy.configValue.equalsIgnoreCase(trimmed)`.
- Throws `IllegalArgumentException` for an unknown non-blank token.
- Token mappings: `FAIL` → `"fail"`, `SAMPLE` → `"sample"`, `MAP_REDUCE` → `"mapReduce"`, `DETERMINISTIC` → `"deterministic"`.

#### Public API
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse config token.
- `DEFAULT: AiOversizeStrategy` — default strategy.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — strategy constants.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` if a non-blank value matches no strategy; the error includes the original value.

#### Concurrency
- Enum constants and `configValue` are immutable.
