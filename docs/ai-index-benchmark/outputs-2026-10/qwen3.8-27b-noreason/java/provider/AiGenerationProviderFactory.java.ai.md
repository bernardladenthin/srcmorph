### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:50Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that resolves and instantiates the correct AI generation provider (mock, llama.cpp JNI, or OpenAI-compatible) based on configuration keys.

#### Purpose
- Selects and constructs an `AiGenerationProvider` implementation by name.
- Resolves the effective provider key from run-level and model-level configuration.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok)

#### Input
- `create()`: `String providerName`, `LlamaCppJniConfig llamaConfig`, `AiPromptSupport promptSupport`
- `effectiveProvider()`: `@Nullable String runProvider`, `@Nullable String modelProvider`
- Field: `Java8CompatibilityHelper compatibilityHelper`

#### Output
- `create()` returns a new `AiGenerationProvider` instance
- `effectiveProvider()` returns `@Nullable String` (the resolved provider key)

#### Core logic
- `effectiveProvider()`: returns `modelProvider.trim()` if non-null and non-blank, otherwise falls back to `runProvider`
- `create()`:
  - Resolves effective provider via `effectiveProvider(providerName, llamaConfig.provider())`
  - Defaults to `MockAiGenerationProvider` if effective key is null or blank
  - Switches on effective key:
    - `"mock"` → `MockAiGenerationProvider`
    - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
    - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`
    - default → throws `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` -> `void` — no-op constructor
- `PROVIDER_MOCK` -> `String` — constant `"mock"`
- `PROVIDER_LLAMACPP_JNI` -> `String` — constant `"llamacpp-jni"`
- `PROVIDER_OPENAI_COMPATIBLE` -> `String` — constant `"openai-compatible"`
- `effectiveProvider(@Nullable String, @Nullable String)` -> `@Nullable String` — resolves provider key precedence
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider` — instantiates provider by name

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiGenerationProvider`

#### Exceptions / Errors
- `IllegalArgumentException` thrown when provider name is not one of the three recognized keys
- Null/blank handling: `effectiveProvider` treats null or whitespace-only `modelProvider` as absent, falling back to `runProvider`
