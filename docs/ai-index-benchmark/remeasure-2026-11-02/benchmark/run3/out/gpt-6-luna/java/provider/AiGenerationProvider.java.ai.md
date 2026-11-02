### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:47Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates AI text from source-file requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports providers with or without generation timing data.

#### Type
- `AiGenerationProvider` — public interface; extends `AutoCloseable`.

#### Input
- `AiGenerationRequest` supplied to generation methods.

#### Output
- Generated text as `String`.
- `AiGenerationTimings` containing text and timing values; the default reports zero timings.
- `close()` may release provider resources; the default implementation does nothing.

#### Core logic
- `generate(request)` is the provider contract for generating text with default sampling parameters.
- `generateWithTimings(request)` delegates to `generate(request)` and wraps the text with zero timing values; providers may override it.
- `close()` is a no-op unless overridden.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — close provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `generate` is documented to return non-null text, possibly blank.
- `close()` declares `IOException`.
