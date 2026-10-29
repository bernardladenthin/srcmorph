### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:17Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that selects and instantiates the correct AI generation provider (mock, llama.cpp JNI, or OpenAI-compatible) based on a provider key resolved from run and model configuration.

#### Purpose
- Resolves the effective provider key from run-level and model-level configuration.
- Instantiates the matching `AiGenerationProvider` implementation for AI text generation.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated with `@ToString` (Lombok)

#### Input
- `create`: `String providerName`, `LlamaCppJniConfig llamaConfig`, `AiPromptSupport promptSupport`
- `effectiveProvider`: `@Nullable String runProvider`, `@Nullable String modelProvider`
- Field: `Java8CompatibilityHelper compatibilityHelper` (instantiated inline)

#### Output
- `create` returns a new `AiGenerationProvider` instance (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`)
- `effectiveProvider` returns `@Nullable String` — the resolved provider key

#### Core logic
- `effectiveProvider`: if `modelProvider` is null or blank, returns `runProvider`; otherwise returns trimmed `modelProvider`
- `create`:
  1. Resolves effective key via `effectiveProvider(providerName, llamaConfig.provider())`
  2. If effective key is null or blank → returns `MockAiGenerationProvider` (default)
  3. Switch on effective key:
     - `"mock"` → `new MockAiGenerationProvider()`
     - `"llamacpp-jni"` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
     - `"openai-compatible"` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`
     - default → throws `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` -> `void` — no-op constructor
- `effectiveProvider(@Nullable String, @Nullable String)` -> `@Nullable String` — resolves model-level over run-level key
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider` — instantiates provider by resolved key
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
- `IllegalArgumentException` thrown when `effective` provider key does not match any known constant
- Null/blank handling: `effectiveProvider` guards against null and empty `modelProvider`; `create` treats null/blank effective key as mock default
