### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:12Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies generated text for AI transformation tasks.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider` – interface, extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` – receives a request with prompt key, source file, source text, and current header.
- `generateWithTimings(AiGenerationRequest request)` – same request, returns text plus timing data.

#### Output
- `generate` returns a non‑null `String` (may be blank).
- `generateWithTimings` returns an `AiGenerationTimings` containing generated text and timing metrics.
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; concrete providers implement text generation.
- `generateWithTimings` delegates to `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` is a no‑op default implementation.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – close provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` throw `IOException` if provider fails.

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.
