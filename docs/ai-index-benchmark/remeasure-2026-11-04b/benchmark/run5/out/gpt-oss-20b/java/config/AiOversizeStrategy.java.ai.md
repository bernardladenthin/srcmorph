### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:44Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for oversized source files.
- Provides default strategy and parsing from config.

#### Type
- `public enum AiOversizeStrategy`  
  - Modifiers: `public`  
  - Supertypes: `java.lang.Enum<AiOversizeStrategy>`  
  - Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - Static field: `public static final AiOversizeStrategy DEFAULT`  
  - Instance field: `private final String configValue`  
  - Constructor: `AiOversizeStrategy(String configValue)`  
  - Methods:  
    - `public String configValue()`  
    - `public static AiOversizeStrategy fromConfig(@Nullable String value)`

#### Input
- Constructor parameter: `String configValue` (strategy token).
- `fromConfig` parameter: `@Nullable String value` (config token).

#### Output
- `configValue()` returns strategy token string.
- `fromConfig` returns matching `AiOversizeStrategy` or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim `value`; if empty → return `DEFAULT`.
  3. Iterate over `values()`; compare `strategy.configValue` with `trimmed` case‑insensitively.
  4. Return matching strategy.
  5. If none match → throw `IllegalArgumentException` with message listing valid tokens.

#### Public API
- `FAIL(String)` – abort build.  
- `SAMPLE(String)` – trim and summarize head.  
- `MAP_REDUCE(String)` – chunk, summarize, combine.  
- `DETERMINISTIC(String)` – emit deterministic metadata.  
- `DEFAULT` – default strategy (`FAIL`).  
- `configValue()` → strategy token.  
- `fromConfig(String)` → strategy from config.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
