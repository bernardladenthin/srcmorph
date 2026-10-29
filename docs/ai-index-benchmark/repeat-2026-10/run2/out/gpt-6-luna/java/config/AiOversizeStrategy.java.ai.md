### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:55Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled, from failing the build to sampling, chunked summarization, or deterministic output.

#### Purpose
- Defines per-routing-rule strategies for files exceeding a model’s context window.
- Uses `FAIL` as the default when no `<onOversize>` value is configured.

#### Type
- Public enum `AiOversizeStrategy`.
- Four constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.

#### Input
- `fromConfig` accepts a nullable `<onOversize>` string.
- Each enum constant is initialized with its configuration token.

#### Output
- Returns the corresponding strategy from `fromConfig`; null or blank input returns `DEFAULT`.
- `configValue()` returns the constant’s configuration token.
- `DEFAULT` references `FAIL`.

#### Core logic
- `FAIL` aborts processing so the user can route the file to a larger-context model.
- `SAMPLE` summarizes the file’s head trimmed to the model window.
- `MAP_REDUCE` splits at line boundaries, summarizes chunks, then combines summaries.
- `DETERMINISTIC` emits a model-free body containing metadata and head/tail samples.
- `fromConfig` trims input, matches tokens case-insensitively, and rejects unknown non-blank values.

#### Public API
- `DEFAULT` — default strategy (`FAIL`).
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — oversized-file handling strategies.
- `configValue() -> String` — returns the configuration token.
- `fromConfig(value) -> AiOversizeStrategy` — parses a configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values that match no strategy.
- Null and blank values resolve to `DEFAULT`.
