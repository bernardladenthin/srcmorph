### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from `AiGenerationRequest`.
- Supports local AI backends and test mock providers.

#### Type
- `AiGenerationProvider` — public interface; extends `AutoCloseable`.

#### Input
- `generate` and `generateWithTimings` consume an `AiGenerationRequest` containing a prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text, documented as never null but potentially blank.
- `generateWithTimings` returns generated text and timing data in `AiGenerationTimings`.
- `close` has no output.

#### Core logic
- `generate` is the provider’s required text-generation operation.
- Default `generateWithTimings` delegates to `generate` and returns zero-valued timing rates and measurements.
- Providers with timing data can override `generateWithTimings`.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text with default sampling.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — close provider resources; default implementation does nothing.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException` for provider failures.
- Default timing values are zero when timing data is unavailable.
