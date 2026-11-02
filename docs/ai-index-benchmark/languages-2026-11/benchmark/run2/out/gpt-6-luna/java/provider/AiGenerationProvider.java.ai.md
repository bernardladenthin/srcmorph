### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:49Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable AI backend contract for producing text from `AiGenerationRequest`.
- Supports local providers and test mocks.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a generation request containing prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it may be blank but is documented as never `null`.
- `generateWithTimings` returns `AiGenerationTimings`; its default reports zero timing rates and values.

#### Core logic
- `generate` is the provider implementation contract and may throw `IOException`.
- Default `generateWithTimings` delegates to `generate` and wraps the text with zero timing values; providers can override it.
- Default `close` performs no action.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timings.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings` (referenced return and construction type)

#### Exceptions / Errors
- `generate` and `generateWithTimings` may throw `IOException` when the provider fails.
- Generated text is documented as non-null; blank output is allowed.
