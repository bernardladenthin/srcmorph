### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:21:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider based on a name

#### Purpose  
- Creates and returns an AI generation provider instance by provider name  
- Falls back to mock provider if name is missing or invalid  

#### Type  
class public net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory

#### Input  
- providerName (String): key for provider selection; defaults to "mock" if null/blank  
- llamaConfig (LlamaCppJniConfig): config for llama.cpp JNI provider  
- promptSupport (AiPromptSupport): prompt lookup support for providers needing it  

#### Output  
- AiGenerationProvider: a newly instantiated provider instance  

#### Core logic  
- Returns mock provider if providerName is null or blank  
- Uses switch to select provider based on providerName  
- Returns LlamaCppJniAiGenerationProvider for "llamacpp-jni"  
- Throws IllegalArgumentException for unrecognized provider names  

#### Public API  
create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider: instantiates provider by name  

#### Dependencies  
LlamaCppJniConfig, AiPromptSupport, MockAiGenerationProvider, LlamaCppJniAiGenerationProvider

#### Exceptions / Errors  
- IllegalArgumentException when providerName is unrecognized  

#### Concurrency  
none
