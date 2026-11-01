### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:19Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for oversized source files.
- Provides parsing from configuration strings.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`

#### Input
- Constructor parameter: `String configValue` for each enum constant.
- `fromConfig(String)` accepts a nullable configuration string.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching enum constant or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig` trims and checks for null/blank, then iterates over `values()` comparing case‑insensitively to `configValue`.  
- If no match, throws `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config string.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
