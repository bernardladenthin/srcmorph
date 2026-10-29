### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:57Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates AI generation providers (mock, llama.cpp, or OpenAI-compatible) based on configuration keys.

#### Purpose
- Factory to resolve provider names to concrete `AiGenerationProvider` implementations.
- Determines effective provider key by prioritizing model configuration over run configuration.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok).
- No explicit `extends` or `implements`.

#### Input
- Constructor: none (no-op).
- `effectiveProvider(String runProvider, String modelProvider)`: run-level and model-level provider keys.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`: provider name, model config, and prompt support helper.

#### Output
- `effectiveProvider`: resolved provider key string (nullable).
- `create`: instantiated `AiGenerationProvider` instance.
- Side effects: instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.

#### Core logic
- `effectiveProvider`: returns `modelProvider` if non-empty, otherwise returns `runProvider`.
- `create`: resolves effective provider name using `effectiveProvider`.
- `create`: defaults to `MockAiGenerationProvider` if effective name is null or blank.
- `create`: switches on effective name to instantiate specific provider:
  - `PROVIDER_MOCK`: instantiates `MockAiGenerationProvider`.
  - `PROVIDER_LLAMACPP_JNI`: instantiates `LlamaCppJniAiGenerationProvider` with config and prompt support.
  - `PROVIDER_OPENAI_COMPATIBLE`: instantiates `OpenAiCompatibleAiGenerationProvider` with config and prompt support.
- `create`: throws `IllegalArgumentException` for unrecognized provider names.

#### Public API
- `AiGenerationProviderFactory()` -> `void` (constructor)
- `effectiveProvider(String, String) -> String` (resolves effective provider key)
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` (instantiates provider)
- `PROVIDER_MOCK` (constant)
- `PROVIDER_LLAMACPP_JNI` (constant)
- `PROVIDER_OPENAI_COMPATIBLE` (constant)

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
- `IllegalArgumentException`: thrown if provider name is not recognized in `create`.
- Null handling: `effectiveProvider` handles null inputs; `create` handles null/blank effective names by defaulting to mock.

#### Concurrency
- No concurrency mechanisms or thread-safety notes present.
- Factory instance state is minimal (single `Java8CompatibilityHelper` field).
