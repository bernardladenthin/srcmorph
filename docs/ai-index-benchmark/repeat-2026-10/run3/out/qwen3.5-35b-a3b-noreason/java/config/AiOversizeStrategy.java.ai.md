### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:41Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed a model's context window during AI-assisted code routing.

#### Purpose
- Configures behavior when a source file is larger than the routed model's context window.
- Provides options to fail, sample, map-reduce, or emit deterministic metadata for oversized files.

#### Type
- `public enum AiOversizeStrategy`
- Key constants: `DEFAULT` (alias for `FAIL`), `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Annotated with `@Nullable` on method parameters.

#### Input
- `value` (String): Configuration string from `<onOversize>` token, potentially null or blank.
- `configValue` (String): Internal string token associated with each enum constant.

#### Output
- `configValue()`: Returns the string token (e.g., "mapReduce").
- `fromConfig(String)`: Returns the matching `AiOversizeStrategy` or `DEFAULT`.
- `IllegalArgumentException`: Thrown if the input string does not match any known strategy.

#### Core logic
- Enum constants map specific handling behaviors to string identifiers.
- `fromConfig` performs case-insensitive matching of input strings against `configValue` of all enum constants.
- Null or blank input defaults to `FAIL` strategy.
- `MAP_REDUCE` strategy implies splitting files at line boundaries, summarizing chunks, and combining results.
- `SAMPLE` strategy implies trimming the file head to fit the context window.
- `DETERMINISTIC` strategy implies generating metadata without AI model interaction.

#### Public API
- `configValue() -> String` returns the configuration token.
- `fromConfig(String) -> AiOversizeStrategy` parses and resolves the strategy.
- `values() -> AiOversizeStrategy[]` (inherited) returns all constants.
- `valueOf(String) -> AiOversizeStrategy` (inherited) returns the enum constant.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if the input string is non-blank and does not match any strategy.
- Null-safe handling: `null` or empty strings return `DEFAULT`.

#### Concurrency
- Enum constants are inherently thread-safe.
- No mutable state or synchronization required.
