### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:08Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests, optionally reporting model timing for calibration.

#### Purpose
- Defines a pluggable AI backend for producing text from `AiGenerationRequest`.
- Supports local and test/mock providers.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text, or generated text with model timing.
- `close()` has no default side effects.

#### Core logic
- `generate` is the required generation operation; implementations supply the provider-specific behavior.
- Default `generateWithTimings` delegates to `generate` and returns `AiGenerationTimings` with all timing rates and values set to zero.
- Providers with timing data may override `generateWithTimings`.
- Default `close` does nothing.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `AutoCloseable`
- `IOException`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- Generated text is documented as never `null`; it may be blank.
