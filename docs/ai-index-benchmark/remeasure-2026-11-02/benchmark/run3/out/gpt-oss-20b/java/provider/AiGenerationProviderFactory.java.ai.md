### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:42Z
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
`public class AiGenerationProviderFactory` – final, no superclass, implements no interfaces.

#### Input
- `String providerName` – run‑level provider key.
- `LlamaCppJniConfig llamaConfig` – model configuration, may supply its own provider key.
- `AiPromptSupport promptSupport` – prompt lookup for providers that need it.

#### Output
- `AiGenerationProvider` instance matching the resolved provider key.

#### Core logic
- `effectiveProvider` selects model’s provider if present, otherwise run’s provider.
- `create` trims and checks the effective key:
  - Blank or null → `MockAiGenerationProvider`.
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.
  - Otherwise throws `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when provider key is unrecognised.

#### Concurrency
- Stateless; thread‑safe.
