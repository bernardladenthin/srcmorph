### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:23Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures how source files exceeding an AI model’s context window are handled: abort, sample, map-reduce, or deterministic output.

#### Purpose
- Defines oversize handling strategies for AI source-file routing rules.
- Parses the `<onOversize>` configuration token and supplies the default strategy.

#### Type
- `AiOversizeStrategy` — enum; no supertypes declared; no type parameters.

#### Input
- `fromConfig(@Nullable String value)` consumes a configured strategy token; accepts `null`, blank, and case-insensitive tokens.
- Enum constructor receives each strategy’s config token.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(...)` returns a matching strategy or `DEFAULT`.

#### Core logic
- Defines `FAIL` (`fail`), `SAMPLE` (`sample`), `MAP_REDUCE` (`mapReduce`), and `DETERMINISTIC` (`deterministic`).
- Sets `DEFAULT` to `FAIL`.
- `fromConfig` returns `DEFAULT` for `null` or blank input; otherwise trims input and matches tokens case-insensitively.
- Throws `IllegalArgumentException` when a non-blank token matches no strategy.

#### Public API
- `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` — oversize handling strategies.
- `DEFAULT: AiOversizeStrategy` — default strategy (`FAIL`).
- `configValue() -> String` — return configuration token.
- `fromConfig(String) -> AiOversizeStrategy` — parse configuration token.

#### Dependencies
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `fromConfig` throws `IllegalArgumentException` for an unknown non-blank strategy token.
- `fromConfig` treats `null` and blank input as `DEFAULT`.
