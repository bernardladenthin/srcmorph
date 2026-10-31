### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:01:49Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend for generating text from source morphing requests, supporting local or mock implementations.

#### Purpose
- Defines the contract for AI text generation providers in the srcmorph tool.
- Supports default timing measurement for calibration goals.

#### Type
- Interface named `AiGenerationProvider` in package `net.ladenthin.srcmorph.provider`.
- Modifiers: `public`.
- Extends `AutoCloseable`.
- No generics or type bounds.
- No notable annotations.

#### Input
- Method parameter `request` of type `AiGenerationRequest` containing prompt key, source file, source text, and current header.
- No constructor parameters (interface).
- No injected dependencies or consumed fields.

#### Output
- Method `generate` returns `String` (generated text, never null, may be blank).
- Method `generateWithTimings` returns `AiGenerationTimings` (generated text plus timing, rates 0 when unavailable).
- Method `close` returns `void` (no-op default implementation).
- No mutated fields (interface).

#### Core logic
- `generate` produces text for an `AiGenerationRequest` using provider-specific logic.
- `generateWithTimings` delegates to `generate` and wraps the result in `AiGenerationTimings` with zero timing values by default.
- `close` provides a no-op default implementation for resource cleanup.

#### Public API
- `generate(AiGenerationRequest) -> String` generates text for a request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` generates text with timing info.
- `close() -> void` closes the provider (no-op default).

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (referenced in default method return type and instantiation)
- `java.lang.AutoCloseable`

#### Exceptions / Errors
- `generate` throws `IOException` if the underlying provider fails.
- `generateWithTimings` throws `IOException` if the underlying provider fails.
- `close` throws `IOException` (declared via `AutoCloseable`, default implementation does not throw).
- `generate` return is never null but may be blank.

#### Concurrency
- No threading, synchronization, or thread-safety notes specified.
- Interface with default methods; implementors determine concurrency behavior.
