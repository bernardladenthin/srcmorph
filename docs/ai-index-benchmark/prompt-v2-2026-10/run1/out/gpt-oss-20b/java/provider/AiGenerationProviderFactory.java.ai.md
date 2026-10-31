### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:30Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Factory for `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key.  
  - `llamaConfig`: model configuration, may supply its own provider key.  
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a concrete `AiGenerationProvider` instance.  
- May throw `IllegalArgumentException` for unknown provider keys.

#### Core logic
- `effectiveProvider` selects the provider key: model’s key if present, otherwise run’s key.  
- `create` trims the effective key; defaults to `MockAiGenerationProvider` if blank.  
- Switch on the effective key:
  - `"mock"` → `MockAiGenerationProvider`.  
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider`.  
  - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider`.  
  - else → `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` → resolves provider key.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` → creates provider.

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
- Stateless; thread‑safe.  
- Uses a single `Java8CompatibilityHelper` instance.
