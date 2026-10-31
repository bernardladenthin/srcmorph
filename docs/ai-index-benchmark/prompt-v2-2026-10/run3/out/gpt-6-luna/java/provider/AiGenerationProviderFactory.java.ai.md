### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:20Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the configured AI generation provider for a model run, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves a model-specific provider key ahead of the run-level key.
- Creates mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `AiGenerationProviderFactory` — class; annotated `@ToString`.
- Public, non-final; no superclass or implemented interfaces.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` accepts the run provider name, model configuration, and prompt lookup support.
- Holds a `Java8CompatibilityHelper` instance.

#### Output
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- `create(...)` returns a new `AiGenerationProvider` implementation.

#### Core logic
- Uses the model configuration’s provider as the effective key when nonblank; otherwise uses the run-level provider.
- Defaults to `MockAiGenerationProvider` when the effective key is null or blank.
- Maps `mock`, `llamacpp-jni`, and `openai-compatible` to their corresponding provider implementations.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` -> constructor; initializes factory.
- `effectiveProvider(String, String)` -> `@Nullable String`; resolves provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`; creates configured provider.
- `PROVIDER_MOCK` -> `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` -> `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` -> `String`; OpenAI-compatible provider key.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `create(...)` throws `IllegalArgumentException` for an unsupported nonblank provider key.
- Null or blank effective provider keys select mock generation.

#### Concurrency
- No synchronization or mutable shared state; each `create(...)` call constructs a new provider.
