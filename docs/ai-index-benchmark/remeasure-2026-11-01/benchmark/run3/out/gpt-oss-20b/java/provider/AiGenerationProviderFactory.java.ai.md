### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:43Z
- G: 
- A: 0.0.0
- X: file
---
> Factory that selects and instantiates an AI generation provider by name.

#### Purpose
- Provides a factory for `AiGenerationProvider` implementations.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key (may be null/blank).  
  - `llamaConfig`: model configuration, may supply its own provider key.  
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a concrete `AiGenerationProvider` instance:  
  - `MockAiGenerationProvider` for mock or blank keys.  
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.  
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.  
- Throws `IllegalArgumentException` for unknown keys.

#### Core logic
- `effectiveProvider` trims and prefers model’s provider over run’s.  
- `create` uses `effectiveProvider` to determine key.  
- Blank or null key → `MockAiGenerationProvider`.  
- Switch on key to instantiate appropriate provider.  
- Default case throws `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` → resolves provider key.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` → creates provider instance.

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
- No shared mutable state; thread‑safe.
