### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:57Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider (mock, llama.cpp JNI, or OpenAI-compatible) by provider name from run or model configuration.

#### Purpose
- Factory mapping provider keys ("mock", "llamacpp-jni", "openai-compatible") to concrete AiGenerationProvider instances.
- Resolves the effective provider per model, preferring the model's own provider over the run-level generationProvider.

#### Type
- Class, public, not final, not abstract.
- Annotations: Lombok @ToString.
- Implements no interfaces; extends none (implicit Object).

#### Input
- create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport).
- effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider).
- Reads llamaConfig.provider() to determine the provider override.
- Field compatibilityHelper (Java8CompatibilityHelper), created inline.

#### Output
- Returns a new AiGenerationProvider instance on each create call (no caching).
- effectiveProvider returns a trimmed model provider, or the run provider when model value is null or blank.
- No mutated state; no I/O performed at factory level.

#### Core logic
- effectiveProvider: if modelProvider is null or trims to empty, return runProvider; otherwise return trimmed modelProvider.
- create: compute effective key from providerName and llamaConfig.provider().
- If effective is null or blank (via compatibilityHelper.isBlank), return MockAiGenerationProvider.
- Switch on effective:
  - "mock" → new MockAiGenerationProvider().
  - "llamacpp-jni" → new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport).
  - "openai-compatible" → new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport).
  - default → throw IllegalArgumentException("Unsupported AI provider: " + effective).

#### Public API
- AiGenerationProviderFactory() -> constructor; no-op.
- effectiveProvider(String runProvider, String modelProvider) -> String (static); resolves provider key.
- create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider; builds provider by key.
- Constants: PROVIDER_MOCK = "mock"; PROVIDER_LLAMACPP_JNI = "llamacpp-jni"; PROVIDER_OPENAI_COMPATIBLE = "openai-compatible" (public static final String, 3 total).

#### Dependencies
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- Referenced types: AiGenerationProvider, MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, LlamaCppJniConfig.

#### Exceptions / Errors
- IllegalArgumentException for unrecognised provider key in create.
- Null handling: null or blank providerName (after resolution) falls back to MockAiGenerationProvider.
- llamaConfig is dereferenced unconditionally (llamaConfig.provider()); a null llamaConfig causes NullPointerException.

#### Concurrency
- Stateless apart from an immutable-by-convention compatibilityHelper field; safe to share across threads.
- Each create call returns a fresh provider instance.
