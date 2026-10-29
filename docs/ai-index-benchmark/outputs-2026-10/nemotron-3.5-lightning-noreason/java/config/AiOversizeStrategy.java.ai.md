### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:51:34Z
- G: 
- A: 0.0.0
- X: file
---
> Determines how to handle source files that exceed a model's context window during routing.

#### Purpose
Configures the behavior when a source file is larger than its routed model's context window, selected per routing rule via the `<onOversize>` setting.

#### Type
enum; implements no interface; key constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; annotated with `@Nullable` on parameter; `DEFAULT` field set to `FAIL`.

#### Input
Config value: `<onOversize>` string (e.g., `"fail"`, `"sample"`, `"mapReduce"`, `"deterministic"`); `null` or blank input.

#### Output
Returns the matching `AiOversizeStrategy` enum constant; throws `IllegalArgumentException` if the value is non-blank and does not match any strategy.

#### Core logic
- Enumerates four strategies: `FAIL` aborts the build requiring a larger-context model; `SAMPLE` feeds only the file head trimmed to the window in a single call; `MAP_REDUCE` splits the file into window-sized chunks at line boundaries, summarizes each chunk, then combines partial summaries with a `maxChunks` cap; `DETERMINISTIC` emits a model-free deterministic body containing size, line count, and head/tail sample.
- `fromConfig(String)` parses the config value case-insensitively; `null` or empty yields `DEFAULT`; unknown values throw `IllegalArgumentException`.

#### Public API
- `configValue()` -> `String` — returns the config token for this strategy.
- `fromConfig(@Nullable String value)` -> `AiOversizeStrategy` — parses a config value (case-insensitive); `null`/blank returns `DEFAULT`; throws on unknown value.

#### Dependencies
`org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
Throws `IllegalArgumentException` when the provided config value is non-blank and does not match any known strategy (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Concurrency
No threading or synchronization concerns; pure enum with no mutable state.
