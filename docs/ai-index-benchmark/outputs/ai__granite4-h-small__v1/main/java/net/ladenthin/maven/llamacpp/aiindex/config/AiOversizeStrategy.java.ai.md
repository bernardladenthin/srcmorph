### AiOversizeStrategy.java
- H: 1.0
- C: C8604282
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:03:53Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 2

> Defines how to handle source files exceeding the context window of the selected model.

#### Purpose
- Configures oversized‑file handling per routing rule.
- Provides default and alternative strategies (fail, sample, map‑reduce, deterministic).

#### Type
- `enum` with `public` visibility, no modifiers.
- Constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- Field: `private final String configValue`.
- Static field: `public static final AiOversizeStrategy DEFAULT`.
- Annotated with `@Nullable` for `fromConfig` parameter.

#### Input
- Constructor: `AiOversizeStrategy(String configValue)` receives the config token.
- Method `fromConfig(@Nullable String value)` accepts a config string (may be null or blank).

#### Output
- `configValue()` returns the token string.
- `fromConfig()` returns the matching enum or throws `IllegalArgumentException`.

#### Core logic
- `fromConfig()` trims input; null or empty yields `DEFAULT`.
- Iterates over `values()`; case‑insensitive match on `configValue`.
- Throws exception if no match.

#### Public API
- `configValue() -> String` – return the strategy’s config token.
- `fromConfig(String) -> AiOversizeStrategy` – parse config token, default or error.

#### Dependencies
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` in `fromConfig` when value is non‑blank and unmatched.

#### Concurrency
- Immutable enum; thread‑safe.
