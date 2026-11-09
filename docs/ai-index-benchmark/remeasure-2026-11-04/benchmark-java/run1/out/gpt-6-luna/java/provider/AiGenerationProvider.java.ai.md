### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:53Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates requested text through a pluggable AI backend, optionally returning model timing data for calibration.

#### Purpose
- Defines the backend contract for generating text from an `AiGenerationRequest`.
- Supports providers with optional timing metrics and resource cleanup.

#### Type
- `public interface AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume an `AiGenerationRequest` containing generation context.

#### Output
- `generate` returns generated text.
- `generateWithTimings` returns `AiGenerationTimings`; default timing rates and counts are zero.
- Default `close()` performs no action.

#### Core logic
- Requires implementations to provide `generate`, which may use provider-specific default sampling parameters.
- Default `generateWithTimings` delegates to `generate` and wraps the text with zero timing values; providers can override it.
- Provides a no-op default `close()`.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — release provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `generate` is documented to return non-null text, which may be blank.
