### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:49Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Selects the AI generation provider for a run, using model-level provider overrides and defaulting to deterministic mock generation.

#### Purpose
- `AiGenerationProviderFactory` resolves a provider key and creates the corresponding `AiGenerationProvider`.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- No supertypes or generics declared.

#### Input
- `effectiveProvider(runProvider, modelProvider)` — nullable run and model provider keys.
- `create(providerName, llamaConfig, promptSupport)` — run provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- Private `compatibilityHelper` — `Java8CompatibilityHelper` used to check blank provider names.

#### Output
- `effectiveProvider` returns the model provider when nonblank; otherwise the run provider.
- `create` returns a new mock, llama.cpp JNI, or OpenAI-compatible provider instance.
- No fields are mutated.

#### Core logic
- `effectiveProvider` trims and prefers a nonblank model provider; otherwise returns the run provider.
- `create` uses that effective key; `null` or blank selects `MockAiGenerationProvider`.
- Dispatches `mock` to `MockAiGenerationProvider`, `llamacpp-jni` to `LlamaCppJniAiGenerationProvider`, and `openai-compatible` to `OpenAiCompatibleAiGenerationProvider`.
- Passes `llamaConfig` and `promptSupport` to the llama.cpp JNI and OpenAI-compatible providers.

#### Public API
- `AiGenerationProviderFactory()` — constructs the factory.
- `PROVIDER_MOCK: String` — mock provider key.
- `PROVIDER_LLAMACPP_JNI: String` — llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE: String` — OpenAI-compatible provider key.
- `effectiveProvider(String, String) -> @Nullable String` — resolves model/run provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` — creates selected provider.

#### Dependencies
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniConfig`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unsupported nonblank provider key.
- `providerName` may be `null` according to the method documentation; `effectiveProvider` parameters and return are nullable.
