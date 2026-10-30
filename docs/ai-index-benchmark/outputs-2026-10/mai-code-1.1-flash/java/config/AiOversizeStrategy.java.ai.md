### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:44:06Z
- G: 
- A: 0.0.0
- X: file
---
> Configures what to do when a source file exceeds a routed model’s context window, selecting a safe policy for oversized input.

#### Purpose
- Defines the supported strategies for oversized source handling in `AiOversizeStrategy`.
- Maps configuration tokens like `"sample"` and `"mapReduce"` to the corresponding enum policy.

#### Type
- Kind: `enum`
- Modifiers: none
- Extends: `Enum<AiOversizeStrategy>`
- Implements: none
- Key members:
  - `FAIL("fail")`
  - `SAMPLE("sample")`
  - `MAP_REDUCE("mapReduce")`
  - `DETERMINISTIC("deterministic")`
- Notable field: `public static final AiOversizeStrategy DEFAULT = FAIL;`

#### Input
- Constructor: `AiOversizeStrategy(String configValue)`
- Method parameters:
  - `configValue()` -> none
  - `fromConfig(@Nullable String value)`
- Consumed state:
  - `private final String configValue;`
  - `DEFAULT` static constant
- Reads:
  - `@Nullable` input value from config; trims and checks blank/null state

#### Output
- `configValue()` -> `String`; returns the config token used in `<onOversize>`
- `fromConfig(...)` -> `AiOversizeStrategy`; resolves a token to the matching enum
- State produced:
  - enum constant holds the normalized config token value
- Side effects:
  - throws `IllegalArgumentException` for unknown non-blank strategy strings
  - returns `DEFAULT` when input is `null` or blank

#### Core logic
- `AiOversizeStrategy` enumerates the allowed oversized-file policies: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `fromConfig` treats `null` and blank strings as `DEFAULT`.
- `fromConfig` compares each enum constant’s `configValue` with the incoming token using case-insensitive matching.
- On no match, `fromConfig` throws `IllegalArgumentException` with the expected values: `fail/sample/mapReduce/deterministic`.
- `configValue()` exposes the canonical config token for each strategy.

#### Public API
- `FAIL -> AiOversizeStrategy` ; default abort strategy
- `SAMPLE -> AiOversizeStrategy` ; head-only sample strategy
- `MAP_REDUCE -> AiOversizeStrategy` ; chunk-and-summarize strategy
- `DETERMINISTIC -> AiOversizeStrategy` ; deterministic metadata-only strategy
- `DEFAULT -> AiOversizeStrategy` ; fallback when config is null/blank
- `configValue() -> String` ; exposes config token
- `fromConfig(String value) -> AiOversizeStrategy` ; resolves config token

#### Dependencies
- `org.jspecify.annotations.Nullable`
- `java.lang.IllegalArgumentException`
- `java.lang.String`
- `java.lang.Enum`
- `AiOversizeStrategy`

#### Exceptions / Errors
- `IllegalArgumentException` in `fromConfig(...)` when the value is non-blank and does not match any enum’s `configValue`
- Null handling: `value == null` returns `DEFAULT`
- Blank handling: `trimmed.isEmpty()` returns `DEFAULT`

#### Concurrency
- None; immutable enum constants and a final field, no shared mutable state or synchronization.
