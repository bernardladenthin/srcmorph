### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:21Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests, optionally reporting model timing metrics for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports providers with measured generation timings and providers without them.

#### Type
- Public interface `AiGenerationProvider`; extends `AutoCloseable`.

#### Input
- `AiGenerationRequest` containing the prompt key, source file, source text, and current header.

#### Output
- Generated text; documented as non-null and potentially blank.
- `AiGenerationTimings` containing generated text and timing metrics.
- `close()` has no default side effects.

#### Core logic
- `generate` is the required text-generation operation.
- Default `generateWithTimings` delegates to `generate` and returns timing values `0, 0, 0.0d, 0, 0.0d`.
- Providers can override `generateWithTimings` to expose measured timings.
- Default `close` does nothing.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — close provider resources.

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `AutoCloseable`
- `IOException`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException`.
- `generateWithTimings` propagates failures from `generate`.

#### Concurrency
- No concurrency or thread-safety guarantees are specified.
