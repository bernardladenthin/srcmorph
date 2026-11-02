### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:04Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Configures oversized‑file handling per routing rule.  
- Provides default, sample, map‑reduce, and deterministic strategies.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static final: `DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`

#### Input
- `fromConfig(String value)` receives a config token (nullable, case‑insensitive).  
- Enum constants are initialized with their string tokens.

#### Output
- `configValue()` returns the token string.  
- `fromConfig(String)` returns the matching strategy or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig` trims and checks for null/blank → returns `DEFAULT`.  
- Iterates over `values()` comparing `configValue` case‑insensitively.  
- On match, returns strategy; otherwise throws `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` parses config token.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` thrown when `value` is non‑blank and does not match any strategy.

#### Concurrency
- Immutable enum; thread‑safe.
