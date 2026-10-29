### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:02:57Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI text generation backends, supporting local models and test mocks.

#### Purpose
* Defines the contract for AI generation backends producing text from requests.
* Supports local execution (e.g., llama.cpp) and mock implementations for testing.

#### Type
Interface `AiGenerationProvider` extends `AutoCloseable`.

#### Input
* `AiGenerationRequest` containing prompt key, source file, source text, and current header.

#### Output
* `String` generated text (never null, may be blank).
* `AiGenerationTimings` object containing timing metrics when `generateWithTimings` is used.
* Side effect: resource closure via `close()`.

#### Core logic
* `generate()` invokes the provider's default sampling parameters to produce text.
* `generateWithTimings()` delegates to `generate()` and constructs timing data with zero rates by default.
* `close()` releases underlying resources or performs cleanup.

#### Public API
* `generate(AiGenerationRequest request) -> String` (produces text)
* `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` (produces text + timing)
* `close()` (closes resources)

#### Dependencies
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `AiGenerationTimings` (defined in same package)

#### Exceptions / Errors
* `IOException` thrown if the underlying provider fails.
* Null handling: `generate()` guarantees non-null return, though content may be blank.

#### Concurrency
* No explicit concurrency notes; assumes single-threaded execution per request.
