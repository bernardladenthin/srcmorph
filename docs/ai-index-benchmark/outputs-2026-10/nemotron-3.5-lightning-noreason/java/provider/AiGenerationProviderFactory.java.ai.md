### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:17Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by name.

#### Purpose
Factory class responsible for selecting and instantiating an {@link AiGenerationProvider} implementation based on a provider name and configuration.

#### Type
Class; final; concrete; extends Object; implements no interfaces; annotated with {@lombok @ToString}; generic type parameters: none.

#### Input
- {@code providerName}: the run's provider key; accepted values are {@value #PROVIDER_MOCK}, {@value #PROVIDER_LLAMACPP_JNI}, {@value #PROVIDER_OPENAI_COMPATIBLE} (defaults to mock when blank or {@code null})
- {@code llamaConfig}: the model's configuration of type {@link LlamaCppJniConfig}
- {@code promptSupport}: prompt lookup support of type {@link AiPromptSupport}
- Constructor parameters: none (no-arg constructor)

#### Output
- Returns a newly-created {@link AiGenerationProvider} instance
- Throws {@link IllegalArgumentException} if the provider name is not recognised

#### Core logic
- Computes effective provider via {@link #effectiveProvider} using model's provider or run's provider
- If effective provider is null or blank, returns a {@link MockAiGenerationProvider}
- Switches on effective provider key to instantiate the corresponding provider:
  - {@value #PROVIDER_MOCK}: returns {@link MockAiGenerationProvider}
  - {@value #PROVIDER_LLAMACPP_JNI}: returns {@link LlamaCppJniAiGenerationProvider} with {@code llamaConfig} and {@code promptSupport}
  - {@value #PROVIDER_OPENAI_COMPATIBLE}: returns {@link OpenAiCompatibleAiGenerationProvider} with {@code llamaConfig} and {@code promptSupport}
  - Default: throws {@link IllegalArgumentException} with message "Unsupported AI provider: {effective}"

#### Public API
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` - Creates an AI generation provider for the given provider name
- `effectiveProvider(String runProvider, String modelProvider) -> @Nullable String` - Computes the effective provider key

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`

#### Exceptions / Errors
- Throws {@link IllegalArgumentException} when the provider name is not recognised (default case in switch)

#### Concurrency
- No threading or synchronization concerns; stateless factory with only a single injected {@link Java8CompatibilityHelper} dependency
