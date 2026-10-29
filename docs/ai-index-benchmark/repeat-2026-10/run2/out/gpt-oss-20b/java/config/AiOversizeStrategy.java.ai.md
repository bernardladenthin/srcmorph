### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:12:07Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Configures per‑routing rule handling for files exceeding model context windows.

#### Type
- `enum` AiOversizeStrategy  
  - constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
  - field: `private final String configValue`  
  - static field: `public static final AiOversizeStrategy DEFAULT = FAIL`

#### Input
- Constructor parameter `String configValue` for each enum constant.  
- `fromConfig(String)` receives a config token (nullable).

#### Output
- `configValue()` returns the token string.  
- `fromConfig(String)` returns a matching strategy or throws `IllegalArgumentException`.

#### Core logic
- Store a string token per strategy.  
- `configValue()` simply returns this token.  
- `fromConfig(String)`  
  1. Return `DEFAULT` if input is `null` or blank.  
  2. Trim input, compare case‑insensitively against each strategy’s token.  
  3. Return matching strategy.  
  4. Throw `IllegalArgumentException` if no match.

#### Public API
- `configValue() -> String` – get config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parse token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` from `fromConfig` on unknown token.

#### Concurrency
- Immutable enum; thread‑safe.
