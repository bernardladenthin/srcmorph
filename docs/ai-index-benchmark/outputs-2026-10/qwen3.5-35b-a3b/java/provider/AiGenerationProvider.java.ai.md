### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:25:49Z
- G: 
- A: 0.0.0
- X: file
---
> Interface defining contract for pluggable AI text generation backends.

#### Purpose
*   Defines contract for AI text generation services.
*   Supports local inference or test mocks.

#### Type
*   `public interface AiGenerationProvider`
*   Extends `AutoCloseable`

#### Input
*   `AiGenerationRequest` parameter (prompt key, source file, text, header).

#### Output
*   `String` generated text (never null).
*   `AiGenerationTimings` wrapper with metrics.

#### Core logic
*   `generate`: Invokes backend model inference.
*   `generateWithTimings`: Wraps `generate` result with zero timing defaults.
*   `close`: Releases resources (default no-op).

#### Public API
*   `generate(AiGenerationRequest) -> String` Generates text using default sampling parameters.
*   `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Generates text with measured timing metrics.
*   `close() -> void` Closes underlying provider resources.
*   (3 total)

#### Dependencies
*   `java.io.IOException`
*   `net.ladenthin.srcmorph.document.AiGenerationRequest`
*   `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
*   `IOException` thrown on failure.
*   `generate` never returns null.
