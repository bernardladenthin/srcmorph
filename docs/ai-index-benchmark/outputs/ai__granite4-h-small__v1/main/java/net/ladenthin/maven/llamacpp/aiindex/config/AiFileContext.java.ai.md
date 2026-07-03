### AiFileContext.java
- H: 1.0
- C: ADE292A5
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T21:50:07Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 5

> Immutable snapshot of file metadata for condition evaluation

#### Purpose
- Holds file facts for AiCondition evaluation.
- Immutable data holder for name, path, size, lines, modified time.

#### Type
- final class AiFileContext  
- @ToString  
- No superclass, no interfaces.

#### Input
- Constructor parameters: `fileName` (String), `relativePath` (String), `sizeBytes` (long), `lineCount` (int), `lastModifiedEpochMilli` (long).

#### Output
- `fileName()` → String  
- `relativePath()` → String  
- `sizeBytes()` → long  
- `lineCount()` → int  
- `lastModifiedEpochMilli()` → long  

#### Core logic
- Store constructor arguments into final fields.  
- Provide getter methods returning those fields.  
- @ToString automatically generates `toString` representation.

#### Public API
- `AiFileContext(String fileName, String relativePath, long sizeBytes, int lineCount, long lastModifiedEpochMilli)` → AiFileContext  (constructor)  
- `fileName()` → String  (returns file name)  
- `relativePath()` → String  (returns relative path)  
- `sizeBytes()` → long  (returns size in bytes)  
- `lineCount()` → int  (returns line count)  
- `lastModifiedEpochMilli()` → long  (returns last-modified epoch milliseconds)

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCondition` (via Javadoc reference)  

#### Exceptions / Errors
- None declared; constructor does not validate inputs.

#### Concurrency
- Immutable; thread‑safe by design.
