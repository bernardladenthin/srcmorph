### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:38:55Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices by providing a Java implementation for generating AI-driven content using a local llama.cpp JNI binding, supporting various model configurations and parameters for efficient AI generation.

#### Purpose
- Provide a Java implementation for generating AI-driven content using a local llama.cpp JNI binding.
- Support various model configurations and parameters for efficient AI generation.

#### Type
- Class (`final`): `LlamaCppJniAiGenerationProvider`
- Implements: `AiGenerationProvider`, `AutoCloseable`
- Extends: None
- Generics: None
- Modifiers: `final`

#### Input
- Constructor parameters:
  - `config`: `LlamaCppJniConfig`
  - `promptSupport`: `AiPromptSupport`
- Method parameters:
  - `request`: `AiGenerationRequest`

#### Output
- Return types:
  - `String`: from `generate(AiGenerationRequest request)`
  - `ChatResponse`: from `chatResponse(AiGenerationRequest request)`
  - `AiGenerationTimings`: from `generateWithTimings(AiGenerationRequest request)`
- Produced state:
  - `model`: `LlamaModel` (cached after first use)
  - `response`: `ChatResponse`
  - `timings`: `Timings`

#### Core logic
- Loads the GGUF model lazily on first use.
- Configures model parameters based on the provided configuration.
- Generates AI content based on the request.
- Parses the chat completion response.
- Handles exceptions and errors in the generation process.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates AI content based on the request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: Generates AI content with timing information.
- `close()`: Closes the provider, releasing resources.

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `LlamaModel`
- `ChatResponseParser`
- `Java8CompatibilityHelper`
- `FlashAttn`
- `ReasoningFormat`
- `ModelParameters`
- `ChatResponse`
- `Timings`
- `AiGenerationRequest`
- `LlamaCppJniProviderSupport`

#### Exceptions / Errors
- Throws `IOException` in `generate(AiGenerationRequest request)`.
- Handles `null` values and exceptions internally.

#### Concurrency
- The `model` is lazily initialized and accessed in a thread-safe manner.
- The provider is `AutoCloseable`, ensuring resources are released when no longer needed.
