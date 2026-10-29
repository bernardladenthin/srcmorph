### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:13:08Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Supplies AI generation for source‑morph requests.
- Handles model loading, inference, and timing extraction.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- Lombok `@ToString` (excludes native handle).

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)`
- `generateWithTimings(AiGenerationRequest request)`
- `close()`

#### Output
- `generate` returns generated text `String`.
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy‑load `LlamaModel` on first `generate`/`chatResponse` call.
- Build `ModelParameters` from `config` (context, threads, GPU layers, KV cache, flash‑attn, quantization, batch sizes, device selection).
- Use `LlamaCppJniProviderSupport` to build inference parameters and chat template kwargs.
- Execute `model.chatComplete(...)` and parse full JSON via `ChatResponseParser`.
- Extract first content for completion text; log truncation and cache reuse.
- For timings, parse `Timings` from response; provide defaults if missing.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with metrics.
- `close() -> void` – release native resources.

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
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `IOException` (though underlying chat path throws unchecked).
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization. The native `LlamaModel` is reused across calls.
