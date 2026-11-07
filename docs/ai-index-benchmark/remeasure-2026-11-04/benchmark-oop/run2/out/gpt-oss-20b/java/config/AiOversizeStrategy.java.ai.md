### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:16Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Enumerates oversize handling strategies for source routing.
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
    - `public String configValue()`  
    - `public static AiOversizeStrategy fromConfig(@Nullable String value)`

#### Input
- Constructor parameter: `String configValue` (strategy token).
- `fromConfig` parameter: `@Nullable String value` (config token).

#### Output
- `configValue()` returns the strategy’s token string.
- `fromConfig` returns an `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig`:
  1. Return `DEFAULT` if `value` is `null` or blank after trimming.
  2. Iterate over `values()`; compare `configValue` case‑insensitively to trimmed input.
  3. Return matching strategy.
  4. Throw `IllegalArgumentException` if no match.

#### Public API
- `FAIL(String)` – abort build; default.  
- `SAMPLE(String)` – trim head to window, summarize once.  
- `MAP_REDUCE(String)` – chunk by lines, summarize each, combine.  
- `DETERMINISTIC(String)` – emit deterministic metadata.  
- `DEFAULT: AiOversizeStrategy` – default strategy.  
- `configValue() -> String` – get token.  
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parse config.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when token is unknown.

#### Concurrency
- No mutable shared state; enum constants are thread‑safe.
