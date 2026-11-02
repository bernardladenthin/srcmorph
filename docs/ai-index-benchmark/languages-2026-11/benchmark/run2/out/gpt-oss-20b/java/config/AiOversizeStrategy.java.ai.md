### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:39Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window.

#### Purpose
- Configures oversized‑file handling per routing rule.
- Provides default and alternative strategies.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: none

#### Input
- Constructor parameter `String configValue` for each enum constant.
- `fromConfig(String)` accepts a nullable config string.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching strategy or throws `IllegalArgumentException` for unknown values.

#### Core logic
- `fromConfig` trims and normalizes input; returns `DEFAULT` for null/blank.
- Iterates over `values()` to match case‑insensitively.
- Throws `IllegalArgumentException` if no match.

#### Public API
- `configValue() -> String` – returns config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
