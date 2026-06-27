### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:46:14Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Interface for pluggable AI backend that produces text for generation requests

#### Purpose
- Defines an interface for AI text generation providers
- Allows for local or mock implementations

#### Type
interface + public; implements AutoCloseable

#### Input
AiGenerationRequest object with prompt key, source file, source text, and current header

#### Output
String containing generated text; may be empty but never null; throws IOException on failure

#### Core logic
- Generate text based on AiGenerationRequest
- Allow temperature override for retry attempts
- Provide close method for resource management

#### Public API
generate(AiGenerationRequest) -> String
generate(AiGenerationRequest, float) -> String
close() -> void

#### Dependencies
AiGenerationRequest
IOException

#### Exceptions / Errors
Throws IOException if underlying provider fails
