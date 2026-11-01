### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:53Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and creates the configured AI generation provider, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Chooses an AI generation provider using the run-level provider key and optional model-level override.
- Creates mock, llama.cpp JNI, or OpenAI-compatible providers.

#### Type
- `AiGenerationProviderFactory` — public class.
- `@ToString` Lombok annotation.
- Holds a private final `Java8CompatibilityHelper`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` — nullable run and model provider keys.
- `create(providerName, llamaConfig, promptSupport)` — provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.

#### Output
- `effectiveProvider(...)` returns the model provider when nonblank; otherwise returns the run provider.
- `create(...)` returns a newly instantiated `AiGenerationProvider`.
- Defines provider key constants: `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.

#### Core logic
- Trims the model provider key and prefers it over the run provider when non-null and nonblank.
- Creates `MockAiGenerationProvider` when the effective key is null or blank, or equals `mock`.
- Creates `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.
- Creates `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.
- Rejects unrecognized provider keys.

#### Public API
- `AiGenerationProviderFactory()` — constructs the factory.
- `PROVIDER_MOCK` — mock provider key.
- `PROVIDER_LLAMACPP_JNI` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` — OpenAI-compatible provider key.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model and run keys.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates selected provider.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- `create(...)` throws `IllegalArgumentException` for unsupported nonblank provider keys.
- Null or blank effective provider keys select mock generation.
