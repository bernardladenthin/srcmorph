### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:03Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Determines action when a source file exceeds the model’s context window.

#### Type
`public enum AiOversizeStrategy` – enum, public, final, implements `java.lang.Enum`.

#### Input
- `String value` in `fromConfig(String)` – optional config token.

#### Output
- `AiOversizeStrategy` instance from `fromConfig`.
- `String` token from `configValue()`.

#### Core logic
- Enum constants map to config tokens (`fail`, `sample`, `mapReduce`, `deterministic`).
- `configValue()` returns the token.
- `fromConfig(String)`:
  1. Return `DEFAULT` if `value` is `null` or blank.
  2. Trim and compare case‑insensitively against each constant’s token.
  3. Return matching constant or throw `IllegalArgumentException` if none match.

#### Public API
- `configValue() -> String` – get config token.
- `fromConfig(String) -> AiOversizeStrategy` – parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `fromConfig` for unknown tokens.

#### Concurrency
- Immutable enum; thread‑safe.
