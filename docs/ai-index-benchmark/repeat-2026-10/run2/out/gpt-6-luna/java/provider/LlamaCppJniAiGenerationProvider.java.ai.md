### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:32Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions locally from GGUF models using llama.cpp, with configurable inference, cache reuse, and throughput reporting.

#### Purpose
- Provides `AiGenerationProvider` completions through the `net.ladenthin:llama` JNI binding.
- Loads and retains the native model on first generation; exposes generation timings and releases the model on close.

#### Type
- `public final` class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; both are required.
- Generation methods accept `AiGenerationRequest`.
- Model initialization reads configuration for model path, context, threads, GPU/device selection, KV cache, attention, batching, and lazy loading.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` with completion text, token counts, cache tokens, and measured rates; missing timings produce zero metrics.
- Emits support warnings for truncated answers and logs prompt-cache reuse.
- `close` closes the loaded `LlamaModel`, if initialized.

#### Core logic
- Constructor validates configuration and prompt support with `Objects.requireNonNull`, then creates `LlamaCppJniProviderSupport`.
- `model()` lazily builds `ModelParameters` and creates/caches `LlamaModel`:
  - Always sets model path, context size, threads, automatic reasoning format, and chat-template kwargs.
  - Applies optional SWA, cache reuse, GPU/MoE/FFN placement, KV budget, lazy mode, Flash Attention, KV cache types, batch sizes, and device selection according to configuration guards.
- `chatResponse()` builds inference parameters, invokes `chatComplete`, and parses the full response.
- `generate()` warns about truncation, logs cache reuse, and parses the first response content with `AiCompletionParser`.
- `generateWithTimings()` returns response text and native timing/cache metrics, or zero metrics when timings are absent.
- `close()` closes the cached model without resetting the field.

#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport)` → constructor; validates inputs and initializes support.
- `generate(request) -> String` — generates parsed completion text.
- `generateWithTimings(request) -> AiGenerationTimings` — returns completion and model metrics.
- `close() -> void` — closes the loaded model.

#### Dependencies
- Provider/configuration: `AiGenerationProvider`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiGenerationTimings`.
- Request and prompt: `AiGenerationRequest`, `AiPromptSupport`, `AiCompletionParser`.
- JNI/model: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`.
- Response/timings: `ChatResponseParser`, `ChatResponse`, `Timings`.
- Utilities: `Objects`, `Map`, `IOException`, `Java8CompatibilityHelper`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor rejects null configuration or prompt support with `NullPointerException`.
- `generate` declares `IOException`; `generateWithTimings` does not declare the interface’s checked exception.
- No timings in the parsed response yield zero-valued metrics.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first calls may initialize more than one model.
- The class does not otherwise establish thread-safety for generation or closing.
