### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:58:10Z
- G: 
- A: 0.0.0
- X: file
---
> Selects a configured AI generation provider, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves the effective provider key from run-level and model-level configuration.
- Creates the matching mock, llama.cpp JNI, or OpenAI-compatible provider.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable run and model provider keys.
- `create(providerName, llamaConfig, promptSupport)` consumes a provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Owns a `Java8CompatibilityHelper` instance.

#### Output
- `effectiveProvider` returns the model provider when nonblank, otherwise the run provider.
- `create` returns a newly instantiated `AiGenerationProvider`.

#### Core logic
- Trims a nonblank model provider key and gives it precedence over the run provider key.
- Defaults to `MockAiGenerationProvider` when the effective key is null or blank.
- Maps `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes the model configuration and prompt support to the JNI and OpenAI-compatible providers.
- Rejects unrecognized provider keys.

#### Public API
- `AiGenerationProviderFactory()` -> constructs factory
- `effectiveProvider(String, String)` -> resolves provider precedence
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> creates selected provider
- `PROVIDER_MOCK` -> mock provider key
- `PROVIDER_LLAMACPP_JNI` -> JNI provider key
- `PROVIDER_OPENAI_COMPATIBLE` -> OpenAI-compatible provider key

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for unsupported nonblank provider keys.
- Null or blank effective keys select the mock provider.
