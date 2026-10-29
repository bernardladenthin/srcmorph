### java/provider
- H: 1.0
- C: 13DE6834
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:39:02Z
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
> Manages pluggable AI text generation backends including local llama.cpp inference and OpenAI-compatible APIs with configuration and timing metrics.

#### Purpose
*   Defines contract and implementations for AI text generation services.
*   Supports local inference via llama.cpp JNI and external OpenAI-compatible endpoints.

#### Responsibilities
*   **Provider Abstraction:** `AiGenerationProvider` interface defines generation contract and resource lifecycle.
*   **Provider Selection:** `AiGenerationProviderFactory` resolves effective provider keys and instantiates implementations.
*   **Local Inference:** `LlamaCppJniAiGenerationProvider` wraps native llama.cpp model execution.
*   **Configuration:** `LlamaCppJniConfig` holds immutable generation parameters and model settings.
*   **External Integration:** `OpenAiCompatibleProviderSupport` constructs HTTP payloads and validates secure endpoints.

#### Key units
*   `AiGenerationProvider`: Interface for text generation with `generate` and `close` methods.
*   `AiGenerationProviderFactory`: Resolves provider keys and creates `AiGenerationProvider` instances.
*   `LlamaCppJniAiGenerationProvider`: Implements inference using `LlamaModel` native handle.
*   `LlamaCppJniConfig`: Immutable value object with 41 generation parameters.
*   `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request construction and security checks.
*   `AiCompletionParser`: Referenced by `LlamaCppJniAiGenerationProvider` for parsing logic.
*   `AiGenerationRequest`: Input parameter containing prompt key, source file, and text.
*   `AiGenerationTimings`: Wrapper for performance metrics and token counts.

#### Data flow
*   Factory resolves provider key from run/model configuration.
*   Provider instance receives `AiGenerationRequest` and configuration.
*   Local provider loads `LlamaModel` lazily and invokes `chatComplete()`.
*   External support serializes request to JSON and validates API key transport security.
*   Output returns generated `String` and `AiGenerationTimings` metrics.

#### Dependencies
*   **Internal:** `AiPromptSupport` for prompt lookup, `AiGenerationConfig` for defaults.
*   **External:** `net.ladenthin.llama` for native model bindings and JSON parsing.
*   **External:** `com.fasterxml.jackson.databind` for JSON serialization.
*   **External:** `lombok` for boilerplate reduction (`@ToString`, `@EqualsAndHashCode`).

#### Cross-cutting
*   **Immutability:** `LlamaCppJniConfig` and support classes use final fields.
*   **Resource Management:** `AutoCloseable` interface ensures native handle cleanup.
*   **Security:** `OpenAiCompatibleProviderSupport` blocks API keys over non-HTTPS unless loopback.
*   **Error Handling:** `IOException` for native/IO failures, `IllegalArgumentException` for invalid keys.
*   **Concurrency:** Stateless logic in support classes; native handle management requires external thread safety.
