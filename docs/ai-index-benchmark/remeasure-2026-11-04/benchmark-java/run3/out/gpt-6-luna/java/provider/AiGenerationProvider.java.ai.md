### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:06Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates text from source-aware AI requests, with optional model timing data for calibration.

#### Purpose
- Defines a pluggable AI backend for generating text from `AiGenerationRequest`.
- Supports local model implementations and test mocks.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it may be blank but is documented as never `null`.
- Default `generateWithTimings` returns `AiGenerationTimings` with generated text and zero timing values.
- `close()` has no default side effect.

#### Core logic
- Implementations provide `generate` using their default sampling parameters.
- Default `generateWithTimings` delegates to `generate` and reports unavailable timings as zero; providers may override it.
- Default `close` does nothing.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `AutoCloseable`
- `IOException`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException` for provider failures.
- Generated text is documented as non-null; it may be blank.
