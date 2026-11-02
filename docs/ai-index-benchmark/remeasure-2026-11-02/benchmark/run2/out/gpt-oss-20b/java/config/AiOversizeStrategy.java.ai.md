### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:29Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Defines how oversized source files are handled during routing, offering abort, head‑only, chunked summarization, or deterministic metadata strategies.

#### Purpose
- Controls routing behavior for files exceeding model context limits.
- Provides configurable strategies per routing rule.

#### Type
`public enum AiOversizeStrategy` – enum with 4 constants, final, implements `java.lang.Enum`.

#### Input
- `fromConfig(String)` receives a configuration token (nullable, case‑insensitive).
- `configValue()` returns the token string for a strategy.

#### Output
- `fromConfig` returns an `AiOversizeStrategy` instance.
- `configValue` returns the strategy’s config string.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` or blank → return `DEFAULT` (`FAIL`).
  2. Trim and compare case‑insensitively against each enum’s `configValue`.
  3. Return matching strategy or throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – get strategy token.
- `fromConfig(String) -> AiOversizeStrategy` – parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` thrown when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe by design.
