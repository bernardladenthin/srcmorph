### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:58:05Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports local AI backends and test mock providers.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate` and `generateWithTimings` accept an `AiGenerationRequest` containing prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it may be blank but is documented as never `null`.
- `generateWithTimings` returns `AiGenerationTimings`; default timing rates and measurements are zero.
- `close` has no default side effects.

#### Core logic
- `generate` defines the provider’s text-generation operation.
- Default `generateWithTimings` delegates to `generate` and wraps the text with zero timing values.
- Providers can override `generateWithTimings` to expose measured timings.
- Default `close` does nothing.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text using default sampling parameters.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — release provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `close` declares `IOException`.
