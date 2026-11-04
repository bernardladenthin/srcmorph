### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:10Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Selects a mock, llama.cpp, or OpenAI-compatible AI generation provider using run-level and model-level provider settings.

#### Purpose
- Creates the provider that generates AI responses for a configured model.
- Uses the model’s provider setting when present; otherwise uses the run’s setting, defaulting to mock.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(String runProvider, String modelProvider)` — nullable run and model provider keys.
- `compatibilityHelper` — internally constructed `Java8CompatibilityHelper`.

#### Output
- `create(...)` returns a newly instantiated `AiGenerationProvider`.
- Provider constants expose keys: `mock`, `llamacpp-jni`, and `openai-compatible`.

#### Core logic
- `effectiveProvider` returns the trimmed model provider if non-null and nonblank; otherwise returns the run provider.
- `create` resolves the effective key and returns `MockAiGenerationProvider` when it is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` — constructs factory.
- `PROVIDER_MOCK` — mock provider key.
- `PROVIDER_LLAMACPP_JNI` — llama.cpp provider key.
- `PROVIDER_OPENAI_COMPATIBLE` — OpenAI-compatible provider key.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model and run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates configured provider.

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
- Throws `IllegalArgumentException` for an unrecognized nonblank provider key.
- Null or blank effective provider selects the mock provider.
