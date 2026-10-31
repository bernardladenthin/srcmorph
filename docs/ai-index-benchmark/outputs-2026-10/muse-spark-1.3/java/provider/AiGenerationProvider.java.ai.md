### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:14:57Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend generating header text for source files for local or mock models.
#### Purpose
- Defines AI backend contract producing text for AiGenerationRequest.
- Supports timing-aware calibration via optional generateWithTimings override.
#### Type
- interface AiGenerationProvider extends AutoCloseable.
#### Input
- AiGenerationRequest request: prompt key, source file, source text, current header.
#### Output
- String generated text: never null, may be blank.
- AiGenerationTimings generated text plus timing: rates 0 when unavailable.
- No state mutation; close releases provider resources.
#### Core logic
- generate produces text using provider default sampling parameters.
- generateWithTimings default delegates to generate with zero timings.
- close default no-op for providers without resources.
- Implementations run locally (llama.cpp) or mock providers for tests.
#### Public API
- generate(AiGenerationRequest) -> String : generates text with default sampling.
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings : generates text with measured timing.
- close() -> void : releases underlying provider resources.
#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- AiGenerationTimings
- AutoCloseable
#### Exceptions / Errors
- IOException on underlying provider failure.
- Null return prohibited; blank output allowed when no tokens produced.
