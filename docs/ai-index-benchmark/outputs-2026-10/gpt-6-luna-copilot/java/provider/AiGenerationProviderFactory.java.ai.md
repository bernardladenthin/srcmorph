### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:35:06Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the AI generation provider for a run, honoring model-level provider overrides and defaulting to a mock provider.

#### Purpose
- Creates an AI generation provider from run and model configuration.
- Resolves the effective provider key.

#### Type
- Public class `AiGenerationProviderFactory`.
- Annotated `@ToString`.

#### Input
- `create` receives a provider name, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Provider selection reads `llamaConfig.provider()`.

#### Output
- Returns a newly created `AiGenerationProvider`; does not mutate state.

#### Core logic
- `effectiveProvider` uses the trimmed model provider when nonblank; otherwise uses the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective provider is null or blank.
- Selects mock, llama.cpp JNI, or OpenAI-compatible provider by key.

#### Public API
- `AiGenerationProviderFactory()` — constructs factory.
- `PROVIDER_MOCK` — mock provider key.
- `PROVIDER_LLAMACPP_JNI` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` — OpenAI-compatible provider key.
- `effectiveProvider(runProvider, modelProvider) -> String` — resolves provider key.
- `create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider` — creates selected provider.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for an unsupported nonblank provider key.

#### Concurrency
- No synchronization or mutable shared state shown.
