### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:37:53Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an {@link AiGenerationProvider} implementation by name.

#### Purpose
- To select and instantiate an AI generation provider based on the given provider name.

#### Type
- Class (`public final class AiGenerationProviderFactory`)
- Modifiers: `final`

#### Input
- Constructor parameters: None
- Method parameters:
  - `providerName`: the run's provider key
  - `llamaConfig`: the model's configuration
  - `promptSupport`: prompt lookup support passed to providers

#### Output
- Return type: `AiGenerationProvider`
- Side effects: Creates and returns an instance of an AI generation provider based on the provider name.

#### Core logic
- Determines the effective provider name using `effectiveProvider` method.
- If the effective provider name is blank or null, returns a `MockAiGenerationProvider`.
- Uses a switch statement to create and return the appropriate `AiGenerationProvider` based on the effective provider name.

#### Public API
- `effectiveProvider(String runProvider, String modelProvider) -> String`: Returns the effective provider name based on the given run and model provider names.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`: Creates and returns an `AiGenerationProvider` instance based on the provider name.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` if the provider name is not recognized.

#### Concurrency
- The class and its methods are not explicitly thread-safe.
