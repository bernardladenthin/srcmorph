### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:31Z
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
  - Static field: `public static final AiOversizeStrategy DEFAULT`  
  - Methods: `public String configValue()`, `public static AiOversizeStrategy fromConfig(@Nullable String value)`  
  - Annotation: `@Nullable` on `fromConfig` parameter.

#### Input
- Constructor parameter `configValue` (String) for each enum constant.
- `fromConfig` parameter `value` (`@Nullable String`).

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig` returns an `AiOversizeStrategy` or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig`  
  1. If `value` is `null` → return `DEFAULT`.  
  2. Trim `value`; if empty → return `DEFAULT`.  
  3. Iterate over `values()`; compare `strategy.configValue` to trimmed string case‑insensitively.  
  4. Return matching strategy.  
  5. If none match → throw `IllegalArgumentException` with message listing valid tokens.

#### Public API
- `FAIL(String)` – abort build (default).  
- `SAMPLE(String)` – summarize head of file.  
- `MAP_REDUCE(String)` – chunk and summarize.  
- `DETERMINISTIC(String)` – emit deterministic metadata.  
- `DEFAULT: AiOversizeStrategy` – default strategy.  
- `configValue() -> String` – get config token.  
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parse token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when token is unknown.

#### Concurrency
- None.
