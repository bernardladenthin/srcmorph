### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:41:38Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files that exceed a routed AI model's context window (fail, sample, map-reduce, or deterministic).

#### Purpose
- Enum representing the `onOversize` policy applied when a source file is larger than the context window of its assigned model.
- Provides config-token parsing (`fromConfig`) and a default (`FAIL`) for the `srcmorph` routing configuration.

#### Type
- `public enum AiOversizeStrategy`
- No extends/implements (implicit `Enum<AiOversizeStrategy>`)
- Uses `org.jspecify.annotations.Nullable`

#### Input
- Constructor: `String configValue` — the `<onOversize>` token (e.g. `"mapReduce"`)
- `fromConfig`: `@Nullable String value` — raw config value, may be null or blank

#### Output
- `configValue()` → `String` — the token for the current constant
- `fromConfig` → `AiOversizeStrategy` — resolved strategy or `DEFAULT`
- Throws `IllegalArgumentException` on unrecognized non-blank value

#### Core logic
- Four constants map to config tokens: `FAIL("fail")`, `SAMPLE("sample")`, `MAP_REDUCE("mapReduce")`, `DETERMINISTIC("deterministic")`.
- `DEFAULT` is `FAIL` — build aborts by default; no automatic model escalation.
- `fromConfig` trims input, returns `DEFAULT` for null/blank, then iterates `values()` with case-insensitive `equalsIgnoreCase` match; throws `IllegalArgumentException` listing valid tokens if no match.

#### Public API
- `DEFAULT` (static field) → `AiOversizeStrategy` — fallback when unconfigured
- `configValue()` → `String` — returns the config token
- `fromConfig(@Nullable String value)` → `AiOversizeStrategy` — parses config token case-insensitively

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when value is non-blank and matches no strategy; message includes the offending value and the list of valid tokens.
