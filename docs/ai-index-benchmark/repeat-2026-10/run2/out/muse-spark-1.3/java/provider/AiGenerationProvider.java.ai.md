### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:30Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that generates text for source-file header requests.

#### Purpose
- Defines pluggable AI backend contract for AiGenerationRequest text generation.
- Supports local (llama.cpp) or mock test providers with optional timing calibration.

#### Type
- interface AiGenerationProvider extends AutoCloseable.

#### Input
- AiGenerationRequest request: prompt key, source file, source text, current header.
- No injected dependencies, fields, or read resources.

#### Output
- String generated text: never null, may be blank if no tokens.
- AiGenerationTimings generated text plus timing for srcmorph:calibrate goal.
- No mutated state; close() performs no-op by default.

#### Core logic
- generate delegates text production to provider-specific backend with default sampling parameters.
- generateWithTimings delegates to generate and reports zero rates when timings unavailable.
- close provides default no-op AutoCloseable cleanup for providers without resources.

#### Public API
- generate(AiGenerationRequest) -> String: generates text with default sampling.
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: generates text with measured timing.
- close() -> void: releases provider resources (no-op).

#### Dependencies
- java.io.IOException.
- net.ladenthin.srcmorph.document.AiGenerationRequest.
- AiGenerationTimings.
- AutoCloseable.

#### Exceptions / Errors
- IOException if underlying provider fails on generate, generateWithTimings, close.
- Null-handling: generate return never null; generateWithTimings rates 0 when unavailable.
