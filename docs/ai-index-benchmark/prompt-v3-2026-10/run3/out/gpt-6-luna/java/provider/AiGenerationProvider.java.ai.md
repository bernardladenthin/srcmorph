### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:52Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI requests, optionally reporting model timing for calibration.

#### Purpose
- Defines the pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports local AI backends and test mocks.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it may be blank but is documented as never `null`.
- `generateWithTimings` returns generated text with measured timing, or zero rates and timings by default.
- `close()` is a no-op by default.

#### Core logic
- `generate` is the required provider-specific generation operation.
- `generateWithTimings` delegates to `generate` and wraps the result in `AiGenerationTimings` with zero timing values; providers can override it.
- Default `close` performs no action.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException` for provider failures.
- Generated text is documented as never `null`; it may be blank.
