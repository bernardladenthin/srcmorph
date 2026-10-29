### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:32Z
- G: 
- A: 0.0.0
- X: file
---
> Selects a model-generation provider from run and model configuration, defaulting to deterministic mock generation.

#### Purpose
- `AiGenerationProviderFactory` resolves provider precedence and creates the selected `AiGenerationProvider`.
- Supports mock, llama.cpp JNI, and OpenAI-compatible providers.

#### Type
- Public class; annotated `@ToString`.
- Holds a private final `Java8CompatibilityHelper`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` uses the run key, `llamaConfig.provider()`, configuration, and prompt support.
- Provider keys: `PROVIDER_MOCK` (`"mock"`), `PROVIDER_LLAMACPP_JNI` (`"llamacpp-jni"`), `PROVIDER_OPENAI_COMPATIBLE` (`"openai-compatible"`).

#### Output
- `effectiveProvider` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- `create` returns a newly constructed provider instance.

#### Core logic
- `effectiveProvider` gives the model’s nonblank provider precedence over the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective provider is null or blank.
- Dispatches recognized keys to mock, llama.cpp JNI, or OpenAI-compatible provider constructors.
- Passes `llamaConfig` and `promptSupport` to both non-mock providers.

#### Public API
- `AiGenerationProviderFactory()` -> constructor; initializes factory.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model/run precedence.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates selected provider.
- Public constants: `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unsupported nonblank provider key.
- Null or blank effective provider selects mock; a null model provider falls back to the run provider.

#### Concurrency
- No synchronization or mutable shared state; each `create` call constructs a new provider.
