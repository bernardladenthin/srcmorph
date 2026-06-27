### main/java
- H: 1.0
- C: E9AF7086
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:38:18Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [net/](net/package.ai.md)
---
> Manages native Java-GGUF inference via JNI to generate text from source files with configurable sampling parameters.

#### Purpose
*   Orchestrates local LLM text generation using native `llama.cpp` bindings to index and summarize source files.
*   Bridges Maven POM definitions (`AiModelDefinition`) with runtime inference configurations (`AiGenerationConfig`).

#### Responsibilities
*   **Native Inference Management**: Loads GGUF models via JNI, manages chat context windows, and executes token generation with configurable temperature and top-p settings.
*   **Configuration Resolution**: Maps Maven `AiModelDefinition` entries to `AiGenerationConfig` instances and selects file-specific prompt templates.
*   **Prompt Construction & Parsing**: Builds chat messages from source files and parses raw model outputs to strip reasoning markers and extract clean answers.
*   **Provider Abstraction**: Provides a unified interface for switching between native Llama.cpp inference and deterministic mock providers.

#### Key units
*   `AiGenerationProvider`: Defines the contract for generating text from prompts and source file paths.
*   `LlamaCppJniAiGenerationProvider`: Implements native inference via JNI, handling lazy initialization of `LlamaModel` and chat message construction.
*   `LlamaCppJniConfig`: Immutable record holding model paths, context limits, sampling parameters, and stop strings.
*   `AiCompletionParser`: Extracts clean text from raw completion strings by removing reasoning block markers and handling budget exhaustion.
*   `AiGenerationProviderFactory`: Instantiates specific providers (`Mock` or `LlamaCppJni`) based on configuration names.
*   `AiModelDefinitionSupport`: Resolves Maven POM `AiModelDefinition` entries into active inference configurations.

#### Data flow
1.  **Configuration Load**: Maven POM defines `AiModelDefinition` objects; `AiModelDefinitionSupport` validates keys and converts them to `AiGenerationConfig` instances.
2.  **Provider Selection**: The framework queries `AiGenerationProviderFactory` or directly instantiates `LlamaCppJniAiGenerationProvider` using the resolved config.
3.  **Inference Execution**: For a given source file, `AiPromptSupport` constructs a chat message including headers and content; the native backend executes `chatCompleteText`.
4.  **Response Processing**: Raw output is passed to `AiCompletionParser` to remove `THINKING_BLOCK_START/END_MARKERS`, returning a clean string or raising an exception on malformed reasoning blocks.

#### Dependencies
*   **Internal Modules**: `net.ladenthin.llama.parameters.ModelParameters`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
*   **Native Bindings**: JNI wrappers for llama.cpp (`libllama`), native memory management, and GGUF model loading.
*   **Standard Library**: `java.nio.file.Path`, `java.io.IOException`, `java.util.HashMap`, `java.util.List`.

#### Cross-cutting
*   **Immutability Strategy**: Configuration objects (e.g., `LlamaCppJniConfig`) use immutable records to ensure thread-safety without external locks.
*   **Null Safety**: Default constructors rely on static constants; null inputs in setters trigger graceful resets rather than immediate exceptions.
*   **Error Handling**: Standardizes `IOException` for native failures and reasoning block mismatches; zero-token outputs return blank strings instead of nulls.
*   **Testing Strategy**: Includes `MockAiGenerationProvider` to decouple unit tests from native dependencies, ensuring deterministic behavior during development.
