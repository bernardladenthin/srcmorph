### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:50:51Z
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
- Fields: `private final Java8CompatibilityHelper compatibilityHelper`  
- Constants: `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`

#### Input
- Constructor: no parameters.
- `effectiveProvider(String runProvider, String modelProvider)` – two optional strings.
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – provider key, model config, prompt support.

#### Output
- `effectiveProvider` returns the resolved provider key or `null`.
- `create` returns a new `AiGenerationProvider` instance or throws `IllegalArgumentException`.

#### Core logic
- `effectiveProvider`: if `modelProvider` is null/blank, return `runProvider`; else trimmed `modelProvider`.
- `create`:  
  1. Resolve effective provider via `effectiveProvider`.  
  2. If null or blank, instantiate `MockAiGenerationProvider`.  
  3. Switch on effective provider:  
     - `mock` → `MockAiGenerationProvider`.  
     - `llamacpp-jni` → `LlamaCppJniAiGenerationProvider`.  
     - `openai-compatible` → `OpenAiCompatibleAiGenerationProvider`.  
  4. Throw `IllegalArgumentException` for unknown provider.

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
- `IllegalArgumentException` when provider name is unrecognized.

#### Concurrency
- Stateless after construction; thread‑safe.
