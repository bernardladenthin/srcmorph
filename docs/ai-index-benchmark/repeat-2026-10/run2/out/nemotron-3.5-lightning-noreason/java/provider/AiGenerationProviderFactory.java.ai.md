### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:42Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by name.

#### Purpose
Factory that selects and instantiates an {@link AiGenerationProvider} implementation by name.

#### Type
Class; final; concrete. Extends nothing; implements nothing. Key generics: none. Notable annotation: {@lombok @ToString}.

#### Input
- {@code providerName}: the run's provider key; one of {@value #PROVIDER_MOCK}, {@value #PROVIDER_LLAMACPP_JNI}, {@value #PROVIDER_OPENAI_COMPATIBLE} (defaults to mock when blank or {@code null})
- {@code llamaConfig}: the model's configuration of type {@link LlamaCppJniConfig}
- {@code promptSupport}: prompt lookup support of type {@link AiPromptSupport}

#### Output
- Returns a newly-created {@link AiGenerationProvider} instance: {@link MockAiGenerationProvider}, {@link LlamaCppJniAiGenerationProvider}, or {@link OpenAiCompatibleAiGenerationProvider}
- Throws {@link IllegalArgumentException} if the provider name is not recognised

#### Core logic
- Computes effective provider via {@link #effectiveProvider} from model and run provider settings
- If effective provider is null/blank, returns a {@link MockAiGenerationProvider}
- Switches on effective provider key to instantiate the matching provider:
  - {@value #PROVIDER_MOCK} → {@link MockAiGenerationProvider}
  - {@value #PROVIDER_LLAMACPP_JNI} → {@link LlamaCppJniAiGenerationProvider} with config and prompt support
  - {@value #PROVIDER_OPENAI_COMPATIBLE} → {@link OpenAiCompatibleAiGenerationProvider} with config and prompt support
  - Any other value → throws {@link IllegalArgumentException}

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
- Throws {@link IllegalArgumentException} with message "Unsupported AI provider: {effective}" when provider name is not recognised

#### Concurrency
No threading or synchronization concerns; stateless factory with only a single injected {@link Java8CompatibilityHelper} field.
