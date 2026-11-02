### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:24Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Configures per‑routing rule handling for files exceeding model context windows.

#### Type
- `public enum AiOversizeStrategy`  
  - constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
  - field: `private final String configValue`  
  - static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
  - methods: `configValue()`, `fromConfig(String)`

#### Input
- Constructor argument `configValue` for each enum constant.  
- `fromConfig(String)` receives a configuration string (nullable).

#### Output
- `configValue()` returns the strategy’s config token.  
- `fromConfig(String)` returns the matching enum constant or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig` trims and checks for null/blank → returns `DEFAULT`.  
- Iterates over `values()` comparing `configValue` case‑insensitively.  
- If no match, throws `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
