### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:20Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Controls routing behavior for files exceeding model context limits.

#### Type
- `public enum AiOversizeStrategy`  
  final enum, no generics, implements `java.lang.Enum<AiOversizeStrategy>`.

#### Input
- Constructor parameter `String configValue` for each enum constant.
- `fromConfig(String)` receives a nullable config string.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns an `AiOversizeStrategy` instance or throws `IllegalArgumentException`.

#### Core logic
- Enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` with associated tokens.
- `DEFAULT` set to `FAIL`.
- `configValue()` simply returns the token.
- `fromConfig(String)`:
  1. Return `DEFAULT` if input is `null` or blank after trim.
  2. Iterate over `values()` comparing case‑insensitively to `configValue`.
  3. Return matching strategy.
  4. Throw `IllegalArgumentException` if no match.

#### Public API
- `configValue() -> String` – returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config string.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` for unknown tokens.

#### Concurrency
- Immutable enum; thread‑safe.
