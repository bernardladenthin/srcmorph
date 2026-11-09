### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:24Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Generates AI completions from locally loaded GGUF models and reports llama.cpp token, throughput, and prompt-cache metrics.

#### Purpose
- `LlamaCppJniAiGenerationProvider` runs chat inference through the llama.cpp JNI binding for `AiGenerationRequest` inputs.
- Lazily loads and reuses a configured model; parses completions and generation timings.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class implementing `AiGenerationProvider`, `AutoCloseable`.
- `@ToString` — Lombok-generated diagnostic string; excludes the native model handle and `support`.

#### Input
- Constructor accepts `LlamaCppJniConfig config` and `AiPromptSupport promptSupport`; both are checked for null.
- `generate` and `generateWithTimings` accept an `AiGenerationRequest`.
- Model initialization reads configuration for model path, context size, threads, reasoning format, chat-template kwargs, SWA, cache reuse, GPU/CPU layer placement, KV settings, lazy mode, Flash Attention, cache types, batch sizing, and device selection.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and per-request inference parameters.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache/predicted token counts, and throughput; absent timings produce zero metrics.
- `close` closes the loaded `LlamaModel`, if initialized.

#### Core logic
- Constructor validates configuration and prompt support, then creates `LlamaCppJniProviderSupport`; model loading remains deferred.
- `model()` builds `ModelParameters` from configuration, applying optional settings only under their respective guards, then creates and caches one `LlamaModel`.
- Chat requests use support-generated inference parameters; `ChatResponseParser` parses the complete native response.
- `generate` logs truncation and prompt-cache reuse through `support`, then parses first-choice content with `AiCompletionParser`.
- `generateWithTimings` extracts response text and timing fields, including KV-cache prompt tokens.
- `close` releases the cached native model when present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — creates provider with lazy model loading.
- `generate(AiGenerationRequest) -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns text and model metrics.
- `close() -> void` — closes loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiPromptSupport`, `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config` and `promptSupport`.
- `generate` declares `IOException`; `generateWithTimings` does not.
- Invalid model parameters or native inference failures may propagate from the JNI binding; no local catch is present.
- A null timing object yields zero token counts and rates.

#### Concurrency
- The lazily initialized `model` is neither synchronized nor volatile; concurrent first calls may race to initialize it.
- The cached `LlamaModel` is shared across generation calls.
