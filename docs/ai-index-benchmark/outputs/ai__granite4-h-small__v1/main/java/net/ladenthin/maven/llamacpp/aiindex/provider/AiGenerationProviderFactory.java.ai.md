### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T22:07:09Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 3; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 1

> Instantiates AI generation providers based on a string key, defaulting to a mock implementation.

#### Purpose
- Supplies an `AiGenerationProvider` instance for a given provider name.  
- Handles defaulting, validation, and provider-specific construction.

#### Type
- Class, final (implicitly), no inheritance.  
- Annotated with `@ToString`.  
- Declares a private final field `compatibilityHelper` of type `Java8CompatibilityHelper`.

#### Input
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: key string (`"mock"` or `"llamacpp-jni"`).  
  - `llamaConfig`: configuration for the JNI provider.  
  - `promptSupport`: lookup support for providers that need it.

#### Output
- Returns a new `AiGenerationProvider` instance.  
- Throws `IllegalArgumentException` for unsupported provider names.

#### Core logic
- If `providerName` is null or blank (checked via `compatibilityHelper.isBlank`), return `MockAiGenerationProvider`.  
- Switch on `providerName`:  
  - `"mock"` → `MockAiGenerationProvider`.  
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
  - Default → throw `IllegalArgumentException("Unsupported AI provider: " + providerName)`.

#### Public API
- `AiGenerationProviderFactory()` → constructor, no parameters.  
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` – builds provider by name.

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`  
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniAiGenerationProvider`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig` (assumed from context)

#### Exceptions / Errors
- Throws `IllegalArgumentException` when `providerName` is unrecognized.

#### Concurrency
- No explicit synchronization; instances are stateless except for the single helper field.  
- Thread‑safe by default.
