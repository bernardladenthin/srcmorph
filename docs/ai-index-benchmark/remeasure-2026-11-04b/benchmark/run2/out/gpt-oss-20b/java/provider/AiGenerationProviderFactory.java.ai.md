### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:34Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
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
  - Methods:  
    - `public AiGenerationProviderFactory()` – no‑op constructor  
    - `public static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – resolves provider key  
    - `public AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method  

#### Input
- `providerName` (String) – run‑level provider key, may be null or blank  
- `llamaConfig` (LlamaCppJniConfig) – model configuration, provides `provider()`  
- `promptSupport` (AiPromptSupport) – lookup support for prompts  

#### Output
- Returns an instance of `AiGenerationProvider` (Mock, LlamaCppJni, or OpenAiCompatible).  
- Throws `IllegalArgumentException` if provider key unrecognised.

#### Core logic
- Call `effectiveProvider(providerName, llamaConfig.provider())` to get `effective`.  
- If `effective` is null or blank (`compatibilityHelper.isBlank`), return `new MockAiGenerationProvider()`.  
- Switch on `effective`:  
  - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`  
  - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`  
  - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`  
  - default → `throw new IllegalArgumentException("Unsupported AI provider: " + effective)`  

#### Public API
- `effectiveProvider(String, String) -> String?` – resolves provider key  
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` – creates provider instance  

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
- `IllegalArgumentException` when provider key not in {mock, llamacpp-jni, openai-compatible}.  

#### Concurrency
- No explicit synchronization; class is stateless except for a single helper instance.  
- Thread‑safe by default.
