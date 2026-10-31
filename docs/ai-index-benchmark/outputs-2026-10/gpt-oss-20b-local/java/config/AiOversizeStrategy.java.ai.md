### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:10:02Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Determines oversize handling strategy per routing rule.
- Enables automatic or manual routing based on file size.

#### Type
- `enum` with `public` visibility.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Field: `private final String configValue`.
- Static field: `public static final AiOversizeStrategy DEFAULT = FAIL`.

#### Input
- Constructor argument `String configValue` for each enum constant.
- `fromConfig(String)` receives a configuration string (nullable).

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns the matching `AiOversizeStrategy` or throws `IllegalArgumentException`.

#### Core logic
- Store a token string per strategy.
- `configValue()` simply returns the stored token.
- `fromConfig(String)`:
  1. Return `DEFAULT` if input is `null` or blank after trimming.
  2. Iterate over all enum values.
  3. Compare each strategy’s `configValue` case‑insensitively to the trimmed input.
  4. Return the matching strategy.
  5. If none match, throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns the strategy token.
- `fromConfig(String) -> AiOversizeStrategy` – parses config string.

#### Dependencies
- `org.jspecify.annotations.Nullable` for null‑safety annotation.

#### Exceptions / Errors
- `IllegalArgumentException` when an unknown strategy string is supplied to `fromConfig`.

#### Concurrency
- Immutable enum; thread‑safe by design.
