### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:18Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Selects the configured AI generation backend for a model run, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves a run-level or model-level provider key and creates the matching `AiGenerationProvider`.
- Supports mock, llama.cpp JNI, and OpenAI-compatible providers.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(...)` receives the run provider name, `LlamaCppJniConfig`, and `AiPromptSupport`.
- `effectiveProvider(...)` receives nullable run and model provider keys.
- Holds a private `Java8CompatibilityHelper` instance.

#### Output
- `create(...)` returns a newly created `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise the run provider.
- Provider keys: `PROVIDER_MOCK = "mock"`, `PROVIDER_LLAMACPP_JNI = "llamacpp-jni"`, `PROVIDER_OPENAI_COMPATIBLE = "openai-compatible"`.

#### Core logic
- `effectiveProvider` prefers a non-null, nonblank model provider, trimming it; otherwise uses the run provider.
- `create` defaults to `MockAiGenerationProvider` when the effective provider is null or blank.
- Dispatches recognized keys to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`; passes configuration and prompt support to the latter two.
- Rejects unrecognized provider keys.

#### Public API
- `AiGenerationProviderFactory()` -> constructor
- `PROVIDER_MOCK: String` -> mock provider key
- `PROVIDER_LLAMACPP_JNI: String` -> llama.cpp JNI provider key
- `PROVIDER_OPENAI_COMPATIBLE: String` -> OpenAI-compatible provider key
- `effectiveProvider(String, String) -> @Nullable String` — resolve model or run provider
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — instantiate selected provider
- `toString() -> String` — Lombok-generated representation

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
- `create` throws `IllegalArgumentException` for an unsupported provider key, with message `Unsupported AI provider: <key>`.
- Null or blank effective provider selects the mock provider.
