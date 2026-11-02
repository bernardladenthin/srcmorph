### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:37Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend contract for producing text from `AiGenerationRequest`.
- Supports local or test implementations and optional generation timing.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text; the documented contract says it is non-null but may be blank.
- `generateWithTimings` returns `AiGenerationTimings`; its default result reports zero timings.

#### Core logic
- Requires implementations to provide `generate` and throw `IOException` on provider failure.
- Default `generateWithTimings` delegates to `generate` and constructs `AiGenerationTimings` with zero timing values.
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
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- Generated text is documented as non-null, but may be blank.

#### Concurrency
- No synchronization or thread-safety guarantees are specified.
