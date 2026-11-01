### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:45Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests, optionally reporting model performance timings for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports local AI backends and test mock providers.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate` and `generateWithTimings` accept an `AiGenerationRequest` containing prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text.
- `generateWithTimings` returns `AiGenerationTimings` containing generated text and timing rates.

#### Core logic
- Requires implementations to provide `generate` using default sampling parameters.
- Default `generateWithTimings` delegates to `generate` and supplies zero timings.
- Default `close` performs no action.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timings.
- `close()` -> `void` — close provider.

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `AutoCloseable`
- `IOException`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- Generated text is documented as non-null; it may be blank.
- Default timings are zero when unavailable.

#### Concurrency
- No concurrency or thread-safety guarantees specified.
