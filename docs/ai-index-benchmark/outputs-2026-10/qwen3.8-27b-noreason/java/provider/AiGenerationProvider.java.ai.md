### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:48Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable contract for AI text generation backends used by the srcmorph tool.

#### Purpose
- Interface for AI providers that generate text from an `AiGenerationRequest`.
- Supports both local inference (e.g., llama.cpp) and mock implementations for testing.

#### Type
- `public interface AiGenerationProvider`
- `extends AutoCloseable`

#### Input
- `AiGenerationRequest request`: contains prompt key, source file, source text, and current header.

#### Output
- `String`: generated text (never null, may be blank).
- `AiGenerationTimings`: generated text plus timing metrics (prompt tokens, completion tokens, total time, prompt rate, completion rate).

#### Core logic
- `generate`: abstract method; implementations produce text using default sampling parameters.
- `generateWithTimings`: default method; delegates to `generate` and wraps result in `AiGenerationTimings` with zeroed timing values; overridable by providers that expose real timings.
- `close`: default no-op; allows implementations to release resources.

#### Public API
- `generate(AiGenerationRequest request) -> String` — generates text for the request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` — generates text with timing data.
- `close() -> void` — releases provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate`, `generateWithTimings`, and `close` when the underlying provider fails.
