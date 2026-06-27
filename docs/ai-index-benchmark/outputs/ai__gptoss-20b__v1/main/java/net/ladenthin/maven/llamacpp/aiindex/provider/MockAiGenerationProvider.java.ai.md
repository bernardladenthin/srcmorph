### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:28:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides deterministic AI summary mock for testing purposes.

#### Purpose
- Supplies a mock AiGenerationProvider for unit tests.  
- Returns a predictable summary string for a given source file.  

#### Type
- `class MockAiGenerationProvider`  
- `public`  
- implements `AiGenerationProvider`  
- annotated with `@ToString` (Lombok)  

#### Input
- `AiGenerationRequest request`  
- `request.sourceFile()` yields a `Path`  

#### Output
- `String` in format `"Mock summary for <fileName>"`  
- May throw `IOException`  

#### Core logic
- Retrieve source file path from request.  
- Resolve file name: `file.getFileName()` or fallback to full path string.  
- Concatenate prefix with file name and return.  

#### Public API
- `generate(AiGenerationRequest) -> String` – returns deterministic mock summary.  

#### Dependencies
- `java.io.IOException`  
- `java.nio.file.Path`  
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider`  

#### Exceptions / Errors
- Declares `throws IOException`; no internal error handling.  
- Handles null `fileNamePath` with fallback to full path string.  

#### Concurrency
- No synchronization; class is stateless and thread‑safe.
