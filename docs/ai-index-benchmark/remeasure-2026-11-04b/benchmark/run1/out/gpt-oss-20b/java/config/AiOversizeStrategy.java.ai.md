### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:15Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Defines how oversized source files are handled during routing: abort, sample, map‑reduce, or deterministic metadata.

#### Purpose
- Provides strategies for processing source files that exceed the model’s context window.
- Parses strategy tokens from configuration and supplies default behavior.

#### Type
- `public enum AiOversizeStrategy`  
  - extends `java.lang.Enum<AiOversizeStrategy>`  
  - constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
  - fields:  
    - `public static final AiOversizeStrategy DEFAULT = FAIL`  
    - `private final String configValue`  
  - constructor: `AiOversizeStrategy(String configValue)`  
  - methods:  
    - `public String configValue()`  
    - `public static AiOversizeStrategy fromConfig(@Nullable String value)`  

#### Input
- `fromConfig` receives a `String value` (nullable) from configuration.

#### Output
- `fromConfig` returns an `AiOversizeStrategy` (one of the enum constants).  
- `configValue()` returns the strategy’s config token string.

#### Core logic
- `fromConfig`:
  1. If `value` is `null` → return `DEFAULT`.
  2. Trim `value`; if empty → return `DEFAULT`.
  3. Iterate `values()`; compare each `strategy.configValue` to trimmed value, case‑insensitive.
  4. Return matching strategy.
  5. If none match → throw `IllegalArgumentException` with message listing expected tokens.

#### Public API
- `configValue() -> String` – returns the config token for the strategy.  
- `fromConfig(@Nullable String) -> AiOversizeStrategy` – parses a config token into a strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when a non‑blank value does not match any strategy.

#### Concurrency
- No concurrency constructs; enum is immutable and thread‑safe.
