### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:52:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI text from a document request using configurable sampling parameters

#### Purpose
- Enables pluggable AI backends (e.g., llama.cpp or mock providers) to produce text from document prompts  
- Supports per-call temperature overrides to avoid early termination and empty responses  

#### Type
- interface  
- extends AutoCloseable  
- key generics: none  
- notable annotations: none  

#### Input
- AiGenerationRequest containing: prompt key, source file, source text, current header  
- temperatureOverride (float) for override-based generation  

#### Output
- String with generated text; never null, may be blank if no tokens produced  

#### Core logic
- Uses default sampling parameters when no temperature override is provided  
- On temperature override, temporarily overrides provider’s internal temperature setting  
- Delegates to base generate() method in default implementation  
- Handles I/O errors from underlying AI provider  

#### Public API
- generate(AiGenerationRequest) → String (generates text with default settings)  
- generate(AiGenerationRequest, float) → String (overrides temperature for retry resilience)  

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest  

#### Exceptions / Errors
- Throws IOException if provider fails during generation or I/O operations  
- No null inputs; request is guaranteed to be non-null  

#### Concurrency
- Not thread-safe; no synchronization or concurrency handling  
- Designed for single-threaded AI generation calls
