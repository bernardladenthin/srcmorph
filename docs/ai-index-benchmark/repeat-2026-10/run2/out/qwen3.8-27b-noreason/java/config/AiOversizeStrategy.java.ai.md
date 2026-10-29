### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:53Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the strategy enum for handling source files that exceed a routed AI model's context window during build processing.

#### Purpose
- Enumerates four strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for processing oversized source files in the `srcmorph` build pipeline.
- Provides case-insensitive parsing from the `<onOversize>` configuration token.

#### Type
- `public enum AiOversizeStrategy`
- Implements no interfaces; extends `java.lang.Enum` implicitly.
- Uses `org.jspecify.annotations.Nullable` for null-safety annotations.

#### Input
- Constructor parameter: `String configValue` (the config token for each constant).
- `fromConfig` parameter: `@Nullable String value` (the raw `<onOversize>` config string).

#### Output
- `configValue()` returns the `String` config token for the enum constant.
- `fromConfig` returns the matching `AiOversizeStrategy` instance or `DEFAULT` (`FAIL`).
- Throws `IllegalArgumentException` for unrecognized non-blank values.

#### Core logic
- Four constants map to config tokens: `FAIL`→`"fail"`, `SAMPLE`→`"sample"`, `MAP_REDUCE`→`"mapReduce"`, `DETERMINISTIC`→`"deterministic"`.
- `DEFAULT` is a `public static final` field pointing to `FAIL`.
- `fromConfig` logic:
  - `null` input → returns `DEFAULT`.
  - Blank (empty after trim) input → returns `DEFAULT`.
  - Iterates `values()`, compares `configValue` case-insensitively against trimmed input.
  - No match → throws `IllegalArgumentException` with a message listing all valid tokens.

#### Public API
- `configValue() -> String` — returns the config token.
- `fromConfig(@Nullable String) -> AiOversizeStrategy` — parses config to strategy.
- `DEFAULT` (field) — `FAIL` constant reference.

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.Enum` (implicit)
- `java.lang.IllegalArgumentException`
- `java.lang.String`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when a non-blank value matches no strategy; message includes the offending value and all valid tokens.
- `null` and blank inputs are handled gracefully, returning `DEFAULT`.

#### Concurrency
- Immutable enum; all fields are `final`. Thread-safe by design.
