### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:52:47Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that selects and instantiates AiGenerationProvider implementations by name.

#### Purpose
- Factory for selecting and instantiating AI generation providers by name.
- Resolves provider keys and delegates provider creation.

#### Type
- public class AiGenerationProviderFactory (no extends, no implements)

#### Input
- Constructor: no parameters.
- effectiveProvider(runProvider, modelProvider): both nullable strings.
- create(providerName, llamaConfig, promptSupport): providerName string, llamaConfig LlamaCppJniConfig, promptSupport AiPromptSupport.

#### Output
- Returns AiGenerationProvider instance (MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, or OpenAiCompatibleAiGenerationProvider).
- Throws IllegalArgumentException for unsupported provider.

#### Core logic
- effectiveProvider returns modelProvider if non-null/non-empty, else runProvider.
- create computes effective provider; if null/blank returns MockAiGenerationProvider.
- Switch on effective: mock -> MockAiGenerationProvider; llamacpp-jni -> LlamaCppJniAiGenerationProvider; openai-compatible -> OpenAiCompatibleAiGenerationProvider.
- Throws IllegalArgumentException for unknown provider.

#### Public API
- AiGenerationProviderFactory() -> creates instance
- PROVIDER_MOCK..PROVIDER_OPENAI_COMPATIBLE (3 total) -> constants
- effectiveProvider(runProvider, modelProvider) -> provider key string
- create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider

#### Dependencies
- Lombok @ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- LlamaCppJniConfig
- AiGenerationProvider (interface)
- MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider

#### Exceptions / Errors
- IllegalArgumentException if provider name is not recognised
- effectiveProvider may return null when neither run nor model specifies a provider

#### Concurrency
- No explicit synchronization; factory is stateless and thread-safe.
