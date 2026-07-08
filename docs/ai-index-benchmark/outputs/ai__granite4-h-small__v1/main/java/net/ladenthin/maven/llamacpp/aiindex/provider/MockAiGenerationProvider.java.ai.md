### MockAiGenerationProvider.java
- H: 1.0
- C: CEF2EE28
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:13:31Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 2; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 4

> Provides deterministic AI summaries for test purposes.

#### Purpose
- Supplies mock AI generation results.
- Supplies fixed timing metrics for deterministic tests.

#### Type
- Class `MockAiGenerationProvider`  
  Implements `AiGenerationProvider`  
  Annotated `@ToString`  
  No modifiers.

#### Input
- `AiGenerationRequest request` in `generate` and `generateWithTimings`.  
  Uses `request.sourceFile()` and `request.sourceText()`.

#### Output
- `String` summary in `generate`.  
- `AiGenerationTimings` in `generateWithTimings` containing:  
  - mock summary string  
  - prompt token count  
  - prefill throughput 1000.0  
  - predicted tokens 64  
  - decode throughput 100.0

#### Core logic
- `generate`: Extract file name from `request.sourceFile()`; return `"Mock summary for " + fileName`.
- `generateWithTimings`: Compute `promptTokens` as `request.sourceText().length() / 4`; create new `AiGenerationTimings` with mock data.

#### Public API
- `MockAiGenerationProvider()` → constructor, no side effects.  
- `generate(AiGenerationRequest) -> String` → returns deterministic summary.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` → returns mock timings.

#### Dependencies
- `java.io.IOException`  
- `java.nio.file.Path`  
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`

#### Exceptions / Errors
- Both public methods declare `throws IOException` but never throw.

#### Concurrency
- Stateless; thread‑safe.

---
