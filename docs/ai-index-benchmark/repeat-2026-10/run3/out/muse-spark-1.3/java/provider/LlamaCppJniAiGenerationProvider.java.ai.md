### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:22:11Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF models via llama.cpp to generate AI text completions.
#### Purpose
- `LlamaCppJniAiGenerationProvider` generates AI completions locally via llama.cpp JNI binding.
- Lazily loads native `LlamaModel` on first generation for cheap construction/cached runs.
#### Type
- final class `LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`.
- Notable annotation `@ToString` with `@ToString.Exclude` on `model`, `support`.
#### Input
- Constructor `config: LlamaCppJniConfig`, `promptSupport: AiPromptSupport` via `Objects.requireNonNull`.
- Method input `AiGenerationRequest` for `generate`, `generateWithTimings`, `chatResponse`.
- Consumed config fields: `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK/V`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.
- Read resources: GGUF file at `config.modelPath()`, native llama.cpp context, `support.buildChatTemplateKwargs()`, `support.buildInferenceParameters(request)`.
#### Output
- `generate` returns parsed completion `String` via `AiCompletionParser`.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache/predicted counts and rates.
- Produced state: cached `LlamaModel model` on first `model()` call.
- Side effects: native model load, `support.warnOnTruncatedAnswer`, `support.logPromptCacheReuse`, `model.close()`.
#### Core logic
- `model()` builds `ModelParameters` from `LlamaCppJniConfig`: model path, ctx size, threads, `ReasoningFormat.AUTO`, chat-template kwargs.
- Conditionally forwards optional native tuning: `enableSwaFull`, `setCacheReuse`, `setGpuLayers`, `setCpuMoeLayers`, `setCpuFfnLayers`, `setKvUnifiedPerSlot`, `setLazyMode`, `setFlashAttn.ON`, `setCacheTypeK/V`, `setBatchSize/UbatchSize`, `setThreadsBatch`, `setMainGpu`, `setDevices`.
- `chatResponse` calls `model().chatComplete(support.buildInferenceParameters)` then `ChatResponseParser.parseResponse`.
- `generate` parses `response.getFirstContent()` via `completionParser.parseCompletion` after truncation/cache logging.
- `generateWithTimings` extracts `Timings.promptN/cacheN/promptPerSecond/predictedN/predictedPerSecond` from `ChatResponse`, falls back to zeros if `timings==null`.
- `close` closes cached `LlamaModel` if non-null.
#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport) -> LlamaCppJniAiGenerationProvider` — lazy-loads GGUF on first use.
- `generate(request) -> String` — runs chat, returns parsed completion.
- `generateWithTimings(request) -> AiGenerationTimings` — returns text plus native throughput.
- `close() -> void` — releases native model handle.
#### Dependencies
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`.
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.
- Internal collaborators: `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationTimings`.
#### Exceptions / Errors
- `generate` throws `IOException` per `AiGenerationProvider` interface.
- `generateWithTimings` narrows away `IOException`; chat path throws only unchecked exceptions.
- `NullPointerException` via `requireNonNull` on null `config`/`promptSupport`.
- Degrades to zero rates when `response.getTimings()==null`.
#### Concurrency
- Lazy `model` initialization is unsynchronized non-volatile; not thread-safe for concurrent first `generate`.
