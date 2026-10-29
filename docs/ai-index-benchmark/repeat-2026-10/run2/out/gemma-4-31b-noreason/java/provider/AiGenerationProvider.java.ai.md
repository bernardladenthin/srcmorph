### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:54:36Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text based on source code transformation requests.

#### Purpose
* Defines a contract for AI text generation providers (e.g., local LLMs or mocks).
* Supports performance calibration via timing metrics.

#### Type
Interface. Extends `AutoCloseable`.

#### Input
* `AiGenerationRequest`: Contains prompt key, source file, source text, and current header.

#### Output
* `String`: Generated text (non-null, potentially blank).
* `AiGenerationTimings`: Generated text paired with model performance metrics.

#### Core logic
* Abstracts the mechanism of sending a prompt to an AI model and receiving a text response.
* Provides a default timing implementation that returns zeroed metrics if the specific provider does not support timing measurement.
* Implements a no-op default `close()` method for resource cleanup.

#### Public API
* `generate(AiGenerationRequest) -> String`: Produces text for a request.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Produces text and timing data.
* `close() -> void`: Closes provider resources.

#### Dependencies
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.document.AiGenerationTimings`
* `java.io.IOException`

#### Exceptions / Errors
* `IOException`: Thrown when the underlying AI provider fails.
