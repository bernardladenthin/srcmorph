### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:27:44Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation provider implementations based on provider keys and configuration.

#### Purpose
* Factory for creating `AiGenerationProvider` instances.
* Resolves the effective provider key by prioritizing model-specific settings over run-level settings.

#### Type
Class. Annotated with `@ToString` (Lombok).

#### Input
* `providerName` (String): The run-level provider key.
* `llamaConfig` (LlamaCppJniConfig): Model configuration containing a provider override.
* `promptSupport` (AiPromptSupport): Support object for prompt lookup.

#### Output
* `AiGenerationProvider`: A concrete implementation (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`).
* `String`: The resolved effective provider key via `effectiveProvider`.

#### Core logic
* **Provider Resolution**: `effectiveProvider` returns `modelProvider` if it is present and non-blank; otherwise, it returns `runProvider`.
* **Instantiation**:
    * If the effective provider is null or blank, defaults to `MockAiGenerationProvider`.
    * Maps `PROVIDER_MOCK` to `MockAiGenerationProvider`.
    * Maps `PROVIDER_LLAMACPP_JNI` to `LlamaCppJniAiGenerationProvider` using `llamaConfig` and `promptSupport`.
    * Maps `PROVIDER_OPENAI_COMPATIBLE` to `OpenAiCompatibleAiGenerationProvider` using `llamaConfig` and `promptSupport`.
    * Throws `IllegalArgumentException` for unrecognized keys.

#### Public API
* `AiGenerationProviderFactory()` -> void: Constructor.
* `effectiveProvider(String, String)` -> String: Resolves priority between run and model providers.
* `create(String, LlamaCppJniConfig, AiPromptSupport)` -> AiGenerationProvider: Instantiates the specific provider implementation.

#### Dependencies
* `AiGenerationProvider`
* `MockAiGenerationProvider`
* `LlamaCppJniAiGenerationProvider`
* `OpenAiCompatibleAiGenerationProvider`
* `LlamaCppJniConfig`
* `AiPromptSupport`
* `Java8CompatibilityHelper`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown when an unsupported provider key is passed to `create`.
* Handles `null` or blank strings for provider keys by defaulting to the mock provider.
