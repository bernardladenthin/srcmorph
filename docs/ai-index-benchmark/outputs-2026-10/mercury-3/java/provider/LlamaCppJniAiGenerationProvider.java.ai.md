### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:32Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation for srcmorph by running local GGUF models through the llama.cpp JNI binding, with full response/timings parsing and lazy model loading.

#### Purpose
- Implements `AiGenerationProvider` backed by the `net.ladenthin:llama` JNI binding; runs GGUF models locally via llama.cpp.
- Lombok `@ToString` renders config/prompt-support fields for build-log diagnostics while excluding the native `LlamaModel` handle and `LlamaCppJniProviderSupport`.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` ; `implements AiGenerationProvider, AutoCloseable`.
- Annotations: `@ToString` (Lombok), `@ToString.Exclude` on `model` and `support` fields.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport` (both `Objects.requireNonNull`).
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume request prompts.
- Config-driven model parameters read from `config`: `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK`, `cacheTypeV`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.

#### Output
- `generate` returns `String` (parsed completion text).
- `generateWithTimings` returns `AiGenerationTimings` (text, promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond).
- `close()` closes the native `LlamaModel` handle if loaded.
- `model()` lazily initialises and caches the `LlamaModel` field on first generation.

#### Core logic
- `model()` builds `ModelParameters` from config with conditional setters: `ReasoningFormat.AUTO`, `enableSwaFull()`, `setCacheReuse`, `setGpuLayers`, `setCpuMoeLayers`, `setCpuFfnLayers`, `setKvUnifiedPerSlot`, `setLazyMode`, `FlashAttn.ON`, `setCacheTypeK/V`, `setBatchSize`, `setUbatchSize`, `setThreadsBatch`, `setMainGpu`, `setDevices`.
- `chatResponse(request)` calls `chatResponseParser.parseResponse(model().chatComplete(support.buildInferenceParameters(request)))` and returns the full `ChatResponse` (finish reason, token usage, KV-cache counters).
- `generate` calls `chatResponse`, then `support.warnOnTruncatedAnswer` and `support.logPromptCacheReuse`, then `completionParser.parseCompletion(response.getFirstContent())`.
- `generateWithTimings` parses `Timings` from the response; if null, degrades to an `AiGenerationTimings` with zero rates so caller fallback can act.
- `close()` closes the native model only if it was loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> constructor` — loads nothing; model lazy.
- `generate(AiGenerationRequest) throws IOException -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generates with measured timings.
- `close() -> void` — closes native model handle.

#### Dependencies
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`

#### Exceptions / Errors
- `generate` declares `IOException` per interface, but the chat path throws only unchecked exceptions (noted in comment as "BED" narrowing).
- `Objects.requireNonNull` on `config` and `promptSupport`.
- `@Nullable` `model` field; null-checked before lazy init.
- `Timings == null` handled by returning zero-rate `AiGenerationTimings`.

#### Concurrency
- No synchronization or volatile on the lazily initialised `model` field; single-threaded assumption across `generate`/`generateWithTimings` calls.
- Model is cached after first use; native handle is closed via `close()`.
