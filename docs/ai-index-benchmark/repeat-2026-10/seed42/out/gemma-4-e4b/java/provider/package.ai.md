### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:10Z
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
> This package provides a pluggable framework for interfacing with various AI model backends, supporting both local GGUF inference and OpenAI-compatible API calls.

#### Purpose
*   To define a standardized contract (`AiGenerationProvider`) for generating text from diverse AI sources.
*   To manage the instantiation and configuration of specific AI providers (e.g., Llama.cpp, OpenAI compatible).

#### Responsibilities
*   **Provider Abstraction:** Defining the core interface for AI text generation (`AiGenerationProvider`).
*   **Provider Selection:** Determining the correct AI backend implementation based on configuration (`AiGenerationProviderFactory`).
*   **Local Inference:** Implementing text generation via local GGUF models using JNI bindings (`LlamaCppJniAiGenerationProvider`).
*   **API Compatibility:** Structuring requests and handling security checks for OpenAI-like APIs (`OpenAiCompatibleProviderSupport`).
*   **Configuration Management:** Providing immutable configuration objects for complex provider settings (`LlamaCppJniConfig`).
*   **Response Post-processing:** Cleaning raw LLM outputs by stripping internal reasoning blocks (`AiCompletionParser`).

#### Key units
*   `AiGenerationProvider`: Interface defining the contract for text generation and resource cleanup.
*   `AiGenerationProviderFactory`: Factory responsible for creating the correct `AiGenerationProvider` instance.
*   `LlamaCppJniAiGenerationProvider`: Concrete implementation for local GGUF model inference via JNI.
*   `LlamaCppJniConfig`: Immutable configuration object holding 37 parameters for Llama.cpp interaction.
*   `OpenAiCompatibleProviderSupport`: Utility class for constructing OpenAI-formatted JSON requests and validating API key security.
*   `AiCompletionParser`: Utility class that parses raw LLM responses to extract only the final answer, stripping thinking blocks.

#### Data flow
Configuration (`LlamaCppJniConfig`) feeds into the `AiGenerationProviderFactory` to select and instantiate a provider (e.g., `LlamaCppJniAiGenerationProvider`). The provider uses its configuration and a request (`AiGenerationRequest`) to generate text. If the provider is OpenAI-compatible, `OpenAiCompatibleProviderSupport` constructs the necessary JSON request body. The raw output is then processed by `AiCompletionParser` to yield a clean final answer.

#### Dependencies
*   **Internal:** `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `ChatResponse`, `LlamaModel`, `ChatResponseParser`.
*   **External/Libraries:** `lombok`, `net.ladenthin.srcmorph.*`, `java.io.IOException`.
*   **Providers:** Depends on `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
*   **Configuration:** Extensive use of immutable configuration objects (`LlamaCppJniConfig`) managed via a Builder pattern.
*   **Error Handling:** Consistent use of `IOException` for I/O failures across providers, and `IllegalArgumentException` for configuration errors in the factory.
*   **Resource Management:** Providers implement `AutoCloseable` to ensure native resources are released.
*   **Pattern:** Factory pattern (`AiGenerationProviderFactory`) is used for provider instantiation.
