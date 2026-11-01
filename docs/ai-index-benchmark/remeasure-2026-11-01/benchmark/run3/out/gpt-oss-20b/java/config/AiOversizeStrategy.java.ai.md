### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:02Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window.

#### Purpose
- Enumerates oversize handling strategies for routing rules.
- Provides parsing from configuration strings.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- constructor: `AiOversizeStrategy(String)`  
- methods: `public String configValue()`, `public static AiOversizeStrategy fromConfig(String)`

#### Input
- Constructor parameter: `configValue` string for each enum constant.
- `fromConfig` parameter: `@Nullable String value` from configuration.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig` returns the matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown values.

#### Core logic
- `fromConfig` trims and checks for null/blank; iterates over enum values comparing `configValue` case‑insensitively; returns match or throws exception.

#### Public API
- `configValue() -> String` – returns config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
