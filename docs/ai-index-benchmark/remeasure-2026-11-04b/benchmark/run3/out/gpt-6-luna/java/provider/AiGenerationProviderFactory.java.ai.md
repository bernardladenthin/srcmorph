### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:41Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Selects the configured AI generation provider for a run, falling back to the model’s provider or a deterministic mock.

#### Purpose
- Resolves run-level and model-level provider keys and creates the matching `AiGenerationProvider`.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` — run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(String runProvider, String modelProvider)` — nullable run and model provider keys.
- Private field `compatibilityHelper` — `Java8CompatibilityHelper` used to check blank provider names.

#### Output
- `create(...)` returns a newly created `AiGenerationProvider`.
- `effectiveProvider(...)` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- No fields are mutated.

#### Core logic
- `effectiveProvider` prefers `modelProvider.trim()` when `modelProvider` is non-null and nonblank; otherwise returns `runProvider`.
- `create` resolves the key using `effectiveProvider(providerName, llamaConfig.provider())`.
- If the effective key is `null` or `compatibilityHelper.isBlank(effective)`, returns `MockAiGenerationProvider`.
- Switches on the effective key:
  - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`.
  - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
  - Otherwise throws `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` — constructs factory.
- `PROVIDER_MOCK: String` — mock provider key.
- `PROVIDER_LLAMACPP_JNI: String` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE: String` — OpenAI-compatible provider key.
- `effectiveProvider(String runProvider, String modelProvider) -> @Nullable String` — resolves provider precedence.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` — creates selected provider.
- Lombok-generated `toString() -> String` — factory string representation.

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
- `create(...)` throws `IllegalArgumentException` for unsupported nonblank provider keys.
- `effectiveProvider` accepts nullable inputs; returns `null` if neither specifies a provider.
- Blank provider keys fall back to the mock provider.
