### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:12:08Z
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
- Contains constants, a helper field, and three public methods.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
  - `providerName`: run‑level provider key (may be null/blank).
  - `llamaConfig`: model configuration, provides `provider()` string.
  - `promptSupport`: lookup support for prompts.
- `effectiveProvider(String runProvider, String modelProvider)`
  - `runProvider`: provider key from run.
  - `modelProvider`: provider key from model definition.

#### Output
- `create` returns a concrete `AiGenerationProvider` instance:
  - `MockAiGenerationProvider` for mock or blank keys.
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.
- `effectiveProvider` returns the resolved provider key or null.

#### Core logic
- `effectiveProvider` selects `modelProvider` if present and non‑blank; otherwise returns `runProvider`.
- `create`:
  1. Resolve effective provider key via `effectiveProvider`.
  2. If key is null or blank, instantiate `MockAiGenerationProvider`.
  3. Switch on key:
     - `mock`: `MockAiGenerationProvider`.
     - `llamacpp-jni`: `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
     - `openai-compatible`: `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
     - Default: throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory() -> void` – default constructor.
- `effectiveProvider(String, String) -> @Nullable String` – resolves provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` – factory method.

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for unsupported provider keys.

#### Concurrency
- No synchronization; instance is stateless except for a single `Java8CompatibilityHelper` helper. Thread‑safe by design.
