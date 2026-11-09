### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:54:02Z
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
- `public class AiGenerationProviderFactory` (class, public, final by default, annotated `@ToString`)

#### Input
- Constructor: no parameters.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: run‑level provider key, may be null or blank.  
  - `llamaConfig`: model configuration, provides `provider()` method.  
  - `promptSupport`: lookup support for prompts.

#### Output
- Returns an `AiGenerationProvider` instance:  
  - `MockAiGenerationProvider` for mock or blank provider.  
  - `LlamaCppJniAiGenerationProvider` for `llamacpp-jni`.  
  - `OpenAiCompatibleAiGenerationProvider` for `openai-compatible`.  
- Throws `IllegalArgumentException` if provider key unrecognised.

#### Core logic
- `effectiveProvider(String runProvider, String modelProvider)`  
  - Returns `modelProvider.trim()` if non‑empty; otherwise `runProvider`.  
- `create(...)`  
  - Calls `effectiveProvider` to get `effective`.  
  - If `effective` is null or blank (`compatibilityHelper.isBlank`), return `MockAiGenerationProvider`.  
  - Switch on `effective`:  
    - `PROVIDER_MOCK`: return `MockAiGenerationProvider`.  
    - `PROVIDER_LLAMACPP_JNI`: return `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
    - `PROVIDER_OPENAI_COMPATIBLE`: return `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
    - default: throw `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` -> constructor, no-op.  
- `static String effectiveProvider(String runProvider, String modelProvider)` -> provider key resolution.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` -> provider creation.

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
- `IllegalArgumentException` when provider key not in `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, or `PROVIDER_OPENAI_COMPATIBLE`.

#### Concurrency
- No explicit synchronization; instance fields are immutable (`compatibilityHelper`).  
- Thread‑safe by design.
