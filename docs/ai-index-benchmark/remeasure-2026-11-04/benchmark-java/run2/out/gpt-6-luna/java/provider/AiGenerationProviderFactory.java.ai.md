### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:52Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Selects the configured AI text-generation backend for a run, allowing a model-level provider override and defaulting to deterministic mock generation.

#### Purpose
- Resolves the effective provider key from run and model configuration.
- Creates mock, llama.cpp JNI, or OpenAI-compatible generation providers.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(providerName, llamaConfig, promptSupport)` consumes the run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- Holds a `Java8CompatibilityHelper` instance for blank-key checks.

#### Output
- Returns a newly created `AiGenerationProvider`.
- `effectiveProvider` returns the trimmed model provider when nonblank; otherwise the run provider.

#### Core logic
- `effectiveProvider` prefers a non-null, nonblank model provider; trims the selected model key.
- `create` resolves the model-level override, then defaults null or blank keys to `MockAiGenerationProvider`.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Throws `IllegalArgumentException` for unrecognized provider keys.

#### Public API
- `AiGenerationProviderFactory()` -> constructor; initializes factory.
- `PROVIDER_MOCK` -> `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` -> `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` -> `String`; OpenAI-compatible provider key.
- `effectiveProvider(String, String)` -> `@Nullable String`; resolves model/run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`; instantiates selected provider.

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
- Throws `IllegalArgumentException` with the unsupported provider key when no provider matches.
- Null or blank effective provider selects the mock provider.
