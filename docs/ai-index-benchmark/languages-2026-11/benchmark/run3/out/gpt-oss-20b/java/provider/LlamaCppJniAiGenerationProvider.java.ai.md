### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:04:28Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Supplies AI text generation for `AiGenerationProvider`.
- Handles model loading, inference, and timing extraction.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Extends `Object`; no inheritance.  
- Implements `AiGenerationProvider`, `AutoCloseable`.  
- Lombok `@ToString` with exclusions.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.  
- Internal `chatResponse(AiGenerationRequest request)` uses `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns generated text `String`.  
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.  
- `close` releases native `LlamaModel` resources.

#### Core logic
- Lazy‑load `LlamaModel` on first `generate`/`chatResponse` call.  
- Build `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.  
- Configure GPU, KV cache, flash attention, quantization, batch sizes, device selection, etc.  
- Execute `model().chatComplete(...)` with inference parameters.  
- Parse full JSON response via `ChatResponseParser`.  
- Extract first content, warn on truncation, log cache reuse.  
- Parse completion text with `AiCompletionParser`.  
- For timings, read `Timings` from `ChatResponse` and construct `AiGenerationTimings`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text with metrics.  
- `close() -> void` – release native model.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.  
- Lombok `@ToString`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `chatResponse` and `generateWithTimings` rely on unchecked exceptions from native calls.  
- `close` silently ignores null model.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- `model` field is lazily initialized without double‑checked locking; safe if provider is not shared concurrently.
