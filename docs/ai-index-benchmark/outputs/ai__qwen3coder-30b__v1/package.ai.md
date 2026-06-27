### ai
- H: 1.0
- C: F01CDC07
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T23:05:48Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [main/](main/package.ai.md)
---
> Automates intelligent code summarization and indexing for Java projects using configurable AI backends.

#### Purpose
- Configures AI workflows for analyzing and summarizing Java source files.
- Enables flexible, pluggable text generation backends including local llama.cpp models and mock responses.

#### Responsibilities
- **Configuration Management**: Defines AI model settings, prompt templates, and file selection rules.
- **Inference Execution**: Provides local GGUF model support via JNI and deterministic mocking for testing.
- **Output Processing**: Cleans AI completions by removing internal reasoning blocks for clean indexing.

#### Key units
- AiFieldGenerationConfig: Maps file extensions to prompt templates and model definitions.
- AiFieldGenerationSelector: Matches input files to appropriate generation configurations.
- AiGenerationConfig: Carries mutable inference parameters for llama.cpp.
- AiGenerationProvider: Interface for AI text generation implementations.
- LlamaCppJniAiGenerationProvider: Implements native GGUF model inference using JNI.
- MockAiGenerationProvider: Supplies deterministic mock responses for testing.
- AiCompletionParser: Strips reasoning blocks from raw AI outputs.

#### Data flow
- Maven plugin loads AiFieldGenerationConfig entries to match files with prompt templates.
- AiFieldGenerationSelector resolves the correct config by file extension.
- Config references an AiModelDefinition, resolved into AiGenerationConfig via AiModelDefinitionSupport.
- AiGenerationProviderFactory selects between LlamaCppJniAiGenerationProvider or MockAiGenerationProvider.
- Input prompts are processed through AiPromptSupport, native inference is called, and output is cleaned by AiCompletionParser.

#### Dependencies
- Internal: AiFieldGenerationConfig, AiFieldGenerationSelector, AiGenerationConfig, AiModelDefinition, AiModelDefinitionSupport, AiGenerationProviderFactory, LlamaCppJniAiGenerationProvider, MockAiGenerationProvider, AiCompletionParser
- External: Native llama.cpp via JNI bindings, Lombok annotations, org.jspecify.annotations.Nullable

#### Cross-cutting
- Defensive copying in setters ensures immutability of collections.
- Null handling with @Nullable and explicit checks across configuration classes.
- Thread safety: Not thread-safe; designed for single-threaded Maven plugin use.
- Immutable configuration objects (`LlamaCppJniConfig`) enforce consistency.
- Stateful lazy initialization in `LlamaCppJniAiGenerationProvider`.
- Shared exception handling via IOException.
