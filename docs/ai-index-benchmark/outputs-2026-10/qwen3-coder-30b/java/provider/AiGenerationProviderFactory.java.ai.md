### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:14:39Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on named configurations for mock, llama.cpp JNI, or OpenAI-compatible services.

#### Purpose
- Selects and creates instances of AI generation providers.
- Resolves effective provider names from run-time and model configurations.

#### Type
class AiGenerationProviderFactory
- Implements no interfaces.
- Uses Lombok's @ToString annotation.
- Depends on Java8CompatibilityHelper for null/blank checks.

#### Input
- Constructor: no parameters.
- `effectiveProvider` method: `@Nullable String runProvider`, `@Nullable String modelProvider`.
- `create` method: `String providerName`, `LlamaCppJniConfig llamaConfig`, `AiPromptSupport promptSupport`.

#### Output
- `effectiveProvider` method: `@Nullable String` representing the resolved provider key.
- `create` method: `AiGenerationProvider` instance based on the provider key.

#### Core logic
- Determines effective provider name using `effectiveProvider` to prioritize model-specific provider over run-time provider.
- Instantiates provider implementations based on the resolved provider key using a switch statement.
- Defaults to `MockAiGenerationProvider` when no valid provider is specified.

#### Public API
- `effectiveProvider(runProvider, modelProvider) -> String` selects effective provider name.
- `create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider` creates provider instance.

#### Dependencies
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- net.ladenthin.srcmorph.provider.AiGenerationProvider
- net.ladenthin.srcmorph.provider.MockAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider
- net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniConfig
- org.jspecify.annotations.Nullable
- lombok.ToString

#### Exceptions / Errors
- Throws `IllegalArgumentException` for unrecognized provider names in `create` method.

#### Concurrency
- No explicit concurrency handling; assumes thread-safe usage of dependencies.
