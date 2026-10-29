### java/provider
- H: 1.0
- C: 62191CA5
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:07:15Z
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
> This package provides pluggable infrastructure for interfacing with various Large Language Model backends, supporting both local GGUF inference and OpenAI-compatible APIs.

#### Purpose
* To define a standardized, pluggable interface for AI text generation across different backend implementations.
* To manage the configuration and instantiation of specific AI providers (e.g., Llama.cpp, OpenAI-compatible).

#### Responsibilities
* **Provider Abstraction:** Defining the `AiGenerationProvider` contract for text generation.
* **Provider Selection:** Using `AiGenerationProviderFactory` to select and instantiate the correct provider based on configuration.
* **Local Inference:** Implementing `LlamaCppJniAiGenerationProvider` to handle local LLM interaction via JNI bindings.
* **API Compatibility:** Providing support logic in `OpenAiCompatibleProviderSupport` for constructing requests and validating security for OpenAI-like endpoints.
* **Response Parsing:** Utilizing `AiCompletionParser` to clean raw LLM outputs by stripping internal reasoning blocks.
* **Configuration Management:** Defining immutable configuration structures via `LlamaCppJniConfig` and its Builder.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for AI text generation.
* `AiGenerationProviderFactory`: Factory responsible for creating specific `AiGenerationProvider` instances.
* `LlamaCppJniAiGenerationProvider`: Concrete implementation for local LLM inference using llama.cpp JNI.
* `LlamaCppJniConfig`: Immutable configuration object controlling Llama.cpp parameters (e.g., `contextSize`, `gpuLayers`, `flashAttn`).
* `AiCompletionParser`: Utility class to extract clean answers from raw model responses by removing thinking blocks.
* `OpenAiCompatibleProviderSupport`: Logic layer for constructing OpenAI-compatible JSON requests and validating endpoint security.

#### Data flow
Requests flow from the caller through the `AiGenerationProviderFactory` to instantiate a specific provider (e.g., `LlamaCppJniAiGenerationProvider`). The provider uses its configuration (`LlamaCppJniConfig`) and support logic (`OpenAiCompatibleProviderSupport` if applicable) to generate text. The raw output is then processed by `AiCompletionParser` to yield the final, clean answer.

#### Dependencies
* **Internal:** `AiGenerationRequest`, `AiGenerationTimings`, `LlamaModel`, `ChatResponseParser`, `ChatResponse`.
* **External/Support:** `AiPromptSupport`, `Java8CompatibilityHelper`, `ObjectMapper` (for JSON serialization), `Lombok` utilities.
* **Configuration:** Relies heavily on `LlamaCppJniConfig` for detailed parameterization.

#### Cross-cutting
* **Configuration:** `LlamaCppJniConfig` provides a comprehensive, immutable configuration structure used across provider implementations.
* **Error Handling:** `IOException` is a common exception thrown during generation calls (`AiGenerationProvider` methods).
* **Patterns:** The Builder pattern is used extensively in `LlamaCppJniConfig` for safe, fluent construction of configuration objects.
* **Security:** `OpenAiCompatibleProviderSupport` enforces security checks (HTTPS/loopback) when handling API keys.
