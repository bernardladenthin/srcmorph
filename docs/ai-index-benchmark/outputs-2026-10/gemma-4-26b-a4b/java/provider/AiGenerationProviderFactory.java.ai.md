### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:39:23Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates specific AI generation provider implementations based on provided configuration keys.

#### Purpose
* Acts as a factory to select and create an `AiGenerationProvider` (Mock, LlamaCppJNI, or OpenAI-compatible).

#### Type
Class; final (implicit via no inheritance shown); uses Lombok `@ToString`.

#### Input
* `providerName` (String): The run-level provider key.
* `llamaConfig` (LlamaCppJniConfig): Configuration containing model-specific provider settings.
* `promptSupport` (AiPromptSupport): Support for prompt lookups.
* `runProvider` / `modelProvider` (String): Parameters for determining the effective provider.

#### Output
* `AiGenerationProvider`: A concrete implementation of the provider interface.
* `String`: The resolved effective provider key.

#### Core logic
* Resolves the "effective" provider by prioritizing the model's specific configuration over the run-level provider via `effectiveProvider`.
* Defaults to `MockAiGenerationProvider` if the effective provider name is null or blank.
* Maps specific string constants (`mock`, `llamacpp-jni`, `openai-compatible`) to their respective class implementations.
* Throws `IllegalArgumentException` if an unrecognized provider string is provided.

#### Public API
* `AiGenerationProviderFactory()` -> `void` + Constructor.
* `effectiveProvider(runProvider, modelProvider)` -> `String` + Determines priority between run and model provider keys.
* `create(providerName, llamaConfig, promptSupport)` -> `AiGenerationProvider` + Instantiates the requested provider implementation.

#### Dependencies
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `net.ladenthin.srcmorph.provider.AiGenerationProvider`
* `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
* `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
* `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
* `lombok.ToString`
* `org.jspecity.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown when the resolved provider name does not match known constants.
* Handles `null` or blank provider strings by defaulting to `MockAiGenerationProvider`.

#### Concurrency
* Stateless factory logic; thread-safe for concurrent instantiation.
