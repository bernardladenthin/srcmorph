### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:42Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides an interface for AI text generation backends, exposing generation and optional timing metrics.

#### Purpose
- Declares contract for AI text generation providers.
- Supplies default timing wrapper for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `generate(AiGenerationRequest) -> String`, `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`, `close()`.
  - Default implementation of `generateWithTimings` delegates to `generate` and returns zeroed timings.
  - Implements `AutoCloseable` with no-op `close()`.

#### Input
- `generate(AiGenerationRequest request)` receives an `AiGenerationRequest` containing prompt key, source file, source text, and current header.
- `generateWithTimings(AiGenerationRequest request)` receives the same request.

#### Output
- `generate` returns a non‑null `String` (may be blank).
- `generateWithTimings` returns an `AiGenerationTimings` with generated text and zeroed timing fields.
- `close` performs no action.

#### Core logic
- `generateWithTimings`:
  1. Calls `generate(request)`.
  2. Constructs `new AiGenerationTimings(generatedText, 0, 0, 0.0d, 0, 0.0d)`.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – close provider (no-op).

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException` for provider failures.

#### Concurrency
- No explicit concurrency handling; interface is stateless.
