### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:51:57Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text based on source code transformation requests.

#### Purpose
* Defines a contract for AI text generation providers (e.g., local LLMs or mocks).
* Supports performance calibration via timing metadata.

#### Type
Interface. Extends `AutoCloseable`.

#### Input
* `AiGenerationRequest`: Contains prompt key, source file, source text, and current header.

#### Output
* `String`: Generated text (non-null, potentially blank).
* `AiGenerationTimings`: Generated text paired with model performance metrics.

#### Core logic
* Abstracts the interaction with an AI model to decouple the request logic from the specific backend implementation.
* Provides a default fallback for timing-aware requests that returns zeroed metrics.

#### Public API
* `generate(AiGenerationRequest) -> String`: Produces text for a given request.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Produces text and performance metrics.
* `close() -> void`: Releases provider resources.

#### Dependencies
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.document.AiGenerationTimings`
* `java.io.IOException`

#### Exceptions / Errors
* `IOException`: Thrown when the underlying AI provider fails.
