### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:20Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Selects the configured AI generation backend for a model, defaulting to deterministic mock generation when no provider is specified.

#### Purpose
- Resolves a model-specific provider key over the run-level key and creates the matching AI generation provider.
- Defaults blank or absent provider configuration to `MockAiGenerationProvider`.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- `@ToString` generates a `toString()` method.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider)` — nullable run and model provider keys.
- Private final field `compatibilityHelper`, initialized as `new Java8CompatibilityHelper()`.

#### Output
- `create(...)` returns a newly created `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- `create(...)` constructs a mock, llama.cpp JNI, or OpenAI-compatible provider.

#### Core logic
- `effectiveProvider` checks whether `modelProvider` is `null` or `modelProvider.trim().isEmpty()`:
  - If so, returns `runProvider`.
  - Otherwise, returns `modelProvider.trim()`.
- `create` calls `effectiveProvider(providerName, llamaConfig.provider())`.
- If the effective key is `null` or `compatibilityHelper.isBlank(effective)`, returns `new MockAiGenerationProvider()`.
- Switches on `effective`:
  - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`.
  - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
  - Otherwise throws `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` → constructor; creates factory.
- `PROVIDER_MOCK: String` — mock provider key.
- `PROVIDER_LLAMACPP_JNI: String` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE: String` — OpenAI-compatible provider key.
- `effectiveProvider(String runProvider, String modelProvider) -> @Nullable String` — resolves model/run provider key.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` — creates configured provider.

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
- `create` throws `IllegalArgumentException` for an unsupported nonblank provider key.
- `effectiveProvider` accepts nullable keys; blank model keys fall back to the run key.
