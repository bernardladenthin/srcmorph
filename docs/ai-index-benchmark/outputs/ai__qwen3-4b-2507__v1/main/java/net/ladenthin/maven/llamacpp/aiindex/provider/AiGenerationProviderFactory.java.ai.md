### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:53:03Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider based on configuration name

#### Purpose
- Creates and returns an AI generation provider instance by provider name  
- Supports fallback to mock provider when name is missing or invalid  

#### Type
- class: public, final (not explicitly marked), non-sealed  
- implements: none  
- extends: none  
- generics: none  
- annotations: @ToString  

#### Input
- providerName: String, provider key (e.g. "mock", "llamacpp-jni")  
- llamaConfig: LlamaCppJniConfig, configuration for JNI-based provider  
- promptSupport: AiPromptSupport, prompt handling support for providers that need it  

#### Output
- AiGenerationProvider instance: new provider object based on providerName  

#### Core logic
- Returns mock provider if providerName is null or blank  
- Uses switch statement to select provider by name: "mock" → MockAiGenerationProvider; "llamacpp-jni" → LlamaCppJniAiGenerationProvider  
- Throws IllegalArgumentException for unrecognized provider names  

#### Public API
- create(String, LlamaCppJniConfig, AiPromptSupport) → AiGenerationProvider (instantiates provider by name)  

#### Dependencies
- Java8CompatibilityHelper  
- AiPromptSupport  
- LlamaCppJniConfig  
- MockAiGenerationProvider  
- LlamaCppJniAiGenerationProvider  

#### Exceptions / Errors
- IllegalArgumentException if providerName is unknown or unsupported  

#### Concurrency
- No thread safety concerns; stateless, single-threaded creation logic
