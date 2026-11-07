### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:50Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Defines how to handle source files that exceed the context window of the routed model.

#### Purpose
- Enumerates oversize handling strategies for source‑file routing.
- Provides default strategy and parsing from configuration.

#### Type
- `public enum AiOversizeStrategy`  
  - Modifiers: `public`  
  - Supertypes: `java.lang.Enum<AiOversizeStrategy>`  
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - Field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
  - Field: `private final String configValue`  
  - Constructor: `AiOversizeStrategy(String configValue)`  
  - Methods:  
    - `public String configValue()` – returns token  
    - `public static AiOversizeStrategy fromConfig(@Nullable String value)` – parses token

#### Input
- Constructor parameter: `String configValue` per enum constant.
- `fromConfig` parameter: `@Nullable String value` (config token).

#### Output
- `configValue()` returns the token string.
- `fromConfig` returns matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim `value`; if empty → return `DEFAULT`.
  3. Iterate over `values()`; compare `configValue` case‑insensitively.
  4. Return matching strategy.
  5. If none match → throw `IllegalArgumentException` with message listing expected tokens.

#### Public API
- `configValue() -> String` – get config token  
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parse config token

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when token is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
