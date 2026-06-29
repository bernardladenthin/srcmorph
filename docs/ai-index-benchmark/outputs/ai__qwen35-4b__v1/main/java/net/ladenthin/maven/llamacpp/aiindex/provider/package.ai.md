### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:27:01Z
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
> Provides a pluggable local LLM inference backend using JNI-backed llama.cpp for AI document processing, supporting deterministic mock testing and configurable sampling parameters.

#### Purpose
*   Implements local AI text generation for AI index documents via native llama.cpp or fallback mock providers.
*   Manages lifecycle of native resources (models, contexts) and provides a clean interface for prompt-based completion.

#### Responsibilities
*   **Native LLM Inference**: Handles GGUF model loading, chat message construction, and token generation using JNI.
*   **Configuration Management**: Stores immutable inference parameters (context size, temperature, threads, stop strings).
*   **Response Parsing**: Extracts clean answer text from raw model outputs by truncating reasoning blocks.
*   **Factory & Abstraction**: Provides interfaces for provider selection and mock implementations for testing.

#### Key units
*   `AiGenerationProvider`: Interface defining the contract for generating text from prompts and source files.
*   `LlamaCppJniAiGenerationProvider`: Implements native inference via JNI, managing lazy initialization of `LlamaModel`.
*   `LlamaCppJniConfig`: Immutable holder for model paths, context sizes, sampling settings (temperature, top-p/k), and stop strings.
*   `AiCompletionParser`: Utility to parse raw completion strings, removing reasoning markers and handling budget exhaustion errors.
*   `AiGenerationProviderFactory`: Factory class instantiating specific provider types (`Mock` or `LlamaCppJni`) based on name.
*   `MockAiGenerationProvider`: Deterministic test implementation returning static summaries based on file paths.

#### Data flow
1.  **Request**: `AiGenerationRequest` containing prompt, source file path, and header context enters the system.
2.  **Configuration**: `LlamaCppJniConfig` supplies model path, context limits, and sampling parameters to the provider.
3.  **Inference**: `LlamaCppJniAiGenerationProvider` constructs chat messages using `AiPromptSupport`, calls native `chatCompleteText`, and receives raw output.
4.  **Parsing**: Raw output is fed into `AiCompletionParser` to strip reasoning blocks (`THINKING_BLOCK_START/END_MARKERS`).
5.  **Output**: Cleaned string is returned; if markers are incomplete, an `IOException` indicates token budget exhaustion.

#### Dependencies
*   **Internal**: `AiPromptSupport`, `LlamaModel`, `InferenceParameters`, `Java8CompatibilityHelper`.
*   **External**: JNI bindings for llama.cpp (`libllama`, native memory), `java.nio.file.Path`, `java.io.IOException`.
*   **Data**: GGUF model files, configuration strings, stop token lists.

#### Cross-cutting
*   **Immutability**: `LlamaCppJniConfig` uses `@ConvertToRecord` for thread-safe configuration storage.
*   **Error Handling**: Standardizes `IOException` for native failures and reasoning block mismatches; returns blank strings on zero-token outputs instead of nulls.
*   **Concurrency**: Relies on lazy initialization and immutability; native resources are not shared across threads without explicit synchronization.
*   **Testing**: Includes a `MockAiGenerationProvider` to decouple unit tests from native dependencies.
