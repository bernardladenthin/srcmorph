### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:29Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Factory for `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory` – class, public, no modifiers.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
  - `providerName`: run‑level provider key.
  - `llamaConfig`: model configuration, may supply its own provider key.
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a concrete `AiGenerationProvider` instance:
  - `MockAiGenerationProvider`
  - `LlamaCppJniAiGenerationProvider`
  - `OpenAiCompatibleAiGenerationProvider`

#### Core logic
- `effectiveProvider` selects model’s provider if present; otherwise run’s provider.
- `create` trims and checks the effective key:
  - Blank or null → `MockAiGenerationProvider`.
  - Matches `PROVIDER_MOCK` → `MockAiGenerationProvider`.
  - Matches `PROVIDER_LLAMACPP_JNI` → `LlamaCppJniAiGenerationProvider`.
  - Matches `PROVIDER_OPENAI_COMPATIBLE` → `OpenAiCompatibleAiGenerationProvider`.
  - Else throws `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → factory constructor.
- `static @Nullable String effectiveProvider(String, String)` → resolve provider key.
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → instantiate provider.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`

#### Exceptions / Errors
- `IllegalArgumentException` if provider key is unrecognised.

#### Concurrency
- No mutable shared state; thread‑safe by default.
