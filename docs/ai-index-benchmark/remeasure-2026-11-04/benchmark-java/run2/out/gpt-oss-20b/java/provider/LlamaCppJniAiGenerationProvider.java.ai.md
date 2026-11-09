### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:18Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides local GGUF model inference via llama.cpp for AI text generation.

#### Purpose
- Implements `AiGenerationProvider` using native llama.cpp.
- Lazily loads and caches a `LlamaModel` for repeated requests.
- Parses chat responses and exposes timings.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Lombok `@ToString` (excludes native handle).  
- Fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` consume request data.  
- `model()` builds `ModelParameters` from `config` and `support`.

#### Output
- `generate` returns generated text string.  
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.  
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy initialization of `LlamaModel` in `model()`: builds `ModelParameters` with config options (context, threads, GPU layers, KV cache, flash attention, quantization, batch sizes, device selection).  
- `generate`: runs `chatResponse`, warns on truncation, logs cache reuse, parses completion text.  
- `chatResponse`: calls `model().chatComplete` with inference parameters from `support`, parses JSON via `ChatResponseParser`.  
- `generateWithTimings`: obtains `ChatResponse`, extracts text and `Timings`, constructs `AiGenerationTimings` with prompt/cache/predicted metrics.  
- `close`: closes native model if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, Lombok `@ToString`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- `model` is lazily initialized without double‑checked locking; safe in single‑threaded context.
