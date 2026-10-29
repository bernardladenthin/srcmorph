### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:56:50Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text based on a specific generation request.

#### Purpose
* Defines contract for AI text generation providers.
* Allows implementations for local or mock AI services.

#### Type
interface AiGenerationProvider extends AutoCloseable;

#### Input
* AiGenerationRequest request (in generate method).
* AiGenerationRequest request (in generateWithTimings method).

#### Output
* String (generated text) (from generate method).
* AiGenerationTimings (generated text + timing) (from generateWithTimings method).
* void (on close).

#### Core logic
* Implementations must provide text generation via generate(AiGenerationRequest).
* Providers can override generateWithTimings to report model timing metrics.
* Providers must implement AutoCloseable for resource cleanup.

#### Public API
generate(AiGenerationRequest request) -> String: Generates text using default sampling parameters.
generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings: Generates text with model timing metrics.
close() -> void: Releases provider resources.

#### Dependencies
java.io.IOException, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.provider.AiGenerationTimings

#### Exceptions / Errors
IOException (thrown by generate and generateWithTimings).

#### Concurrency
No explicit concurrency mechanisms defined in the interface.
