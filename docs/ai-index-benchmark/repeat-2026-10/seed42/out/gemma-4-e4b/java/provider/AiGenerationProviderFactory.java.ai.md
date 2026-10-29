### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:50:04Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates the appropriate AI generation provider implementation based on configured provider keys.

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
* `create` uses `effectiveProvider` to determine the provider key.
* `create` returns MockAiGenerationProvider if effective provider is null or blank.
* `create` uses a switch statement to instantiate MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, or OpenAiCompatibleAiGenerationProvider based on the effective key.
* `create` throws IllegalArgumentException for unrecognized provider keys.

#### Public API
effectiveProvider(runProvider, modelProvider) -> String (nullable) : Determines the effective provider key.
create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider : Creates the specified AI provider instance.

#### Dependencies
Lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable, AiGenerationProvider, MockAiGenerationProvider, LlamaCppJniConfig, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.

#### Exceptions / Errors
Throws IllegalArgumentException if providerName is not recognized in `create`.

#### Concurrency
No explicit concurrency mechanisms present; relies on standard Java object instantiation.
