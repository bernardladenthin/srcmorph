### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:37Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Generates text from source-aware requests through interchangeable AI backends, optionally reporting model timing for calibration.

#### Purpose
- Defines the provider contract for generating text from `AiGenerationRequest`.
- Supplies default timing and close behavior for providers that do not expose timings or require cleanup.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume an `AiGenerationRequest` containing prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text; documented as non-null, possibly blank.
- `generateWithTimings` returns `AiGenerationTimings`; the default uses generated text and zero rates/counts.
- `close` has no output or default side effects.

#### Core logic
- Default `generateWithTimings(final AiGenerationRequest request)` calls `generate(request)` and constructs `new AiGenerationTimings(..., 0, 0, 0.0d, 0, 0.0d)`.
- Default `close()` does nothing; providers may override it.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — release provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException` for provider failures.
- Generated text is documented as never `null`, but may be blank.
