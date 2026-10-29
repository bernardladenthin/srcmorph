### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:37:05Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by name.

#### Purpose
Factory that selects and instantiates an {@link AiGenerationProvider} implementation by name.

#### Type
Class; final; concrete; extends Object; implements no interfaces. Key generics: none. Notable annotations: @ToString from Lombok.

#### Input
- providerName: String — the run's provider key; defaults to mock when blank or null
- llamaConfig: LlamaCppJniConfig — the model's configuration
- promptSupport: AiPromptSupport — prompt lookup support passed to providers that need it

#### Output
- AiGenerationProvider — a newly-created provider instance
- Throws IllegalArgumentException if the provider name is not recognised

#### Core logic
- Calls effectiveProvider to resolve the final provider key from providerName and llamaConfig.provider()
- If effective is null or blank, returns new MockAiGenerationProvider()
- Switches on effective key:
  - PROVIDER_MOCK → new MockAiGenerationProvider()
  - PROVIDER_LLAMACPP_JNI → new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)
  - PROVIDER_OPENAI_COMPATIBLE → new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)
  - default → throws IllegalArgumentException("Unsupported AI provider: " + effective)

#### Public API
- create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider: Creates an AI generation provider for the given provider name
- effectiveProvider(String runProvider, String modelProvider) -> @Nullable String: Resolves the effective provider key

#### Dependencies
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.provider.AiGenerationProvider
- net.ladenthin.srcmorph.provider.MockAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider
- net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniConfig

#### Exceptions / Errors
- IllegalArgumentException thrown when provider name is not recognised (default case in switch)

#### Concurrency
No threading or synchronization concerns; pure factory logic with no shared mutable state.
