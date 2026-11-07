### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:48Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates text from source-aware AI requests, optionally reporting model performance for calibration.

#### Purpose
- Defines a pluggable AI generation backend for local models or test mocks.
- Provides an optional timing result for calibration workflows.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Generation methods return text; text may be blank but is documented as never `null`.
- Default `generateWithTimings` returns `AiGenerationTimings` with generated text and zero timing rates/values.
- Default `close()` has no side effects.

#### Core logic
- `generate` is the provider contract for producing text with default sampling parameters.
- `generateWithTimings` delegates to `generate` and supplies zero timing values unless overridden.
- `close` is a no-op unless overridden.

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
- Generation methods declare `IOException` when the underlying provider fails.
- Generated text is documented as non-null; it may be blank.
