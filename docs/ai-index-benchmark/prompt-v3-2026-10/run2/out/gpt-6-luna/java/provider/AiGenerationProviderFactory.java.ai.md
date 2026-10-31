### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:29Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and creates mock, llama.cpp JNI, or OpenAI-compatible AI generation providers from run and model configuration.

#### Purpose
- Resolves the effective provider key, preferring a model’s configured provider over the run’s provider.
- Creates the matching `AiGenerationProvider`, defaulting to the mock provider when no key is specified.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated with Lombok `@ToString`.

#### Input
- `create(providerName, llamaConfig, promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(runProvider, modelProvider)` — nullable run and model provider keys.
- Owns a `Java8CompatibilityHelper` instance for blank-string checks.

#### Output
- `create(...)` returns a newly instantiated `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model key when nonblank; otherwise returns the run key.

#### Core logic
- `effectiveProvider` uses the model’s trimmed provider key when non-null and nonblank; otherwise uses the run provider key.
- `create` returns `MockAiGenerationProvider` when the effective key is null or blank.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the JNI and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` → constructor; creates factory.
- `PROVIDER_MOCK` → `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` → `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` → `String`; OpenAI-compatible provider key.
- `effectiveProvider(String, String)` → `@Nullable String`; resolves provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` → `AiGenerationProvider`; creates selected provider.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for an unsupported nonblank provider key.
- `effectiveProvider` permits null keys; `create` defaults to mock when the effective key is null or blank.
