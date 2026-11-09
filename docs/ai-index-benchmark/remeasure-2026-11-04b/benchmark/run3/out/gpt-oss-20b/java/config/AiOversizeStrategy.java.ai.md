### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:28Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for oversized source files.
- Provides parsing from configuration strings.

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
- Constructor parameter: `String configValue` (strategy token)
- `fromConfig` parameter: `@Nullable String value` (config string)

#### Output
- `configValue()` returns strategy token string.
- `fromConfig` returns matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown values.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim `value`; if empty → return `DEFAULT`.
  3. Iterate `values()`; compare `strategy.configValue.equalsIgnoreCase(trimmed)`.
  4. Return matching strategy.
  5. If none match → throw `IllegalArgumentException` with message listing expected tokens.

#### Public API
- `FAIL(String)` – enum constant, token `"fail"`.
- `SAMPLE(String)` – enum constant, token `"sample"`.
- `MAP_REDUCE(String)` – enum constant, token `"mapReduce"`.
- `DETERMINISTIC(String)` – enum constant, token `"deterministic"`.
- `DEFAULT: AiOversizeStrategy` – default strategy.
- `configValue() -> String` – get token.
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parse config.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
