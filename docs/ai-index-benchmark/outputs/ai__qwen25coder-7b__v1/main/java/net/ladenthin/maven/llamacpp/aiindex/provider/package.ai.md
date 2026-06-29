### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:53:20Z
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
> This package provides a suite of classes and interfaces for handling AI text generation, including parsing raw completion text, generating text based on requests, and selecting appropriate generation providers.

#### Purpose
- Parses raw LLM completion text to extract model answers.
- Provides a pluggable interface for AI text generation.
- Selects and instantiates AI generation providers based on configuration.

#### Responsibilities
- **Text Parsing**: Responsible for cleaning raw LLM completion text by removing internal thinking blocks and storing the cleaned answer in an AI index file.
- **Text Generation**: Defines contracts for generating text from AI requests, supporting both default and temperature-overridden generations.
- **Provider Selection**: Selects and instantiates appropriate AI generation providers based on configuration.

#### Key units
- **AiCompletionParser**: Parses raw LLM completion text to extract the model answer by stripping any internal thinking block.
- **AiGenerationProvider**: Defines a contract for generating text from AI requests, supporting both default and temperature-overridden generations.
- **AiGenerationProviderFactory**: Selects and instantiates an `AiGenerationProvider` based on the provided name.
- **LlamaCppJniAiGenerationProvider**: Implements an AI generation provider using the llama.cpp JNI binding to generate text based on prompts.
- **LlamaCppJniConfig**: Provides immutable configuration for the llama.cpp JNI provider.

#### Data flow
- **Input**: Raw LLM completion text or AI generation requests.
- **Process**: Parsing, generation, and provider selection.
- **Output**: Cleaned answer text or generated text based on requests.

#### Dependencies
- **Internal Collaborations**:
  - `AiCompletionParser` depends on `java.io.IOException`.
  - `AiGenerationProviderFactory` depends on `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`.
  - `LlamaCppJniAiGenerationProvider` depends on various classes from the `net.ladenthin.llama` package and other utilities.
- **External Modules/Tables**: None noted.

#### Cross-cutting
- **Shared Types/Interfaces**: `AiGenerationProvider`.
- **Exception/Errors**: `IOException` is thrown in several places for handling errors during text generation.
- **Concurrency**: Not explicitly handled in the provided source.
