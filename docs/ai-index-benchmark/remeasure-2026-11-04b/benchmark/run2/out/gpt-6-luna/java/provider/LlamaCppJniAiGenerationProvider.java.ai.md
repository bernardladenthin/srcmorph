### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:27Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Generates local AI completions from GGUF models through llama.cpp, with configurable inference, GPU, and KV-cache behavior.

#### Purpose
- Implements request-to-completion generation using a locally loaded GGUF model.
- Exposes model timing and KV-cache reuse metrics, and releases the native model handle on close.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both required non-null.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume generation requests.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters from configuration and prompts.
- Model setup reads `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK`, `cacheTypeV`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, and `devices`.

#### Output
- `generate(...)` returns parsed completion text; may log truncation and prompt-cache reuse.
- `generateWithTimings(...)` returns `AiGenerationTimings` with text, prompt token count, cache token count, prompt throughput, predicted token count, and predicted throughput.
- Lazily creates and caches a `LlamaModel`; `close()` closes it if loaded.
- Lombok-generated `toString` excludes `model` and `support`.

#### Core logic
- `model()` returns the cached `model` when present; otherwise:
  - Gets `chatTemplateKwargs` from `support.buildChatTemplateKwargs()`.
  - Builds `ModelParameters` with model path, context size, threads, `ReasoningFormat.AUTO`, and chat-template kwargs.
  - Applies optional settings when their guards pass: SWA full, cache reuse, GPU layers, CPU MoE/FFN layers, unified KV slot budget, lazy mode, flash attention, K/V cache types, batch and ubatch sizes, batch threads, main GPU, and explicit devices.
  - Converts lazy mode and cache types through `LlamaCppJniProviderSupport.lazyMode(...)` and `LlamaCppJniProviderSupport.cacheType(...)`.
  - Creates `new LlamaModel(modelParameters)`, stores it in `model`, and returns it.
- `chatResponse(request)` calls `support.buildInferenceParameters(request)`, runs `model().chatComplete(...)`, and parses the full response with `chatResponseParser.parseResponse(...)`.
- `generate(request)` obtains the response, calls `support.warnOnTruncatedAnswer(...)` and `support.logPromptCacheReuse(...)`, then returns `completionParser.parseCompletion(response.getFirstContent())`.
- `generateWithTimings(request)` extracts response text and timings; missing timings yield zero counts and rates. Otherwise returns prompt/predicted counts and rates plus `timings.getCacheN()`.
- `close()` closes the cached model when non-null.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — create provider with lazy model loading.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and measured metrics.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `Java8CompatibilityHelper`, `Objects`, `Map`, `IOException`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config` and `promptSupport`.
- `generate(...)` declares `IOException`.
- `generateWithTimings(...)` handles absent timings by returning zero metrics.
- `close()` only closes a model if one has been initialized.

#### Concurrency
- Lazy initialization and cached `model` are unsynchronized; no thread-safety guarantee is declared.
