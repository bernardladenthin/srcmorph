### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:05Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides an interface for AI backends that generate text for a generation request and optionally report timing metrics.

#### Purpose
- Declares contract for AI text generation providers.
- Supplies default timing wrapper for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `String generate(AiGenerationRequest) throws IOException`; `default AiGenerationTimings generateWithTimings(AiGenerationRequest) throws IOException`; `default void close() throws IOException`.
  - Extends `AutoCloseable`.

#### Input
- `AiGenerationRequest request` parameter in `generate` and `generateWithTimings`.
- `AiGenerationRequest` contains prompt key, source file, source text, current header.

#### Output
- `String` from `generate`; never `null`, may be blank.
- `AiGenerationTimings` from `generateWithTimings`; default rates all zero.
- `close()` performs no action.

#### Core logic
- `generate` is abstract; implementation must produce text.
- `generateWithTimings` calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException` for provider failures.

#### Concurrency
- No explicit concurrency handling; interface is stateless.
