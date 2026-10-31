### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:21Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed a routed model's context window during srcmorph builds.

#### Purpose
- Enum selecting behavior when a source file is larger than its routed model's context window.
- Configured per routing rule via the `<onOversize>` string, parsed by `fromConfig`.

#### Type
- Enum, public, extends `Enum<AiOversizeStrategy>` implicitly.
- Imports `org.jspecify.annotations.Nullable` for the parse parameter.

#### Input
- Constructor takes a String `configValue` token per constant.
- `fromConfig` accepts `@Nullable String value` (the configured `<onOversize>` string).

#### Output
- `configValue()` returns the String token (e.g. `"mapReduce"`).
- `fromConfig` returns the matching `AiOversizeStrategy`, or `DEFAULT` for null/blank.
- `DEFAULT` constant resolves to `FAIL`.

#### Core logic
- Four strategies: `FAIL` aborts the build; `SAMPLE` trims to the head; `MAP_REDUCE` chunks and summarizes; `DETERMINISTIC` emits a model-free metadata body.
- `fromConfig` returns `DEFAULT` when input is null or blank after trimming.
- Matches trimmed input case-insensitively against each strategy's `configValue`.
- Throws `IllegalArgumentException` listing valid tokens when no strategy matches.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — public static final enum constants.
- `DEFAULT -> AiOversizeStrategy`: Default when none configured.
- `configValue() -> String`: Returns `<onOversize>` token.
- `fromConfig(@Nullable String) -> AiOversizeStrategy`: Parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.String`
- `java.lang.IllegalArgumentException`

#### Exceptions / Errors
- `IllegalArgumentException` for unknown non-blank config value.

#### Concurrency
- Immutable enum; constants are singleton instances, thread-safe.
