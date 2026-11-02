### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:10Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates text from source-aware AI requests and optionally reports model timing for calibration.

#### Purpose
- Defines the pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports local AI models and test mock providers.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text, which may be blank but is documented as never `null`.
- `generateWithTimings` returns `AiGenerationTimings`; the default implementation reports zero timing rates and values.
- `close()` has no default side effects.

#### Core logic
- Requires implementations to generate text using their default sampling parameters.
- Provides a default timing-aware operation that delegates to `generate` and wraps its result with zero timing values.
- Allows providers with timing data to override `generateWithTimings`.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException`.
- Generated text is documented as non-null, but may be blank.

#### Concurrency
- No synchronization or thread-safety guarantees are specified.
