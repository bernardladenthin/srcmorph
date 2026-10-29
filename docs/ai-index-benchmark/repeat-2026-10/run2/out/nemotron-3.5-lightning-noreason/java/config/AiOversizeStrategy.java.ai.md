### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:30Z
- G: 
- A: 0.0.0
- X: file
---
> Determines how to handle source files that exceed a model's context window during routing.

#### Purpose
Defines oversize handling strategies for source files that exceed a model's context window, configured per routing rule.

#### Type
enum; implements no interface; no extends; four constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC; no generics or type bounds; no notable annotations.

#### Input
Config value string passed to {@link #fromConfig(String)}; parsed via {@link #configValue()}.

#### Output
Returns matching {@link AiOversizeStrategy} instance; throws {@link IllegalArgumentException} for unknown values.

#### Core logic
- Enumerates four strategies for handling oversized files
- {@code FAIL} — aborts build; user must route to larger-context model (default)
- {@code SAMPLE} — feeds only the file head trimmed to window size in a single call; fast and bounded
- {@code MAP_REDUCE} — splits file into window-sized chunks at line boundaries, summarizes each chunk, then combines partial summaries in a final call; capped by {@code maxChunks} to bound time
- {@code DETERMINISTIC} — emits a model-free deterministic body containing size, line count, and head/tail sample; instant, for pure data where no AI analysis is needed

#### Public API
- `values() -> [FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC]` (4 total)
- `valueOf(String name) -> enum constant`
- `configValue() -> String` — returns the config token (fail, sample, mapReduce, deterministic)
- `fromConfig(@Nullable String value) -> AiOversizeStrategy` — parses config value case-insensitively; null or blank returns DEFAULT

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws {@link IllegalArgumentException} when config value is non-blank and matches no strategy
- null or blank input yields {@link #DEFAULT}

#### Concurrency
No threading or synchronization concerns; pure enum with no mutable state.
