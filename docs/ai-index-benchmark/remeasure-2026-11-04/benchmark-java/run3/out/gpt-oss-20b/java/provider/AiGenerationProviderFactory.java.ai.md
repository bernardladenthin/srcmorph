### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:10Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Selects and creates an `AiGenerationProvider` implementation.

#### Type
`public class AiGenerationProviderFactory` – class, final, no generics, annotated `@ToString`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – provider key, model config, prompt lookup.
- `effectiveProvider(String runProvider, String modelProvider)` – provider keys.

#### Output
- `create` returns a concrete `AiGenerationProvider` instance.
- `effectiveProvider` returns the resolved provider key.

#### Core logic
- Resolve provider key via `effectiveProvider`.
- If key blank or null, return `MockAiGenerationProvider`.
- Switch on key:
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.
  - else throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` – construct factory.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolve provider key.
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – instantiate provider.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` when provider name unsupported.

#### Concurrency
- No explicit synchronization; instance is stateless except for a helper.
