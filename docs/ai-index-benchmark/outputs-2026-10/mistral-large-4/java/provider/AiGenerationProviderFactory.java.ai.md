### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:38:34Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates the AI generation backend (mock, llama.cpp JNI, or OpenAI-compatible) selected by provider name for source code transformation runs.

#### Purpose
- Factory mapping provider name strings to concrete AiGenerationProvider implementations
- Resolves effective provider key from run-level and model-level configuration

#### Type
- Class, public, non-final, non-abstract
- Lombok @ToString annotation
- No superclass or interfaces

#### Input
- `create()`: providerName (String, run provider key), llamaConfig (LlamaCppJniConfig, model config), promptSupport (AiPromptSupport)
- `effectiveProvider()`: runProvider (@Nullable String), modelProvider (@Nullable String)
- Consumed field: compatibilityHelper (Java8CompatibilityHelper) for null-safe blank checks

#### Output
- `create()` returns AiGenerationProvider (MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, or OpenAiCompatibleAiGenerationProvider)
- `effectiveProvider()` returns @Nullable String (trimmed key or null)
- Throws IllegalArgumentException for unrecognized provider keys

#### Core logic
- `effectiveProvider()`: returns modelProvider.trim() when modelProvider is non-null and non-blank; otherwise returns runProvider
- `create()`: computes effective key via effectiveProvider(providerName, llamaConfig.provider()); returns MockAiGenerationProvider when key is null or blank; switch instantiates LlamaCppJniAiGenerationProvider or OpenAiCompatibleAiGenerationProvider with llamaConfig and promptSupport; default branch throws IllegalArgumentException

#### Public API
- `PROVIDER_MOCK -> String` (constant "mock" key)
- `PROVIDER_LLAMACPP_JNI -> String` (constant "llamacpp-jni" key)
- `PROVIDER_OPENAI_COMPATIBLE -> String` (constant "openai-compatible" key)
- `AiGenerationProviderFactory() -> void` (no-op constructor)
- `effectiveProvider(runProvider: String, modelProvider: String) -> @Nullable String` (model provider overrides run provider)
- `create(providerName: String, llamaConfig: LlamaCppJniConfig, promptSupport: AiPromptSupport) -> AiGenerationProvider` (instantiates provider by resolved key)

#### Dependencies
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- AiGenerationProvider
- MockAiGenerationProvider
- LlamaCppJniAiGenerationProvider
- OpenAiCompatibleAiGenerationProvider
- LlamaCppJniConfig

#### Exceptions / Errors
- `create()` throws IllegalArgumentException("Unsupported AI provider: " + effective) for unknown keys
- Null-safe handling: `effectiveProvider()` accepts null inputs; `create()` defaults to mock when effective key is null or blank via compatibilityHelper.isBlank()

#### Concurrency
- Immutable instance (single final field); thread-safe for concurrent `create()` calls
