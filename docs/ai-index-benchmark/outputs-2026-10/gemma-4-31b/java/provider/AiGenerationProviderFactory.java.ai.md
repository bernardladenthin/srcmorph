### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:30:39Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on configuration keys.

#### Purpose
* Factory for creating and selecting `AiGenerationProvider` implementations.

#### Type
class; `@ToString`.

#### Input
* `providerName` (String): The run's provider key.
* `llamaConfig` (`LlamaCppJniConfig`): Model configuration containing an optional provider override.
* `promptSupport` (`AiPromptSupport`): Support for prompt lookup.
* `Java8CompatibilityHelper`: Injected internal helper for string validation.

#### Output
* `AiGenerationProvider`: A newly instantiated provider implementation.
* `String`: The resolved effective provider key via `effectiveProvider`.

#### Core logic
* Resolves the effective provider key by prioritizing the `LlamaCppJniConfig` provider over the `runProvider`.
* Defaults to `MockAiGenerationProvider` if the resolved provider key is null or blank.
* Maps specific provider keys to implementations:
    * `mock` $\rightarrow$ `MockAiGenerationProvider`
    * `llamacpp-jni` $\rightarrow$ `LlamaCppJniAiGenerationProvider`
    * `openai-compatible` $\rightarrow$ `OpenAiCompatibleAiGenerationProvider`

#### Public API
* `AiGenerationProviderFactory()` -> void: Constructor.
* `effectiveProvider(String, String) -> String`: Resolves priority provider key.
* `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider`: Instantiates provider.

#### Dependencies
* `AiPromptSupport`
* `Java8CompatibilityHelper`
* `AiGenerationProvider`
* `MockAiGenerationProvider`
* `LlamaCppJniAiGenerationProvider`
* `OpenAiCompatibleAiGenerationProvider`
* `LlamaCppJniConfig`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* Throws `IllegalArgumentException` if the resolved provider key is not recognized.
