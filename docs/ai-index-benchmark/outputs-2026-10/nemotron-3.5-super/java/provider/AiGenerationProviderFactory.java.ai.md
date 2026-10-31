### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:44:40Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by provider key, defaulting to a mock provider when no provider is specified.

#### Purpose
- Selects and instantiates an AiGenerationProvider implementation by name.
- Resolves the effective provider key from run-level and model-level configuration.

#### Type
- public class AiGenerationProviderFactory
- Modifiers: public, non-final, non-abstract, non-sealed
- Annotation: lombok @ToString
- No superclass; no interfaces
- Public constants: PROVIDER_MOCK, PROVIDER_LLAMACPP_JNI, PROVIDER_OPENAI_COMPATIBLE
- Private field: Java8CompatibilityHelper compatibilityHelper

#### Input
- Constructor: no parameters
- effectiveProvider: final @Nullable String runProvider, final @Nullable String modelProvider
- create: final String providerName, final LlamaCppJniConfig llamaConfig, final AiPromptSupport promptSupport
- Reads private field Java8CompatibilityHelper compatibilityHelper

#### Output
- effectiveProvider returns @Nullable String: provider key, null or empty only when neither names one
- create returns AiGenerationProvider: newly-created provider instance
- No mutated fields; compatibilityHelper is effectively immutable after construction
- Side effect: throws IllegalArgumentException on unrecognized provider key

#### Core logic
- effectiveProvider returns modelProvider.trim() when modelProvider is non-null and non-blank, otherwise returns runProvider.
- create computes effective = effectiveProvider(providerName, llamaConfig.provider()).
- When effective is null or blank, create returns new MockAiGenerationProvider().
- Switch on effective: PROVIDER_MOCK returns MockAiGenerationProvider; PROVIDER_LLAMACPP_JNI returns LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport); PROVIDER_OPENAI_COMPATIBLE returns OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport); default throws IllegalArgumentException("Unsupported AI provider: " + effective).

#### Public API
- PROVIDER_MOCK -> String selects mock provider
- PROVIDER_LLAMACPP_JNI -> String selects llama.cpp JNI provider
- PROVIDER_OPENAI_COMPATIBLE -> String selects OpenAI-compatible provider
- AiGenerationProviderFactory() -> AiGenerationProviderFactory constructs factory instance
- effectiveProvider(String, String) -> String resolves effective provider key
- create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider instantiates provider by key

#### Dependencies
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- AiGenerationProvider (same package)
- MockAiGenerationProvider (same package)
- LlamaCppJniAiGenerationProvider (same package)
- OpenAiCompatibleAiGenerationProvider (same package)
- LlamaCppJniConfig (same package)

#### Exceptions / Errors
- create throws IllegalArgumentException when provider name is not recognised.
- effectiveProvider accepts null inputs and returns null when neither names a provider.
- create treats null or blank providerName as mock provider.

#### Concurrency
- No synchronization; stateless except immutable compatibilityHelper field; safe for concurrent use if dependencies are thread-safe.
