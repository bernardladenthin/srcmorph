### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:58Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates the appropriate AI generation provider implementation based on configuration keys.

#### Purpose
- Factory class to resolve provider names to concrete `AiGenerationProvider` implementations.
- Determines effective provider priority between run-level and model-level configurations.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok).
- No explicit `extends` or `implements` clauses.

#### Input
- Constructor: No arguments.
- `effectiveProvider(String runProvider, String modelProvider)`: Run-level provider key and model-level provider key.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`: Provider name string, LLM configuration object, and prompt support utility.

#### Output
- `effectiveProvider`: Returns `@Nullable String` representing the selected provider key.
- `create`: Returns a new `AiGenerationProvider` instance (Mock, LlamaCppJni, or OpenAiCompatible).
- Throws `IllegalArgumentException` for unrecognized provider names.

#### Core logic
- `effectiveProvider`: Returns `modelProvider` if present and non-empty; otherwise returns `runProvider`.
- `create`:
  - Computes effective provider name using `effectiveProvider`.
  - Defaults to `MockAiGenerationProvider` if effective name is null or blank.
  - Dispatches to specific constructor based on string match:
    - `PROVIDER_MOCK`: Instantiates `MockAiGenerationProvider`.
    - `PROVIDER_LLAMACPP_JNI`: Instantiates `LlamaCppJniAiGenerationProvider`.
    - `PROVIDER_OPENAI_COMPATIBLE`: Instantiates `OpenAiCompatibleAiGenerationProvider`.
  - Throws exception if provider name does not match known constants.

#### Public API
- `AiGenerationProviderFactory()` -> `void` (Constructor)
- `effectiveProvider(String, String) -> String` (Resolves effective provider key)
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` (Instantiates provider)
- `PROVIDER_MOCK` (String constant)
- `PROVIDER_LLAMACPP_JNI` (String constant)
- `PROVIDER_OPENAI_COMPATIBLE` (String constant)

#### Dependencies
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if `create` receives an unrecognized provider name.
- Null handling: `effectiveProvider` handles null inputs gracefully; `create` treats null/blank effective names as mock.

#### Concurrency
- No synchronization or thread-safety mechanisms present.
- State is immutable (constants) or transient (local variables).
