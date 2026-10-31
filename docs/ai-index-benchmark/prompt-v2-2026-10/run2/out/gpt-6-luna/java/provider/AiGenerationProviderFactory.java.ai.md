### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:11Z
- G: 
- A: 0.0.0
- X: file
---
> Selects a mock, llama.cpp, or OpenAI-compatible AI generation provider using run-level and model-level configuration.

#### Purpose
- `AiGenerationProviderFactory` resolves the effective provider key and creates the matching provider.
- Defaults to the deterministic mock provider when the effective key is blank or absent.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- No superclass or implemented interfaces declared.

#### Input
- `create(providerName, llamaConfig, promptSupport)` consumes a run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` accepts nullable run and model provider keys.
- Holds a `Java8CompatibilityHelper` instance.

#### Output
- `create(...)` returns a newly created `AiGenerationProvider`.
- `effectiveProvider(...)` returns the model provider when nonblank; otherwise returns the run provider.

#### Core logic
- `effectiveProvider` trims and prefers a nonblank model provider; falls back to the run provider.
- `create` returns `MockAiGenerationProvider` if the effective provider is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`.
- Dispatches `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, passing model configuration and prompt support.
- Dispatches `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`, passing model configuration and prompt support.
- Throws for unrecognized provider keys.

#### Public API
- `AiGenerationProviderFactory()` — creates a factory.
- `effectiveProvider(String, String) -> String` — resolves run and model provider keys.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates the selected provider.
- `PROVIDER_MOCK` — provider key `mock`.
- `PROVIDER_LLAMACPP_JNI` — provider key `llamacpp-jni`.
- `PROVIDER_OPENAI_COMPATIBLE` — provider key `openai-compatible`.

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- `@Nullable`
- Lombok `@ToString`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unsupported provider key.
- Nullable run and model provider keys are supported by `effectiveProvider`.

#### Concurrency
- No synchronization or explicit concurrency controls.
- The factory’s `Java8CompatibilityHelper` field is `final`.
