### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:49:39Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Factory for `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
- Class `AiGenerationProviderFactory` (public, final by default).
- Annotated `@ToString`.
- Contains private final `Java8CompatibilityHelper compatibilityHelper`.

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
- `effectiveProvider` selects model provider if present, else run provider.
- `create`:
  1. Resolve effective provider via `effectiveProvider`.
  2. If blank or null → `MockAiGenerationProvider`.
  3. Switch on effective provider:
     - `"mock"` → `MockAiGenerationProvider`.
     - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
     - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
     - default → `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory() -> void` – no‑op constructor.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider) -> String` – selects provider key.
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
- `IllegalArgumentException` if provider name not recognized.

#### Concurrency
- Stateless factory; thread‑safe.
