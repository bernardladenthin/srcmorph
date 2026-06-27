### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:19:37Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a pluggable AI backend that generates text for AiGenerationRequest objects.

#### Purpose
- Supplies a text generation service for Maven llama.cpp AI indexing.
- Supports local or mock implementations.

#### Type
- `interface`  
- `extends AutoCloseable`  
- Default methods for temperature override and close.

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.  
- `generate(AiGenerationRequest request, float temperatureOverride)` – same request plus override temperature.  
- `close()` – no arguments.

#### Output
- `String generate(...)` – generated text, never `null`.  
- `String generate(..., float)` – delegates to default, may ignore temperature.  
- `void close()` – does nothing by default.

#### Core logic
- `generate(AiGenerationRequest)` – must produce text using provider’s default sampling.  
- `generate(..., float)` – default delegates to `generate(request)`.  
- `close()` – default no‑op; can be overridden for resource cleanup.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.  
- `generate(AiGenerationRequest, float) -> String` – generate with temperature override.  
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`

#### Exceptions / Errors
- `throws IOException` if underlying provider fails.  
- Temperature override ignored by default; provider may override.

#### Concurrency
- No synchronization; default implementation stateless.
