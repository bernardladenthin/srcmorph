### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:42:09Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that selects and instantiates the correct `AiGenerationProvider` implementation (mock, llama.cpp JNI, or OpenAI-compatible) based on a provider key resolved from run and model configuration.

#### Purpose
- Factory for creating `AiGenerationProvider` instances by provider name.
- Resolves the effective provider key from a run-level default and a model-level override.

#### Type
- `public class AiGenerationProviderFactory`
- Annotated `@ToString` (Lombok)
- No extends / implements

#### Input
- `create`: `String providerName` (run-level key), `LlamaCppJniConfig llamaConfig` (model config carrying its own `provider()` field), `AiPromptSupport promptSupport`
- `effectiveProvider`: `@Nullable String runProvider`, `@Nullable String modelProvider`
- Field: `Java8CompatibilityHelper compatibilityHelper` (instantiated inline)

#### Output
- `create` returns a newly-created `AiGenerationProvider` instance (one of three concrete types)
- `effectiveProvider` returns the resolved provider key or `null`/empty when neither source names one

#### Core logic
- `effectiveProvider`: if `modelProvider` is null or blank, fall back to `runProvider`; otherwise return trimmed `modelProvider`.
- `create`:
  1. Resolve effective key via `effectiveProvider(providerName, llamaConfig.provider())`.
  2. If effective key is null or blank → return `MockAiGenerationProvider` (default).
  3. Switch on effective key:
     - `"mock"` → `MockAiGenerationProvider`
     - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
     - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`
     - default → throw `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` — no-op constructor
- `effectiveProvider(@Nullable String, @Nullable String) -> @Nullable String` — resolves model-level override over run-level
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — instantiates provider by resolved key

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- Referenced types: `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` thrown when the resolved provider key matches none of the three known constants.
- Null/blank provider key silently defaults to mock (no exception).
