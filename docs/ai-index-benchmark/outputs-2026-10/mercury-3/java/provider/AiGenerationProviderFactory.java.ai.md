### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:30Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AiGenerationProvider implementation from a provider key, with fallback to mock when no key is given.

#### Purpose
- Factory that maps provider key strings (mock, llamacpp-jni, openai-compatible) to concrete AiGenerationProvider implementations.
- Also resolves effective provider from run-level and model-level provider names.

#### Type
- Class, public, non-final, non-abstract.
- Annotated with @ToString (Lombok).
- No extends/implements.

#### Input
- Constructor: `AiGenerationProviderFactory()` no-op, no params.
- Public static method `effectiveProvider(String runProvider, String modelProvider)` receives run provider and model provider, both nullable.
- Public method `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` receives provider key, model config, prompt support.
- Private field: `Java8CompatibilityHelper compatibilityHelper`, constructed inline.

#### Output
- `effectiveProvider` returns `@Nullable String`: model provider trimmed if non-empty, else run provider; null when both null/empty.
- `create` returns AiGenerationProvider instance: MockAiGenerationProvider (two paths), LlamaCppJniAiGenerationProvider, or OpenAiCompatibleAiGenerationProvider.
- Throws IllegalArgumentException for unrecognized provider key.

#### Core logic
- Provider constants: `PROVIDER_MOCK = "mock"`, `PROVIDER_LLAMACPP_JNI = "llamacpp-jni"`, `PROVIDER_OPENAI_COMPATIBLE = "openai-compatible"`.
- `effectiveProvider` precedence: model provider wins if non-blank; otherwise run provider.
- `create`: compute effective provider from providerName and llamaConfig.provider().
- If effective provider is null or blank, default to MockAiGenerationProvider.
- Switch on effective provider string: mock -> MockAiGenerationProvider; llamacpp-jni -> LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport); openai-compatible -> OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport); default -> IllegalArgumentException.

#### Public API
- `AiGenerationProviderFactory()` -> no-op constructor, public.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model provider over run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — instantiates provider by key.
- `PROVIDER_MOCK` -> static final String, constant.
- `PROVIDER_LLAMACPP_JNI` -> static final String, constant.
- `PROVIDER_OPENAI_COMPATIBLE` -> static final String, constant.
- Public members total: 3 fields, 3 methods = 6 members.

#### Dependencies
- net.ladenthin.srcmorph.provider.AiGenerationProvider
- net.ladenthin.srcmorph.provider.MockAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider
- net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider
- net.ladenthin.srcmorph.provider.LlamaCppJniConfig
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- lombok.ToString

#### Exceptions / Errors
- `create` throws IllegalArgumentException when effective provider string is not one of the three recognized keys.
- `effectiveProvider` handles null inputs gracefully, returns null when neither provider names one.
- `create` handles blank/null effective provider by returning mock provider.

#### Concurrency
- Class is effectively immutable after construction except for private `compatibilityHelper` field which is final and constructed inline; no mutable shared state.
- Methods are stateless except for `compatibilityHelper` usage; no synchronization needed.
