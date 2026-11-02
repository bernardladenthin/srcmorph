### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:17:21Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window.

#### Purpose
- Configures per‑routing rule the action for oversized source files.

#### Type
`public enum AiOversizeStrategy` – enum with constants FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC; public static final DEFAULT; private final String configValue; implements `fromConfig(String)` and `configValue()`.

#### Input
- Constructor parameter `String configValue` for each enum constant.
- `fromConfig(String value)` receives a config token (nullable, case‑insensitive).

#### Output
- `configValue()` returns the token string.
- `fromConfig(String)` returns the matching enum constant or throws `IllegalArgumentException`.

#### Core logic
- Store a string token per strategy.
- `configValue()` simply returns that token.
- `fromConfig(String)`:
  1. If null or blank → return `DEFAULT`.
  2. Trim and compare case‑insensitively against each strategy’s token.
  3. Return matching strategy.
  4. If none match → throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – get strategy token.
- `fromConfig(String) -> AiOversizeStrategy` – parse config value.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
