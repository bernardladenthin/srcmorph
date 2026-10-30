### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:41:12Z
- G: 
- A: 0.0.0
- X: file
---
Preparing a dense, structured markdown summary of the Java source file for a searchable code index.

Analyzing the factory class structure. It is a public Lombok `@ToString` class in the `net.ladenthin.srcmorph.provider` package with no superclass or interface. It defines static final provider name constants such as `PROVIDER_MOCK` and `PROVIDER_LLAMACPP_JNI`.

The factory also defines `PROVIDER_OPENAI_COMPATIBLE` and a private final `Java8CompatibilityHelper`. It exposes a constructor, a static `effectiveProvider` method that resolves provider names from run and model arguments, and a `create` method that instantiates the selected implementation using a `LlamaCppJniConfig` and `AiPromptSupport`.

The public API is now fully enumerated. The `effectiveProvider` method resolves a provider name from run and model arguments. The `create` method returns an `AiGenerationProvider`. Notable dependencies include `AiPromptSupport`, `Java8CompatibilityHelper`, and `org.jspecify.annotations.Nullable`.

The factory throws IllegalArgumentException for unrecognized provider names. Null or blank inputs default to MockAiGenerationProvider. It is not thread-safe; the effectiveProvider method is static, but mutable state is not shared across calls.

Drafting the lead blockquote and section headers for the markdown summary. The lead will capture the factory's purpose: selecting an AI generation backend and instantiating the matching provider. The structure will include Purpose, Type, Input, Output, Core logic, Public API, Dependencies, and Exceptions. Concurrency notes will be kept minimal since the class is not thread-special.

The concurrency note will be brief: no locks are used, the create method is reentrant, and the helper field is final. The public API list is being drafted with `name(params) -> returnType` entries, each kept under six words.> Selects the AI generation backend for a model run and instantiates the matching provider.

#### Purpose
- Factory that maps a provider key to an `AiGenerationProvider` for source-morph AI generation.
- Resolves whether the model’s own provider overrides the run’s `generationProvider`.

#### Type
- Concrete public class `AiGenerationProviderFactory`; not abstract, final, or sealed.
- No superclass or interfaces. Lombok `@ToString`.

#### Input
- `create`: run `providerName`, `LlamaCppJniConfig llamaConfig` (including `provider()`), `AiPromptSupport promptSupport`.
- `effectiveProvider`: nullable `runProvider` and nullable `modelProvider`.
- Field `compatibilityHelper` (`Java8CompatibilityHelper`) used for blank checks.

#### Output
- `effectiveProvider` returns the trimmed model provider, else the run provider (`null`/empty only if neither names one).
- `create` returns a new `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- Unrecognized names throw `IllegalArgumentException`.

#### Core logic
- `effectiveProvider`: if `modelProvider` is null or `trim()` is empty, return `runProvider`; otherwise return `modelProvider.trim()`.
- `create` computes `effectiveProvider(providerName, llamaConfig.provider())`.
- Null or blank effective key defaults to `new MockAiGenerationProvider()`.
- Switch on effective key: `PROVIDER_MOCK` (`"mock"`) → `MockAiGenerationProvider`; `PROVIDER_LLAMACPP_JNI` (`"llamacpp-jni"`) → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`; `PROVIDER_OPENAI_COMPATIBLE` (`"openai-compatible"`) → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
- Default branch throws `IllegalArgumentException` with the unsupported key.

#### Public API
- `AiGenerationProviderFactory() -> AiGenerationProviderFactory` — Constructs an empty factory.
- `PROVIDER_MOCK` — Key for mock provider.
- `PROVIDER_LLAMACPP_JNI` — Key for llama.cpp JNI.
- `PROVIDER_OPENAI_COMPATIBLE` — Key for OpenAI-compatible API.
- `effectiveProvider(runProvider, modelProvider) -> String` — Picks model or run key.
- `create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider` — Instantiates the selected provider.

#### Dependencies
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`, `Nullable`, Lombok `ToString`, `IllegalArgumentException`.

#### Exceptions / Errors
- `IllegalArgumentException` when the effective provider name is not one of the three keys.
- Null or blank effective name is not an error; it selects `MockAiGenerationProvider`.
- `effectiveProvider` treats null/empty model provider as “use run provider”; it does not blank-check `runProvider`.

#### Concurrency
- No locks or shared mutable state. `compatibilityHelper` is a private final field; `effectiveProvider` is static.
