### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:21:56Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend generating header text for source files from prompts and existing content.
#### Purpose
- Defines AI text-generation contract for srcmorph header generation.
- Supports calibration via timed generation and clean shutdown.
#### Type
- interface AiGenerationProvider extends AutoCloseable.
#### Input
- AiGenerationRequest request: prompt key, source file, source text, current header.
#### Output
- String generated text: never null, may be blank.
- AiGenerationTimings text plus timing: rates 0 when unavailable.
#### Core logic
- generate produces text using provider default sampling parameters.
- generateWithTimings delegates to generate, returns zeroed timings by default.
- Local llama.cpp and mock test providers implement generate override.
- close releases provider resources, no-op by default.
#### Public API
- generate(AiGenerationRequest) -> String: generates text with default sampling.
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: generates text plus measured timing.
- close() -> void: releases underlying provider resources.
#### Dependencies
- java.io.IOException, AutoCloseable.
- net.ladenthin.srcmorph.document.AiGenerationRequest, AiGenerationTimings.
#### Exceptions / Errors
- IOException on underlying provider failure.
- generate returns blank string if model produced no tokens.
- generateWithTimings reports 0 rates when timings unavailable.
