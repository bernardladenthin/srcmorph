### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:34Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions from locally loaded GGUF models through llama.cpp and reports inference and prompt-cache metrics.

#### Purpose
- `LlamaCppJniAiGenerationProvider` implements local AI text generation using the llama.cpp JNI binding.
- Loads and caches a model on first generation; exposes completion text and, when requested, generation timings.

#### Type
- `LlamaCppJniAiGenerationProvider` — `public final class`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor receives `LlamaCppJniConfig` and `AiPromptSupport`; both are required.
- Generation methods receive `AiGenerationRequest`.
- Model initialization reads configuration for model path, context size, threads, GPU/CPU placement, KV cache, attention, batching, and devices.
- Inference parameters and chat-template arguments are built through `LlamaCppJniProviderSupport`.

#### Output
- `generate` returns parsed completion text and reports truncation/cache-reuse diagnostics.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache/predicted token counts, and throughput rates.
- Lazily creates and retains a `LlamaModel`; `close` closes it if initialized.

#### Core logic
- Constructor null-checks configuration and prompt support, then creates `LlamaCppJniProviderSupport`.
- `model()` initializes the native model only on first use:
  - Builds chat-template kwargs and `ModelParameters` from configuration.
  - Applies optional settings only when their configured values meet their respective guards.
  - Constructs and caches `LlamaModel`.
- `chatResponse()` builds inference parameters, calls `chatComplete`, and parses the full response.
- `generate()` warns on truncated answers, logs prompt-cache reuse, and parses the first response content.
- `generateWithTimings()` returns zero rates and counts when response timings are absent; otherwise exposes model-reported prompt, cache, and predicted token metrics.
- `close()` closes the cached model when present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; validates dependencies
- `generate(AiGenerationRequest)` -> `String`; generates completion text
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings`; returns completion and metrics
- `close()` -> `void`; releases cached model

#### Dependencies
- Internal: `AiGenerationProvider`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiGenerationRequest`, `AiPromptSupport`, `AiCompletionParser`, `AiGenerationTimings`, `Java8CompatibilityHelper`
- llama.cpp JNI: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`
- Java: `IOException`, `Map`, `Objects`
- Annotations: Lombok `@ToString`, JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not declare checked exceptions.
- Native model initialization, inference, parsing, and model closure may propagate unchecked failures.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first calls may initialize multiple models.
- No explicit thread-safety guarantees are provided.
