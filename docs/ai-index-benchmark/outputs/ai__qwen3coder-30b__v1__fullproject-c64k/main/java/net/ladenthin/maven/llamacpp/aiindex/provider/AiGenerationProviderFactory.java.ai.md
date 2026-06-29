### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:01:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI model provider implementation based on a configuration key.

#### Purpose
- Instantiates AI generation providers dynamically.
- Maps provider names to concrete implementations.

#### Type
class public final  
Implements: none  
Generics: none  
Annotations: @ToString

#### Input
- Constructor: none  
- Method create: providerName (String), llamaConfig (LlamaCppJniConfig), promptSupport (AiPromptSupport)

#### Output
- Return type: AiGenerationProvider  
- Side effects: none  
- Mutated fields: none

#### Core logic
- Checks if providerName is null or blank, defaults to MockAiGenerationProvider.  
- Matches providerName against known keys ("mock", "llamacpp-jni").  
- Throws IllegalArgumentException for unrecognized provider names.

#### Public API
create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider  
Instantiates AI provider by name

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport  
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper  
- net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider  
- net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniAiGenerationProvider  
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider  
- net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig

#### Exceptions / Errors
Throws IllegalArgumentException for unsupported provider names.  
Handles null or blank providerName by defaulting to MockAiGenerationProvider.

#### Concurrency
None noted. Class is stateless and immutable.
