### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:11Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Purpose
- Factory class for creating `AiGenerationProvider` instances based on a provider name.

#### Type
- Class
- `@ToString`

#### Input
- `providerName`: The run's provider key; one of `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, or `PROVIDER_OPENAI_COMPATIBLE` (defaults to mock when blank or `null`).
- `llamaConfig`: The model's configuration.
- `promptSupport`: Prompt lookup support passed to providers that need it.

#### Output
- Returns a newly-created `AiGenerationProvider` instance.
- May return `null` or empty only when neither `providerName` nor `llamaConfig.provider()` names one.

#### Core logic
- Determines the effective provider name using `effectiveProvider`.
- Returns a `MockAiGenerationProvider` if the effective provider name is `null` or empty.
- Uses a `switch` statement to instantiate the appropriate `AiGenerationProvider` based on the effective provider name:
  - `PROVIDER_MOCK`: Returns a `MockAiGenerationProvider`.
  - `PROVIDER_LLAMACPP_JNI`: Returns a `LlamaCppJniAiGenerationProvider` with `llamaConfig` and `promptSupport`.
  - `PROVIDER_OPENAI_COMPATIBLE`: Returns an `OpenAiCompatibleAiGenerationProvider` with `llamaConfig` and `promptSupport`.
- Throws an `IllegalArgumentException` if the provider name is not recognized.

#### Public API
- `AiGenerationProviderFactory()`: Creates a new `AiGenerationProviderFactory`.
- `effectiveProvider(@Nullable String runProvider, @Nullable String modelProvider) -> @Nullable String`: Determines the effective provider name.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`: Creates an `AiGenerationProvider` instance based on the provider name.

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IllegalArgumentException` if the provider name is not recognized.

#### Concurrency
- No concurrency or synchronization mechanisms are used in this class.
