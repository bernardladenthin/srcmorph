### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:42Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from source-generation requests through interchangeable AI backends, optionally reporting model timing metrics.

#### Purpose
- Defines the pluggable AI generation contract for local or test providers.
- Supplies default timing and resource-close behavior.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` receive a request containing a prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text.
- `generateWithTimings` returns generated text with timing values; its default reports zero rates.
- Default `close` performs no action.

#### Core logic
- Requires implementations to generate text for an `AiGenerationRequest`.
- Default timing implementation delegates to `generate` and constructs `AiGenerationTimings` with timing values `0, 0, 0.0d, 0, 0.0d`.
- Default `close` is a no-op.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate with timing metrics
- `close()` -> `void` — release provider resources

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `IOException`
- `AutoCloseable`

#### Exceptions / Errors
- Generation methods declare `IOException` for provider failures.
- `generate` is documented to return non-null text; output may be blank.
- `generateWithTimings` reports zero rates when timings are unavailable.

#### Concurrency
- No synchronization or thread-safety guarantees are specified.
