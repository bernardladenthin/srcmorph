### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:38:16Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable AI backend contract for generating text from source-morphing requests, supporting local llama.cpp models, test mocks, and calibration timing.

#### Purpose
- Contract for AI text generation backends in the srcmorph source transformation pipeline
- Enables pluggable implementations: local models (llama.cpp) or mocks for tests

#### Type
- Interface
- extends AutoCloseable
- Package: net.ladenthin.srcmorph.provider

#### Input
- AiGenerationRequest request: carries prompt key, source file, source text, and current header

#### Output
- String from generate(): generated text (never null, may be blank)
- AiGenerationTimings from generateWithTimings(): text plus timing metrics (default rates 0)
- close(): releases provider resources (default no-op side effect)

#### Core logic
- generate() abstract: provider-specific text generation from the request
- generateWithTimings() default: delegates to generate(), wraps result in AiGenerationTimings with zeroed metrics (0, 0, 0.0d, 0, 0.0d) for the srcmorph:calibrate goal
- close() default: no-op override of AutoCloseable.close() narrowing throws to IOException

#### Public API
- generate(AiGenerationRequest request) -> String - core text generation (throws IOException)
- generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings - generation with timing data (throws IOException)
- close() -> void - resource cleanup (throws IOException)

#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.provider.AiGenerationTimings (same package)
- java.lang.AutoCloseable

#### Exceptions / Errors
- IOException: thrown by generate(), generateWithTimings(), and close() when the underlying provider fails
- generate() contract: return value never null, may be blank if model produced no tokens

#### Concurrency
- No synchronization specified; implementations responsible for their own thread-safety
