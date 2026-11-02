### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:26:11Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on a provider key, supporting mock, llama.cpp JNI, and OpenAI‑compatible backends.

#### Purpose
- Factory for `AiGenerationProvider` instances.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory` – class, public, no modifiers.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
  - `providerName`: run‑level provider key (may be null/blank).
  - `llamaConfig`: model configuration, provides `provider()` string.
  - `promptSupport`: lookup support for prompts.
- `effectiveProvider(String runProvider, String modelProvider)` – static helper.

#### Output
- `create`: returns a concrete `AiGenerationProvider` (Mock, LlamaCppJni, or OpenAiCompatible).
- `effectiveProvider`: returns resolved provider key or null.

#### Core logic
- `effectiveProvider` trims `modelProvider`; if empty, returns `runProvider`.
- `create`:
  1. Resolve effective provider via `effectiveProvider`.
  2. If null or blank, instantiate `MockAiGenerationProvider`.
  3. Switch on effective key:
     - `mock` → `MockAiGenerationProvider`.
     - `llamacpp-jni` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
     - `openai-compatible` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
     - default → throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` – no‑op constructor.
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key.
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method.

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
- `create` throws `IllegalArgumentException` for unsupported provider keys.

#### Concurrency
- Stateless factory; thread‑safe. The `Java8CompatibilityHelper` instance is immutable.
