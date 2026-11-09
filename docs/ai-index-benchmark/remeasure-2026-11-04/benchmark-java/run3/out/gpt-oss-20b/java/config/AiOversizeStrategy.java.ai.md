### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:55Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Determines action when a source file exceeds the model’s context window.

#### Type
`public enum AiOversizeStrategy` – enum, final, implements `java.lang.Enum`.

#### Input
- `String value` in `fromConfig(String)` (nullable, trimmed).

#### Output
- `AiOversizeStrategy` instance from `fromConfig`.
- `String` token via `configValue()`.

#### Core logic
- Enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` each with a config token.
- `DEFAULT` set to `FAIL`.
- `configValue()` returns the token.
- `fromConfig(String)`:
  1. Return `DEFAULT` if `value` is `null` or blank after trim.
  2. Iterate over `values()`; compare `configValue` case‑insensitively.
  3. Return matching strategy.
  4. Throw `IllegalArgumentException` if no match.

#### Public API
- `configValue() -> String` – get config token.
- `fromConfig(String) -> AiOversizeStrategy` – parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` in `fromConfig` for unknown strategy.

#### Concurrency
- Immutable enum; thread‑safe.
