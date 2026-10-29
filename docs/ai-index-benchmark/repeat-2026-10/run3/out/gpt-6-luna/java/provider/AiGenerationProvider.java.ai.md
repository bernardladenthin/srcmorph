### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from source-editing requests through interchangeable AI backends, optionally reporting model timing.

#### Purpose
- Defines the provider contract for generating text from an `AiGenerationRequest`.
- Supports providers with or without measured generation timings.

#### Type
- Public interface `AiGenerationProvider`; extends `AutoCloseable`.

#### Input
- `generate` and `generateWithTimings` accept an `AiGenerationRequest` containing the prompt key, source file, source text, and current header.

#### Output
- Generated text; documented as non-null but potentially blank.
- `generateWithTimings` returns `AiGenerationTimings`; its default reports zero timings.
- Default `close` performs no action.

#### Core logic
- `generate` is the required backend generation operation.
- Default `generateWithTimings` delegates to `generate` and wraps the text with zero timing values.
- Providers exposing timings may override `generateWithTimings`.
- Default `close` is a no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` — generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text with timings.
- `close() -> void` — close provider resources.

#### Dependencies
- `AutoCloseable`
- `IOException`
- `AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `close` declares `IOException`.
- Generated text is documented as never `null`; it may be blank.

#### Concurrency
- No synchronization or thread-safety guarantees are specified.
