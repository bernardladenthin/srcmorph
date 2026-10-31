### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:50:03Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Controls oversized file handling per routing rule.
- Offers abort, sample, map‑reduce, or deterministic strategies.

#### Type
`public enum AiOversizeStrategy`  
- constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
- field: `private final String configValue`  
- static final: `DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: `@Nullable` on parameter.

#### Input
- `fromConfig(String)` receives a configuration token (`null`, blank, or string).

#### Output
- `fromConfig(String)` returns an `AiOversizeStrategy` instance.  
- `configValue()` returns the strategy’s config token string.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim and check for emptiness → return `DEFAULT`.
  3. Iterate over `values()`; compare `configValue` case‑insensitively.
  4. Return matching strategy or throw `IllegalArgumentException` with message listing valid tokens.

#### Public API
- `configValue() -> String` – returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if non‑blank value does not match any strategy.

#### Concurrency
- Immutable enum; thread‑safe.
