### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:08Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Creates an AI generation provider instance based on a provider key or model configuration.

#### Purpose
- Instantiates an `AiGenerationProvider` implementation.
- Resolves the effective provider key from run or model configuration.

#### Type
- `public class AiGenerationProviderFactory`  
  - Modifiers: public, @ToString  
  - Supertypes: none  
  - Fields:  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper for blank checks  
  - Constants:  
    - `public static final String PROVIDER_MOCK = "mock"`  
    - `public static final String PROVIDER_LLAMACPP_JNI = "llamacpp-jni"`  
    - `public static final String PROVIDER_OPENAI_COMPATIBLE = "openai-compatible"`  
  - Methods:  
    - `public AiGenerationProviderFactory()` – no‑op constructor  
    - `public static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key  
    - `public AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method

#### Input
- `providerName` – run’s provider key (may be null or blank)  
- `llamaConfig` – model configuration (`LlamaCppJniConfig`)  
- `promptSupport` – `AiPromptSupport` for prompt lookup  
- `runProvider` and `modelProvider` parameters in `effectiveProvider`

#### Output
- Returns an `AiGenerationProvider` instance:  
  - `MockAiGenerationProvider` for mock or blank provider  
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`  
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`  
- Throws `IllegalArgumentException` for unknown provider key

#### Core logic
- `effectiveProvider` trims `modelProvider`; if empty, returns `runProvider`.  
- `create` obtains `effective` provider key via `effectiveProvider`.  
- If `effective` is null or blank, instantiate `MockAiGenerationProvider`.  
- Switch on `effective`:  
  - `"mock"` → `MockAiGenerationProvider`  
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`  
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`  
  - default → throw `IllegalArgumentException`

#### Public API
- `AiGenerationProviderFactory()` – constructs factory  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – creates provider instance

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` if provider key is unrecognized in `create`.

#### Concurrency
- No explicit synchronization; instance fields are immutable after construction.
