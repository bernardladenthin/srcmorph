### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:39:46Z
- G: 
- A: 0.0.0
- X: file
---
> Determines the build's response when a source file exceeds its routed AI model's context window, supporting fail, sample, map-reduce, or deterministic handling.

#### Purpose
- Encapsulates the `<onOversize>` routing-rule behavior for oversized source files.
- Provides case-insensitive parsing from config strings with `FAIL` as the default.

#### Type
- `public enum AiOversizeStrategy` in package `net.ladenthin.srcmorph.config`
- Enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` (4 total)
- Implicitly extends `java.lang.Enum<AiOversizeStrategy>`

#### Input
- `fromConfig(@Nullable String value)`: configured `<onOversize>` token; accepts `null` or blank
- Constructor `AiOversizeStrategy(String configValue)`: package-private, assigns the strategy's config token

#### Output
- `configValue()`: returns the stored `String` config token
- `fromConfig(String)`: returns matching `AiOversizeStrategy` or `DEFAULT` (`FAIL`) for null/blank input
- No mutable state; no fields are mutated after construction

#### Core logic
- Each enum constant maps to a config token: `fail`, `sample`, `mapReduce`, `deterministic`
- `fromConfig` trims the input, returns `DEFAULT` when null or empty after trim
- Otherwise iterates `values()` and compares tokens case-insensitively
- Unknown non-blank values throw `IllegalArgumentException` listing expected tokens
- `DEFAULT` is `FAIL`, which aborts the build unless a larger-context model is configured

#### Public API
- `DEFAULT -> AiOversizeStrategy` (build-abort default when unset)
- `configValue() -> String` (returns the config token)
- `fromConfig(@Nullable String) -> AiOversizeStrategy` (parses config token case-insensitively)

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.String`
- `java.lang.IllegalArgumentException`
- `java.lang.Enum`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank, unrecognized values
- `null` or blank input is handled gracefully and resolves to `DEFAULT`

#### Concurrency
- Enum constants and `final configValue` field are immutable; thread-safe for concurrent reads
