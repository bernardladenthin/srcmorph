### java/provider
- H: 1.0
- C: 9FE1B40A
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:55:05Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Provides a pluggable framework for integrating AI text generation backends, supporting local GGUF models via JNI and remote OpenAI-compatible APIs.

#### Purpose
* Abstracts AI text generation to decouple request logic from specific model implementations.
* Manages the lifecycle, configuration, and response parsing of LLM providers.

#### Responsibilities
* **Provider Orchestration**: Managing the instantiation and resolution of AI backends via a factory.
* **Local Inference**: Interfacing with native `llama.cpp` bindings for local model execution and hardware offloading.
* **Remote API Integration**: Handling JSON serialization, endpoint management, and secure API key transmission for OpenAI-compatible services.
* **Response Processing**: Cleaning raw LLM output by stripping chain-of-thought "thinking" blocks.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for generating text and performance timings.
* `AiGenerationProviderFactory`: Factory that resolves and creates providers (Mock, LlamaCppJni, or OpenAI-Compatible).
* `LlamaCppJniAiGenerationProvider`: Implementation for local GGUF models using native `LlamaModel` handles.
* `LlamaCppJniConfig`: Immutable configuration object for model paths, sampling parameters, and GPU/CPU offloading.
* `OpenAiCompatibleProviderSupport`: Utility for constructing JSON payloads and validating secure API key transmission.
* `AiCompletionParser`: Utility to extract final answers by removing `<thought>` blocks.

#### Data flow
1. `AiGenerationProviderFactory` instantiates a provider based on configuration.
2. `AiGenerationRequest` is passed to the provider's `generate` or `generateWithTimings` method.
3. The provider (e.g., `LlamaCppJniAiGenerationProvider`) uses `AiPromptSupport` to render the prompt.
4. The request is sent to the native model or remote API.
5. Raw response text is passed through `AiCompletionParser` to strip reasoning blocks.
6. Final cleaned text and `AiGenerationTimings` (token counts/throughput) are returned.

#### Dependencies
* **Native/External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ChatResponse`, `Usage`).
* **Libraries**: `Jackson` (JSON serialization), `Lombok` (boilerplate reduction).
* **Internal**: `AiPromptSupport` (prompt rendering), `AiGenerationRequest` and `AiGenerationTimings` (data transfer objects).

#### Cross-cutting
* **Immutability**: Configuration is handled via immutable objects (`LlamaCppJniConfig`) using the Builder pattern.
* **Security**: API keys are blocked over plain HTTP unless the destination is a loopback address.
* **Resource Management**: Providers implement `AutoCloseable` to ensure native memory/handles are released.
* **Error Handling**: Use of `IOException` for provider failures and `IllegalArgumentException` for configuration errors.
