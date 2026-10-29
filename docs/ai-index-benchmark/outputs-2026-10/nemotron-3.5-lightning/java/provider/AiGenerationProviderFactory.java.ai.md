### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:01:26Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates AI generation providers by name

#### Purpose
- Creates AI generation provider instances by provider key
- Maps provider names to concrete implementations (mock, llama.cpp JNI, OpenAI-compatible)

#### Type
- Kind: class
- Modifiers: public, @ToString (Lombok)
- No extends or implements
- Notable annotation: @ToString

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – provider key, model config, prompt support
- `effectiveProvider(String runProvider, String modelProvider)` – run and model provider keys
- No-arg constructor
- Private final field `Java8CompatibilityHelper compatibilityHelper`

#### Output
- `create` returns `AiGenerationProvider`
- `effectiveProvider` returns `@Nullable String`
- May throw `IllegalArgumentException`
- Produces `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`

#### Core logic
- Resolves effective provider key from `providerName` and `llamaConfig.provider()`
- Returns `MockAiGenerationProvider` when key is blank or null
- Dispatches to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider` via switch on effective key
- Throws `IllegalArgumentException` for unrecognized provider

#### Public API
- `AiGenerationProviderFactory() -> initializes factory`
- `PROVIDER_MOCK -> mock provider key`
- `PROVIDER_LLAMACPP_JNI -> llama.cpp JNI key`
- `PROVIDER_OPENAI_COMPATIBLE -> OpenAI-compatible key`
- `effectiveProvider(String runProvider, String modelProvider) -> @Nullable String: resolves effective provider key`
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider: instantiates AI provider by name`

#### Dependencies
- Imports: `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`
- Referenced types: `AiGenerationProvider`, `LlamaCppJniConfig`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`

#### Exceptions / Errors
- Throws `IllegalArgumentException` when provider name is unrecognized
- Handles null/empty `modelProvider` in `effectiveProvider`
- Null-safety via `Java8CompatibilityHelper.isBlank`

#### Concurrency
- No instance-level mutable state; `compatibilityHelper` is final
- Methods are stateless; thread-safety depends on provider implementations
