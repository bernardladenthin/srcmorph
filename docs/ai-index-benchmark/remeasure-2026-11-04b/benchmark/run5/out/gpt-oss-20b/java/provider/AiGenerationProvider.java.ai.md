### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides a pluggable AI backend for generating text from an `AiGenerationRequest`.

#### Purpose
- Supplies text generation for AI requests.
- Supports optional timing metrics for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `generate(AiGenerationRequest)`, `generateWithTimings(AiGenerationRequest)`, `close()`.
  - Default implementation of `generateWithTimings` returns `AiGenerationTimings` with zero rates.
  - Default `close` does nothing.

#### Input
- `generate(AiGenerationRequest request)` receives an `AiGenerationRequest` containing prompt key, source file, source text, and current header.
- `generateWithTimings(AiGenerationRequest request)` receives the same request.

#### Output
- `generate` returns a non‑null `String` (may be blank).
- `generateWithTimings` returns an `AiGenerationTimings` with generated text and zeroed timing fields.
- `close` has no side effects.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings`:
  1. Calls `generate(request)`.
  2. Constructs `new AiGenerationTimings(text, 0, 0, 0.0d, 0, 0.0d)`.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.

#### Concurrency
- No explicit concurrency handling; interface is thread‑safe only if implementations ensure it.
