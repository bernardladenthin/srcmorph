### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:22Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the `AiGenerationProvider.java` file:

```markdown
## Purpose
- Pluggable AI backend that produces text for an `AiGenerationRequest`.

## Type
- Class: `AiGenerationProvider`
- Extends: `AutoCloseable`
- Implemented by: implementations may run locally (llama.cpp) or be mock providers for tests

## Input
- Constructor and method parameters: `AiGenerationRequest request`
- Consumed fields: none
- Read resources: none

## Output
- Returned types: `String` (generated text)
- Produced state: none
- Mutated fields: none
- Written resources: none
- Side effects: none

## Core Logic
- `generate(AiGenerationRequest request) throws IOException`: Generates text for the given request using the provider's default sampling parameters.
- `generateWithTimings(AiGenerationRequest request) throws IOException`: Generates text and returns it together with the model's measured timing, for the `srcmorph:calibrate` goal.

## Public API
- `generate(AiGenerationRequest request) throws IOException`: Generates text for the given request using the provider's default sampling parameters.
- `generateWithTimings(AiGenerationRequest request) throws IOException`: Generates text and returns it together with the model's measured timing, for the `srcmorph:calibrate` goal.

## Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

## Exceptions / Errors
- `IOException`: Thrown by `generate` and `generateWithTimings` if the underlying provider fails.

## Concurrency
- Not explicitly noted in the source, but as an `interface` it is likely thread-safe if implemented correctly.
```
