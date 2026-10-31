### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:38:55Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text based on specific generation requests.

#### Purpose
* Defines the contract for AI providers to produce text for `AiGenerationRequest` objects.
* Supports various implementations ranging from local models (e.g., llama.cpp) to mock providers for testing.

#### Type
interface extends `AutoCloseable`

#### Input
* `AiGenerationRequest` objects containing prompt keys, source files, source text, and current headers.

#### Output
* Generated text strings.
* `AiGenerationTimings` containing text and performance metrics.

#### Core logic
* `generate` serves as the primary execution point for text production.
* `generateWithTimings` provides a default implementation that wraps `generate` with zeroed-out timing metrics, intended to be overridden by providers capable of measuring performance.
* `close` provides a default no-op implementation for resource cleanup.

#### Public API
* `generate(AiGenerationRequest) -> String` generates text using default sampling parameters.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` returns text paired with model timing metrics.
* `close() -> void` closes the provider and releases resources.

#### Dependencies
* `java.io.IOException`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
* Throws `IOException` if the underlying provider fails during generation or closing.
