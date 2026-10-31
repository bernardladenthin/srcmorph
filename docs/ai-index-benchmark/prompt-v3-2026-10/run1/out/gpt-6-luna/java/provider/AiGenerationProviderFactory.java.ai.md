### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:57Z
- G: 
- A: 0.0.0
- X: file
---
> Selects a configured AI generation provider, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves a model-level provider preference over the run-level provider setting.
- Creates mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `AiGenerationProviderFactory` — public class.
- Annotated `@ToString`.
- Owns a `Java8CompatibilityHelper`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` consumes a run provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.

#### Output
- `effectiveProvider(...)` returns the model provider when nonblank; otherwise the run provider.
- `create(...)` returns a newly instantiated `AiGenerationProvider`.

#### Core logic
- `effectiveProvider` uses the model’s `provider` if non-null and nonblank, trimming it; otherwise returns the run provider unchanged.
- `create` resolves the effective provider using `llamaConfig.provider()`.
- Returns `MockAiGenerationProvider` for a null or blank effective key, or the `"mock"` key.
- Creates `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleAiGenerationProvider` for their respective keys, passing both configuration and prompt support.
- Throws `IllegalArgumentException` for an unrecognized key.

#### Public API
- `AiGenerationProviderFactory()` -> constructor; initializes factory.
- `effectiveProvider(String, String)` -> `@Nullable String`; resolves provider precedence.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`; creates selected provider.
- `PROVIDER_MOCK` -> `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` -> `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` -> `String`; OpenAI-compatible provider key.

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
- Throws `IllegalArgumentException` with the unsupported provider key for unknown providers.
- Null or blank effective provider keys fall back to mock.

#### Concurrency
- No explicit synchronization or mutable shared state; helper field is final.
