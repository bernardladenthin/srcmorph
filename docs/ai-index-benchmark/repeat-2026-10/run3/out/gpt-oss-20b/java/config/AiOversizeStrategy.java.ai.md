### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:52:37Z
- G: 
- A: 0.0.0
- X: file
---
> Determines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Configures oversized‑file handling per routing rule.
- Provides default and alternative strategies for AI processing.

#### Type
- `enum` AiOversizeStrategy  
  - Modifiers: `public`  
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - Field: `private final String configValue`  
  - Static field: `public static final AiOversizeStrategy DEFAULT = FAIL`

#### Input
- Constructor parameter `String configValue` for each enum constant.
- `fromConfig(String)` receives a configuration token (nullable).

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching enum constant or throws `IllegalArgumentException`.

#### Core logic
- Store a string token per strategy.
- `configValue()` simply returns this token.
- `fromConfig(String)`:
  1. Return `DEFAULT` if input is `null` or blank after trim.
  2. Iterate over all enum values.
  3. Compare each strategy’s `configValue` case‑insensitively to the trimmed input.
  4. Return the matching strategy.
  5. If none match, throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns strategy token  
- `fromConfig(String) -> AiOversizeStrategy` – parses config token

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
