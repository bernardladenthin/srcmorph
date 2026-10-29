### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:41:22Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates specific AI generation provider implementations based on configuration keys.

#### Purpose
* Factory for creating and managing `AiGenerationProvider` instances.
* Resolves provider precedence between run-time settings and model-specific configurations.

#### Type
class; @ToString.

#### Input
* `AiGenerationProviderFactory()`: No parameters.
* `effectiveProvider(String runProvider, String modelProvider)`: Two nullable strings.
* `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`: Provider key, Llama configuration, and prompt support.

#### Output
* `effectiveProvider`: Returns a non-null/non-blank `String` or `null`.
* `create`: Returns a new `AiGenerationProvider` instance (Mock, LlamaCppJni, or OpenAiCompatible).

#### Core logic
* **Provider Resolution**: `effectiveProvider` prioritizes the model's specific provider over the run's provider if the model's provider is non-empty.
* **Instance Creation**: `create` determines the provider type via a switch statement on the effective key.
* **Defaulting**: Returns `MockAiGenerationProvider` if the effective provider key is null or blank.
* **Mapping**:
    * `PROVIDER_MOCK` -> `MockAiGenerationProvider`
    * `PROVIDER_LLAMACPP_JNI` -> `LlamaCppJniAiGenerationProvider`
    * `PROVIDER_OPENAI_COMPATIBLE` -> `OpenAiCompatibleAiGenerationProvider`

#### Public API
* `AiGenerationProviderFactory()` -> void (Default constructor)
* `PROVIDER_MOCK` -> String (Constant for mock provider)
* `PROVIDER_LLAMACPP_JNI` -> String (Constant for llama.cpp JNI provider)
* `PROVIDER_OPENAI_COMPATIBLE` -> String (Constant for OpenAI-compatible provider)
* `effectiveProvider(String, String)` -> String (Resolves provider precedence)
* `create(String, LlamaCppJniConfig, AiPromptSupport)` -> AiGenerationProvider (Factory method)

#### Dependencies
* `net.ladenthin.srcmorph.provider.AiGenerationProvider`
* `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
* `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
* `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown by `create` if the provider key does not match any known constants.
