### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:01Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed a model's context window during AI routing.

#### Purpose
- Configures behavior when a source file is larger than the routed model's context window.
- Provides parseable tokens for build configuration to handle oversized files.

#### Type
- `public enum AiOversizeStrategy`
- Key constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`, `DEFAULT`.
- Annotation: `@Nullable` on method parameter.

#### Input
- Constructor: `configValue` (String) - the configuration token.
- Method `fromConfig`: `value` (String) - the raw configuration string from XML or properties.

#### Output
- `configValue()`: Returns the lowercase string token (e.g., "mapReduce").
- `fromConfig(String)`: Returns the matching `AiOversizeStrategy` enum constant or `DEFAULT`.
- Side effects: Throws `IllegalArgumentException` on invalid input.

#### Core logic
- Enum constants map specific behaviors to string tokens:
  - `FAIL`: Aborts build, requires manual routing.
  - `SAMPLE`: Trims file to window head for single-call summarization.
  - `MAP_REDUCE`: Splits file at line boundaries, summarizes chunks, combines results.
  - `DETERMINISTIC`: Emits metadata (size, line count, head/tail) without AI.
- `fromConfig` performs case-insensitive matching against `configValue` of all enum constants.
- Null or blank input defaults to `FAIL`.

#### Public API
- `configValue() -> String` returns the configuration token string.
- `fromConfig(String) -> AiOversizeStrategy` parses and returns the strategy.

#### Dependencies
- `net.ladenthin.srcmorph.config` (package)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if `fromConfig` receives a non-blank string that does not match any strategy.
- Null safety: `fromConfig` handles `null` input by returning `DEFAULT`.

#### Concurrency
- No concurrency concerns; stateless enum constants.
