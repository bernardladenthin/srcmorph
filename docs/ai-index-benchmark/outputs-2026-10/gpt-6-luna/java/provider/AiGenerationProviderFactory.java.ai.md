### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:28Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the configured AI generation provider, defaulting to a deterministic mock when no provider is specified.

#### Purpose
- Resolves a run-level or model-level provider key and creates the corresponding `AiGenerationProvider`.
- Supports mock, llama.cpp JNI, and OpenAI-compatible providers.

#### Type
- Public class `AiGenerationProviderFactory`; annotated `@ToString`.
- Has a private final `Java8CompatibilityHelper` instance.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` accepts the run provider key, model configuration, and prompt lookup support.

#### Output
- `effectiveProvider(...)` returns the model provider when nonblank, otherwise the run provider.
- `create(...)` returns a new mock, llama.cpp JNI, or OpenAI-compatible provider.

#### Core logic
- `effectiveProvider` prefers a non-null, nonblank model provider after trimming; otherwise returns the run provider unchanged.
- `create` resolves the effective provider using `llamaConfig.provider()`.
- A null or blank effective key selects `MockAiGenerationProvider`.
- Provider keys `mock`, `llamacpp-jni`, and `openai-compatible` instantiate their corresponding provider; the latter two receive `llamaConfig` and `promptSupport`.
- Any other key causes `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructor; initializes factory.
- `PROVIDER_MOCK` → `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` → `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` → `String`; OpenAI-compatible provider key.
- `effectiveProvider(runProvider, modelProvider)` → `@Nullable String`; resolves provider precedence.
- `create(providerName, llamaConfig, promptSupport)` → `AiGenerationProvider`; creates selected provider.

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
- Throws `IllegalArgumentException` for an unsupported nonblank provider key.
- Null or blank provider keys fall back to the mock provider.

#### Concurrency
- No synchronization or mutable shared state; the compatibility helper field is final.
