### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:04:23Z
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
- Constructor: no parameters.  
- `effectiveProvider(String runProvider, String modelProvider)`  
  - `runProvider`: provider key from run, may be null.  
  - `modelProvider`: provider key from model definition, may be null or empty.  
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run’s provider key.  
  - `llamaConfig`: model configuration object.  
  - `promptSupport`: lookup support for prompts.

#### Output
- `effectiveProvider` returns the resolved provider key (`String` or null).  
- `create` returns a new `AiGenerationProvider` instance or throws `IllegalArgumentException`.

#### Core logic
- `effectiveProvider`: if `modelProvider` is null/blank, return `runProvider`; else return trimmed `modelProvider`.  
- `create`:  
  1. Resolve effective provider via `effectiveProvider`.  
  2. If null or blank, instantiate `MockAiGenerationProvider`.  
  3. Switch on effective provider:  
     - `"mock"` → `MockAiGenerationProvider`.  
     - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
     - `"openai-compatible"` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
     - default → throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.  
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider instance.

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
- `create` throws `IllegalArgumentException` for unsupported provider names.

#### Concurrency
- Instance field `compatibilityHelper` is immutable; class is thread‑safe.
