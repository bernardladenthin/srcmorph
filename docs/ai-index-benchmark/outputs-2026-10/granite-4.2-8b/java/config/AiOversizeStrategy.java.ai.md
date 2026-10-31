### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:23:54Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Enumerates strategies for handling source files larger than the routed model's context window, with configurable behavior via a string token.

#### Purpose
- Defines four possible actions when a source file exceeds its model's context window size.
- Allows configuration per routing rule via `<onOversize>` token.
- Provides a default (`FAIL`) that aborts the build unless overridden.

#### Type
- `public enum AiOversizeStrategy` in package `net.ladenthin.srcmorph.config`.
- No abstract, final, or sealed modifiers.
- Stores a private `String configValue` per constant.

#### Input
- Constructor: `String configValue` (lowercase token like `"fail"`).
- Method `fromConfig`: `@Nullable String value` (may be `null` or blank).

#### Output
- Method `configValue()`: returns the stored `String` config token.
- Method `fromConfig`: returns an `AiOversizeStrategy` enum constant or throws `IllegalArgumentException`.
- No mutated state; all fields are private final.

#### Core logic
- Each constant holds a `configValue` string (`"fail"`, `"sample"`, `"mapReduce"`, `"deterministic"`).
- `fromConfig` trims input, returns `DEFAULT` for `null` or blank.
- Iterates over all enum constants, case-insensitively compares `configValue` against `trimmed` value.
- Throws `IllegalArgumentException` if no match found, listing allowed values.

#### Public API
- `configValue() -> String`: returns the config token for the strategy.
- `fromConfig(@Nullable String value) -> AiOversizeStrategy`: parses a config token string; returns `DEFAULT` for `null`/blank; throws `IllegalArgumentException` for unknown tokens.
- `DEFAULT` static final field: the default strategy (`FAIL`).

#### Dependencies
- `org.jspecify.annotations.Nullable` (imported for Javadoc/type annotation).

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when `value` is non-blank and does not match any strategy's `configValue`.
- No other exceptions; `null` input handled gracefully.

#### Concurrency
- No threading, synchronization, or mutable state; all operations are pure and thread-safe by default.

#### Notes
- Four enum constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `DEFAULT` field set to `FAIL`.
- `configValue` field is `private final String`.
- `fromConfig` uses `values()` to iterate constants.
- Case-insensitive matching via `equalsIgnoreCase`.
- Error message includes expected values (`fail/sample/mapReduce/deterministic`).
