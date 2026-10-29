### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:41Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed a model's context window during AI routing.

#### Purpose
- Configures behavior when a source file is larger than the routed model's context window.
- Provides options to fail, sample, map-reduce, or emit deterministic metadata for oversized files.

#### Type
- `public enum AiOversizeStrategy`
- Key constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`, `DEFAULT`
- Annotation: `@Nullable` on method parameter

#### Input
- Constructor parameter: `configValue` (String)
- Static method `fromConfig` parameter: `value` (String, nullable)

#### Output
- `configValue()` returns the configuration token string.
- `fromConfig` returns the matching `AiOversizeStrategy` or `DEFAULT`.
- Throws `IllegalArgumentException` for invalid tokens.

#### Core logic
- Enum constants map specific tokens ("fail", "sample", "mapReduce", "deterministic") to strategies.
- `fromConfig` performs case-insensitive matching of input string against enum `configValue` fields.
- Null or blank input defaults to `FAIL` strategy.
- Invalid input triggers an exception listing valid options.

#### Public API
- `configValue() -> String` returns the config token.
- `fromConfig(String) -> AiOversizeStrategy` parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown if input string does not match any strategy.
- Null or empty input handled gracefully by returning `DEFAULT`.

#### Concurrency
- Enum constants are inherently thread-safe.
- No mutable state or synchronization required.
