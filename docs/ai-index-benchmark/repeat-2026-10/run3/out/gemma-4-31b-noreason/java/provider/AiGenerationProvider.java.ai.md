### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:27:37Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text based on source code transformation requests.

#### Purpose
* Define a contract for AI text generation providers (e.g., local LLMs or mocks).
* Support performance calibration via timing metadata.

#### Type
Interface. Extends `AutoCloseable`.

#### Input
* `AiGenerationRequest`: Contains prompt key, source file, source text, and current header.

#### Output
* `String`: Generated text (non-null, potentially blank).
* `AiGenerationTimings`: Generated text paired with model performance metrics.

#### Core logic
* `generate`: Primary method for producing AI-generated text from a request.
* `generateWithTimings`: Default implementation that wraps `generate` output into an `AiGenerationTimings` object with zeroed metrics.
* `close`: Default no-op implementation for resource cleanup.

#### Public API
* `generate(AiGenerationRequest) -> String`: Produces text for a request.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Produces text and timing metrics.
* `close() -> void`: Closes provider resources.

#### Dependencies
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.document.AiGenerationTimings`
* `java.io.IOException`

#### Exceptions / Errors
* `IOException`: Thrown if the underlying AI provider fails.
