### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:36Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Selects and creates an `AiGenerationProvider` implementation.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory` – class, public, no modifiers.

#### Input
- `String providerName` – run‑level provider key.
- `LlamaCppJniConfig llamaConfig` – model configuration, may supply its own provider key.
- `AiPromptSupport promptSupport` – prompt lookup for providers that need it.

#### Output
- Returns a concrete `AiGenerationProvider` instance.
- Throws `IllegalArgumentException` for unknown provider keys.

#### Core logic
- `effectiveProvider` chooses model’s provider if present; otherwise uses run provider.
- `create` trims and checks the effective key:
  - Blank or null → `MockAiGenerationProvider`.
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.
  - Else → `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` → resolves provider key.
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` → creates provider.

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` when provider key is unrecognised.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
