### main/java/net/ladenthin/maven/llamacpp
- H: 1.0
- C: 52B8025B
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T19:07:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [aiindex/](aiindex/package.ai.md)
---
> Orchestrates local Llama.cpp inference for Maven builds by mapping source files to AI prompts, executing native GGUF generation, and sanitizing reasoning outputs before indexing.

#### Purpose
- Enables local LLM inference via JNI using GGUF models while stripping internal chain-of-thought reasoning for clean indexing.
- Orchestrates AI-driven field generation for Maven builds by resolving file extensions to prompt templates and model configurations.

#### Responsibilities
- **Local Inference Engine**: Executes text generation through `LlamaCppJniAiGenerationProvider` using native bindings, handling lazy model loading and parameter construction.
- **Prompt & Model Management**: Maps source file extensions to specific prompts via `AiFieldGenerationSelector` and resolves model definitions for inference parameters.
- **Data Sanitization**: Parses raw completion text to remove internal thinking blocks before storage or indexing.
- **Configuration Lifecycle**: Manages runtime settings for context size, temperature, and threads across file-level and package-level generation scopes.

#### Key units
- `LlamaCppJniAiGenerationProvider`: Implements native inference via `LlamaModel` and `InferenceParameters`.
- `AiCompletionParser`: Strips `THINKING_BLOCK` markers from raw responses to return clean answers.
- `AiFieldGenerationConfig`: Links prompt templates and model definitions to file extension filters.
- `AiGenerationProviderFactory`: Selects and instantiates providers (`Mock`, `LlamaCppJni`) by name string.
- `AiModelDefinitionSupport`: Resolves definition keys to active generation configurations via lookup maps.
- `AiGenerationKind`: Enumerates operation scopes (`FILE_SUMMARY`, `PACKAGE_SUMMARY`).

#### Data flow
1. User POM configuration defines `AiFieldGenerationConfig` mapping extensions to prompts/models.
2. `AiFieldGenerationSelector` matches incoming source files against configs during build execution.
3. `AiGenerationProviderFactory` selects the appropriate provider (e.g., `LlamaCppJni`) based on request settings.
4. Native inference executes via `chatCompleteText()`, producing raw completion containing potential reasoning blocks.
5. `AiCompletionParser` sanitizes the output by stripping internal thinking markers.
6. Cleaned text is returned for AI indexing or stored in the package index.

#### Dependencies
- **Internal**: `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`, `LlamaCppJniConfig`, `AiPromptSupport`.
- **External Modules**: `net.ladenthin.llama.parameters.ModelParameters`, native `LlamaModel` (GGUF), `Java8CompatibilityHelper`, standard Java collections.

#### Cross-cutting
- **Immutability**: Configuration records (`LlamaCppJniConfig`) use final fields and defensive copying to ensure thread safety.
- **Null Safety**: Selectors handle null entries in configuration lists gracefully by skipping or falling back.
- **Resource Management**: Native model handles are released via `AutoCloseable` pattern using `close()`.
- **Error Handling**: Centralized usage of `IOException` for native failures and incomplete thinking blocks.
