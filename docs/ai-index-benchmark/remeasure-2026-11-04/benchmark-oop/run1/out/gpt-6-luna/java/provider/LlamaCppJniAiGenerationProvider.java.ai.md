### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:54Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Locally generates AI completions from GGUF models through llama.cpp, with configurable hardware, cache, and throughput reporting.

#### Purpose
- Provides AI text generation using the `net.ladenthin:llama` JNI binding and rendered request prompts.
- Loads the native model only when generation first occurs; reports model timing and prompt-cache metrics.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`; annotated `@ToString`.
- No generics.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both required non-null.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume generation requests.
- Configuration supplies model path, context and thread counts, reasoning format behavior, GPU/CPU placement, KV cache, batching, lazy mode, Flash Attention, cache types, and device selection.
- `AiPromptSupport` builds chat-template arguments and inference parameters.

#### Output
- `generate(...)` returns parsed completion text.
- `generateWithTimings(...)` returns `AiGenerationTimings` containing text, prompt/prediction token counts, cache-hit tokens, and throughput; missing timing data yields zero metrics.
- Lazily creates and caches a `LlamaModel`; `close()` closes it if initialized.
- Lombok-generated `toString()` omits the native model handle and provider support helper.

#### Core logic
- Constructor validates `config` and `promptSupport`, then creates `LlamaCppJniProviderSupport`.
- `model()` initializes the native model on first use:
  - Builds chat-template kwargs and `ModelParameters` with model path, context size, threads, and automatic reasoning parsing.
  - Applies configured SWA, cache reuse, GPU layers, CPU MoE/FFN layers, per-slot KV limit, lazy mode, Flash Attention, KV cache types, batch sizes, batch threads, main GPU, and explicit devices, subject to each option’s guard.
  - Stores the resulting `LlamaModel` for subsequent requests.
- `chatResponse()` builds inference parameters, runs `chatComplete`, and parses the full response.
- `generate()` warns about truncated answers, logs prompt-cache reuse, then parses the first content.
- `generateWithTimings()` extracts completion text and timing metrics; returns zero rates and counts if timings are absent.
- `close()` closes the model only if it was initialized.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` → provider initialization.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and timing metrics.
- `close() -> void` — release initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiPromptSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`, `AiCompletionParser`
- `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `@ToString`, JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config` and `promptSupport`.
- `generate(...)` declares `IOException`; `generateWithTimings(...)` does not.
- Invalid model parameters or native inference failures may propagate from the llama binding; no local handling is shown.
- Missing response timings are handled by returning zero-valued metrics.

#### Concurrency
- Lazy `model` initialization and cached model access are unsynchronized; concurrent first calls may race.
- No synchronization is shown around inference or close.
