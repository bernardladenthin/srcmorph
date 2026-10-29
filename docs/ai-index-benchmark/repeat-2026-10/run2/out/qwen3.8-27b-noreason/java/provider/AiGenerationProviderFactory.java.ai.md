### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:19Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that resolves and instantiates the correct `AiGenerationProvider` implementation (mock, llama.cpp JNI, or OpenAI-compatible) based on a provider key and model configuration.

#### Purpose
- Selects and constructs an `AiGenerationProvider` instance by name.
- Resolves the effective provider key from run-level and model-level configuration.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok)

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`
- `effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider)`
- Field: `Java8CompatibilityHelper compatibilityHelper`

#### Output
- `create(...)` returns a new `AiGenerationProvider` instance
- `effectiveProvider(...)` returns `@Nullable String` (the resolved provider key)

#### Core logic
- `effectiveProvider`: returns `modelProvider.trim()` if non-null and non-blank, otherwise falls back to `runProvider`.
- `create`:
  1. Resolves effective provider via `effectiveProvider(providerName, llamaConfig.provider())`.
  2. If effective is null or blank, defaults to `MockAiGenerationProvider`.
  3. Switches on effective key:
     - `"mock"` → `new MockAiGenerationProvider()`
     - `"llamacpp-jni"` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
     - `"openai-compatible"` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`
     - default → throws `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` -> `void` — no-op constructor
- `effectiveProvider(String, String) -> @Nullable String` — resolves provider key
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — instantiates provider by name
- `PROVIDER_MOCK` -> `String` — constant `"mock"`
- `PROVIDER_LLAMACPP_JNI` -> `String` — constant `"llamacpp-jni"`
- `PROVIDER_OPENAI_COMPATIBLE` -> `String` — constant `"openai-compatible"`

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- Referenced types: `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` thrown when provider name is unrecognized
- Null/blank provider names default to mock provider (no exception)
