### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:53:23Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel`.
- Parses chat responses into completions and timing metrics.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- Lombok `@ToString` (excludes native handle).

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume `AiGenerationRequest`.
- `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns a `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt throughput, predicted tokens, predicted throughput).
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy initialization of `LlamaModel` in `model()`: constructs `ModelParameters` with config options (context, threads, SWA, cache reuse, GPU layers, MoE/FFN CPU layers, KV limits, lazy mode, flash attention, cache quantization, batch sizes, GPU selection, device list).
- `generate`: runs `chatResponse`, warns on truncation, logs cache reuse, parses completion text.
- `chatResponse`: calls `model().chatComplete` with inference parameters from `support`, parses full JSON via `ChatResponseParser`.
- `generateWithTimings`: obtains `ChatResponse`, extracts text and `Timings`, constructs `AiGenerationTimings` with prompt/cache/predicted metrics.
- `close`: closes native `LlamaModel` if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text completion.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `model()` may throw runtime exceptions from native binding if parameters invalid.
- `close` handles null safely.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization. The native `LlamaModel` is reused across calls but not thread‑safe by design.
