### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:48Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Generates local AI completions from GGUF models using llama.cpp, with configurable inference, cache reuse, and model-throughput reporting.

#### Purpose
- Implements AI generation through the `net.ladenthin:llama` JNI binding, loading and caching a local GGUF model on first use.
- Converts chat responses into completion text or `AiGenerationTimings`, and closes the native model handle.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`; annotated `@ToString`.
- Lombok excludes `model` and `support` from generated `toString()`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both required non-null.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume generation requests.
- `model()` reads configuration for model path, context, threads, GPU/CPU placement, KV cache, attention, batching, devices, and lazy loading; obtains chat-template kwargs from `support`.
- Inference parameters are built from each request by `support.buildInferenceParameters(request)`.

#### Output
- `generate(...)` returns parsed completion text.
- `generateWithTimings(...)` returns completion text with prompt/prediction token counts, cache tokens, and throughput; missing timings produce zero metrics.
- Lazily stores a `LlamaModel` in `model`; `close()` closes it if initialized.
- Emits support warnings for truncated answers and logs prompt-cache reuse.

#### Core logic
- `model()` returns the cached model, or creates `ModelParameters` with `modelPath()`, `contextSize()`, `threads()`, `ReasoningFormat.AUTO`, and `support.buildChatTemplateKwargs()`.
- Applies configured options conditionally: `enableSwaFull()`, `setCacheReuse(...)`, GPU/CPU layer placement, unified KV budget, `lazyMode(...)`, flash attention, KV cache types, batch sizes, thread count, main GPU, and explicit devices.
- Creates `new LlamaModel(modelParameters)` and caches it in `model`. Initialization is lazy and unsynchronized.
- `chatResponse(request)` calls `model().chatComplete(support.buildInferenceParameters(request))` and parses the full result with `chatResponseParser.parseResponse(...)`.
- `generate(request)` warns via `support.warnOnTruncatedAnswer(...)`, logs via `support.logPromptCacheReuse(...)`, then parses `response.getFirstContent()` with `completionParser.parseCompletion(...)`.
- `generateWithTimings(request)` extracts first content and timings; if timings are absent, returns zero metrics; otherwise maps prompt count, cache count, prompt rate, predicted count, and predicted rate into `AiGenerationTimings`.
- `close()` closes the cached model when non-null.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — initialize provider.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and metrics.
- `close() -> void` — release native model.

#### Dependencies
- `AiGenerationProvider`, `AutoCloseable`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiGenerationRequest`, `AiPromptSupport`, `AiCompletionParser`, `AiGenerationTimings`.
- JNI binding: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`.
- Utilities: `Objects`, `Map`, `IOException`, `Java8CompatibilityHelper`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config` and `promptSupport`, with respective messages `"config"` and `"promptSupport"`.
- `generate(...)` declares `IOException`; `generateWithTimings(...)` does not.
- Configuration options are forwarded only under their stated guards; invalid backend/configuration combinations may fail during native model setup or inference.

#### Concurrency
- Lazy `model` initialization and `close()` are unsynchronized; concurrent initialization or closure is not guarded.
- `model` is nullable and remains null until generation initializes it.
