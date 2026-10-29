### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:37:06Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that produces text for an AiGenerationRequest.

#### Purpose
- To provide a method for generating text based on an AI generation request.
- To offer an optional method for generating text with timing information.

#### Type
- Interface (`public interface AiGenerationProvider extends AutoCloseable`)
- Implements `AutoCloseable`

#### Input
- `generate(AiGenerationRequest request) throws IOException`
  - Parameter: `request` of type `AiGenerationRequest`
  - Throws: `IOException`

#### Output
- `generate(AiGenerationRequest request) -> String`
  - Returns: generated text as a `String`, never `null`, may be blank
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
  - Returns: `AiGenerationTimings` object containing generated text and timing information
  - Throws: `IOException`

#### Core logic
- The `generate` method takes an `AiGenerationRequest`, processes it, and returns the generated text.
- The `generateWithTimings` method delegates to `generate` and wraps the result in an `AiGenerationTimings` object, adding timing information.

#### Public API
- `generate(AiGenerationRequest request) -> String`
  - Generates text for the given request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
  - Generates text with timing information for the given request.

#### Dependencies
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `java.io.IOException`

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails.

#### Concurrency
- The interface is designed for thread-safe use, assuming implementations handle synchronization appropriately.
