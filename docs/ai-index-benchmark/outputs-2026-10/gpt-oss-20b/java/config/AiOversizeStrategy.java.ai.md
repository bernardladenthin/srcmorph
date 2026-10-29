### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:57Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how oversized source files are handled during routing.

#### Purpose
- Enumerates strategies for oversized source file handling.
- Provides parsing from configuration strings.

#### Type
- `enum` AiOversizeStrategy  
  - constants: FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC  
  - field: `private final String configValue`  
  - static field: `public static final AiOversizeStrategy DEFAULT = FAIL`  
  - constructor: `AiOversizeStrategy(String)`  
  - annotations: none

#### Input
- Constructor parameter `configValue` (String) for each enum constant.
- `fromConfig(String)` receives a configuration value (nullable).

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig(String)` returns a matching strategy or throws `IllegalArgumentException`.

#### Core logic
- `configValue()` simply returns the stored token.
- `fromConfig(String)`:
  1. If null or blank → return `DEFAULT`.
  2. Trim input, compare case‑insensitively against each constant’s `configValue`.
  3. Return matching constant.
  4. If none match, throw `IllegalArgumentException` with a clear message.

#### Public API
- `configValue() -> String` – returns config token.
- `fromConfig(String) -> AiOversizeStrategy` – parses config value.

#### Dependencies
- `org.jspecify.annotations.Nullable` for null‑safety annotation.

#### Exceptions / Errors
- `IllegalArgumentException` when config value is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe.
