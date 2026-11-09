### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:58Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Selects mock, llama.cpp JNI, or OpenAI-compatible AI generation for a model, preferring the model’s provider over the run-level setting.

#### Purpose
- Resolves the effective provider key from model and run configuration.
- Creates the matching `AiGenerationProvider`, defaulting to the deterministic mock provider when no key is set.

#### Type
- `public class AiGenerationProviderFactory` — class.
- Annotated with Lombok `@ToString`.

#### Input
- `create(providerName, llamaConfig, promptSupport)` consumes the run-level provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` consumes nullable provider keys.
- Holds a `Java8CompatibilityHelper` for blank-string checks.

#### Output
- `create(...)` returns a newly instantiated `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise returns the run provider.

#### Core logic
- `effectiveProvider` prefers a non-null, nonblank model provider; trims it before returning.
- `create` uses the effective provider, defaulting to `MockAiGenerationProvider` when null or blank.
- Maps `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.
- Throws `IllegalArgumentException` for an unrecognized provider key.

#### Public API
- `AiGenerationProviderFactory()` — constructs factory.
- `PROVIDER_MOCK` — mock provider key; value `"mock"`.
- `PROVIDER_LLAMACPP_JNI` — llama.cpp JNI provider key; value `"llamacpp-jni"`.
- `PROVIDER_OPENAI_COMPATIBLE` — OpenAI-compatible provider key; value `"openai-compatible"`.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model or run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates configured provider.

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- `create(...)` throws `IllegalArgumentException` with the unsupported provider key when no provider mapping exists.
