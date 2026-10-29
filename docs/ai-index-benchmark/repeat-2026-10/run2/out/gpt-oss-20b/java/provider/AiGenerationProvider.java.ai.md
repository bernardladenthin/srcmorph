### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:12:54Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an AI backend that generates text for a generation request and optionally reports timing metrics.

#### Purpose
- Supplies AI-generated text for `AiGenerationRequest`.
- Supports optional timing data for calibration.

#### Type
- Interface `AiGenerationProvider` (public, extends `AutoCloseable`).

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.
- `close()` – no parameters.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing generated text and timing metrics.
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings` default: calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException` for provider failures.

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.
