### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:30:29Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Creates an AI generation provider instance based on a provider name and model configuration.

#### Purpose
- Selects the appropriate AI generation provider implementation.
- Instantiates the provider using the effective provider key derived from configuration.

#### Type
- Class `AiGenerationProviderFactory`, `final` (inferred), `@ToString`.
- No `abstract`, `sealed`, or `interface` declaration.
- Imports: `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.

#### Input
- Constructor: no required parameters.
- Method `create`: `providerName` (`String`), `llamaConfig` (`LlamaCppJniConfig`), `promptSupport` (`AiPromptSupport`).
- Method `effectiveProvider`: `runProvider` (`String`, nullable), `modelProvider` (`String`, nullable).
- Field `compatibilityHelper` (`Java8CompatibilityHelper`).

#### Output
- Constructor: no output.
- Method `effectiveProvider`: returns `@Nullable String` (effective provider key).
- Method `create`: returns `AiGenerationProvider` instance (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`).
- Mutates no fields (stateless factory).

#### Core logic
- Computes `effective` via `effectiveProvider(providerName, llamaConfig.provider())`.
- If `effective` is null or blank, returns `MockAiGenerationProvider`.
- Switches on `effective`:
  - `PROVIDER_MOCK` → `MockAiGenerationProvider`.
  - `PROVIDER_LLAMACPP_JNI` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - `PROVIDER_OPENAI_COMPATIBLE` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
  - Default → throws `IllegalArgumentException` with message "Unsupported AI provider: " + effective.

#### Public API
- `AiGenerationProviderFactory()` -> `AiGenerationProviderFactory` (constructor, no-op).
- `effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider) -> @Nullable String` (returns effective provider key).
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` (creates provider instance).

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- `LlamaCppJniConfig` (referenced via `llamaConfig.provider()`)
- `AiGenerationProvider` (referenced via return type and new instances)

#### Exceptions / Errors
- Throws `IllegalArgumentException` if `effective` does not match any known provider constant.
- `effectiveProvider` returns `null` or empty string when neither `runProvider` nor `modelProvider` specifies a provider.
- `null` inputs are handled safely: `effectiveProvider` trims and checks for null/empty; `create` passes `null` to `effectiveProvider` safely.

#### Concurrency
- No concurrency features; all methods are stateless and thread-safe by default. No synchronization used.

> Note: `LlamaCppJniConfig`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider` are referenced but not defined in this file; only their usage is recorded.
