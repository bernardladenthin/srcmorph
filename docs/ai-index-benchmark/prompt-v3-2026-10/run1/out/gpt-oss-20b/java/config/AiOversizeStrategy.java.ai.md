### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:38Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed the model's context window, including failure, sampling, map‑reduce, and deterministic metadata emission.

#### Purpose
- Provides oversize handling options for routing rules.
- Determines build behavior when source exceeds context window.

#### Type
- `public enum AiOversizeStrategy`  
  - extends `java.lang.Enum`  
  - no interfaces, generics, or annotations.

#### Input
- Constructor: `AiOversizeStrategy(String configValue)`  
- `fromConfig(@Nullable String value)` receives optional config string.

#### Output
- `configValue()` returns strategy token string.  
- `fromConfig(String)` returns matching `AiOversizeStrategy`.  
- Public constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`, `DEFAULT`.

#### Core logic
- `fromConfig` trims input; if null or empty, returns `DEFAULT`.  
- Iterates over `values()`; matches `configValue` case‑insensitively.  
- Throws `IllegalArgumentException` for unknown tokens.

#### Public API
- `FAIL` -> strategy token  
- `SAMPLE` -> strategy token  
- `MAP_REDUCE` -> strategy token  
- `DETERMINISTIC` -> strategy token  
- `DEFAULT` -> default strategy  
- `configValue()` -> `String` (strategy token)  
- `fromConfig(String)` -> `AiOversizeStrategy` (parses config)

#### Dependencies
- `org.jspecify.annotations.Nullable`  
- `java.lang.Enum`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` on unrecognized token.

#### Concurrency
- Immutable enum; thread‑safe.
