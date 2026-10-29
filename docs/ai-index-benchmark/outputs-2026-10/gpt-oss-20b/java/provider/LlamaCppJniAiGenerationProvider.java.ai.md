### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:49:47Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local inference of chat completions using llama.cpp via a JNI binding, exposing a simple AI generation API.

#### Purpose
- Implements `AiGenerationProvider` for local GGUF model inference.
- Lazily loads and caches a `LlamaModel` instance.
- Parses chat responses and exposes timings.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- Lombok `@ToString` (excludes native handle).

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)`.

#### Output
- `generate` returns a `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.
- `close` releases native resources.

#### Core logic
- **Lazy model loading**: `model()` builds `ModelParameters` from config, sets GPU/CPU layers, KV cache, flash‑attn, quantization, batch sizes, device selection, then creates `LlamaModel`.
- **Chat completion**: `chatResponse` calls `model().chatComplete` with inference parameters from `support`, parses full JSON via `ChatResponseParser`.
- **Completion extraction**: `generate` parses first content of `ChatResponse` with `AiCompletionParser`, logs truncation and cache reuse.
- **Timings extraction**: `generateWithTimings` obtains `Timings` from `ChatResponse`, constructs `AiGenerationTimings` with prompt/predicted token counts and rates.
- **Resource cleanup**: `close` calls `LlamaModel.close()` if loaded.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce completion with metrics.
- `close() -> void` – release native model.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.*` (FlashAttn, ReasoningFormat)
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`, `Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `generateWithTimings` handles missing `Timings` by returning zeroed rates.
- `close` silently ignores if model not loaded.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization. The native `LlamaModel` is reused across calls.
