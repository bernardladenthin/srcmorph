### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:48Z
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
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – provider key, model config, prompt support.
- `effectiveProvider(String runProvider, String modelProvider)` – run and model provider keys.

#### Output
- `AiGenerationProvider` instance: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Throws `IllegalArgumentException` for unknown provider.

#### Core logic
- `effectiveProvider`: choose model provider if present; otherwise run provider.
- `create`: trim and check effective provider; default to mock if blank.
- Switch on effective provider:
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.
  - else throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider.

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` if provider name not recognized.

#### Concurrency
- No synchronization; instance fields are immutable after construction.
