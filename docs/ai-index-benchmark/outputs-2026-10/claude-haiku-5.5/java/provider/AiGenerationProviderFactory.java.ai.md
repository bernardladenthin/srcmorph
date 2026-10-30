### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:56Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates the AI generation provider (mock, llama.cpp JNI, or OpenAI-compatible) from a provider name.

#### Purpose
- Factory that maps a provider key to a concrete `AiGenerationProvider` instance.
- Resolves the effective provider per model, preferring the model's own `provider` over the run's `generationProvider`.

#### Type
- Class, public, not final, not abstract.
- Annotations: `@ToString` (Lombok).
- Implements no interfaces; extends none.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`.
- `effectiveProvider(String runProvider, String modelProvider)` (both `@Nullable`).
- Reads `llamaConfig.provider()`.
- Field `compatibilityHelper` (`Java8CompatibilityHelper`), used for `isBlank`.

#### Output
- Returns a new `AiGenerationProvider` on each `create` call; no caching or retained state.
- Possible instances: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- No I/O or mutated fields.

#### Core logic
- `effectiveProvider`: returns `runProvider` when `modelProvider` is null or blank; otherwise returns trimmed `modelProvider`.
- `create`: computes the effective key from `providerName` and `llamaConfig.provider()`.
- If the effective key is null or blank, returns a mock provider (default).
- Switch on the key:
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
  - Default → throws `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructor, no-op.
- `effectiveProvider(String runProvider, String modelProvider) -> String` → resolves model vs run provider key.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` → instantiates provider by key.
- Constants: `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE` (3 public static final String keys).

#### Dependencies
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for unrecognised provider names.
- Null or blank provider names fall back to mock; no NPE guard for a null `llamaConfig` (dereferenced via `llamaConfig.provider()`).

#### Concurrency
- No shared mutable state beyond the final `compatibilityHelper` field; stateless factory, safe for concurrent use.
