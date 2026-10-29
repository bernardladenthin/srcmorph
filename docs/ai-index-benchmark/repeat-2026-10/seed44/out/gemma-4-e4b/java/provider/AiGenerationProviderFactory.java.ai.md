### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:03:49Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates a specific AI generation provider implementation based on a provided provider name and configuration.

#### Purpose
* Factory for creating AiGenerationProvider instances.
* Determines the effective provider key from run and model configurations.

#### Type
class AiGenerationProviderFactory

#### Input
* Constructor: None.
* Method `effectiveProvider`: runProvider (String), modelProvider (String).
* Method `create`: providerName (String), llamaConfig (LlamaCppJniConfig), promptSupport (AiPromptSupport).
* Field `compatibilityHelper`: Java8CompatibilityHelper instance.

#### Output
* Method `effectiveProvider`: String (nullable).
* Method `create`: AiGenerationProvider instance.

#### Core logic
* `effectiveProvider` prioritizes modelProvider over runProvider if modelProvider is non-empty.
* `create` determines the effective provider key using `effectiveProvider`.
* `create` returns `MockAiGenerationProvider` if the effective provider is null or blank.
* `create` uses a switch statement on the effective provider key to instantiate the correct provider: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
* `create` throws `IllegalArgumentException` for unrecognized provider names.

#### Public API
effectiveProvider(runProvider, modelProvider) -> String: Determines the effective provider key.
create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider: Creates the specified AI provider instance.

#### Dependencies
lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable, MockAiGenerationProvider, LlamaCppJniConfig, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.

#### Exceptions / Errors
IllegalArgumentException: Thrown if the provider name is not recognized in `create`.

#### Concurrency
No explicit concurrency mechanisms present; relies on standard Java object instantiation.
