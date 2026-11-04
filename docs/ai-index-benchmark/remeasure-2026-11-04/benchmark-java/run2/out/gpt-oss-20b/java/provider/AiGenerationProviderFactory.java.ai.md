### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:14Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Supplies an `AiGenerationProvider` instance for a run or model.
- Resolves provider key via `effectiveProvider`.

#### Type
`public class AiGenerationProviderFactory`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key (may be null/blank).  
  - `llamaConfig`: model configuration, provides `provider()` key.  
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns a concrete `AiGenerationProvider` instance:
  - `MockAiGenerationProvider` for mock or blank keys.
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.
- Throws `IllegalArgumentException` for unknown keys.

#### Core logic
- `effectiveProvider` selects the non‑blank provider key, preferring the model’s key over the run’s.
- `create`:
  1. Determine effective key via `effectiveProvider`.
  2. If key is null or blank, return `MockAiGenerationProvider`.
  3. Switch on key:
     - `mock`: return `MockAiGenerationProvider`.
     - `llamacpp-jni`: instantiate `LlamaCppJniAiGenerationProvider` with config and prompt support.
     - `openai-compatible`: instantiate `OpenAiCompatibleAiGenerationProvider` with config and prompt support.
     - default: throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → no-op constructor.  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` → resolve provider key.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` → instantiate provider.

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
