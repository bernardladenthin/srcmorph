### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:31:01Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF language models via llama.cpp JNI to generate text completions and throughput timings for source-code documentation requests.

#### Purpose
- Provides an `AiGenerationProvider` backed by the `net.ladenthin.llama` JNI binding (`LlamaModel`).
- Lazily loads the GGUF model, parses chat responses, and returns completion text plus measured timings.

#### Type
- `class`, `public final`, Lombok `@ToString`.
- `implements AiGenerationProvider, AutoCloseable`.
- `@ToString.Exclude` on `model` and `support` fields.
- `@Nullable` (org.jspecify) on `model` field.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `AiGenerationRequest request` per `generate` and `generateWithTimings` call.
- Config fields read: `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK`, `cacheTypeV`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.
- Support delegate builds chat template kwargs and inference parameters.

#### Output
- `generate(...) -> String`: parsed completion text.
- `generateWithTimings(...) -> AiGenerationTimings`: text, prompt/predicted token counts, cache hit count, per-second rates.
- Lazily creates `LlamaModel` field `model`.
- `close()` releases the native model.
- Side effects: `support.warnOnTruncatedAnswer`, `support.logPromptCacheReuse`.

#### Core logic
- `model()` builds `ModelParameters` once and caches the `LlamaModel`:
  - Always sets model path, context size, threads, `ReasoningFormat.AUTO`, chat template kwargs.
  - Conditional: `enableSwaFull()`, `setCacheReuse`, `setGpuLayers` (>= 0), `setCpuMoeLayers` (>= 0), `setCpuFfnLayers` (>= 0), `setKvUnifiedPerSlot` (> 0), `setLazyMode`, `setFlashAttn(FlashAttn.ON)`, `setCacheTypeK`/`setCacheTypeV`, `setBatchSize`/`setUbatchSize`/`setThreadsBatch` (> 0), `setMainGpu` (>= 0), `setDevices` (non-empty).
- `chatResponse(request)`: `model().chatComplete(support.buildInferenceParameters(request))` then `chatResponseParser.parseResponse(...)`.
- `generate`: chat response, warn on truncation, log cache reuse, `completionParser.parseCompletion(getFirstContent())`.
- `generateWithTimings`: reads `Timings`; if null returns zero rates; otherwise maps `promptN`, `cacheN`, `promptPerSecond`, `predictedN`, `predictedPerSecond`.
- `close()`: closes the model if loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` : constructor, null-checks, builds support.
- `generate(AiGenerationRequest) -> String` : parsed completion text. Throws `IOException`.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` : completion with throughput metrics.
- `close() -> void` : releases native model handle.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `ChatResponseParser`.
- `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`.
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationTimings`.
- `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.util.Map`, `java.util.Objects`, `java.io.IOException`.

#### Exceptions / Errors
- Constructor: `NullPointerException` via `Objects.requireNonNull` for `config` and `promptSupport`.
- `generate` declares `IOException`; `generateWithTimings` declares none.
- Chat path throws only unchecked exceptions.
- `timings == null` yields zero-rate result, no exception.
- `model` nullable; loaded on demand.

#### Concurrency
- No synchronization on `model` field; lazy init in `model()` is not thread-safe.
- `support`, `config`, `promptSupport` are final fields.
