### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:28Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how source files exceeding a routed model’s context window are handled.

#### Purpose
- Defines per-routing-rule `<onOversize>` strategies for oversized source files.
- Defaults to aborting rather than automatically selecting another model.

#### Type
- `AiOversizeStrategy` — enum; constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `DEFAULT` is `FAIL`.

#### Input
- Constructor receives each strategy’s configuration token.
- `fromConfig(@Nullable String value)` receives the configured `<onOversize>` value.

#### Output
- `configValue()` returns the configuration token.
- `fromConfig(...)` returns a matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates strategies with tokens: `fail`, `sample`, `mapReduce`, `deterministic`.
- Documents behaviors: abort; summarize trimmed file head; summarize line-boundary chunks and combine; or emit a deterministic metadata and head/tail body.
- Trims input and matches tokens case-insensitively.

#### Public API
- `configValue()` -> `String` — return config token.
- `fromConfig(String)` -> `AiOversizeStrategy` — parse configured strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for non-blank values matching no strategy.
