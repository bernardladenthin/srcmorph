### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:38Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the AI generation provider for a run, honoring a model-specific provider override and defaulting to mock generation.

#### Purpose
- Resolves provider selection between run-level and model-level configuration.
- Creates mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- Owns a `Java8CompatibilityHelper` instance.

#### Input
- `effectiveProvider`: nullable run provider and nullable or blank model provider.
- `create`: provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Model provider is read from `llamaConfig.provider()`.

#### Output
- Returns a newly instantiated `AiGenerationProvider`.
- Blank or absent effective provider selects `MockAiGenerationProvider`.

#### Core logic
- `effectiveProvider` uses the trimmed model provider when nonblank; otherwise returns the run provider.
- `create` resolves the effective provider and defaults null or blank values to mock.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes the model configuration and prompt support to the llama.cpp JNI and OpenAI-compatible providers.
- Throws for an unrecognized nonblank provider key.

#### Public API
- `AiGenerationProviderFactory()` — constructs a factory.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model and run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates selected provider.
- `PROVIDER_MOCK` — provider key `mock`.
- `PROVIDER_LLAMACPP_JNI` — provider key `llamacpp-jni`.
- `PROVIDER_OPENAI_COMPATIBLE` — provider key `openai-compatible`.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `@ToString`, JSpecify `@Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` with the unsupported provider key for unrecognized values.
- `effectiveProvider` may return `null` when neither provider is specified.

#### Concurrency
- No synchronization or shared mutable state; the compatibility helper is final and each `create` call returns a new provider instance.
