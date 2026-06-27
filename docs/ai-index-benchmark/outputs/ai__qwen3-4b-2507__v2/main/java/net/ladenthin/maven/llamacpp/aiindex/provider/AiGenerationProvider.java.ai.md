### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:21:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides text generation for AI requests via pluggable backend implementations

#### Purpose
- Enables local or mock-based AI text generation from source code prompts  
- Supports temperature override during retries to avoid early EOS failures  

#### Type
interface AiGenerationProvider extends AutoCloseable

#### Input
- AiGenerationRequest (prompt key, source file, source text, current header)

#### Output
- String (generated text; never null, may be blank)

#### Core logic
- Generates text using default sampling parameters  
- Overrides temperature per-call to break retry loops that produce empty responses  
- Delegates temperature override to default implementation if not implemented  

#### Public API
generate(AiGenerationRequest) -> String: Generate text with default settings  
generate(AiGenerationRequest, float) -> String: Generate text with overridden temperature  

#### Dependencies
AiGenerationRequest

#### Exceptions / Errors
- IOException on provider failure; no null inputs allowed  

#### Concurrency
none
