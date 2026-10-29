### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:26Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the configured AI generation provider for a run, falling back to a model-specific provider or the deterministic mock provider.

#### Purpose
- `AiGenerationProviderFactory` resolves provider configuration and creates the matching `AiGenerationProvider`.
- Uses the model’s provider when configured; otherwise uses the run’s provider.

#### Type
- Public class; annotated with Lombok `@ToString`.
- No superclass or implemented interfaces declared.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable run and model provider keys.
- `create(providerName, llamaConfig, promptSupport)` consumes a provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.

#### Output
- Returns a newly created `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Defines provider keys `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, and `PROVIDER_OPENAI_COMPATIBLE`.

#### Core logic
- `effectiveProvider` prefers a non-null, non-blank model provider, trimming whitespace; otherwise returns the run provider.
- `create` defaults to the mock provider if the effective key is null or blank.
- Dispatches recognized keys to their corresponding provider implementations; passes model configuration and prompt support to non-mock providers.

#### Public API
- `AiGenerationProviderFactory()` → constructor; initializes factory.
- `effectiveProvider(runProvider, modelProvider)` → `@Nullable String`; selects effective provider key.
- `create(providerName, llamaConfig, promptSupport)` → `AiGenerationProvider`; constructs selected provider.
- `PROVIDER_MOCK` → `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` → `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` → `String`; OpenAI-compatible provider key.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unrecognized effective provider key.
- Blank or null effective provider keys select the mock provider.

#### Concurrency
- No synchronization or mutable shared state; the compatibility helper is held in a final instance field.
