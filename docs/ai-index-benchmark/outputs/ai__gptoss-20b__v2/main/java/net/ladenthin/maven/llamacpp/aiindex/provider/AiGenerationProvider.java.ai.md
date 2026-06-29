### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:11:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> AiGenerationProvider defines a pluggable backend that generates AI text from an AiGenerationRequest.

#### Purpose
- Pluggable AI backend for generating text from AiGenerationRequest.  
- Supports local llama.cpp or mock providers for testing.

#### Type
- interface AiGenerationProvider extends AutoCloseable.

#### Input
- `generate(AiGenerationRequest request)` – request includes prompt key, source file, source text, and current header.  
- `generate(AiGenerationRequest request, float temperatureOverride)` – same request plus per‑call temperature.

#### Output
- `String` – never `null`, may be blank if no tokens.  
- `void close()` – default no‑op.

#### Core logic
- `generate(request)` must produce text.  
- `generate(request, temperatureOverride)` defaults to delegating to `generate(request)`.  
- `close()` performs no operation.

#### Public API
- `generate(AiGenerationRequest) -> String`: generates text with default sampling parameters.  
- `generate(AiGenerationRequest, float) -> String`: generates text overriding temperature; defaults to `generate(request)`.  
- `close() -> void`: closes the provider (no-op by default).

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest

#### Exceptions / Errors
- Both `generate` methods and `close` declare `throws IOException`.
