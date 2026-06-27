### ai
- H: 1.0
- C: F01CDC07
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:41:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [main/](main/package.ai.md)
---
> Orchestrates local Large Language Model (LLM) text generation and summarization using native C++ bindings via JNI, bridging Maven model definitions with runtime inference configurations.

#### Purpose
*   Provides a unified interface for generating text from source files using configurable native `llama.cpp` inference or deterministic mock providers.
*   Resolves Maven `AiModelDefinition` entries into runtime `AiGenerationConfig` and manages chat context windows and sampling parameters.

#### Responsibilities
*   **Native Inference Management**: Loads GGUF models via JNI, initializes `LlamaModel`, and executes token generation with dynamic temperature and top-p settings.
*   **Configuration Resolution**: Maps Maven POM artifacts to immutable inference configs and selects file-specific prompt templates.
*   **Prompt Engineering & Parsing**: Constructs chat messages from source content and strips reasoning block markers (`THINKING_BLOCK_START/END_MARKERS`) from raw outputs.
*   **Provider Abstraction**: Switches between native `LlamaCppJni` and `Mock` providers based on configuration names without changing the application logic.

#### Key units
*   `AiGenerationProvider`: Core interface defining contracts for prompt execution, source file paths, and output extraction.
*   `LlamaCppJniAiGenerationProvider`: Implements native inference logic, handling lazy initialization of `LlamaModel` and chat message construction.
*   `LlamaCppJniConfig`: Immutable record specifying model paths, context limits, sampling parameters, and stop strings.
*   `AiCompletionParser`: Extracts clean text by removing reasoning markers and handling budget exhaustion or malformed blocks.
*   `AiGenerationProviderFactory`: Factory class instantiating specific providers (`Mock` or `LlamaCppJni`) based on configuration keys.
*   `AiModelDefinitionSupport`: Utility resolving Maven `AiModelDefinition` entries into active inference configurations.

#### Data flow
1.  **Configuration Initialization**: Maven POM defines `AiModelDefinition` objects; `AiModelDefinitionSupport` validates keys and converts them to `AiGenerationConfig`.
2.  **Provider Instantiation**: Framework queries `AiGenerationProviderFactory` or directly constructs `LlamaCppJniAiGenerationProvider` using the resolved config.
3.  **Inference Execution**: `AiPromptSupport` builds chat messages with headers and source content; native backend calls `chatCompleteText`.
4.  **Output Extraction**: Raw completion string flows to `AiCompletionParser`, which removes reasoning block markers and returns a clean string or raises an exception on errors.

#### Dependencies
*   **Native Bindings**: JNI wrappers for `libllama` (llama.cpp), native memory management, and GGUF model loading libraries.
*   **Internal Modules**: `net.ladenthin.llama.parameters.ModelParameters`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
*   **Standard Library**: `java.nio.file.Path`, `java.io.IOException`, `java.util.HashMap`, `java.util.List`.

#### Cross-cutting
*   **Immutability & Thread Safety**: Configuration objects (e.g., `LlamaCppJniConfig`) utilize immutable records to ensure thread-safety without external locks.
*   **Null Safety Patterns**: Default constructors rely on static constants; null inputs in setters trigger graceful resets rather than immediate exceptions.
*   **Error Handling Strategy**: Standardizes `IOException` for native failures and reasoning block mismatches; zero-token outputs return blank strings instead of nulls.
*   **Testing Architecture**: Includes `MockAiGenerationProvider` to decouple unit tests from native dependencies, ensuring deterministic behavior during development.
