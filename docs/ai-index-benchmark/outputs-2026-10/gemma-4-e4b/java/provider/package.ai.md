### java/provider
- H: 1.0
- C: 861A7934
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:59:56Z
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
> This package provides a pluggable framework for generating AI responses, supporting local GGUF models via JNI bindings and OpenAI-compatible remote APIs.

#### Purpose
*   To define and manage interchangeable providers for AI text generation.
*   To handle the configuration and parsing specific to different AI backend implementations (Llama.cpp, OpenAI-compatible).

#### Responsibilities
*   **Provider Abstraction:** Defining the `AiGenerationProvider` interface for pluggable AI backend contracts.
*   **Provider Selection:** Using `AiGenerationProviderFactory` to instantiate the correct provider based on configuration keys.
*   **Local Model Integration:** Implementing `LlamaCppJniAiGenerationProvider` to interface with GGUF models via JNI, managing native resource lifecycle.
*   **Configuration Management:** Providing immutable configuration objects (`LlamaCppJniConfig`) for fine-tuning Llama.cpp parameters.
*   **API Compatibility:** Supporting the construction of OpenAI-compatible request bodies and validating API key security via `OpenAiCompatibleProviderSupport`.
*   **Response Processing:** Parsing raw LLM outputs to extract clean answers, specifically handling reasoning blocks in `AiCompletionParser`.

#### Key units
*   `AiGenerationProvider`: Interface defining the contract for AI text generation services.
*   `AiGenerationProviderFactory`: Factory responsible for creating and selecting concrete `AiGenerationProvider` implementations.
*   `LlamaCppJniAiGenerationProvider`: Concrete provider implementing text generation using local GGUF models via JNI.
*   `LlamaCppJniConfig`: Immutable configuration object holding 37 parameters for Llama.cpp model tuning.
*   `AiCompletionParser`: Utility class that strips internal reasoning blocks from raw LLM responses.
*   `OpenAiCompatibleProviderSupport`: Utility class for constructing OpenAI-style JSON requests and validating API key security.

#### Data flow
Configuration (`LlamaCppJniConfig`, `AiPromptSupport`) feeds into the `AiGenerationProviderFactory` to select and create a provider (e.g., `LlamaCppJniAiGenerationProvider`). The provider accepts an `AiGenerationRequest`, executes inference (potentially using `OpenAiCompatibleProviderSupport` for remote calls or internal logic for local calls), and returns a `String` or `AiGenerationTimings`. If the response is raw text, `AiCompletionParser` may process it to yield the final answer.

#### Dependencies
*   **Internal:** `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `AiPromptSupport`, `ChatResponse`, `ChatResponseParser`.
*   **External/Support:** `Java8CompatibilityHelper`, `ObjectMapper`, `Lombok` utilities.
*   **Providers:** Depends on `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
*   **Configuration:** Extensive use of `LlamaCppJniConfig` for granular control over generation parameters (e.g., `temperature`, `contextSize`, `flashAttn`).
*   **Resource Management:** `AiGenerationProvider` and `LlamaCppJniAiGenerationProvider` implement `AutoCloseable` for resource cleanup.
*   **Error Handling:** `IOException` is used for I/O failures in providers; `IllegalArgumentException` is thrown by the factory for unrecognized provider keys.
*   **Immutability:** `LlamaCppJniConfig` is designed to be immutable after construction.
