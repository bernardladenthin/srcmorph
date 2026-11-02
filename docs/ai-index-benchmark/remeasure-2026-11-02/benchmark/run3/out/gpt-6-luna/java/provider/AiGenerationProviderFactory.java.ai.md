### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:51Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Selects a model-generation backend from run or model configuration, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolve the effective AI provider key, preferring a model-specific provider over the run-level choice.
- Create mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `public class AiGenerationProviderFactory` — class.
- Annotated `@ToString`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` accepts a run provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Holds a `Java8CompatibilityHelper` for blank-string checks.

#### Output
- `effectiveProvider` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- `create` returns a newly instantiated `AiGenerationProvider`.

#### Core logic
- `effectiveProvider` uses the model’s provider when present and nonblank; otherwise falls back to the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective key is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` — constructs factory.
- `effectiveProvider(String, String) -> @Nullable String` — resolves provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates configured provider.
- `PROVIDER_MOCK` — provider key `"mock"`.
- `PROVIDER_LLAMACPP_JNI` — provider key `"llamacpp-jni"`.
- `PROVIDER_OPENAI_COMPATIBLE` — provider key `"openai-compatible"`.

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
- `create` throws `IllegalArgumentException` for an unrecognized effective provider key.
- Null or blank effective provider keys select the mock provider.
