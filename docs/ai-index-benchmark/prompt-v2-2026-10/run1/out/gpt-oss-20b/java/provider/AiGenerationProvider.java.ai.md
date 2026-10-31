### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:22Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI backends that generate text for AI generation requests.

#### Purpose
- Supplies AI-generated text for `AiGenerationRequest`.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Default method `generateWithTimings`.
- Implements `AutoCloseable` with no-op `close`.

#### Input
- `generate(AiGenerationRequest request)`  
  * `AiGenerationRequest` containing prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)`  
  * Same request object.

#### Output
- `generate` returns `String` (never null, may be blank).
- `generateWithTimings` returns `AiGenerationTimings` (generated text + timing data).
- `close` performs no action.

#### Core logic
- `generate` is abstract; concrete providers implement text generation.
- `generateWithTimings` calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.  
- `close() -> void` – close provider (no-op by default).

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException` for provider failures.

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.
