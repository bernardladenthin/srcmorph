### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 3EAED1F5
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:08:54Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 3; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 9

> Provides local AI generation by loading a GGUF model via llama.cpp and generating text or timing information from an `AiGenerationRequest`.

#### Purpose
- Implements `AiGenerationProvider` for local generation.
- Offers generation with or without timing data.

#### Type
- Final class `LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`.
- Extends `Object`; no generics.
- Annotated with Lombok `@ToString`.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume an `AiGenerationRequest` (prompt key, source file, source text).
- Reads configuration fields (temperature, max tokens, etc.) and prompt templates via `promptSupport`.

#### Output
- `generate` returns a `String` (completion text).
- `generateWithTimings` returns `AiGenerationTimings` (text plus token counts and rates).
- `close` releases the native `LlamaModel` resource.

#### Core logic
- Lazy‑loads `LlamaModel` on first generation; configures model parameters (model path, context, threads, GPU options, SWA, cache reuse, reasoning format, chat‑template kwargs).
- Builds `InferenceParameters` from request and config: system prompt, user message, chat template, temperature, token limits, sampling, repetition penalties, dry settings, stop strings, cache prompt, reuse slot.
- Executes `model().chatCompleteText` or `model().chatComplete` and parses the response via `AiCompletionParser` or `ChatResponseParser`.
- Extracts timings from the parsed `ChatResponse` and creates `AiGenerationTimings`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produces completion text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produces completion text with timing metrics.
- `close() -> void` – releases native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `InferenceParameters`, `ModelParameters`, `ReasoningFormat`, `ChatResponseParser`, `AiCompletionParser`.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`.
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`.
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`.
- `java.util` collections, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `IOException` (though underlying calls throw unchecked exceptions).
- `generateWithTimings` and other methods propagate unchecked runtime exceptions from the native binding.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization. The native `LlamaModel` is reused across calls.
