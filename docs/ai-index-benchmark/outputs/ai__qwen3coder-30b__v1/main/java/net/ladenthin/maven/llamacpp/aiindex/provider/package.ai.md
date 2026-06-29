### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:56:51Z
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
> Enables pluggable local and mock AI text generation for code indexing, with support for native llama.cpp inference and reasoning block parsing.

#### Purpose
- Provides flexible AI inference backends for generating code summaries.
- Supports both local GGUF model execution and deterministic mock responses for testing.

#### Responsibilities
- AI text generation via JNI-bound llama.cpp models
- Mock AI response generation for unit testing
- Parsing of LLM completion outputs to remove internal reasoning
- Configuration management for native inference parameters

#### Key units
- **AiCompletionParser** cleans model outputs by stripping thinking blocks
- **AiGenerationProvider** defines the contract for text generation
- **AiGenerationProviderFactory** selects and instantiates AI providers
- **LlamaCppJniAiGenerationProvider** implements local GGUF model inference
- **LlamaCppJniConfig** holds immutable configuration for llama.cpp invocation
- **MockAiGenerationProvider** supplies deterministic mock summaries

#### Data flow
1. Input request with prompt and source file is passed to `AiGenerationProvider`
2. Factory selects appropriate provider (`LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`)
3. Provider builds prompt using `AiPromptSupport`, configures inference, and calls native model
4. Raw completion text is processed by `AiCompletionParser` to remove reasoning blocks
5. Cleaned output is returned for indexing

#### Dependencies
- Native llama.cpp library via JNI bindings
- `AiPromptSupport` for prompt templating
- `LlamaModel` and related inference classes from native bindings
- `AiGenerationRequest` for input structure
- `ConvertToRecord` for configuration value semantics

#### Cross-cutting
- All providers implement `AiGenerationProvider` interface
- Shared exception handling via `IOException`
- Lombok annotations (`@ToString`, `@EqualsAndHashCode`) for boilerplate
- Immutable configuration objects (`LlamaCppJniConfig`)
- Stateful lazy initialization in `LlamaCppJniAiGenerationProvider`
- Thread safety considerations noted for native model usage
