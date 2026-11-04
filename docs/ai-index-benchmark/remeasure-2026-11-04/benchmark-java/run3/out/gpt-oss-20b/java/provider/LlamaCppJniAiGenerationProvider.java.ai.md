### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:13Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides local GGUF model inference via llama.cpp for AI text generation.

#### Purpose
- Implements `AiGenerationProvider` using native llama.cpp.
- Lazily loads and caches a `LlamaModel` for repeated requests.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`; Lombok `@ToString` with exclusions.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` receive request objects.
- `close()` consumes internal model.

#### Output
- `generate` returns generated text `String`.
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.
- `close` releases native resources.

#### Core logic
- `model()` lazily builds `LlamaModel` with `ModelParameters` derived from `config` and `support`.
- `generate`:
  1. Calls `chatResponse` to run chat completion.
  2. Warns on truncated answers.
  3. Logs prompt cache reuse.
  4. Parses completion text.
- `chatResponse`:
  - Builds inference parameters via `support`.
  - Calls `model().chatComplete(...)` and parses JSON into `ChatResponse`.
- `generateWithTimings`:
  - Calls `chatResponse`.
  - Extracts text and `Timings`; if absent, returns zeros.
  - Returns `AiGenerationTimings` with prompt, cache, predicted counts and rates.
- `close` disposes native `LlamaModel` if loaded.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with metrics.
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`
- `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `generateWithTimings` handles missing `Timings` by returning zeroed metrics.
- `close` silently ignores if model is null.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization. The native `LlamaModel` is cached and reused across calls.
