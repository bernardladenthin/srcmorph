### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:35:01Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable AI backend for local or test generation providers.

#### Type
- Public interface `AiGenerationProvider`; extends `AutoCloseable`.

#### Input
- `generate` and `generateWithTimings` accept an `AiGenerationRequest`.

#### Output
- Returns generated text or `AiGenerationTimings`.
- Default `close` performs no action.

#### Core logic
- `generate` is the provider implementation contract.
- Default `generateWithTimings` delegates to `generate` and reports zero timing rates and counts.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing
- `close() -> void` — close provider

#### Dependencies
- `AutoCloseable`, `IOException`, `AiGenerationRequest`, `AiGenerationTimings`

#### Exceptions / Errors
- Generation methods declare `IOException`.
- `generate` is documented to return non-null text, possibly blank.
