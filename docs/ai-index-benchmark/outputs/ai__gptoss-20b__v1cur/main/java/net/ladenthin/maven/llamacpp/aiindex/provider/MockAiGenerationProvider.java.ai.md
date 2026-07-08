### MockAiGenerationProvider.java
- H: 1.0
- C: CEF2EE28
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:18:43Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 2; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 4

> Provides deterministic AI summary generation for testing, returning a mock summary string and synthetic timing metrics.  

#### Purpose
- Supplies a mock implementation of `AiGenerationProvider` for unit tests.  
- Returns deterministic summaries and timing data without invoking an actual model.  

#### Type
- Class `MockAiGenerationProvider`  
- Implements `AiGenerationProvider`  
- Annotated with `@ToString`  

#### Input
- `AiGenerationRequest request` (source file path and source text).  

#### Output
- `String` mock summary: `"Mock summary for <fileName>"`.  
- `AiGenerationTimings` with synthetic token counts and throughputs.  

#### Core logic
- Extract file name from `request.sourceFile()`; fallback to file path if absent.  
- Construct summary string using the file name.  
- Compute prompt token count: `request.sourceText().length() / MOCK_CHARS_PER_TOKEN`.  
- Create `AiGenerationTimings` with:  
  - summary string, prompt token count, prefill throughput, predicted token count, decode throughput.  

#### Public API
- `MockAiGenerationProvider()` → constructs instance.  
- `generate(AiGenerationRequest)` → returns mock summary.  
- `generateWithTimings(AiGenerationRequest)` → returns mock timing data.  

#### Dependencies
- `java.io.IOException`  
- `java.nio.file.Path`  
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`  
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider` (interface)  
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationTimings`  

#### Exceptions / Errors
- Throws `IOException` (declared but not actually thrown in current logic).  

#### Concurrency
- Stateless; thread‑safe.  

#### Fields
- `MOCK_PREFILL_TOKENS_PER_SECOND = 1000.0d`  
- `MOCK_DECODE_TOKENS_PER_SECOND = 100.0d`  
- `MOCK_CHARS_PER_TOKEN = 4`  
- `MOCK_PREDICTED_TOKENS = 64`
