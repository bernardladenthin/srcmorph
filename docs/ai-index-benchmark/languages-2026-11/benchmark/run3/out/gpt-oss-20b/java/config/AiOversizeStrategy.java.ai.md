### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:24Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window.

#### Purpose
- Configures oversized‑file handling per routing rule.
- Provides default, sample, map‑reduce, and deterministic strategies.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: none

#### Input
- Constructor argument `configValue` for each enum constant.
- `fromConfig(String)` receives a nullable config string.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching enum constant or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig` trims and checks for null/blank → returns `DEFAULT`.  
- Iterates over `values()` comparing `configValue` case‑insensitively.  
- If no match, throws `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config string.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if config string is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
