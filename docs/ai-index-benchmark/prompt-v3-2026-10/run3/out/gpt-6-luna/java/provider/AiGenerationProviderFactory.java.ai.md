### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:55Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the AI generation backend for a run, preferring a model-specific provider and defaulting to a deterministic mock when none is configured.

#### Purpose
- `AiGenerationProviderFactory` resolves provider configuration and creates the matching `AiGenerationProvider`.
- Supports mock, llama.cpp JNI, and OpenAI-compatible backends.

#### Type
- `AiGenerationProviderFactory` — public class; annotated `@ToString`.
- No superclass or implemented interfaces.

#### Input
- `effectiveProvider(runProvider, modelProvider)` consumes nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` consumes the run provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Holds a `Java8CompatibilityHelper` instance for blank-string checks.

#### Output
- Returns a provider key from `effectiveProvider`, or `null` if neither key names one.
- Returns a newly created `AiGenerationProvider` from `create`.

#### Core logic
- `effectiveProvider` uses the model provider when it is non-null and nonblank, trimming it; otherwise uses the run provider.
- `create` resolves the effective provider and returns `MockAiGenerationProvider` if it is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.

#### Public API
- `PROVIDER_MOCK` (`String`) — mock provider key.
- `PROVIDER_LLAMACPP_JNI` (`String`) — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` (`String`) — OpenAI-compatible provider key.
- `AiGenerationProviderFactory()` — constructs factory.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model/run provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates configured provider.

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- `@ToString`
- `@Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unrecognized nonblank provider key.
- Null or blank effective provider selects the mock backend.

#### Concurrency
- No synchronization or mutable provider state; each `create` call constructs a new provider instance.
