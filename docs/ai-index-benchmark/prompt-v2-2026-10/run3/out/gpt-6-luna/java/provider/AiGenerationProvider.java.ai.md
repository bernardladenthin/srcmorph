### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:16Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests, with optional model timing data for calibration.

#### Purpose
- Defines a pluggable AI backend for generating text from `AiGenerationRequest`.
- Supports local providers and test mocks.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text.
- `generateWithTimings` returns `AiGenerationTimings`; the default reports zero timing rates and counts.
- `close` defaults to a no-op.

#### Core logic
- `generate` is the provider’s required text-generation operation.
- Default `generateWithTimings` delegates to `generate` and wraps its text with zero timing values; providers can override it to expose timings.

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
- Generated text is documented as non-null, but may be blank.
