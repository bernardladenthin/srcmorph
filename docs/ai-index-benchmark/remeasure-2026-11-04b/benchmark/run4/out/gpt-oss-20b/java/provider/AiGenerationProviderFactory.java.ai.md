### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:55Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Selects an `AiGenerationProvider` implementation by name or model configuration.
- Provides a default mock provider when no key is supplied.

#### Type
- `public class AiGenerationProviderFactory`  
  - Modifiers: public, final (implicit), @ToString  
  - Supertype: `Object`  
  - Fields:  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper for blank checks  
  - Methods:  
    - `public AiGenerationProviderFactory()` – no‑op constructor  
    - `public static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key  
    - `public AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method  

#### Input
- `providerName` (String) – run‑level provider key, may be null or blank.  
- `llamaConfig` (`LlamaCppJniConfig`) – model configuration, provides `provider()` method.  
- `promptSupport` (`AiPromptSupport`) – lookup support for providers that need it.  

#### Output
- Returns an instance of `AiGenerationProvider`:  
  - `MockAiGenerationProvider` when key is blank or `PROVIDER_MOCK`.  
  - `LlamaCppJniAiGenerationProvider` when key is `PROVIDER_LLAMACPP_JNI`.  
  - `OpenAiCompatibleAiGenerationProvider` when key is `PROVIDER_OPENAI_COMPATIBLE`.  
- Throws `IllegalArgumentException` for unknown keys.

#### Core logic
- `effectiveProvider` trims `modelProvider`; if empty, returns `runProvider`.  
- `create` calls `effectiveProvider` to get `effective`.  
- If `effective` is null or blank, instantiate `MockAiGenerationProvider`.  
- Switch on `effective`:  
  - `PROVIDER_MOCK` → `MockAiGenerationProvider()`  
  - `PROVIDER_LLAMACPP_JNI` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`  
  - `PROVIDER_OPENAI_COMPATIBLE` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`  
  - default → `IllegalArgumentException("Unsupported AI provider: " + effective)`  

#### Public API
- `effectiveProvider(String, String) -> String?` – resolves provider key.  
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` – creates provider instance.  

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
- `IllegalArgumentException` when provider key is unrecognised.  

#### Concurrency
- No explicit synchronization; instance fields are immutable after construction.
