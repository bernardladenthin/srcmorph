### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:52:01Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation provider implementations based on provider keys and configuration.

#### Purpose
* Acts as a factory for creating `AiGenerationProvider` instances.
* Resolves the effective provider key by prioritizing model-specific configuration over run-level defaults.

#### Type
Class. Annotated with `@ToString` (Lombok).

#### Input
* `providerName` (String): The requested provider key.
* `llamaConfig` (`LlamaCppJniConfig`): Model configuration containing an optional provider override.
* `promptSupport` (`AiPromptSupport`): Support object for prompt lookup.
* `Java8CompatibilityHelper`: Internal dependency for string blank checks.

#### Output
* `AiGenerationProvider`: An instance of `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
* `String`: The resolved effective provider key via `effectiveProvider`.

#### Core logic
* **Provider Resolution**: `effectiveProvider` returns the `modelProvider` if it is present and non-empty; otherwise, it returns the `runProvider`.
* **Instantiation**:
    * If the effective provider is null or blank, defaults to `MockAiGenerationProvider`.
    * Maps `PROVIDER_MOCK` to `MockAiGenerationProvider`.
    * Maps `PROVIDER_LLAMACPP_JNI` to `LlamaCppJniAiGenerationProvider` using `llamaConfig` and `promptSupport`.
    * Maps `PROVIDER_OPENAI_COMPATIBLE` to `OpenAiCompatibleAiGenerationProvider` using `llamaConfig` and `promptSupport`.
    * Throws `IllegalArgumentException` for unrecognized keys.

#### Public API
* `AiGenerationProviderFactory()` -> void: Default constructor.
* `effectiveProvider(String, String)` -> `String`: Resolves priority between run and model providers.
* `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`: Instantiates the specific provider implementation.
* `PROVIDER_MOCK` (constant) -> `String`: Key for mock provider.
* `PROVIDER_LLAMACPP_JNI` (constant) -> `String`: Key for llama.cpp JNI provider.
* `PROVIDER_OPENAI_COMPATIBLE` (constant) -> `String`: Key for OpenAI-compatible API provider.

#### Dependencies
* `AiGenerationProvider`
* `MockAiGenerationProvider`
* `LlamaCppJniAiGenerationProvider`
* `OpenAiCompatibleAiGenerationProvider`
* `LlamaCppJniConfig`
* `AiPromptSupport`
* `Java8CompatibilityHelper`
* `org.jspecify.annotations.Nullable`
* `lombok.ToString`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown when an unsupported provider key is passed to `create`.
* `Nullable`: Used to indicate that provider strings may be null.
