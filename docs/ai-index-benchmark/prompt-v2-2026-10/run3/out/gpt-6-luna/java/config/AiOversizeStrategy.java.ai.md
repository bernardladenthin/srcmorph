### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:00Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how oversized source files are handled when their routed model cannot fit the file.

#### Purpose
- Defines per-routing-rule `<onOversize>` strategies for files exceeding a model context window.
- Defaults to aborting; alternatives sample the head, summarize chunks, or produce deterministic output.

#### Type
- `public enum AiOversizeStrategy` — enum.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `DEFAULT` is `FAIL`.

#### Input
- Enum constructor receives each strategy’s configuration token.
- `fromConfig(String)` receives a nullable `<onOversize>` value.

#### Output
- `configValue()` returns the configuration token.
- `fromConfig(String)` returns the matching strategy; null or blank input returns `DEFAULT`.

#### Core logic
- Associates each enum constant with a config token: `fail`, `sample`, `mapReduce`, or `deterministic`.
- Trims input and matches tokens case-insensitively.
- Uses `FAIL` when the input is null or blank.
- Rejects nonblank values that match no strategy.

#### Public API
- `configValue() -> String` — return config token.
- `fromConfig(String) -> AiOversizeStrategy` — parse strategy token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for an unknown nonblank token.
