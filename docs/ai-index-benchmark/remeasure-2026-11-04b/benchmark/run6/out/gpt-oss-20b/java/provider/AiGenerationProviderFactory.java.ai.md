### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:50Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Creates an AI generation provider instance based on a provider key or model configuration.

#### Purpose
- Instantiates an `AiGenerationProvider` implementation.
- Resolves provider key from run or model configuration.

#### Type
- `public class AiGenerationProviderFactory` (class, public, final by default, annotated `@ToString`).

#### Input
- Constructor: no parameters.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`:
  - `providerName`: run‑level provider key, may be null or blank.
  - `llamaConfig`: model configuration, provides `provider()` method.
  - `promptSupport`: lookup support for prompts.
- `effectiveProvider(String runProvider, String modelProvider)`:
  - `runProvider`: run‑level provider key, may be null.
  - `modelProvider`: model‑level provider key, may be null or empty.

#### Output
- `create`: returns an `AiGenerationProvider` instance:
  - `MockAiGenerationProvider` for mock or blank keys.
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.
- `effectiveProvider`: returns the resolved provider key or null.

#### Core logic
- `effectiveProvider`:
  - If `modelProvider` is null or empty after trim, return `runProvider`; else return trimmed `modelProvider`.
- `create`:
  - Compute `effective` via `effectiveProvider`.
  - If `effective` is null or blank (`compatibilityHelper.isBlank`), return `new MockAiGenerationProvider()`.
  - Switch on `effective`:
    - `PROVIDER_MOCK`: `new MockAiGenerationProvider()`.
    - `PROVIDER_LLAMACPP_JNI`: `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
    - `PROVIDER_OPENAI_COMPATIBLE`: `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
    - default: throw `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` -> no-op constructor.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` -> resolve provider key.
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` -> instantiate provider.

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
- `IllegalArgumentException` thrown for unsupported provider keys in `create`.

#### Concurrency
- No explicit concurrency constructs; instance fields are immutable after construction.
