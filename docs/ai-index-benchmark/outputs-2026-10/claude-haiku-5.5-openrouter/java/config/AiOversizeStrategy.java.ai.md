### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:33Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how an AI-based source summarizer handles files exceeding the model's context window, parsed from an onOversize config string.

#### Purpose
- Enumerates oversize-file handling strategies: FAIL (default), SAMPLE, MAP_REDUCE, DETERMINISTIC.
- Maps each strategy to its `<onOversize>` config token and parses user config values.

#### Type
- enum `AiOversizeStrategy` (public), four constants.
- Annotation: `@Nullable` (org.jspecify) on `fromConfig` parameter.

#### Input
- Constructor `AiOversizeStrategy(String configValue)`, one per constant.
- `fromConfig(@Nullable String value)`: configured `<onOversize>` string, may be null or blank.

#### Output
- `configValue()` returns the config token string.
- `fromConfig(...)` returns a matching `AiOversizeStrategy`.
- Throws `IllegalArgumentException` on unknown non-blank value.

#### Core logic
- Each constant carries a token: `fail`, `sample`, `mapReduce`, `deterministic`.
- `fromConfig` returns `DEFAULT` (FAIL) when input is null or blank after trim.
- Otherwise trims input and scans `values()` with case-insensitive comparison to `configValue`.
- No match throws `IllegalArgumentException` listing the expected tokens.
- SAMPLE: trims source to window, summarizes head in one call.
- MAP_REDUCE: splits at line boundaries into window-sized chunks, summarizes each, combines partials in one final call; bounded by a `maxChunks` cap (documented, not in this file).
- DETERMINISTIC: no model call; emits size, line count, head/tail sample.
- FAIL: aborts build, user must route to larger-context model.

#### Public API
- `configValue() -> String`: returns config token.
- `fromConfig(String value) -> AiOversizeStrategy`: parses config token, case-insensitive.
- `DEFAULT: AiOversizeStrategy`: public static constant, equals FAIL.
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`: enum constants.

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.IllegalArgumentException` (implicit)
- Referenced by config rule types via `<onOversize>` (not in this file).

#### Exceptions / Errors
- `IllegalArgumentException` from `fromConfig` for unrecognized non-blank values; message lists valid tokens.
- Null input accepted, returns DEFAULT (no NPE).
- Blank input returns DEFAULT.

#### Concurrency
- Immutable enum state (final `configValue`), safe for concurrent use.
