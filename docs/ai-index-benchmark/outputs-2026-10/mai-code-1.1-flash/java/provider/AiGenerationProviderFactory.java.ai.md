### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:44:51Z
- G: 
- A: 0.0.0
- X: file
---
> Selects the correct AI generation backend for a run or model, defaulting to the mock provider when no provider is configured.

#### Purpose
- Chooses an `AiGenerationProvider` implementation from provider keys.
- Resolves provider selection precedence and normalizes blank or null values.

#### Type
- Kind: `class`
- Modifiers: `public`
- Annotations: `@ToString`
- Extends: none
- Implements: none
- Notable types: `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`

#### Input
- Constructor: `AiGenerationProviderFactory()`
- `effectiveProvider(runProvider, modelProvider)` reads `String` provider values, including nullable inputs.
- `create(providerName, llamaConfig, promptSupport)` consumes:
  - `providerName`: run-level provider key
  - `llamaConfig`: model config with `provider()`
  - `promptSupport`: prompt helper passed to provider implementations
- Internal field used: `compatibilityHelper` for blank detection

#### Output
- `effectiveProvider(...) -> @Nullable String`: returns the effective provider key.
- `create(...) -> AiGenerationProvider`: returns a concrete provider instance.
- Side effects: instantiates provider classes; throws `IllegalArgumentException` for unknown providers.

#### Core logic
- `effectiveProvider(...)` returns `modelProvider.trim()` when non-null and non-blank; otherwise returns `runProvider`.
- `create(...)` computes `effective = effectiveProvider(providerName, llamaConfig.provider())`.
- If `effective` is null or blank, `create(...)` returns `new MockAiGenerationProvider()`.
- `switch (effective)` matches provider constants:
  - `PROVIDER_MOCK` -> `new MockAiGenerationProvider()`
  - `PROVIDER_LLAMACPP_JNI` -> `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
  - `PROVIDER_OPENAI_COMPATIBLE` -> `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`
- Default branch throws `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory() -> AiGenerationProviderFactory` constructor
- `PROVIDER_MOCK -> String` static constant
- `PROVIDER_LLAMACPP_JNI -> String` static constant
- `PROVIDER_OPENAI_COMPATIBLE -> String` static constant
- `effectiveProvider(String, String) -> @Nullable String` resolves effective provider key
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` creates provider instance

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` when `effective` is not one of the supported provider constants.
- Null/blank provider values fall back to `MockAiGenerationProvider`; no exception for blank input.
- `effectiveProvider(...)` accepts nullable arguments and trims a non-empty model provider before returning.

#### Concurrency
- No explicit synchronization.
- Factory is effectively stateless apart from one `Java8CompatibilityHelper` instance field.
- No thread-safety guarantees stated in this file.
