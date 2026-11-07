### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:28Z
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
  - Modifiers: public, final (implied by lack of `abstract`/`open`).  
  - Annotations: `@ToString`.  
  - Supertypes: none.  
  - Fields:  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper for blank checks.  
  - Methods:  
    - `public AiGenerationProviderFactory()` – no‑op constructor.  
    - `public static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key.  
    - `public AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method.  
  - Constants:  
    - `public static final String PROVIDER_MOCK = "mock"`  
    - `public static final String PROVIDER_LLAMACPP_JNI = "llamacpp-jni"`  
    - `public static final String PROVIDER_OPENAI_COMPATIBLE = "openai-compatible"`

#### Input
- `create` parameters:  
  - `providerName` – run‑level provider key.  
  - `llamaConfig` – model configuration (`LlamaCppJniConfig`).  
  - `promptSupport` – `AiPromptSupport` instance.  
- `effectiveProvider` parameters:  
  - `runProvider` – run’s provider key.  
  - `modelProvider` – model’s provider key.

#### Output
- `create` returns an `AiGenerationProvider` instance:  
  - `MockAiGenerationProvider` if provider key is blank or `"mock"`.  
  - `LlamaCppJniAiGenerationProvider` if `"llamacpp-jni"`.  
  - `OpenAiCompatibleAiGenerationProvider` if `"openai-compatible"`.  
- Throws `IllegalArgumentException` for unknown provider keys.

#### Core logic
- `effectiveProvider` trims `modelProvider`; if empty, returns `runProvider`.  
- `create` obtains `effective` provider key via `effectiveProvider`.  
- If `effective` is null or blank, instantiate `MockAiGenerationProvider`.  
- Switch on `effective`:  
  - `"mock"` → `MockAiGenerationProvider`.  
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
  - default → throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` – constructs factory.  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – creates provider instance.

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
- `IllegalArgumentException` thrown in `create` when provider key is unrecognised.

#### Concurrency
- No explicit synchronization; factory is stateless except for a single helper instance.
