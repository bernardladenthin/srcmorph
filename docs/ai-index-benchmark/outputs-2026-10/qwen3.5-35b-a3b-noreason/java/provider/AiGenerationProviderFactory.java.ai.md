### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:17Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation based on a configuration key.

#### Purpose
- Factory to resolve provider names to specific {@link AiGenerationProvider} implementations.
- Handles fallback logic between run-level and model-level provider configurations.

#### Type
- `public class` with `@ToString` annotation.
- Extends `Object` (default).
- Implements no interfaces.
- Contains `private final Java8CompatibilityHelper` field.

#### Input
- `effectiveProvider(String runProvider, String modelProvider)`: nullable strings for run and model provider keys.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`: provider name, model config, and prompt support.

#### Output
- `effectiveProvider`: nullable String representing the resolved provider key.
- `create`: instantiated {@link AiGenerationProvider} (e.g., {@link MockAiGenerationProvider}, {@link LlamaCppJniAiGenerationProvider}, {@link OpenAiCompatibleAiGenerationProvider}).
- Throws `IllegalArgumentException` for unrecognized provider names.

#### Core logic
- Resolve effective provider key by prioritizing model provider over run provider if model provider is non-empty.
- Default to {@link MockAiGenerationProvider} if effective key is null, empty, or whitespace.
- Dispatch to specific provider implementation via switch statement:
  - `PROVIDER_MOCK` -> {@link MockAiGenerationProvider}.
  - `PROVIDER_LLAMACPP_JNI` -> {@link LlamaCppJniAiGenerationProvider}.
  - `PROVIDER_OPENAI_COMPATIBLE` -> {@link OpenAiCompatibleAiGenerationProvider}.
- Validate provider name against known constants before instantiation.

#### Public API
- `effectiveProvider(runProvider, modelProvider) -> String` resolves provider priority.
- `create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider` instantiates provider.
- `PROVIDER_MOCK` (constant) selects mock provider.
- `PROVIDER_LLAMACPP_JNI` (constant) selects llama.cpp JNI provider.
- `PROVIDER_OPENAI_COMPATIBLE` (constant) selects OpenAI-compatible provider.

#### Dependencies
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- `IllegalArgumentException` thrown if provider name is not one of the three supported constants.
- Null handling in `effectiveProvider` and `create` methods to prevent crashes on missing configuration.

#### Concurrency
- No concurrency mechanisms or thread-safety annotations present.
