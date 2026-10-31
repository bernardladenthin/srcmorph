### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:53:51Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Supplies AI text generation for `AiGenerationProvider`.
- Loads and caches a native `LlamaModel` lazily.
- Parses chat responses into plain text and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Lombok `@ToString` (excludes native handle).  
- Fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)`.  
- Uses `support.buildInferenceParameters(request)` for inference arguments.  
- Reads config values for model path, context size, threads, GPU layers, etc.

#### Output
- `generate` returns a `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt TPS, predicted tokens, predicted TPS).  
- `close` releases native model resources.

#### Core logic
- **Lazy model loading** (`model()`): builds `ModelParameters` from config, sets GPU, CPU, KV, quantization, flash‑attn, batch, device options, then creates `LlamaModel`.  
- **Chat completion** (`chatResponse`): calls `model().chatComplete(...)`, parses JSON via `ChatResponseParser`.  
- **Generate**: obtains `ChatResponse`, warns on truncation, logs cache reuse, extracts first content via `AiCompletionParser`.  
- **Generate with timings**: similar to `generate`, but extracts `Timings` from response and constructs `AiGenerationTimings`.  
- **Close**: calls `model.close()` if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, Lombok `@ToString`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked runtime exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; native `LlamaModel` is not thread‑safe.  
- No explicit synchronization; provider is not thread‑safe.
