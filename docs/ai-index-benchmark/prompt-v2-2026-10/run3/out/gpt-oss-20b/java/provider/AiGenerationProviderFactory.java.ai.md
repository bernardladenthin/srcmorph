### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:04:40Z
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
  - `runProvider`: provider key from run, may be `null`.  
  - `modelProvider`: provider key from model config, may be `null` or empty.  
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key, may be `null` or blank.  
  - `llamaConfig`: model configuration object.  
  - `promptSupport`: lookup support for prompts.

#### Output
- `effectiveProvider`: trimmed provider key or `null`.  
- `create`: instance of `AiGenerationProvider` (one of `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`).

#### Core logic
- `effectiveProvider`: returns `modelProvider` if non‑blank; otherwise `runProvider`.  
- `create`:  
  1. Resolve effective provider via `effectiveProvider`.  
  2. If blank or `null`, instantiate `MockAiGenerationProvider`.  
  3. Switch on effective provider:  
     - `mock` → `MockAiGenerationProvider`.  
     - `llamacpp-jni` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
     - `openai-compatible` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
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
- `IllegalArgumentException` thrown in `create` when provider key is unrecognised.

#### Concurrency
- No mutable shared state; thread‑safe by default.
