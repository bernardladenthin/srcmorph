### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:14:17Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider by name.

#### Purpose
- Selects and instantiates an AI generation provider.
- Provides a factory method for creating AI generation providers based on configuration.

#### Type
- Class
- Public
- Uses Lombok's `@ToString` annotation

#### Input
- `providerName`: String (AI provider key)
- `llamaConfig`: LlamaCppJniConfig (model configuration)
- `promptSupport`: AiPromptSupport (prompt lookup support)

#### Output
- AiGenerationProvider instance

#### Core logic
- Determines effective provider based on input parameters
- Creates appropriate AiGenerationProvider instance based on the effective provider

#### Public API
- `AiGenerationProviderFactory()` -> Constructor
- `effectiveProvider(String, String) -> @Nullable String` -> Determines effective provider
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` -> Creates provider instance

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for unsupported provider names

#### Concurrency
- Thread-safe due to immutable state
