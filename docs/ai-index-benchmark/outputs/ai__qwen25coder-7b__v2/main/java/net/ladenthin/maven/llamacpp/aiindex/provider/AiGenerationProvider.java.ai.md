### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:33:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend for generating text from an `AiGenerationRequest`, supporting default and temperature-override generation.

#### Purpose
- Interface for AI backend implementations that can generate text based on a request.
- Allows for local execution (e.g., llama.cpp) or mock providers for testing.

#### Type
- Interface + public; implements `AutoCloseable`; no generics; no notable annotations.

#### Input
- `generate(AiGenerationRequest request)`: The generation request containing prompt, source file, source text, and current header.
- `generate(AiGenerationRequest request, float temperatureOverride)`: The generation request and a specified temperature override for sampling.

#### Output
- `String`: Generated text; never `null`, may be blank if no tokens are produced.

#### Core logic
- **generate(AiGenerationRequest request)**: Generates text using the provider's default sampling parameters.
- **generate(AiGenerationRequest request, float temperatureOverride)**: Delegates to `generate(AiGenerationRequest request)` by default. Implementations supporting per-call temperature overrides should override this method.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text using the provider's default sampling parameters.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates text using a specified temperature override, or delegates to the default implementation.
- `close() -> void`: Closes the provider, throwing an `IOException` if the underlying provider fails.

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails.

#### Concurrency
- No specific concurrency notes; implementations should handle thread safety as needed.
