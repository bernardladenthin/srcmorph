### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:34Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for processing source files that exceed the model’s context window.
- Provides default strategy and parsing from configuration.

#### Type
- `public enum AiOversizeStrategy`  
  - Modifiers: `public`  
  - Supertypes: `java.lang.Enum<AiOversizeStrategy>`  
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - Static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
  - Instance field: `private final String configValue`  
  - Constructor: `AiOversizeStrategy(String configValue)`  
  - Methods:  
    - `public String configValue()` – returns token  
    - `public static AiOversizeStrategy fromConfig(@Nullable String value)` – parses token

#### Input
- Constructor parameter: `String configValue` per enum constant.
- `fromConfig` parameter: `@Nullable String value` (config token).

#### Output
- `configValue()` returns the token string.
- `fromConfig` returns an `AiOversizeStrategy` or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim `value`; if empty → return `DEFAULT`.
  3. Iterate over `values()`; compare `strategy.configValue` case‑insensitively to trimmed token.
  4. Return matching strategy.
  5. If none match → throw `IllegalArgumentException` with message listing valid tokens.

#### Public API
- `FAIL(String)` – abort build (default).  
- `SAMPLE(String)` – trim head, single call.  
- `MAP_REDUCE(String)` – chunk, summarize, combine.  
- `DETERMINISTIC(String)` – emit deterministic body.  
- `DEFAULT: AiOversizeStrategy` – default strategy.  
- `configValue() -> String` – get config token.  
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parse config token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when token is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
