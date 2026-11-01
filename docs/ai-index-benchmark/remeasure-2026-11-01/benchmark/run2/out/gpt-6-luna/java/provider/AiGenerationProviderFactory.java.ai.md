### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:29Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the AI generation provider for a run, defaulting to deterministic mock generation when no provider is configured.

#### Purpose
- Resolves a run’s provider key against the model configuration.
- Creates the matching AI generation provider.

#### Type
- `AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(providerName, llamaConfig, promptSupport)` takes the run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.

#### Output
- Returns a newly created `AiGenerationProvider` implementation.
- Provider keys: `mock`, `llamacpp-jni`, and `openai-compatible`.

#### Core logic
- `effectiveProvider` uses the model’s provider when non-null and non-blank, trimming it; otherwise uses the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective key is null or blank.
- Selects mock, llama.cpp JNI, or OpenAI-compatible provider by key; passes model configuration and prompt support to the latter two.
- Throws `IllegalArgumentException` for an unsupported key.

#### Public API
- `AiGenerationProviderFactory()` -> constructor
- `effectiveProvider(String, String)` -> `@Nullable String` — resolve provider key
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider` — instantiate selected provider
- `PROVIDER_MOCK` -> `String` — mock provider key
- `PROVIDER_LLAMACPP_JNI` -> `String` — llama.cpp JNI provider key
- `PROVIDER_OPENAI_COMPATIBLE` -> `String` — OpenAI-compatible provider key

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` when the provider key is unrecognized.
- Null or blank effective provider keys select the mock provider.
