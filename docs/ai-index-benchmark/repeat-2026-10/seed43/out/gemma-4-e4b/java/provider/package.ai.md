### java/provider
- H: 1.0
- C: 85826AD8
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T14:00:11Z
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
> This package provides a unified, pluggable framework for interfacing with various Large Language Model backends, supporting both local (llama.cpp) and external (OpenAI-compatible) inference.

#### Purpose
* Defines the contract and mechanisms for AI text generation across different providers.
* Manages the instantiation and configuration of specific AI backend implementations.

#### Responsibilities
* **Provider Abstraction:** Defining the core `AiGenerationProvider` interface for text generation requests.
* **Provider Selection:** Using `AiGenerationProviderFactory` to select and create the correct provider based on configuration.
* **Local Inference:** Implementing `LlamaCppJniAiGenerationProvider` to interface with local GGUF models via JNI.
* **OpenAI Compatibility:** Providing support logic in `OpenAiCompatibleProviderSupport` for constructing requests and handling security for OpenAI-like APIs.
* **Response Parsing:** Utilizing `AiCompletionParser` to clean raw LLM output by stripping internal reasoning blocks.
* **Configuration Management:** Defining immutable configuration structures like `LlamaCppJniConfig` for fine-tuning provider parameters.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for AI text generation.
* `AiGenerationProviderFactory`: Factory responsible for creating specific provider instances.
* `LlamaCppJniAiGenerationProvider`: Concrete implementation for local llama.cpp inference.
* `LlamaCppJniConfig`: Immutable configuration object for llama.cpp parameters.
* `OpenAiCompatibleProviderSupport`: Logic helper for constructing OpenAI-compatible API requests and handling security.
* `AiCompletionParser`: Utility class for cleaning LLM responses by removing thinking blocks.

#### Data flow
Configuration (`LlamaCppJniConfig`, `AiPromptSupport`) feeds into the `AiGenerationProviderFactory` to select and instantiate a provider (e.g., `LlamaCppJniAiGenerationProvider`). The provider accepts an `AiGenerationRequest`, executes the generation (potentially using `OpenAiCompatibleProviderSupport` for external calls or direct JNI for local), and returns a result, which may then be processed by `AiCompletionParser` to yield the final, clean answer.

#### Dependencies
* **Internal:** `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniConfig`, `AiCompletionParser`, `OpenAiCompatibleProviderSupport`.
* **External/System:** `java.io.IOException`, `ObjectMapper` (for JSON handling), `LlamaModel` (JNI dependency).

#### Cross-cutting
* **Configuration:** `LlamaCppJniConfig` provides extensive, immutable configuration parameters for controlling generation behavior (e.g., `temperature()`, `contextSize()`, `gpuLayers()`).
* **Error Handling:** `IOException` is a common exception thrown during generation calls across providers.
* **Immutability:** `LlamaCppJniConfig` is immutable, ensuring thread safety for configuration state.
* **Pluggability:** The entire system relies on the `AiGenerationProvider` interface for swapping backend implementations.
