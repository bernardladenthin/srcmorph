### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:26:28Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates AI generation provider implementations based on configuration keys.

#### Purpose
*   Factory for selecting and creating `AiGenerationProvider` instances.
*   Resolves effective provider key from run and model configurations.

#### Type
*   class `AiGenerationProviderFactory`
*   @ToString (Lombok)
*   No extends or implements clauses.

#### Input
*   `effectiveProvider`: `runProvider` (String, nullable), `modelProvider` (String, nullable).
*   `create`: `providerName` (String), `llamaConfig` (`LlamaCppJniConfig`), `promptSupport` (`AiPromptSupport`).
*   Constructor: no parameters.

#### Output
*   `effectiveProvider`: effective provider key (String, nullable).
*   `create`: instantiated `AiGenerationProvider` instance.
*   Constructor: no return value.

#### Core logic
*   Resolve effective provider key prioritizing model configuration over run configuration.
*   Default to `MockAiGenerationProvider` if key is null or blank.
*   Instantiate `LlamaCppJniAiGenerationProvider` for `llamacpp-jni` key.
*   Instantiate `OpenAiCompatibleAiGenerationProvider` for `openai-compatible` key.
*   Throw `IllegalArgumentException` for unrecognized provider keys.

#### Public API
*   `AiGenerationProviderFactory() -> void` creates factory instance.
*   `effectiveProvider(String, String) -> String` resolves effective provider key.
*   `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` instantiates provider.
*   `PROVIDER_MOCK -> String` mock provider key constant.
*   `PROVIDER_LLAMACPP_JNI -> String` llama.cpp JNI provider key constant.
*   `PROVIDER_OPENAI_COMPATIBLE -> String` OpenAI compatible provider key constant.
*   (6 public members total: 1 constructor, 2 methods, 3 constants).

#### Dependencies
*   `lombok.ToString`
*   `net.ladenthin.srcmorph.prompt.AiPromptSupport`
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `org.jspecify.annotations.Nullable`
*   `net.ladenthin.srcmorph.provider.AiGenerationProvider`
*   `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
*   `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
*   `IllegalArgumentException` thrown for unsupported provider names in `create`.
*   Null handling in `effectiveProvider` and `create` (defaults to mock).

#### Concurrency
*   No synchronization or thread-safety notes.
*   Immutable constants.
