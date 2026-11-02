### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:47Z
- G: 
- A: 0.0.0
- X: file
---
> Selects a model-generation provider from run or model configuration, defaulting to deterministic mock generation.

#### Purpose
- Resolves the provider key and creates the matching AI generation provider.
- Uses a model’s provider setting in preference to the run-level setting.

#### Type
- `AiGenerationProviderFactory` — public class.
- Annotated with Lombok `@ToString`.

#### Input
- `effectiveProvider(runProvider, modelProvider)` accepts nullable provider keys.
- `create(providerName, llamaConfig, promptSupport)` accepts the run key, model configuration, and prompt lookup support.

#### Output
- Returns `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Exposes provider keys `mock`, `llamacpp-jni`, and `openai-compatible`.

#### Core logic
- `effectiveProvider` returns the trimmed model provider when non-null and nonblank; otherwise returns the run provider.
- `create` defaults to mock when the effective provider is null or blank.
- Dispatches recognized keys to the corresponding provider implementation, passing configuration and prompt support to providers that need them.
- Rejects unrecognized keys with `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructor; initializes factory.
- `PROVIDER_MOCK` → `String`; mock provider key.
- `PROVIDER_LLAMACPP_JNI` → `String`; llama.cpp JNI provider key.
- `PROVIDER_OPENAI_COMPATIBLE` → `String`; OpenAI-compatible provider key.
- `effectiveProvider(String, String)` → `@Nullable String`; resolve model or run provider.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` → `AiGenerationProvider`; instantiate selected provider.

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for an unsupported provider key.
- `effectiveProvider` allows null inputs and treats a null or trimmed-empty model key as absent.

#### Concurrency
- Holds a final `Java8CompatibilityHelper`; no mutable factory state or synchronization is present.
