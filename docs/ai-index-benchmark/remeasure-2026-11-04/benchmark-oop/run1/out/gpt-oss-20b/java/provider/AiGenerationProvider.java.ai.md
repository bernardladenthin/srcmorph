### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:24Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides a pluggable AI backend for generating text from an AiGenerationRequest.

#### Purpose
- Supplies AI-generated text for source‑code transformation requests.
- Offers optional timing metrics for calibration.

#### Type
- `public interface AiGenerationProvider` (extends `AutoCloseable`)
  - Annotations: none
  - Methods:
    - `String generate(AiGenerationRequest request) throws IOException`
    - `default AiGenerationTimings generateWithTimings(final AiGenerationRequest request) throws IOException`
    - `default void close() throws IOException`

#### Input
- `AiGenerationRequest request` (prompt key, source file, source text, current header)

#### Output
- `String` from `generate`; never `null`, may be blank.
- `AiGenerationTimings` from `generateWithTimings`; contains generated text and timing metrics.
- `void` from `close()` (no side effects).

#### Core logic
- `generate`: implementation‑specific text generation.
- `generateWithTimings`: default implementation calls `generate`, returns `AiGenerationTimings` with zeroed timing fields.
- `close`: default no‑op; may be overridden to release resources.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce AI text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing data.
- `close() -> void` – release provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings` if provider fails.

#### Concurrency
- No explicit synchronization; provider implementations may handle thread safety.
