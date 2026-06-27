### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:03:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> Provides a pluggable local LLM inference engine via llama.cpp JNI, extracting clean model answers by stripping internal reasoning blocks for AI indexing.

#### Purpose
*   Enables local text generation using GGUF models through the llama.cpp native backend.
*   Ensures clean data persistence by stripping internal chain-of-thought reasoning before indexing.

#### Responsibilities
*   **Provider Interface**: Defines contracts for generating text from requests, supporting live and mock implementations.
*   **Factory & Configuration**: Manages instantiation of providers based on name keys and immutable runtime settings (paths, sampling params).
*   **Native Inference**: Executes local LLM inference via JNI, handling lazy model loading and parameter construction.
*   **Data Sanitization**: Parses raw completion text to remove internal thinking blocks before storage.
*   **Testing Support**: Provides deterministic mock implementations for unit testing generation logic.

#### Key units
*   `AiGenerationProvider`: Interface defining the contract for generating text from `AiGenerationRequest` objects.
*   `LlamaCppJniAiGenerationProvider`: Final class implementing local inference via `LlamaModel` and `InferenceParameters`.
*   `LlamaCppJniConfig`: Immutable record holding native library paths, model files, and sampling parameters (temperature, topP, threads).
*   `AiCompletionParser`: Utility stripping `THINKING_BLOCK` markers from raw responses to return clean answers.
*   `AiGenerationProviderFactory`: Factory selecting and instantiating providers (`Mock`, `LlamaCppJni`) by name string.

#### Data flow
1.  **Request Ingestion**: `AiGenerationRequest` (prompt key, source text) enters the system.
2.  **Provider Selection**: `AiGenerationProviderFactory` selects implementation (`Mock` or `LlamaCppJni`) based on `providerName`.
3.  **Inference Execution**:
    *   `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel` if needed.
    *   Constructs `InferenceParameters` (messages, chat template) from `LlamaCppJniConfig` and request.
    *   Calls native `chatCompleteText()` to get raw completion.
4.  **Sanitization**: Raw output passed to `AiCompletionParser` to strip internal reasoning blocks.
5.  **Output**: Cleaned string returned to caller or stored in AI index.

#### Dependencies
*   **Internal**: `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `InferenceParameters`, `Pair`.
*   **External Modules**: `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`, native `LlamaModel` (GGUF), `java.io.IOException`, `java.nio.file.Path`.

#### Cross-cutting
*   **Immutability**: `LlamaCppJniConfig` uses final fields and Lombok annotations to ensure thread-safe configuration.
*   **Null Safety**: Critical checks for `modelPath` in config and request parameters; throws `NullPointerException` if null.
*   **Error Handling**: Centralized `IOException` usage for native failures and incomplete thinking blocks.
*   **Resource Management**: `AutoCloseable` pattern ensures native model handles are released via `close()`.
