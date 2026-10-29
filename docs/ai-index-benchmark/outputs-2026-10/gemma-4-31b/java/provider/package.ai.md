### java/provider
- H: 1.0
- C: 61221DB3
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:40:03Z
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
> Provides a pluggable infrastructure for integrating various AI text generation backends, including local JNI-based models and remote OpenAI-compatible APIs, to process source code requests.

#### Purpose
* Abstract AI text generation to support multiple interchangeable backends.
* Manage local LLM inference via llama.cpp and remote API integrations.

#### Responsibilities
* **Provider Management**: Factory-based instantiation and lifecycle management of AI backends via `AiGenerationProviderFactory`.
* **Local Inference**: Integration with native GGUF models, hardware acceleration, and JNI bindings through `LlamaCppJniAiGenerationProvider`.
* **Remote API Support**: Construction of OpenAI-compatible JSON requests and secure API key handling via `OpenAiCompatibleProviderSupport`.
* **Response Processing**: Extraction of final answers by stripping chain-of-thought reasoning blocks using `AiCompletionParser`.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for text generation and performance timing.
* `AiGenerationProviderFactory`: Resolves provider keys to specific implementations (mock, llama-cpp, or openai-compatible).
* `LlamaCppJniAiGenerationProvider`: Local LLM implementation managing `LlamaModel` handles and native hardware settings.
* `OpenAiCompatibleProviderSupport`: Logic for JSON request construction, endpoint resolution, and API key security.
* `AiCompletionParser`: Utility to remove internal thinking blocks from LLM completions.
* `LlamaCppJniConfig`: Immutable configuration for model paths, context size, and GPU/CPU offloading.

#### Data flow
* `AiGenerationProviderFactory` instantiates an `AiGenerationProvider` based on configuration.
* `AiGenerationRequest` is passed to the provider's `generate` or `generateWithTimings` methods.
* Providers use support classes (`LlamaCppJniProviderSupport` or `OpenAiCompatibleProviderSupport`) to format requests for the AI backend.
* Raw AI responses are passed through `AiCompletionParser` to strip reasoning blocks before returning the final string.
* Performance metrics are captured and returned as `AiGenerationTimings`.

#### Dependencies
* **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`.
* **External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ChatResponse`), `com.fasterxml.jackson.databind.ObjectMapper` (JSON serialization).

#### Cross-cutting
* **Lazy Initialization**: `LlamaCppJniAiGenerationProvider` defers loading large GGUF models until the first generation call.
* **Security**: `OpenAiCompatibleProviderSupport` prevents API key transmission over plain HTTP unless the host is a loopback address.
* **Immutability**: `LlamaCppJniConfig` employs a fluent builder to ensure thread-safe, immutable configuration.
* **Error Handling**: Standardized use of `IOException` for provider failures and `IllegalArgumentException` for invalid configurations.
