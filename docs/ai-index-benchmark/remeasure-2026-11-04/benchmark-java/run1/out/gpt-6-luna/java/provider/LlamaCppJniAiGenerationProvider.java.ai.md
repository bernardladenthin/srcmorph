### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:11:09Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates AI completions from locally loaded GGUF models and reports llama.cpp throughput and prompt-cache usage.

#### Purpose
- Provides `AiGenerationProvider` completions using llama.cpp through the JNI binding.
- Loads the model on first generation and reuses it for subsequent requests.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from the rendered output.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`.
- Generation methods accept `AiGenerationRequest`; `LlamaCppJniProviderSupport` builds inference parameters and chat-template arguments from configuration and prompt support.
- Model configuration includes context size, threads, GPU/CPU layer placement, KV cache, attention, batching, device selection, and lazy loading options.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` with completion text, prompt/cache/prediction token counts, and measured rates; absent timings produce zero metrics.
- `close` closes the loaded `LlamaModel`, if initialized.

#### Core logic
- Constructor rejects null configuration or prompt support and initializes `LlamaCppJniProviderSupport`; it does not load native resources.
- On first use, `model()` builds `ModelParameters` from configuration, applying optional settings only when their configured values meet the relevant guards, then constructs and caches `LlamaModel`.
- Each request builds inference parameters, calls `chatComplete`, and parses the full response as `ChatResponse`.
- `generate` warns about truncated answers, logs prompt-cache reuse, and parses the first response content with `AiCompletionParser`.
- `generateWithTimings` returns first content plus model-reported prompt, cache, and prediction metrics; missing timing data yields zero rates and counts.
- `close` releases the cached model when present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — creates provider with lazy model loading.
- `generate(AiGenerationRequest) -> String` — generates parsed completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generates text and timing metrics.
- `close() -> void` — closes loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `LlamaModel`
- `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`, `AiCompletionParser`
- `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null `config` or `promptSupport`.
- `generate` declares `IOException`; `generateWithTimings` does not declare a checked exception.
- Model loading, inference, and response parsing may propagate unchecked failures; none are caught.
- `generateWithTimings` treats missing timing data as zero metrics.

#### Concurrency
- Lazy model initialization and model access are unsynchronized; concurrent first calls may initialize multiple models.
- Configuration and support fields are final; the cached model reference is mutable.
