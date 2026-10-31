### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:52Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests, optionally reporting model timing data for calibration.

#### Purpose
- Defines the pluggable backend contract for producing text from `AiGenerationRequest`.
- Supports local AI backends and test mock providers.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Produces generated text; documented as never `null`, but possibly blank.
- `generateWithTimings` returns `AiGenerationTimings`; default timing rates and values are zero.

#### Core logic
- `generate` is the required provider-specific text-generation operation.
- Default `generateWithTimings` delegates to `generate` and wraps its text with zero timing values.
- Default `close` performs no action.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — close provider resources.

#### Dependencies
- `AiGenerationRequest` — generation input.
- `AiGenerationTimings` — generated text and timing result.
- `AutoCloseable` — lifecycle contract.
- `IOException` — provider and close failure type.

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `close` declares `IOException`.
