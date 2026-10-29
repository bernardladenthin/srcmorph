### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:46:40Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the strategy enum for handling source files that exceed an AI model's context window during routing.

#### Purpose
- Enumerates four strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for processing oversized source files in the `srcmorph` build pipeline.
- Provides case-insensitive parsing of the `<onOversize>` configuration token.

#### Type
- `public enum AiOversizeStrategy`
- Implements no interfaces; extends `java.lang.Enum` implicitly.
- Uses `org.jspecify.annotations.Nullable` for null-safety annotations.

#### Input
- Constructor parameter: `String configValue` (the configuration token for each constant).
- `fromConfig` parameter: `@Nullable String value` (the raw `<onOversize>` config string).

#### Output
- `configValue()`: returns the `String` token associated with the enum constant.
- `fromConfig`: returns the matching `AiOversizeStrategy` instance or `DEFAULT` (`FAIL`).

#### Core logic
- Each enum constant holds a unique `configValue` string (`"fail"`, `"sample"`, `"mapReduce"`, `"deterministic"`).
- `fromConfig` handles `null` and blank strings by returning `DEFAULT` (`FAIL`).
- Iterates through `values()` to find a case-insensitive match against the trimmed input.
- Throws `IllegalArgumentException` if no match is found for a non-blank input.

#### Public API
- `configValue() -> String` — returns the config token.
- `fromConfig(@Nullable String) -> AiOversizeStrategy` — parses config to strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.IllegalArgumentException`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when the input is non-blank and does not match any strategy's `configValue`.
- `null` and empty/whitespace inputs are handled gracefully by returning `DEFAULT`.
