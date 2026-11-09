### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:14Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Generates requested text through a pluggable AI backend and can report model timing for calibration.

#### Purpose
- Defines the provider contract for generating text from an `AiGenerationRequest`.
- Supports generation timing and resource cleanup through default methods.

#### Type
- `public interface AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- `generate(...)` returns generated text.
- `generateWithTimings(...)` returns `AiGenerationTimings`; its default implementation reports zero timing rates and counts.
- Default `close()` performs no action.

#### Core logic
- `generate` is the provider implementation contract; implementations supply generated text.
- `generateWithTimings` delegates to `generate` and wraps the text with zero-valued timing fields unless overridden.
- `close` defaults to a no-op.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — release provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `generate` contract specifies a non-null result; it may be blank.
- `close` declares `IOException`.
