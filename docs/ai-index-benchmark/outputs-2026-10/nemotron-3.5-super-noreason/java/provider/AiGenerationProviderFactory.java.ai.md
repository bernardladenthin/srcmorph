### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:01:50Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by provider key, defaulting to a mock provider when no provider is configured.

#### Purpose
- Factory that resolves the effective AI provider from run-level and model-level configuration.
- Instantiates `AiGenerationProvider` implementations for mock, llama.cpp JNI, and OpenAI-compatible backends.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok)
- No superclass or interfaces

#### Input
- Constructor: no parameters
- `effectiveProvider(String runProvider, String modelProvider)`: run provider key (nullable), model provider key (nullable or empty)
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`: provider name, model configuration, prompt lookup support
- Field: `Java8CompatibilityHelper compatibilityHelper` (instantiated internally)

#### Output
- `effectiveProvider`: returns the effective provider key string, or `null`/empty when neither names one
- `create`: returns a newly-created `AiGenerationProvider` instance
- Side effect: throws `IllegalArgumentException` for unrecognised provider names

#### Core logic
- `effectiveProvider` prefers the model's own `provider` when non-blank, otherwise falls back to the run's `generationProvider`
- `create` resolves the effective provider via `effectiveProvider`
- When the effective provider is `null` or blank, `create` returns a `MockAiGenerationProvider`
- `PROVIDER_MOCK` constructs `MockAiGenerationProvider`
- `PROVIDER_LLAMACPP_JNI` constructs `LlamaCppJniAiGenerationProvider` with `llamaConfig` and `promptSupport`
- `PROVIDER_OPENAI_COMPATIBLE` constructs `OpenAiCompatibleAiGenerationProvider` with `llamaConfig` and `promptSupport`
- Any other value throws `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` -> no-op constructor
- `effectiveProvider(String, String) -> String` resolves effective provider key
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` instantiates provider by name

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `create` when provider name is not recognised
- Nullable parameters handled via `@Nullable` and null checks
- Blank strings treated as absent via `compatibilityHelper.isBlank`

#### Concurrency
- Stateless factory; no synchronization or shared mutable state
- Thread-safe for concurrent use
