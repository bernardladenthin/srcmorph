### java/provider
- H: 1.0
- C: 6C10FAC9
- D: 2026-10-29T00:07:37Z
- T: 2026-10-29T00:10:07Z
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
> Provides AI generation capabilities for local and remote models, with pluggable implementations for different providers.

## Purpose
- Pluggable AI backend that produces text for an `AiGenerationRequest`.
- Implements text generation for local (llama.cpp) and remote (OpenAI-compatible) models.

## Responsibilities
- `AiGenerationProvider`: Interface for text generation providers.
- `AiGenerationProviderFactory`: Factory to create appropriate provider instances.
- `LlamaCppJniAiGenerationProvider`: JNI-based implementation for local llama.cpp models.
- `OpenAiCompatibleProviderSupport`: Utility for OpenAI-compatible providers.

## Key Units
- `AiGenerationProvider`: Interface for text generation providers.
- `AiGenerationProviderFactory`: Factory for creating generation providers.
- `LlamaCppJniAiGenerationProvider`: JNI-based implementation for local llama.cpp models.
- `OpenAiCompatibleProviderSupport`: Utility for OpenAI-compatible providers.

## Data Flow
1. `AiGenerationRequest` is created with generation parameters.
2. `AiGenerationProviderFactory` selects the appropriate provider based on the provider name or model configuration.
3. The selected provider (`LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`) generates text.
4. `AiGenerationProviderFactory` returns the generated text and optionally timings.

## Dependencies
- `java.io.IOException`: For handling I/O exceptions.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`: Request object for generation.
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`: Support for prompt handling.
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`: Configuration for llama.cpp provider.
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`: Mock provider for testing.
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`: OpenAI-compatible provider.
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`: JNI-based llama.cpp provider.
- `org.jspecify.annotations.Nullable`: Annotation for nullable parameters.

## Cross-cutting
- Thread-safe for read operations, but model loading is not thread-safe and should be handled externally.
- Immutable configuration in `LlamaCppJniConfig`.
- Stateless utility classes where applicable.

This markdown summary captures the essential structure and functionality of the package, focusing on its purpose, responsibilities, key units, data flow, dependencies, and cross-cutting concerns.
