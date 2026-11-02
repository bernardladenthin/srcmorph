### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:14Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Selects a mock, llama.cpp, or OpenAI-compatible AI generation provider using model-level or run-level configuration.

#### Purpose
- Creates the provider used to generate AI responses for a configured model.
- Prefers the model’s provider key when present; otherwise uses the run’s provider key.

#### Type
- `AiGenerationProviderFactory` — public class; annotated `@ToString`.

#### Input
- `create(providerName, llamaConfig, promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` — nullable run and model provider keys.
- Holds a `Java8CompatibilityHelper` instance for blank-string checks.

#### Output
- `create(...)` returns a new `AiGenerationProvider` instance.
- `effectiveProvider(...)` returns the trimmed model key when nonblank; otherwise the run key, which may be `null`.

#### Core logic
- `effectiveProvider` uses the model’s `provider` when it is non-null and nonblank after trimming; otherwise it falls back to the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective provider is null or blank.
- Provider keys select `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` — constructs the factory.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model or run provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates the selected provider.
- `PROVIDER_MOCK` — `"mock"` provider key.
- `PROVIDER_LLAMACPP_JNI` — `"llamacpp-jni"` provider key.
- `PROVIDER_OPENAI_COMPATIBLE` — `"openai-compatible"` provider key.

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
- `create` throws `IllegalArgumentException` for an unsupported nonblank provider key.
- Null or blank effective keys select the mock provider.

#### Concurrency
- No synchronization or mutable shared state; the compatibility helper is a private final instance field.
