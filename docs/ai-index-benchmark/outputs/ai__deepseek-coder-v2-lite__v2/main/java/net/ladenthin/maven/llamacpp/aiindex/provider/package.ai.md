### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:53:33Z
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
> Provides a pluggable AI backend for generating text based on an AI generation request, with support for configurable prompts and inference settings.

#### Purpose
- To enable various AI backend implementations that can generate text from a given request.
- To abstract the details of the AI backend, allowing for different providers to be used interchangeably.

#### Responsibilities
- **AiCompletionParser.java**: Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- **AiGenerationProvider.java**: Provides a pluggable AI backend for generating text based on an AI generation request.
- **AiGenerationProviderFactory.java**: Selects and instantiates an `AiGenerationProvider` implementation by name.
- **LlamaCppJniAiGenerationProvider.java**: Provides a JNI-backed implementation for generating AI text using local llama.cpp models, supporting configurable prompts and inference settings.
- **LlamaCppJniConfig.java**: Immutable configuration for the llama.cpp JNI provider.
- **MockAiGenerationProvider.java**: MockAiGenerationProvider is a deterministic test-only implementation of AiGenerationProvider that returns a mock summary for testing purposes.

#### Key units
- **AiCompletionParser**: Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- **AiGenerationProvider**: Provides a pluggable AI backend for generating text based on an AI generation request.
- **AiGenerationProviderFactory**: Selects and instantiates an `AiGenerationProvider` implementation by name.
- **LlamaCppJniAiGenerationProvider**: Provides a JNI-backed implementation for generating AI text using local llama.cpp models, supporting configurable prompts and inference settings.
- **LlamaCppJniConfig**: Immutable configuration for the llama.cpp JNI provider.
- **MockAiGenerationProvider**: MockAiGenerationProvider is a deterministic test-only implementation of AiGenerationProvider that returns a mock summary for testing purposes.

#### Data flow
Inputs move through the package to outputs as follows:
1. `AiGenerationRequest` objects are processed by `AiGenerationProvider` implementations to generate text.
2. Configurations and prompts are managed by `LlamaCppJniConfig` and passed to `LlamaCppJniAiGenerationProvider`.
3. The result is parsed using `AiCompletionParser` before being stored in an AI index file.

#### Dependencies
- **External modules**: 
  - `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
  - `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
  - `org.jspecify.annotations.Nullable`
- **Internal modules**:
  - `net.ladenthin.llama.LlamaModel`
  - `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`
  - `net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider`

#### Cross-cutting
- **Shared base types**: `IOException`, `AutoCloseable`
- **Common error handling**: Throws `IOException` for issues with input/output operations.
- **Concurrency**: The provider is not thread-safe and should be managed in a single-threaded context or with appropriate synchronization.
- **Configuration management**: Configurations are managed by `LlamaCppJniConfig`, which ensures that `modelPath` is not `null`.
