### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:41Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Selects a model-generation backend—mock, llama.cpp JNI, or an OpenAI-compatible server—from run and model provider settings.

#### Purpose
- Creates the configured `AiGenerationProvider`, defaulting to the deterministic mock provider when no provider is specified.
- Resolves whether the run-level or model-level provider setting takes precedence.

#### Type
- `public class AiGenerationProviderFactory` — class; annotated `@ToString`.
- No superclass or interfaces declared.
- Holds a private final `Java8CompatibilityHelper`.

#### Input
- `create`: provider key, `LlamaCppJniConfig`, and `AiPromptSupport`.
- `effectiveProvider`: nullable run-level and model-level provider keys.
- Provider selection also reads `provider()` from the model configuration.

#### Output
- `create` returns a newly instantiated `AiGenerationProvider`.
- `effectiveProvider` returns the trimmed model provider when nonblank; otherwise returns the run provider.
- Provider instances receive the model configuration and prompt support when required.

#### Core logic
- `effectiveProvider` gives a nonblank model-level provider precedence over the run-level provider; trims the selected model-level value.
- `create` resolves the effective key using the run provider and `llamaConfig.provider()`.
- Returns `MockAiGenerationProvider` when the effective key is null or blank, or equals `mock`.
- Creates `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.
- Creates `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.
- Rejects any other key.

#### Public API
- `PROVIDER_MOCK` -> `String`; provider key `"mock"`.
- `PROVIDER_LLAMACPP_JNI` -> `String`; provider key `"llamacpp-jni"`.
- `PROVIDER_OPENAI_COMPATIBLE` -> `String`; provider key `"openai-compatible"`.
- `AiGenerationProviderFactory()` -> constructor; creates factory.
- `effectiveProvider(String, String)` -> `@Nullable String`; resolve provider precedence.
- `create(String, LlamaCppJniConfig, AiPromptSupport)` -> `AiGenerationProvider`; instantiate selected provider.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` with `Unsupported AI provider: <key>` for unrecognized nonblank provider keys.
- Null or blank effective provider selects the mock provider.

#### Concurrency
- No synchronization or mutable shared state; the compatibility helper is held in a final field.
