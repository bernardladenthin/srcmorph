### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:12Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable interface for generating text responses to AI generation requests with optional timing metrics.

#### Purpose
*   Defines a contract for pluggable AI backends (local or mock) that produce text based on generation requests.
*   Enables calibration of model performance by returning generation timings alongside output.

#### Type
*   `interface` `AiGenerationProvider` extends `AutoCloseable`.
*   Key annotations: None explicitly shown in source.
*   Generics: None.

#### Input
*   Constructor: None (interface).
*   Method parameters: `AiGenerationRequest request` containing prompt key, source file, source text, and current header.
*   Dependencies: Consumes `net.ladenthin.srcmorph.document.AiGenerationRequest`.

#### Output
*   Return types: `String` generated text; `AiGenerationTimings` object containing text and performance metrics.
*   Side effects: None defined in interface; implementations may perform I/O.
*   State mutation: None.

#### Core logic
*   `generate`: Delegates to the specific backend implementation to produce text using default sampling parameters.
*   `generateWithTimings`: Default implementation calls `generate`, wraps result in `AiGenerationTimings` with zeroed rates/times unless overridden.
*   `close`: Default no-op implementation for resource cleanup via `AutoCloseable`.

#### Public API
*   `generate(AiGenerationRequest) -> String` Generates text for the provided request.
*   `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Generates text with measured timing data.
*   `close() -> void` Closes underlying resources.

#### Dependencies
*   `java.io.IOException`
*   `net.ladenthin.srcmorph.document.AiGenerationRequest`
*   `net.ladenthin.srcmorph.provider.AiGenerationTimings` (referenced in return type)

#### Exceptions / Errors
*   Throws `IOException` if the underlying provider fails during generation or closing.
*   Returns non-null strings; blank strings allowed if model produces no tokens.

#### Concurrency
*   No explicit synchronization or thread-safety guarantees defined in the interface.
