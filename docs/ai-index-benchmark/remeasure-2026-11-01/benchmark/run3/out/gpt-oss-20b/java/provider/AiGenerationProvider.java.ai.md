### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:31Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI backends that generate text for AI generation requests.

#### Purpose
- Interface for AI text generation providers.
- Supports local or mock implementations.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`

#### Input
- `generate(AiGenerationRequest request)` – request with prompt, source file, source text, header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns generated text (non‑null, may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing data.
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; concrete providers implement text generation.
- `generateWithTimings` default: calls `generate`, returns timings with zeros.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` throw `IOException` on provider failure.

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.
