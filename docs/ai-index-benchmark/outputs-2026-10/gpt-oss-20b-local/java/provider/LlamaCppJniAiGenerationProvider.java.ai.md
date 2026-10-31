### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:12:35Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into plain text and timing metrics.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- Extends `Object`; no generics.
- Lombok `@ToString` with exclusions for native handle and support.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)` receive `AiGenerationRequest`.
- `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns `String` completion text.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt tokens, cache tokens, prompt/sec, predicted tokens, predicted/sec.
- `close()` releases native `LlamaModel` resources.

#### Core logic
- Lazy initialization of `LlamaModel` in `model()`: constructs `ModelParameters` with numerous config options (context size, threads, SWA, cache reuse, GPU layers, MoE/FFN layers, KV limits, lazy mode, flash attention, cache quantization, batch sizing, GPU selection, device list).
- `generate`: obtains `ChatResponse` via `chatResponse`, warns on truncation, logs cache reuse, parses first content into completion string.
- `chatResponse`: calls `model().chatComplete` with inference parameters from `support.buildInferenceParameters`, parses JSON into `ChatResponse`.
- `generateWithTimings`: similar to `generate` but extracts `Timings` from `ChatResponse` and constructs `AiGenerationTimings`.
- `close`: closes native `LlamaModel` if present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void` – constructs provider.
- `generate(AiGenerationRequest) -> String` – returns completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – returns text with metrics.
- `close() -> void` – releases native resources.

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
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `model()` may throw runtime exceptions from native binding if parameters invalid.
- `close` silently ignores null model.

#### Concurrency
- Instance fields are not thread‑safe; `model()` is lazily initialized without synchronization, so concurrent first calls may load multiple models. Subsequent calls reuse the cached instance. No explicit locking.
