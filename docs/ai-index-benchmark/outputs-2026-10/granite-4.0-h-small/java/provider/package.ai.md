### java/provider
- H: 1.0
- C: 227B76EA
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:15:30Z
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
> **Package**: `package.ai.md`  
> **Domain essence**: Provides a pluggable AI generation framework for source morphing, supporting local and remote backends with extensible configuration and error handling.

#### Purpose
- Enable AI-assisted source morphing by generating text completions from prompts.
- Support multiple AI backends (mock, llama.cpp, OpenAI-compatible) through a unified interface.
- Facilitate testing and development with mock implementations.
- Integrate with llama.cpp for local model inference.
- Ensure compatibility with OpenAI API conventions for remote providers.

#### Responsibilities
- **AiCompletionParser**: Extracts clean answer text from raw LLM completion responses.
- **AiGenerationProvider**: Defines an interface for AI backends to generate text based on source morphing requests.
- **AiGenerationProviderFactory**: Creates instances of `AiGenerationProvider` based on configuration.
- **LlamaCppJniAiGenerationProvider**: Implements AI generation using llama.cpp JNI bindings for local GGUF models.
- **LlamaCppJniConfig**: Holds configuration parameters for llama.cpp JNI provider.
- **OpenAiCompatibleProviderSupport**: Provides core logic for OpenAI-compatible providers, including request construction, endpoint resolution, and error handling.

#### Key units
- `AiCompletionParser`: Parses and cleans LLM completion text.
- `AiGenerationProvider`: Interface for AI generation providers.
- `AiGenerationProviderFactory`: Factory for creating `AiGenerationProvider` instances.
- `LlamaCppJniAiGenerationProvider`: Local AI generation using llama.cpp.
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI provider.
- `OpenAiCompatibleProviderSupport`: Core logic for OpenAI-compatible providers.

#### Data flow
1. **Request Initiation**: A source morphing request is initiated with a prompt.
2. **Provider Selection**: `AiGenerationProviderFactory` selects the appropriate provider based on configuration.
3. **Text Generation**: The selected provider generates text using the provided prompt.
4. **Completion Parsing**: `AiCompletionParser` extracts the final answer from the raw LLM response.
5. **Result Utilization**: The cleaned answer is stored in an AI index file for further processing.

#### Dependencies
- **Internal**: `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `AiPromptSupport`.
- **External**: `java.io.IOException`, `lombok.ToString`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.document.AiGenerationTimings`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.

#### Cross-cutting
- **Error Handling**: `OpenAiCompatibleProviderSupport` provides standardized error messages for non-2xx HTTP statuses.
- **Configuration**: `LlamaCppJniConfig` uses a fluent builder pattern for flexible configuration.
- **Concurrency**: No specific concurrency considerations mentioned, but the design supports pluggable providers which may have their own concurrency models.
- **Testing**: Mock implementations (`MockAiGenerationProvider`) facilitate testing without external dependencies.
