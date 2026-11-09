### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:12Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Produces AI-generated text from source-generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports providers that expose generation timings and providers that do not.

#### Type
- `public interface AiGenerationProvider` — interface extending `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume an `AiGenerationRequest` containing prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated `String`; it is documented as non-null but may be blank.
- `generateWithTimings` returns `AiGenerationTimings`; default implementation supplies zero timing rates.
- Default `close()` has no side effects.

#### Core logic
- `generate` is the required provider operation; implementations supply text generation.
- Default `generateWithTimings` calls `generate(request)` and wraps its text with zero-valued timing fields.
- Providers with timing data may override `generateWithTimings`.
- Default `close()` does nothing.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text with default sampling parameters.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `close` declares `IOException`.
