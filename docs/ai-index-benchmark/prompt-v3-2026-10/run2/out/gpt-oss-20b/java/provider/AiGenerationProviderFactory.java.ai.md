### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:34Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Supplies an `AiGenerationProvider` instance for a run or model.
- Resolves provider key precedence between run and model.

#### Type
`public class AiGenerationProviderFactory`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key (may be null/blank).  
  - `llamaConfig`: model configuration, may supply its own provider key.  
  - `promptSupport`: lookup support for prompt templates.  
- `effectiveProvider(String runProvider, String modelProvider)`  
  - `runProvider`: run‑level provider key.  
  - `modelProvider`: model‑level provider key.

#### Output
- `create(...)` returns an `AiGenerationProvider` instance:  
  - `MockAiGenerationProvider` for mock or blank keys.  
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.  
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.  
- `effectiveProvider(...)` returns the resolved provider key or null.

#### Core logic
- `effectiveProvider` chooses `modelProvider` if present and non‑blank; otherwise `runProvider`.  
- `create` trims the resolved key, defaults to mock if blank.  
- Switch on key to instantiate the corresponding provider; throws `IllegalArgumentException` for unknown keys.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.  
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider instance.

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`  
- Provider implementations: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`  
- `LlamaCppJniConfig`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` if provider key is unrecognised.

#### Concurrency
- Instance field `compatibilityHelper` is immutable; factory is thread‑safe.
