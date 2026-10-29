### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:28Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Implementation of `AiGenerationProvider` for local GGUF model inference using llama.cpp.

#### Type
- Class, final, implements `AiGenerationProvider` and `AutoCloseable`.

#### Input
- `LlamaCppJniConfig` for configuration.
- `AiPromptSupport` for prompt lookup.

#### Output
- `String` for generated text.
- `AiGenerationTimings` for generation timings.

#### Core Logic
- Lazy loading of the native llama.cpp model.
- Configuration of model parameters based on provided settings.
- Parsing of chat responses and extraction of completion text.

#### Public API
- `generate(AiGenerationRequest request)`: Generates text based on the request.
- `generateWithTimings(AiGenerationRequest request)`: Generates text and returns timings.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `LlamaModel`, `AiCompletionParser`, `ChatResponseParser`, `Java8CompatibilityHelper`.

#### Exceptions / Errors
- `IOException` when loading the model.

#### Concurrency
- Thread-safe for read operations, but model loading is not thread-safe and should be handled externally.
