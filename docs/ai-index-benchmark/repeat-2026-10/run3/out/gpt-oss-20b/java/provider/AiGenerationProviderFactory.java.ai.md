### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:53:11Z
- G: 
- A: 0.0.0
- X: file
---
> Factory for selecting and instantiating AI generation providers by name.

#### Purpose
- Instantiates concrete `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
- Class `AiGenerationProviderFactory` (public, final by default).
- Annotated with `@ToString`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
  - `providerName`: run‑level provider key.
  - `llamaConfig`: model configuration, may supply its own provider key.
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a new instance of `AiGenerationProvider`:
  - `MockAiGenerationProvider`
  - `LlamaCppJniAiGenerationProvider`
  - `OpenAiCompatibleAiGenerationProvider`

#### Core logic
- `effectiveProvider` selects the provider key: model’s key if present, otherwise run’s key.
- In `create`:
  1. Resolve effective provider key.
  2. If key is null or blank → return `MockAiGenerationProvider`.
  3. Switch on key:
     - `"mock"` → `MockAiGenerationProvider`.
     - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.
     - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.
  4. Throw `IllegalArgumentException` for unknown keys.

#### Public API
- `AiGenerationProviderFactory() -> void` – constructor.
- `static @Nullable String effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider) -> String` – resolves provider key.
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` – factory method.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` if provider key is unrecognised.

#### Concurrency
- No mutable shared state; thread‑safe.
