### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:31Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from an AI generation request, with optional model timing data for calibration.

#### Purpose
- Defines a pluggable backend contract for generating text from prompts and source context.
- Supports local AI backends and test mocks.

#### Type
- `AiGenerationProvider` — public interface; implements `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing prompt key, source file, source text, and current header.

#### Output
- Returns generated text; it is non-null but may be blank.
- `generateWithTimings` returns `AiGenerationTimings`; default rates and timing values are zero.
- Default `close()` has no side effects.

#### Core logic
- `generate` is the required generation operation; implementations provide its behavior.
- Default `generateWithTimings` delegates to `generate` and wraps its result with zero timing values.
- Providers with timing support may override `generateWithTimings`.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timing data
- `close() -> void` — release provider resources

#### Dependencies
- `AiGenerationRequest`
- `AiGenerationTimings`
- `AutoCloseable`
- `IOException`

#### Exceptions / Errors
- Generation methods may throw `IOException` when the provider fails.
- `generate` is documented to return non-null text, possibly blank.

#### Concurrency
- No thread-safety or concurrency guarantees specified.
