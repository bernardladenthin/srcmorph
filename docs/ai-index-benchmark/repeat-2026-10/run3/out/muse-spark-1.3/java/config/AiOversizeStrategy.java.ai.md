### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:21:21Z
- G: 
- A: 0.0.0
- X: file
---
> Handles oversized source files exceeding AI model context windows via fail, sampling, chunked summarization, or deterministic fallback.

#### Purpose
- Defines per-routing-rule oversize handling for source files larger than routed model context window.
- Parses `<onOversize>` config tokens via case-insensitive lookup with FAIL default.

#### Type
- enum AiOversizeStrategy with 4 constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC.
- Private final String configValue field; public static final AiOversizeStrategy DEFAULT = FAIL.

#### Input
- Constructor parameter configValue String token (`fail`, `sample`, `mapReduce`, `deterministic`).
- fromConfig(@Nullable String value) configured `<onOversize>` value, null/blank allowed.
- Iterates values() comparing configValue case-insensitively after trim.

#### Output
- configValue() returns String config token.
- fromConfig() returns matching AiOversizeStrategy or DEFAULT for null/blank.
- Throws IllegalArgumentException for unknown non-blank token.

#### Core logic
- FAIL aborts build, requires user-routed larger-context model, no automatic choice.
- SAMPLE trims source head to window for single-call bounded summary.
- MAP_REDUCE splits file into window-sized line-boundary chunks, summarizes each, combines via final call.
- DETERMINISTIC emits model-free body with size, line count, head/tail sample.
- fromConfig normalizes null -> DEFAULT, blank -> DEFAULT, else equalsIgnoreCase match.

#### Public API
- configValue() -> String — returns `<onOversize>` config token.
- fromConfig(String value) -> AiOversizeStrategy — parses token, defaults null/blank.

#### Dependencies
- org.jspecify.annotations.Nullable
- java.lang.IllegalArgumentException, String

#### Exceptions / Errors
- IllegalArgumentException on unknown onOversize strategy, message lists fail/sample/mapReduce/deterministic.
- Null-safe: null input returns DEFAULT; blank/whitespace returns DEFAULT.
