### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:13:01Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates an AI generation provider based on a provider key or model configuration.

#### Purpose
- Factory for `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
- Class `AiGenerationProviderFactory` (public, final by default).
- Annotated `@ToString`.
- Extends `Object`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
  - `providerName`: run‑level provider key.
  - `llamaConfig.provider()`: optional model‑level provider key.
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a concrete `AiGenerationProvider` instance:
  - `MockAiGenerationProvider`
  - `LlamaCppJniAiGenerationProvider`
  - `OpenAiCompatibleAiGenerationProvider`
- Throws `IllegalArgumentException` for unknown provider.

#### Core logic
- `effectiveProvider` selects model provider if present; otherwise run provider.
- `create`:
  1. Determine effective provider key via `effectiveProvider`.
  2. If key is null or blank → `MockAiGenerationProvider`.
  3. Switch on key:
     - `"mock"` → `MockAiGenerationProvider`.
     - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
     - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
     - default → throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory() -> void` – no‑op constructor.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider) -> String` – resolves provider key.
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
- `IllegalArgumentException` if provider key not in `{mock, llamacpp-jni, openai-compatible}`.

#### Concurrency
- Stateless after construction; thread‑safe.
