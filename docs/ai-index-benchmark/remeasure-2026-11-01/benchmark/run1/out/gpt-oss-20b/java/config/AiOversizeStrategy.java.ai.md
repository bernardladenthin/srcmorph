### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:25:36Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window.

#### Purpose
- Configures oversize handling per routing rule.
- Provides default and parsing logic for strategy tokens.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: none

#### Input
- `fromConfig(String value)` receives a nullable config string.

#### Output
- `configValue()` returns the strategy’s token string.  
- `fromConfig(String)` returns an `AiOversizeStrategy` or throws `IllegalArgumentException`.

#### Core logic
- `configValue()` simply returns the stored token.  
- `fromConfig(String)`:
  1. If `value` is null or blank → return `DEFAULT`.  
  2. Trim and compare case‑insensitively against each enum’s `configValue`.  
  3. Return matching strategy.  
  4. If none match → throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – get strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
