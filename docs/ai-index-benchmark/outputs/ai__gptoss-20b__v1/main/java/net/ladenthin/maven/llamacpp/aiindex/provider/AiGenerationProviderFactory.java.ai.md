### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:20:57Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Creates an AI generation provider instance based on a supplied provider key.  

#### Purpose
- Factory for `AiGenerationProvider` implementations.  

#### Type
- `class AiGenerationProviderFactory`  
- `@ToString` via Lombok.  
- No inheritance or interfaces.  

#### Input
- Constructor: none.  
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`  
  - `providerName`: key, may be null or blank.  
  - `llamaConfig`: config for JNI provider.  
  - `promptSupport`: prompt lookup support.  

#### Output
- Returns a new `AiGenerationProvider` instance.  
- Throws `IllegalArgumentException` for unknown providerName.  

#### Core logic
- If `providerName` is null or blank → return `MockAiGenerationProvider`.  
- `switch` on `providerName`:  
  - `"mock"` → `MockAiGenerationProvider`.  
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
  - default → throw `IllegalArgumentException`.  

#### Public API
- `AiGenerationProviderFactory()` – no‑op constructor.  
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` – selects provider.  

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`  
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniAiGenerationProvider`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider`  

#### Exceptions / Errors
- `IllegalArgumentException` – “Unsupported AI provider: <name>”.  

#### Concurrency
- No thread‑safety concerns; instance field is immutable helper.
