### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:56:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a deterministic mock summary for AI generation testing

#### Purpose  
- Generates testable, predictable summaries for AI document processing  
- Replaces real AI generation in unit and integration tests  

#### Type  
class, public, final, implements AiGenerationProvider  

#### Input  
- AiGenerationRequest (sourceFile) → Path object  

#### Output  
- String (mock summary of the input file name)  

#### Core logic  
- Extracts source file name from request  
- Constructs a deterministic mock summary using the filename  
- Returns the summary without external dependencies or side effects  

#### Public API  
- generate(AiGenerationRequest request) → String (creates mock summary from file name)  

#### Dependencies  
AiGenerationRequest, Path, IOException  

#### Exceptions  
- Throws IOException on failure (no actual I/O performed)  

#### Concurrency  
- Immutable state, thread-safe by design — no shared mutable state or synchronization needed
