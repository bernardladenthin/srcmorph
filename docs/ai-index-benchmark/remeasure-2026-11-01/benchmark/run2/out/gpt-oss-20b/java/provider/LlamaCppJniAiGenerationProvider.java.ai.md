### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into plain text or timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`; Lombok `@ToString` with exclusions.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` receive a request containing prompt and generation parameters.
- Internal `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns a `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.
- `close` releases native `LlamaModel` resources.

#### Core logic
- Lazy initialization of `LlamaModel` in `model()`: constructs `ModelParameters` with config options (context, threads, GPU layers, KV cache, flash attention, quantization, batch sizes, device selection).
- `generate`: runs `chatResponse`, warns on truncation, logs cache reuse, parses first content via `AiCompletionParser`.
- `chatResponse`: calls `model().chatComplete` with inference parameters from `support`, parses JSON via `ChatResponseParser`.
- `generateWithTimings`: obtains `ChatResponse`, extracts text and `Timings`; if missing, returns zeroed metrics; otherwise returns full timing data.
- `close`: closes native model if present.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`, `Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying chat path throws unchecked exceptions).
- `model()` may throw runtime exceptions if native binding fails.
- `close` handles null safely.

#### Concurrency
- Single‑threaded usage implied; `model` is lazily initialized without synchronization, assuming single‑threaded construction or external guarding. No explicit thread‑safety mechanisms.
