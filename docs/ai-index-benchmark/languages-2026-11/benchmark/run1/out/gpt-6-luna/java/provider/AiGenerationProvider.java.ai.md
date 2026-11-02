### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:34Z
- G: 
- A: 0.0.0
- X: file
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines a pluggable AI backend for generating text from `AiGenerationRequest`.
- Supports local or test/mock providers and optional generation timing.

#### Type
- `AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- Returns generated text; the documented contract says it is never `null`, but may be blank.
- `generateWithTimings` returns `AiGenerationTimings`; its default reports zero rates and timings.

#### Core logic
- Delegates text generation to provider implementations through `generate`.
- Default `generateWithTimings` calls `generate` and wraps the text with zero timing values.
- Default `close` performs no action.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text using default sampling.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings` (referenced return and constructor type)

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `IOException` for provider failures.
- `close` declares `IOException`.
