### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:49Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the AI generation provider for a run or model, defaulting to deterministic mock generation when no provider is configured.

#### Purpose
- Resolves the effective provider key from run-level and model-level configuration.
- Creates the matching mock, llama.cpp JNI, or OpenAI-compatible provider.

#### Type
- `AiGenerationProviderFactory` — class; not final.
- Annotated with Lombok `@ToString`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` — nullable run and model provider keys.
- `create(providerName, llamaConfig, promptSupport)` — run provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.

#### Output
- `effectiveProvider(...)` returns the model provider when nonblank; otherwise the run provider.
- `create(...)` returns a new `AiGenerationProvider` instance.

#### Core logic
- `effectiveProvider` trims the model provider and prefers it when non-null and nonblank; otherwise returns the run provider.
- `create` uses the effective provider and defaults to `MockAiGenerationProvider` if it is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to providers that require them.

#### Public API
- `AiGenerationProviderFactory()` — constructs a factory.
- `PROVIDER_MOCK` — mock provider key.
- `PROVIDER_LLAMACPP_JNI` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` — OpenAI-compatible provider key.
- `effectiveProvider(String, String) -> @Nullable String` — resolves provider precedence.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — instantiates the selected provider.

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
- Null or blank effective provider selects the mock provider.

#### Concurrency
- No synchronization or mutable provider state; the factory holds a final `Java8CompatibilityHelper` instance.
