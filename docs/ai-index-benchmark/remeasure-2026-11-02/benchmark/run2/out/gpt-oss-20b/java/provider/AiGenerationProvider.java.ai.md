### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:46Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides AI text generation for source‑code transformation requests, optionally reporting timing metrics.

#### Purpose
- Supplies generated text for `AiGenerationRequest` objects.
- Supports optional timing data for calibration.

#### Type
`public interface AiGenerationProvider`  
extends `AutoCloseable`; no generics; no annotations.

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing metrics.
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; implementations supply model inference.
- `generateWithTimings` delegates to `generate`, wraps result in `AiGenerationTimings` with zeroed metrics; overrides allowed for real timing.
- `close` is a no‑op default; implementations may release resources.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException` for provider failures.

#### Concurrency
- No explicit synchronization; implementations decide thread safety.
