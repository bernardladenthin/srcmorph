### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:21Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Generates local AI completions from GGUF models through llama.cpp, with configurable inference, cache reuse, and measured generation timings.

#### Purpose
- Implements local AI generation for `AiGenerationRequest` using the `net.ladenthin:llama` JNI binding.
- Lazily loads and reuses a configured GGUF model; exposes completion text and model-reported timing and cache metrics.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — implements `AiGenerationProvider`, `AutoCloseable`; annotated `@ToString`.
- Fields excluded from Lombok `toString`: native `model` (`@Nullable`), `support`.

#### Input
- Constructor takes `LlamaCppJniConfig config` and `AiPromptSupport promptSupport`; both are required and null-checked.
- `generate` and `generateWithTimings` consume `AiGenerationRequest`.
- Reads model path, context size, thread counts, GPU and CPU layer settings, cache and batching options, lazy mode, Flash Attention, devices, and prompt/inference settings from config and support.

#### Output
- `generate` returns parsed completion text; warns on truncated answers and logs prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/predicted token counts, cache token count, and throughput rates.
- Lazily creates and caches `LlamaModel`; `close` closes it if initialized.

#### Core logic
- Constructor creates `LlamaCppJniProviderSupport` from config and prompt support without loading native model resources.
- On first model access, builds `ModelParameters` with model path, context size, threads, automatic reasoning format, and chat-template kwargs.
- Applies optional parameters only under their respective guards: SWA full, positive cache reuse, nonnegative GPU/CPU layer counts and main GPU, positive unified KV/batch/ubatch/batch-thread sizes, nonblank lazy mode and cache types, enabled Flash Attention, and nonempty device selection.
- Constructs and stores `LlamaModel`; later calls reuse the cached handle.
- Builds inference parameters through `support`, runs `chatComplete`, and parses the full response.
- `generate` checks truncation and logs cache reuse before parsing first response content.
- `generateWithTimings` returns zero counts/rates if response timings are absent; otherwise maps prompt, cache, predicted token counts and rates into `AiGenerationTimings`.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — creates provider; defers model loading.
- `generate(AiGenerationRequest) -> String` — generate completion text; throws `IOException`.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and model timings.
- `close() -> void` — close initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiPromptSupport`, `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not.
- `close` is a no-op when the model was never initialized.

#### Concurrency
- Model initialization uses an unsynchronized nullable field; concurrent first calls are not guarded against duplicate initialization.
