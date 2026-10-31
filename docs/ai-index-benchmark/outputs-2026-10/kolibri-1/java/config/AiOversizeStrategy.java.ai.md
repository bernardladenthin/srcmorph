### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:20:43Z
- G: 
- A: 0.0.0
- X: file
---
> Selects how a source file larger than the routed model's context window is handled during AI analysis.

#### Purpose
- Defines behavior for oversized source files in AI routing rules.
- Parses per-rule `onOversize` config tokens into strategy instances.

#### Type
- Enum `AiOversizeStrategy`, final, sealed.
- Four constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC.
- Field `configValue: String`.
- Static `DEFAULT: AiOversizeStrategy`.
- Method `fromConfig(String) -> AiOversizeStrategy`.

#### Input
- Constructor parameter `configValue: String`.
- Method parameter `value: @Nullable String`.

#### Output
- Method `configValue() -> String`.
- Method `fromConfig(String) -> AiOversizeStrategy`.
- Throws `IllegalArgumentException` on unknown non-blank value.

#### Core logic
- `fromConfig` returns `DEFAULT` for null or blank input.
- Trims input and compares case-insensitively against each strategy's `configValue`.
- Returns matching strategy or throws `IllegalArgumentException` listing expected tokens.

#### Public API
- `configValue() -> String` returns the config token.
- `fromConfig(String) -> AiOversizeStrategy` parses a config token.
- `DEFAULT -> AiOversizeStrategy` constant for unset strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` thrown when non-blank value matches no strategy.

#### Concurrency
- None.
