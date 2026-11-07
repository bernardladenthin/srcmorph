### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:42Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Generates text from source-aware AI requests and optionally reports model timing for calibration.

#### Purpose
- Defines the pluggable AI backend contract for generating text from `AiGenerationRequest`.
- Provides default timing and resource-close behavior for providers.

#### Type
- `public interface AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated `String` text.
- `generateWithTimings` returns `AiGenerationTimings`; default rates and timing values are zero.
- `close()` has no default side effects.

#### Core logic
- Default `generateWithTimings(final AiGenerationRequest request)` calls `generate(request)` and wraps the text in `new AiGenerationTimings(..., 0, 0, 0.0d, 0, 0.0d)`.
- Providers may override `generateWithTimings` to supply measured timings.
- Default `close()` does nothing.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling parameters.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException`.
- Generated text is documented as non-null; it may be blank.
