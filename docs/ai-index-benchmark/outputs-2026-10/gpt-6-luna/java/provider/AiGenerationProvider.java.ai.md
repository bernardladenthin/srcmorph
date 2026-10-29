### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable AI backend contract for producing text from `AiGenerationRequest`.
- Supports local or test/mock providers and optional timing measurements.

#### Type
- Interface `AiGenerationProvider`; extends `AutoCloseable`.

#### Input
- `AiGenerationRequest` passed to generation methods; contains the prompt key, source file, source text, and current header.

#### Output
- Generated `String`; documented as non-null, possibly blank.
- `AiGenerationTimings` with generated text and timing rates; default implementation reports zero timings.

#### Core logic
- `generate` defines the provider-specific text generation contract.
- `generateWithTimings` delegates to `generate` and returns zero timing values by default; providers may override it.
- `close` is a no-op by default.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timings.
- `close() -> void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- Generation methods declare `IOException` when the underlying provider fails.
- `generate` is documented to return non-null text, which may be blank.

#### Concurrency
- No synchronization or thread-safety guarantees specified.
