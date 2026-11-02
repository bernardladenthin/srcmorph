### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:41:06Z
- G: 
- A: 0.0.0
- X: file
---
> Instantiates AI generation providers based on a provider key, supporting mock, llama.cpp JNI, and OpenAI‑compatible backends.

#### Purpose
- Factory for `AiGenerationProvider` instances.
- Resolves provider key from run or model configuration.

#### Type
`public class AiGenerationProviderFactory`  
- Modifiers: public  
- Annotations: `@ToString`  
- No superclass or interfaces.

#### Input
- Constructor: no parameters.  
- `effectiveProvider(String, String)` – two optional provider keys.  
- `create(String, LlamaCppJniConfig, AiPromptSupport)` – run provider key, model config, prompt support.

#### Output
- `effectiveProvider` returns the resolved provider key or `null`.  
- `create` returns a concrete `AiGenerationProvider` instance or throws `IllegalArgumentException`.

#### Core logic
- `effectiveProvider`: choose `modelProvider` if present and non‑blank; otherwise use `runProvider`.  
- `create`:  
  1. Resolve effective provider key via `effectiveProvider`.  
  2. If key is blank or `null`, instantiate `MockAiGenerationProvider`.  
  3. Switch on key:  
     - `mock` → `MockAiGenerationProvider`.  
     - `llamacpp-jni` → `LlamaCppJniAiGenerationProvider`.  
     - `openai-compatible` → `OpenAiCompatibleAiGenerationProvider`.  
  4. Throw `IllegalArgumentException` for unknown keys.

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
- `IllegalArgumentException` if provider key is unrecognised.

#### Concurrency
- Stateless factory; thread‑safe.  
- `Java8CompatibilityHelper` is immutable.
