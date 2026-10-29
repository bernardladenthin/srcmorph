### java/provider
- H: 1.0
- C: 90FC146A
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:18:14Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Manages AI generation providers, including local implementations and support for OpenAI-compatible providers.

#### Purpose
- Provides a pluggable interface for generating text using AI.
- Supports both local and mock implementations.
- Manages configuration and request handling for AI generation providers.
- Parses raw LLM completion text to extract the model's answer.
- Implements an AI generation service using the Llama.cpp JNI binding.
- Caches the GGUF model for subsequent generation calls.
- Parses chat responses and extracts completion text.
- Supports logging and warning on truncated answers.
- Measures and returns generation timings.

#### Responsibilities
- **AI Generation Interface**: Defines a contract for AI backend providers that can generate text based on an `AiGenerationRequest`.
- **Configuration Management**: Manages immutable configuration settings for the llama.cpp JNI provider.
- **Provider Factory**: Selects and instantiates an `AiGenerationProvider` implementation by name.
- **Completion Parsing**: Parses raw LLM completion text to isolate the model's answer and strip internal reasoning blocks.
- **Thread Safety**: Ensures the provider is designed to be thread-safe with lazy initialization of the native model handle.

#### Key units
- **AiGenerationProvider**: Interface for generating text using AI.
- **LlamaCppJniAiGenerationProvider**: Class implementing AI generation using the Llama.cpp JNI binding.
- **LlamaCppJniConfig**: Immutable configuration class for the llama.cpp JNI provider.
- **AiCompletionParser**: Class for parsing raw LLM completion text to extract the model's answer.
- **AiGenerationProviderFactory**: Factory class for creating `AiGenerationProvider` instances based on a provider name.

#### Data flow
- **Inputs**: `AiGenerationRequest`, `LlamaCppJniConfig`, `AiPromptSupport`.
- **Outputs**: AI completion text, generation timings.
- **Process**: 
  - The `AiGenerationProviderFactory` determines the effective provider name and creates an instance.
  - The `LlamaCppJniAiGenerationProvider` generates AI completion text using the Llama.cpp JNI binding, caches the model, and handles logging and warnings.
  - The `AiCompletionParser` parses the raw completion text to extract the model's answer and strip internal reasoning blocks.

#### Dependencies
- **Internal Collaborations**:
  - `AiPromptSupport`: For prompt lookup support.
  - `LlamaCppJniConfig`: For configuration settings.
  - `AiGenerationRequest`: For generation requests.
- **External Modules/Packages/Tables**:
  - `net.ladenthin.llama.LlamaModel`: For the native LlamaModel handle.
  - `com.fasterxml.jackson.databind.ObjectMapper`: For JSON processing.
  - `java.io.IOException`: For handling I/O exceptions.
  - `java.util.List`: For handling lists of stop strings and sequence breakers.
  - `java.util.function.Function`: For environment variable lookup.
  - `java.util.regex.Pattern`: For pattern matching in error messages.

#### Cross-cutting
- **Shared Base Types/Interfaces**: `AiGenerationProvider`, `AutoCloseable`.
- **Common Exception/Handling**: Throws `IOException` and `IllegalArgumentException`.
- **Concurrency**: The provider is designed to be thread-safe with lazy initialization of the native model handle.
- **Configuration**: Managed through the `LlamaCppJniConfig` class with a fluent builder pattern.
