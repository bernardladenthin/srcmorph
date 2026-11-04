### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:01Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Controls oversized file handling per routing rule.
- Offers abort, sample, map‑reduce, or deterministic strategies.

#### Type
`public enum AiOversizeStrategy`  
- constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
- field: `private final String configValue`  
- field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
- constructor: `AiOversizeStrategy(String)`  
- methods: `String configValue()`  
- static method: `AiOversizeStrategy fromConfig(String)`  

#### Input
- `configValue` string per constant.
- `fromConfig` receives a nullable `String` value from `<onOversize>` config.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig` returns the matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown tokens.

#### Core logic
- `fromConfig` trims input, defaults to `DEFAULT` if null/blank.
- Iterates over `values()` comparing `configValue` case‑insensitively.
- Throws `IllegalArgumentException` with message listing valid tokens if no match.

#### Public API
- `configValue() -> String` returns strategy token.  
- `fromConfig(String) -> AiOversizeStrategy` parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
