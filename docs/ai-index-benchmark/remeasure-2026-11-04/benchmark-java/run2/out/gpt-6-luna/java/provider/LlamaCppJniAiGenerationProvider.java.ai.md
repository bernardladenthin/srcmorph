### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:59Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates AI completions from locally hosted GGUF models through llama.cpp, with configurable inference, cache reuse, and throughput reporting.

#### Purpose
- Provides `AiGenerationProvider` completions using a lazily loaded local llama.cpp model.
- Exposes parsed completion text and model-reported generation timings.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; Lombok excludes the native model handle and provider support from rendered output.

#### Input
- Constructor receives `LlamaCppJniConfig` and `AiPromptSupport`; rejects null values.
- `generate` and `generateWithTimings` receive `AiGenerationRequest`.
- Model initialization reads configuration for model path, context, threads, GPU/CPU placement, KV cache, attention, batching, devices, and lazy loading.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters from configuration and requests.

#### Output
- `generate` returns parsed completion text; may log warnings for truncated answers and prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` containing completion text, prompt/cache/prediction token counts, and throughput rates.
- `close` closes the loaded native model if present.

#### Core logic
- Loads and caches `LlamaModel` on first inference; configures `ModelParameters` with `ReasoningFormat.AUTO` and chat-template kwargs.
- Applies optional settings only when their configured values meet the relevant guards; creates the native model and stores its handle.
- Builds inference parameters through `LlamaCppJniProviderSupport`, runs `chatComplete`, and parses the full response with `ChatResponseParser`.
- `generate` warns on truncation, logs prompt-cache reuse, then parses the first response content with `AiCompletionParser`.
- `generateWithTimings` returns zeroed rates/counts when response timings are absent; otherwise reports prompt tokens, cache tokens, predicted tokens, and measured rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — construct provider with lazy model loading.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate completion and timing metrics.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null configuration or prompt support.
- `generate` declares `IOException`; `generateWithTimings` does not declare checked exceptions.
- Native model loading, inference, response parsing, and model close may fail through unchecked exceptions.
- Missing response timings produce zero-valued metrics.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first calls may race.
- No other synchronization or thread-safety guarantees are declared.
