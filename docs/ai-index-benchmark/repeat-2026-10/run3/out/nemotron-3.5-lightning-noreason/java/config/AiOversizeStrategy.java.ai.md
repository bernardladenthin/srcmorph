### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:34:41Z
- G: 
- A: 0.0.0
- X: file
---
> Determines how to handle source files that exceed a model's context window during routing.

#### Purpose
Configures the behavior when a source file is larger than its routed model's context window, per routing rule.

#### Type
enum; implements no interface; key generics: none; annotated with no framework-specific markers.

#### Input
Config value: {@code <onOversize>} string parsed via {@link #fromConfig(String)}; {@code null} or blank yields default.

#### Output
Returns the matching {@link AiOversizeStrategy} enum constant; throws {@link IllegalArgumentException} if value is non-blank and unrecognized.

#### Core logic
- Enumerates four strategies: {@code FAIL}, {@code SAMPLE}, {@code MAP_REDUCE}, {@code DETERMINISTIC}.
- {@code FAIL}: aborts the build; user must route to a larger-context model.
- {@code SAMPLE}: feeds only the file head trimmed to the window in a single call.
- {@code MAP_REDUCE}: splits the file into window-sized chunks at line boundaries, summarizes each chunk, then combines partial summaries in a final call, capped by {@code maxChunks}.
- {@code DETERMINISTIC}: emits a model-free deterministic body (size, line count, head/tail sample) instantly.
- Provides {@link #fromConfig(String)} for case-insensitive parsing; {@code null} or empty yields {@link #DEFAULT}.

#### Public API
- `values() -> [FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC]` (4 total)
- `valueOf(String name) -> enum constant`
- `configValue() -> String` (returns the strategy token)
- `fromConfig(@Nullable String value) -> AiOversizeStrategy` (case-insensitive parser)

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws {@link IllegalArgumentException} when config value is non-blank and matches no strategy.

#### Concurrency
- Immutable enum; inherently thread-safe.
