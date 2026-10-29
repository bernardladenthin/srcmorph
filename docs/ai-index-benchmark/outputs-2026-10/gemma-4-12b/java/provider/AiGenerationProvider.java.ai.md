### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:40:10Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable backend for generating text based on AI generation requests.

#### Purpose
* Defines the contract for AI model providers (local or remote).
* Supports text generation with optional performance timing metrics.

#### Type
interface, extends AutoCloseable.

#### Input
* `AiGenerationRequest`: Contains prompt keys, source files, source text, and current headers.
* `IOException`: Handled during generation or closing.

#### Output
* `String`: The generated text (non-null, potentially blank).
* `AiGenerationTimings`: A record containing generated text and performance metrics (rates, counts, and durations).

#### Core logic
* Standardizes the `generate` method for consistent text production across different backends.
* Provides a default `generateWithTimings` implementation that wraps the standard generation and returns zeroed timing metrics.
* Inherits `AutoCloseable` to ensure provider resources (like network connections or model handles) can be released.

#### Public API
* `generate(AiGenerationRequest request) -> String`: Produces text for a request using default sampling.
* `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: Produces text and returns measured timing metrics.
* `close() -> void`: Closes the provider resources.

#### Dependencies
* `java.io.IOException`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Concurrency
* No specific synchronization or thread-safety guarantees defined in the interface.
