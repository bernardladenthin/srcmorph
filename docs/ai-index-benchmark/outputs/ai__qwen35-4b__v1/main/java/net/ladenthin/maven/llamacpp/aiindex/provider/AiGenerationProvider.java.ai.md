### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:18:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend interface for generating text responses from local LLMs (llama.cpp) or mock providers based on input requests.

#### Purpose
*   Defines the contract for generating AI text from prompts, source files, and headers via `AiGenerationRequest`.
*   Supports configurable sampling parameters like temperature to resolve generation failures.

#### Type
Kind: interface; extends AutoCloseable; implements default methods for fallback logic.

#### Input
*   `AiGenerationRequest`: Contains prompt key, source file path, source text, and current header context.
*   `float temperatureOverride`: Optional sampling parameter to override provider configuration during retry logic.

#### Output
*   `String`: Generated text response (never null, may be blank if model produces no tokens).
*   Side effect: Closes underlying resource on invocation of `close()` method.

#### Core logic
*   Validates `AiGenerationRequest` parameters for prompt construction and header injection.
*   Applies optional `temperatureOverride` to sampling configuration before invoking the generation engine.
*   Executes model inference locally via llama.cpp or mock fallback to produce text output.
*   Handles resource cleanup automatically via `AutoCloseable` contract implementation.

#### Public API
*   `generate(request) -> String`: Generates text using default provider parameters.
*   `generate(request, temperatureOverride) -> String`: Generates text with forced temperature override for retry logic.
*   `close() -> void`: Releases underlying AI backend resources safely.

#### Dependencies
*   `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
*   `java.io.IOException`

#### Exceptions / Errors
*   Throws `IOException` if the underlying AI provider fails during text generation.
*   Returns blank strings instead of null if the model produces zero tokens.

#### Concurrency
*   Stateless interface design ensures thread-safety across concurrent requests.
*   No internal synchronization required; relies on external resource management.
