### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:16Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Generates text from an AI generation request and optionally reports model timing for calibration.

#### Purpose
- Defines the pluggable backend contract for generating text from `AiGenerationRequest`.
- Provides default timing and resource-close behavior for providers that do not expose timings or need no cleanup.

#### Type
- `public interface AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a generation request containing a prompt key, source file, source text, and current header.

#### Output
- `generate` returns generated text.
- `generateWithTimings` returns generated text and timing rates; the default reports zero timings.
- Default `close()` performs no action.

#### Core logic
- `generateWithTimings` calls `generate(request)` and wraps its result in `AiGenerationTimings` with timing values `0, 0, 0.0d, 0, 0.0d`.
- Provider implementations supply `generate`; implementations with timing data may override `generateWithTimings`.
- Default `close()` is empty.

#### Public API
- `generate(AiGenerationRequest request) -> String` — generate text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` — generate text with timing data.
- `close() -> void` — release provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException`.
- `generate` is documented to return non-null text, which may be blank.
