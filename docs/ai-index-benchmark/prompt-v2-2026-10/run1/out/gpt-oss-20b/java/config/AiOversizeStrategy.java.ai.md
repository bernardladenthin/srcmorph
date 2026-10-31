### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:00Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for oversized source file handling.
- Provides parsing from configuration strings.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- constructor: `AiOversizeStrategy(String configValue)`  
- methods: `public String configValue()`, `public static AiOversizeStrategy fromConfig(@Nullable String value)`

#### Input
- Constructor: `String configValue` per enum constant.
- `fromConfig`: optional `String value` from `<onOversize>` config.

#### Output
- `configValue()`: returns the strategy's config token.
- `fromConfig`: returns matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig` trims and checks for null/blank → returns `DEFAULT`.
- Iterates over `values()` comparing `configValue` case‑insensitively.
- Throws `IllegalArgumentException` if no match.

#### Public API
- `configValue() -> String` – get config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for unknown strategy tokens.

#### Concurrency
- Immutable enum; thread‑safe.
