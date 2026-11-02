### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:33Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides local GGUF model inference via llama.cpp for AI text generation requests.

#### Purpose
- Implements `AiGenerationProvider` using native llama.cpp.
- Lazily loads and caches a `LlamaModel` for repeated generation.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider, AutoCloseable`; Lombok `@ToString` with exclusions.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume `AiGenerationRequest`.
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns generated text `String`.
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support.buildChatTemplateKwargs()`.
- Configures GPU layers, CPU MoE/FFN layers, KV cache settings, flash attention, quantization, batch sizes, device selection.
- `generate` runs `chatResponse`, warns on truncation, logs cache reuse, parses completion text.
- `chatResponse` calls `model().chatComplete` with inference parameters, parses full JSON via `ChatResponseParser`.
- `generateWithTimings` extracts timings from `ChatResponse` and builds `AiGenerationTimings`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text with metrics.
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
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `model()` may throw runtime exceptions if native binding fails.
- `close` silently ignores null model.

#### Concurrency
- Single‑threaded usage implied; `model` is lazily initialized without synchronization, assuming single‑threaded construction. No explicit thread‑safety guarantees.
