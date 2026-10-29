### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:59:50Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AiGenerationProvider implementation by name.

#### Purpose
- Factory for creating AiGenerationProvider instances based on provider name.
- Supports mock, llama.cpp JNI, and OpenAI-compatible providers.

#### Type
- Class: `AiGenerationProviderFactory`
- Public, non-final, non-abstract.

#### Input
- `providerName`: String, name of the provider to create.
- `llamaConfig`: LlamaCppJniConfig, configuration for llama.cpp provider.
- `promptSupport`: AiPromptSupport, prompt lookup support for providers.

#### Output
- `AiGenerationProvider`: Instance of the requested provider implementation.

#### Core logic
- Determines effective provider name using `effectiveProvider` method.
- Returns MockAiGenerationProvider if provider name is null or blank.
- Creates and returns provider instance based on provider name:
  - `PROVIDER_MOCK`: MockAiGenerationProvider
  - `PROVIDER_LLAMACPP_JNI`: LlamaCppJniAiGenerationProvider
  - `PROVIDER_OPENAI_COMPATIBLE`: OpenAiCompatibleAiGenerationProvider
- Throws IllegalArgumentException for unsupported provider names.

#### Public API
- `AiGenerationProviderFactory()`: Constructor.
- `PROVIDER_MOCK`: String constant for mock provider.
- `PROVIDER_LLAMACPP_JNI`: String constant for llama.cpp JNI provider.
- `PROVIDER_OPENAI_COMPATIBLE`: String constant for OpenAI-compatible provider.
- `effectiveProvider(String, String)`: Static method to determine effective provider name.
- `create(String, LlamaCppJniConfig, AiPromptSupport)`: Creates and returns AiGenerationProvider instance.

#### Dependencies
- `AiGenerationProvider`: Interface for AI generation providers.
- `AiPromptSupport`: Class for prompt lookup support.
- `Java8CompatibilityHelper`: Class for Java 8 compatibility methods.
- `LlamaCppJniConfig`: Class for llama.cpp JNI configuration.
- `MockAiGenerationProvider`: Class for mock AI generation provider.
- `OpenAiCompatibleAiGenerationProvider`: Class for OpenAI-compatible AI generation provider.
- `LlamaCppJniAiGenerationProvider`: Class for llama.cpp JNI AI generation provider.

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown when an unsupported provider name is provided.

#### Concurrency
- No concurrency considerations mentioned in the provided source.
