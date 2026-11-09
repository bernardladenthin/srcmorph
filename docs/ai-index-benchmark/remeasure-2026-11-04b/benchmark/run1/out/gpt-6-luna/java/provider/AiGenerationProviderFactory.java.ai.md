### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:47Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Selects a mock, llama.cpp, or OpenAI-compatible AI generation provider for a model run.

#### Purpose
- Resolves the run’s provider key against the model’s provider override and creates the selected `AiGenerationProvider`.
- Falls back to the deterministic mock provider when no provider key is specified.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` consumes the run provider key, model configuration, and prompt lookup support.
- `effectiveProvider(String runProvider, String modelProvider)` consumes nullable provider keys.
- Private `compatibilityHelper` is a `Java8CompatibilityHelper`.

#### Output
- `effectiveProvider` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- `create` returns a newly constructed `AiGenerationProvider`.
- `@ToString` generates a string representation.

#### Core logic
- `effectiveProvider` checks whether `modelProvider` is null or blank after `trim()`; if so, returns `runProvider`, otherwise returns `modelProvider.trim()`.
- `create` calls `effectiveProvider(providerName, llamaConfig.provider())`.
- If the effective key is null or `compatibilityHelper.isBlank(effective)`, returns `new MockAiGenerationProvider()`.
- Switches on `effective`:
  - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`.
  - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
  - Otherwise throws `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` → constructs the factory.
- `PROVIDER_MOCK: String` — mock provider key.
- `PROVIDER_LLAMACPP_JNI: String` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE: String` — OpenAI-compatible provider key.
- `effectiveProvider(String runProvider, String modelProvider) -> String` — resolves provider override.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` — creates the selected provider.
- `toString() -> String` — Lombok-generated representation.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `Nullable`, Lombok `ToString`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for unrecognized nonblank provider keys.
- `effectiveProvider` accepts nullable keys; blank model keys fall back to the run key.
