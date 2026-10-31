### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:03:49Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Controls oversized source file handling per routing rule.
- Provides strategies: fail, sample, map‑reduce, deterministic.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static final: `DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: none

#### Input
- `fromConfig(String value)` receives a configuration string (nullable, may be blank).

#### Output
- `configValue()` returns the strategy’s config token.  
- `fromConfig(String)` returns an `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown values.

#### Core logic
- `configValue()` simply returns the stored token.  
- `fromConfig(String)`:
  1. If `value` is `null` or blank → return `DEFAULT`.  
  2. Trim and compare case‑insensitively against each enum’s `configValue`.  
  3. Return matching strategy.  
  4. If none match → throw `IllegalArgumentException` with a descriptive message.

#### Public API
- `configValue() -> String` – get strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parse config string.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` when config string is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
