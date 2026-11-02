### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend that generates text for an AiGenerationRequest.

#### Purpose
- Supplies AI-generated text for source morphing tasks.
- Supports timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Default methods: `generateWithTimings`, `close`.

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null generated text (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing data.
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings` delegates to `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.
- No other exceptions thrown.

#### Concurrency
- Stateless interface; default methods are thread‑safe. No synchronization required.
