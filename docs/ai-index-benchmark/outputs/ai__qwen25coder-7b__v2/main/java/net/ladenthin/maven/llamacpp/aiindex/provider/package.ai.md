### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:46:25Z
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
> This package provides the infrastructure and functionality for interacting with AI models, specifically focusing on text generation and parsing of model responses. It includes interfaces, factories, and implementations for generating AI text using different backends, as well as utilities for configuring and parsing the responses.

#### Responsibilities
- **AI Generation**: Interface and implementation for generating text based on requests.
- **Configuration**: Immutable configuration class for setting up the AI model.
- **Parsing**: Utility for extracting the actual model answer from raw completion text.
- **Mock Implementation**: A mock provider for testing purposes.

#### Key Units
- **AiGenerationProvider**: Interface for pluggable AI backend implementations.
- **LlamaCppJniAiGenerationProvider**: Provides AI generation functionality using the Llama.cpp JNI binding.
- **MockAiGenerationProvider**: Mock implementation of `AiGenerationProvider` for testing.
- **AiCompletionParser**: Parses raw LLM completion text to extract the model answer.

#### Data Flow
1. **Input**: An `AiGenerationRequest` containing the prompt and other parameters.
2. **Processing**:
   - The request is passed to an `AiGenerationProvider` implementation, which generates text using a specific backend.
   - The generated text may be processed by `AiCompletionParser` to remove any internal reasoning blocks.
3. **Output**: The cleaned answer text as a String.

#### Dependencies
- Internal collaborations: `AiGenerationRequest`, `LlamaCppJniConfig`, `AiPromptSupport`, and other components within the package.
- External dependencies: `java.io.IOException`.

#### Cross-cutting
- **Concurrency**: No specific concurrency notes; implementations should handle thread safety as needed.
- **Configuration**: Managed through the `LlamaCppJniConfig` class.
- **Error Handling**: Exceptions like `IOException` are handled in various components.
