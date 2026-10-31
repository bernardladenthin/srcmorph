### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:35Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the configured AI generation provider for a run, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves a model-level provider override against the run-level provider.
- Creates mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `AiGenerationProviderFactory` — class; public; annotated `@ToString`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` — nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` — run provider key, model configuration, and prompt lookup support.

#### Output
- Returns a newly created `AiGenerationProvider`.
- Exposes provider keys `mock`, `llamacpp-jni`, and `openai-compatible`.

#### Core logic
- `effectiveProvider` prefers a non-null, non-blank model provider, trimming it; otherwise uses the run provider unchanged.
- `create` resolves the effective key and returns `MockAiGenerationProvider` when it is null or blank.
- Dispatches recognized keys to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Throws for an unrecognized non-blank provider key.

#### Public API
- `AiGenerationProviderFactory()` -> constructor; initializes factory.
- `effectiveProvider(String, String)` -> `@Nullable String`; resolves provider precedence.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`; instantiates selected provider.
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
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unsupported provider key.
- Null provider keys are supported; a null or blank effective key selects the mock provider.
