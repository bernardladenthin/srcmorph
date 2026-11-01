### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:58:18Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java source-analysis responses locally with llama.cpp GGUF models and reports inference and KV-cache performance.

#### Purpose
- Provides `AiGenerationProvider` completion generation using the `net.ladenthin:llama` JNI binding.
- Loads and retains the native model on first generation; supports closing the model.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString` generates a diagnostic representation; native model handle and provider support are excluded.

#### Input
- Constructor receives non-null `LlamaCppJniConfig` and `AiPromptSupport`.
- Generation methods receive an `AiGenerationRequest`.
- Model initialization reads configuration for model path, context, threads, GPU/CPU placement, KV cache, attention, batching, devices, and related options.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters.

#### Output
- `generate` returns parsed completion text and may emit truncation and prompt-cache reuse warnings/logs.
- `generateWithTimings` returns completion text, token counts, cache tokens, and throughput; absent timing data yields zero metrics.
- Lazily creates and caches a `LlamaModel`; `close` closes it if initialized.

#### Core logic
- On first model access, builds `ModelParameters` with configured model path, context size, thread count, automatic reasoning format, and chat-template kwargs.
- Applies optional model settings only when their configured values meet the relevant guards; creates and caches `LlamaModel`.
- Builds inference parameters from each request, calls `chatComplete`, and parses the full response.
- `generate` warns about truncated answers, logs prompt-cache reuse, and parses the first response content as a completion.
- `generateWithTimings` extracts response text and native timing/cache metrics; substitutes zero metrics if timings are absent.
- `close` releases the cached native model when present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — creates provider with lazy model loading.
- `generate(AiGenerationRequest) -> String` — generates parsed completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generates completion with metrics.
- `close() -> void` — closes initialized native model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not.
- `generateWithTimings` handles missing timing data by returning zero metrics.

#### Concurrency
- Model initialization and field access are unsynchronized; concurrent first calls may initialize multiple models.
- Configuration, prompt support, and helper/parser fields are final; cached model reference is mutable.
