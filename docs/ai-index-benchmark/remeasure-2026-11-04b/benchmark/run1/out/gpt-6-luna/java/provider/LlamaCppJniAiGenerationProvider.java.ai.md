### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:57Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Runs local GGUF language models through llama.cpp to generate completions and report inference throughput and KV-cache reuse.

#### Purpose
- Provides AI completion generation backed by the `net.ladenthin:llama` JNI binding, with lazy GGUF model loading.
- Configures llama.cpp model and inference parameters, parses chat responses, and exposes generation timings.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`; annotated `@ToString`.
- Declares no nested types.

#### Input
- Constructor receives `LlamaCppJniConfig config` and `AiPromptSupport promptSupport`; both are required non-null.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume generation requests.
- `model()` reads model and inference options from `config`; `support` builds chat-template kwargs and inference parameters using the configuration and prompt support.

#### Output
- `generate(...)` returns parsed completion text; reports truncation and prompt-cache reuse through `support`.
- `generateWithTimings(...)` returns `AiGenerationTimings` with completion text, prompt/cache/predicted token counts, and measured rates; absent timings produce zero values.
- Lazily creates and caches a `LlamaModel`; `close()` closes it if loaded.

#### Core logic
- `model()` returns cached `model` if present; otherwise:
  - Calls `support.buildChatTemplateKwargs()` and creates `ModelParameters` with `config.modelPath()`, `config.contextSize()`, `config.threads()`, `ReasoningFormat.AUTO`, and the template kwargs.
  - Applies optional settings when their guards pass: `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK`, `cacheTypeV`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, and `devices`.
  - Converts lazy mode and cache types using `LlamaCppJniProviderSupport.lazyMode(...)` and `LlamaCppJniProviderSupport.cacheType(...)`; creates `new LlamaModel(modelParameters)` and stores it in `model`.
- `chatResponse(request)` calls `support.buildInferenceParameters(request)`, `model().chatComplete(...)`, then `chatResponseParser.parseResponse(...)`.
- `generate(request)` obtains the response, calls `support.warnOnTruncatedAnswer(request, response)` and `support.logPromptCacheReuse(request, response)`, then parses `response.getFirstContent()` with `completionParser.parseCompletion(...)`.
- `generateWithTimings(request)` reads response content and timings. If `response.getTimings()` is null, returns zero token counts and rates; otherwise forwards `getPromptN()`, `getCacheN()`, `getPromptPerSecond()`, `getPredictedN()`, and `getPredictedPerSecond()`.
- `close()` closes the current model only when non-null.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — initializes provider dependencies.
- `generate(AiGenerationRequest) -> String` — generate parsed completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate completion with model timings.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ModelParameters`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config` and `promptSupport`, with corresponding parameter names as messages.
- `generate(...)` declares `IOException`; `generateWithTimings(...)` does not.
- Invalid or unsupported model parameters and native inference failures are not caught here.

#### Concurrency
- Lazy model initialization and assignment are unsynchronized; concurrent first calls can initialize multiple models.
- `close()` and generation have no synchronization or coordination.
