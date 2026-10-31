### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:40:19Z
- G: 
- A: 0.0.0
- X: file
---
> Defines how to handle source files that exceed the model’s context window during routing.

#### Purpose
- Configures per‑routing‑rule oversize handling.
- Provides default and alternative strategies for oversized source files.

#### Type
`public enum AiOversizeStrategy`  
- constants: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`  
- field: `private final String configValue`  
- static final: `DEFAULT = FAIL`  
- methods: `configValue()`, `fromConfig(String)`  
- annotations: none

#### Input
- Constructor argument `configValue` for each enum constant.
- `fromConfig` parameter `value` (nullable string).

#### Output
- `configValue()` returns the strategy’s config token.
- `fromConfig` returns a matching `AiOversizeStrategy` or throws `IllegalArgumentException` for unknown values.

#### Core logic
- `configValue()` simply returns the stored token.
- `fromConfig`:
  1. Returns `DEFAULT` if `value` is `null` or blank after trimming.
  2. Iterates over all enum constants, comparing `configValue` case‑insensitively to the trimmed input.
  3. Returns the matching constant.
  4. Throws `IllegalArgumentException` if no match is found.

#### Public API
- `configValue() -> String` – retrieve config token.  
- `fromConfig(String) -> AiOversizeStrategy` – parse config string.

#### Dependencies
- `org.jspecify.annotations.Nullable` for nullable parameter annotation.

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `fromConfig` when input is non‑blank and unrecognized.

#### Concurrency
- Immutable enum; thread‑safe by design.
