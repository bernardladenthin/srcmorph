### AiOversizeStrategy.java
- H: 1.0
- C: C8604282
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:09:06Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 2

> Determines how oversized source files are handled during AI routing.

#### Purpose
- Configures handling of source files larger than a model’s context window.
- Provides strategies for automatic or manual processing of oversized files.

#### Type
- `enum` `AiOversizeStrategy` (public, final).
- Extends `java.lang.Enum`.
- No interfaces or generics.

#### Input
- `AiOversizeStrategy.fromConfig(String)` receives a configuration string (nullable).
- Enum constants internally hold a `String configValue`.

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns an `AiOversizeStrategy` instance.
- `DEFAULT` constant is returned for null/blank values.

#### Core logic
- `fromConfig` trims input, checks for null/blank, iterates over all enum constants comparing `configValue` case‑insensitively, returns match or throws `IllegalArgumentException` if none found.

#### Public API
- `configValue() -> String` – retrieves the config token.
- `fromConfig(String) -> AiOversizeStrategy` – parses strategy from config string.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` thrown when a non‑blank config value does not match any strategy.

#### Concurrency
- Immutable enum; thread‑safe by design.
