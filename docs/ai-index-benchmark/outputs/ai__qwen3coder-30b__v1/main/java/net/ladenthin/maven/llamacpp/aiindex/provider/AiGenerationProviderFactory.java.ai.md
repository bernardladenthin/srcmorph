### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:50:26Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI text generation provider implementation based on a name identifier.

#### Purpose
- Instantiates AI generation providers
- Maps provider names to concrete implementations

#### Type
class public final
extends Object
implements no interfaces
generics no type bounds
annotations @ToString

#### Input
- Constructor: no parameters
- Method create: providerName (String), llamaConfig (LlamaCppJniConfig), promptSupport (AiPromptSupport)

#### Output
- Return type: AiGenerationProvider
- Side effects: none
- State mutation: none

#### Core logic
- Checks if providerName is null or blank, returns MockAiGenerationProvider
- Uses switch statement to match providerName against "mock" or "llamacpp-jni"
- Throws IllegalArgumentException for unrecognized provider names

#### Public API
create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider selects and creates AI provider instance

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniAiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig

#### Exceptions / Errors
- IllegalArgumentException thrown when providerName is not recognized
- Null handling for providerName parameter

#### Concurrency
No concurrency considerations noted
