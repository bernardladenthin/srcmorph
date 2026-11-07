### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:15Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Provides strategies for processing source files that exceed the model’s context window.
- Parses configuration tokens into enum values.

#### Type
- `public enum AiOversizeStrategy`  
  - Modifiers: `public`  
  - Supertypes: `java.lang.Enum<AiOversizeStrategy>`  
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - Field: `private final String configValue`  
  - Constructor: `AiOversizeStrategy(String configValue)`  
  - Methods: `public String configValue()`, `public static AiOversizeStrategy fromConfig(String value)`  
  - Static field: `public static final AiOversizeStrategy DEFAULT = FAIL`

#### Input
- Constructor parameter: `String configValue` (strategy token)
- `fromConfig` parameter: `@Nullable String value` (config token)

#### Output
- `configValue()` returns the strategy’s token string.
- `fromConfig` returns an `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig`  
  1. If `value` is `null` → return `DEFAULT`.  
  2. Trim `value`; if empty → return `DEFAULT`.  
  3. Iterate over `values()`; compare `strategy.configValue` with `trimmed` case‑insensitively.  
  4. Return matching strategy.  
  5. If none match → throw `IllegalArgumentException` with message listing valid tokens.

#### Public API
- `configValue() -> String` – returns the config token for the strategy.  
- `fromConfig(String) -> AiOversizeStrategy` – parses a config token into a strategy.  
- `DEFAULT: AiOversizeStrategy` – default strategy (FAIL).

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when token is non‑blank and unrecognized.

#### Concurrency
- None.
