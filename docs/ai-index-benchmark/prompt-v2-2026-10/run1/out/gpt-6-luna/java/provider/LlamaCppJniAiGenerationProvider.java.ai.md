### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:41Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java-source-related AI completions with a locally loaded llama.cpp GGUF model and reports inference and KV-cache timings.

#### Purpose
- Provides `AiGenerationProvider` completions using the `net.ladenthin:llama` JNI binding.
- Loads the native model only when generation first requires it; reuses it for later requests.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from the generated string.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; rejects either if null.
- `generate` and `generateWithTimings` accept an `AiGenerationRequest`.
- Model initialization reads configuration for model path, context, threads, GPU and CPU layer placement, cache settings, attention, batching, lazy loading, and device selection.
- Provider support builds chat-template arguments and inference parameters from configuration and requests.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` containing completion text, prompt/cache/predicted token counts, and throughput rates.
- Lazily stores a `LlamaModel`; `close` closes it if loaded.
- Logs prompt-cache reuse and warns about truncated answers through provider support.

#### Core logic
- On first model access, builds `ModelParameters` from configuration, applying optional settings only when their values meet the relevant guards.
- Creates and caches a `LlamaModel`; subsequent calls reuse the cached handle.
- Builds inference parameters, calls `chatComplete`, and parses the full response with `ChatResponseParser`.
- For `generate`, checks for truncation, logs cache reuse, and parses the first response content with `AiCompletionParser`.
- For `generateWithTimings`, returns response timing and cache metrics; if timings are absent, returns zero counts and rates.
- `close` releases the model only if it was initialized.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` → constructor; creates provider with lazy model loading.
- `generate(AiGenerationRequest)` → `String`; generates and parses completion text.
- `generateWithTimings(AiGenerationRequest)` → `AiGenerationTimings`; returns completion and inference metrics.
- `close()` → `void`; closes the loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiPromptSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`, `AiCompletionParser`
- `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for configuration and prompt support.
- `generate` declares `IOException`.
- Missing response timings degrade to zero metrics.
- Configuration guards avoid forwarding unsupported or default values, including nonpositive unified KV slot, batch, ubatch, and batch-thread settings.

#### Concurrency
- Model initialization and caching are unsynchronized; concurrent first calls may initialize more than one model.
- No other explicit synchronization or thread-safety guarantees are present.
