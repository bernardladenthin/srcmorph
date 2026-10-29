### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:12:21Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates the appropriate AI generation provider based on configuration and fallback logic

#### Purpose
- Creates and returns an instance of an AI generation provider based on a provider name
- Resolves provider selection using run-level or model-level configuration with fallback to mock

#### Type
class, public, final, @ToString  
implements none  
extends none  
generics: none  
static fields: PROVIDER_MOCK, PROVIDER_LLAMACPP_JNI, PROVIDER_OPENAI_COMPATIBLE  
private field: compatibilityHelper (Java8CompatibilityHelper)

#### Input
- providerName (String): run-level provider key; defaults to "mock" if null or blank  
- llamaConfig (LlamaCppJniConfig): model configuration containing provider setting  
- promptSupport (AiPromptSupport): prompt lookup support for providers that require it  

#### Output
- AiGenerationProvider instance: mock, llama.cpp JNI, or OpenAI-compatible provider  
- null only if no valid provider is selected (fallback to mock)

#### Core logic
- Uses effectiveProvider() to prioritize model-level provider over run-level  
- If no effective provider is found, returns a new MockAiGenerationProvider  
- Switches on effective provider key to instantiate correct implementation  
- Throws IllegalArgumentException for unrecognized provider names  

#### Public API
- effectiveProvider(String, String) → String (resolves provider priority)  
- create(String, LlamaCppJniConfig, AiPromptSupport) → AiGenerationProvider (instantiates provider)

#### Dependencies
- net.ladenthin.srcmorph.prompt.AiPromptSupport  
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper  
- net.ladenthin.srcmorph.provider.MockAiGenerationProvider  
- net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider  
- net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider  

#### Exceptions / Errors
- IllegalArgumentException if providerName is unrecognized (in create())  

#### Concurrency
- Not thread-safe; no synchronization or shared state  
- Immutable inputs; state is created per invocation
