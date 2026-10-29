### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:30:10Z
- G: 
- A: 0.0.0
- X: file
---
>Provides a pluggable abstraction for AI backends to generate text based on source code requests.

#### Purpose
* Defines a standard interface for AI text generation providers (e.g., llama.cpp or mock implementations).
* Facilitates the production of text for `AiGenerationRequest` objects.

#### Type
Interface; extends `AutoCloseable`.

#### Input
* `AiGenerationRequest`: contains prompt key, source file, source text, and current header.

#### Output
* `String`: generated text (non-null, potentially blank).
* `AiGenerationTimings`: generated text combined with performance timing metrics.

#### Core logic
* `generate`: Primary mechanism to produce text using the provider's default sampling parameters.
* `generateWithTimings`: Wraps `generate` to provide timing data; defaults to zeroed rates if the provider does not support timing measurement.
* `close`: Provides a hook for resource cleanup.

#### Public API
* `generate(AiGenerationRequest) -> String`: Generates text using default sampling.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Generates text with timing data.
* `close() -> void`: Closes the provider resources.

#### Dependencies
* `java.io.IOException`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
* `IOException`: Thrown if the underlying AI provider fails.
