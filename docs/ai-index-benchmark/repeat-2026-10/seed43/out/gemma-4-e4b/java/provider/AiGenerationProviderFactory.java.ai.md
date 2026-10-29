### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:56:55Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates the appropriate AI generation provider implementation based on configuration keys.

#### Purpose
* Factory for creating AiGenerationProvider instances.
* Determines the effective AI provider key from run and model configurations.

#### Type
class AiGenerationProviderFactory; @ToString

#### Input
* Constructor: None.
* Method `effectiveProvider`: runProvider (String, nullable), modelProvider (String, nullable).
* Method `create`: providerName (String), llamaConfig (LlamaCppJniConfig), promptSupport (AiPromptSupport).
* Field `compatibilityHelper`: Java8CompatibilityHelper instance.

#### Output
* Method `effectiveProvider`: String (nullable).
* Method `create`: AiGenerationProvider instance.

#### Core logic
* `effectiveProvider` prioritizes modelProvider over runProvider if modelProvider is non-empty.
* `create` uses `effectiveProvider` to determine the provider key.
* `create` instantiates MockAiGenerationProvider if the effective provider is null or blank.
* `create` uses a switch statement to instantiate specific providers (Mock, LlamaCppJni, OpenAiCompatible) based on the effective key.
* `create` throws IllegalArgumentException for unrecognized provider names.

#### Public API
effectiveProvider(String, String) -> String (nullable) + Determines the effective AI provider key.
create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider + Creates provider instance based on name.

#### Dependencies
Lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable, AiGenerationProvider, MockAiGenerationProvider, LlamaCppJniConfig, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.

#### Exceptions / Errors
Throws IllegalArgumentException if providerName is unsupported.

#### Concurrency
No explicit concurrency mechanisms present; relies on standard Java object instantiation.
