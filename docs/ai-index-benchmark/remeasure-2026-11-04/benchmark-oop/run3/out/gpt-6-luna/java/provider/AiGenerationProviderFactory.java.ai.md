### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:16Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Selects a mock, llama.cpp JNI, or OpenAI-compatible AI generation provider using run-level and model-level configuration.

#### Purpose
- `AiGenerationProviderFactory` resolves the effective provider key and creates the matching `AiGenerationProvider`.
- Falls back to the deterministic mock provider when no provider key is configured.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- No explicit superclass or interfaces.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(String runProvider, String modelProvider)` — nullable run and model provider keys.
- Private `compatibilityHelper` — `Java8CompatibilityHelper` used to check blank provider names.

#### Output
- `create(...)` returns a newly created `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise the run provider.
- Creates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.

#### Core logic
- `effectiveProvider` prefers a non-null, nonblank model provider, trimming it; otherwise returns the run provider.
- `create` resolves the effective key and uses the mock provider if it is null or blank.
- Dispatches `mock`, `llamacpp-jni`, and `openai-compatible` keys to their corresponding provider implementations.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` — creates a factory.
- `PROVIDER_MOCK: String` — mock provider key.
- `PROVIDER_LLAMACPP_JNI: String` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE: String` — OpenAI-compatible provider key.
- `effectiveProvider(String runProvider, String modelProvider) -> @Nullable String` — resolves the provider key.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` — creates the configured provider.

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
- Throws `IllegalArgumentException` with `Unsupported AI provider: <key>` for unrecognized nonblank provider keys.
- `effectiveProvider` accepts nullable run and model provider keys.

#### Concurrency
- No synchronization or concurrency behavior is defined in this file.
