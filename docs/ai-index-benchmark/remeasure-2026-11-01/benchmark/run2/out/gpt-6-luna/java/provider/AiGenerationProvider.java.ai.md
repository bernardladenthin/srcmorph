### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:24Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable backend for producing text from `AiGenerationRequest`.
- Supports local AI models and test mocks.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it may be blank but is documented as never `null`.
- `generateWithTimings` returns `AiGenerationTimings`; its default implementation reports zero timing rates.

#### Core logic
- `generate` is the provider-specific text-generation contract.
- Default `generateWithTimings` delegates to `generate` and wraps the text with zero timing values; providers can override it.
- Default `close` does nothing.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `AiGenerationRequest` — generation input.
- `AiGenerationTimings` — generated text and timing result.
- `IOException` — provider and close failure type.
- `AutoCloseable` — resource lifecycle contract.

#### Exceptions / Errors
- `generate` and `generateWithTimings` may throw `IOException` when the provider fails.
- `close` declares `IOException`; the default implementation throws nothing.
