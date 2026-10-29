### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:24Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the strategy enum for handling source files that exceed an AI model's context window during routing.

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
- Four constants map to config tokens: `FAIL("fail")`, `SAMPLE("sample")`, `MAP_REDUCE("mapReduce")`, `DETERMINISTIC("deterministic")`.
- `DEFAULT` is a static final field set to `FAIL`.
- `fromConfig` logic:
  - Returns `DEFAULT` if input is `null` or blank after trimming.
  - Iterates `values()` comparing `configValue` case-insensitively against the trimmed input.
  - Throws `IllegalArgumentException` with a descriptive message if no match is found.

#### Public API
- `configValue() -> String` — returns the config token.
- `fromConfig(@Nullable String) -> AiOversizeStrategy` — parses config to strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when a non-blank value does not match any strategy's `configValue`.
