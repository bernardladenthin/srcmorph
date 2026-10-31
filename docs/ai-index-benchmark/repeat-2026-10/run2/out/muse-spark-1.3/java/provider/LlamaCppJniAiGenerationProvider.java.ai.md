### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:46Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF models via llama.cpp to generate AI text completions for source files.
#### Purpose
- `AiGenerationProvider` backed by `net.ladenthin:llama` JNI binding running llama.cpp locally.
- Lazily loads GGUF model on first generation to keep construction cheap and native-free.
#### Type
- Final class `LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`.
- Lombok `@ToString` with `@ToString.Exclude` on native `LlamaModel` handle and `LlamaCppJniProviderSupport`.
#### Input
- Constructor `config: LlamaCppJniConfig`, `promptSupport: AiPromptSupport` via `Objects.requireNonNull`.
- `generate(request: AiGenerationRequest)`, `generateWithTimings(request: AiGenerationRequest)` consume generation request.
- `LlamaCppJniProviderSupport.buildInferenceParameters`, `buildChatTemplateKwargs` supply inference parameters and chat template kwargs.
- Config fields read: `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK/V`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.
#### Output
- `generate` returns parsed completion `String` from `ChatResponse.getFirstContent`.
- `generateWithTimings` returns `AiGenerationTimings` with text plus prompt/cache/predicted counts and rates.
- Lazily creates and caches `LlamaModel` from `ModelParameters`; `close` closes native handle.
- Side effects: `warnOnTruncatedAnswer`, `logPromptCacheReuse` diagnostics via support.
#### Core logic
- `model()` builds `ModelParameters` with `ReasoningFormat.AUTO`, context size, threads, chat template kwargs, then conditionally forwards only explicitly configured options.
- Enables `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `FlashAttn.ON`, `cacheTypeK/V`, batch sizes, `mainGpu`, `devices` behind `>=0` / `>0` / non-blank / non-empty guards preserving native defaults.
- `chatResponse` calls `LlamaModel.chatComplete` with inference parameters then `ChatResponseParser.parseResponse` to retain finish reason, usage, timings, KV-cache counters.
- `generate` parses first content via `AiCompletionParser.parseCompletion` after truncation warning and cache-reuse logging.
- `generateWithTimings` extracts `Timings.promptN/cacheN/promptPerSecond/predictedN/predictedPerSecond` from `ChatResponse`; falls back to zero rates when `timings == null`.
#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport)` -> `LlamaCppJniAiGenerationProvider` — loads lazily, stores config.
- `generate(request)` -> `String` — runs chat, returns parsed completion.
- `generateWithTimings(request)` -> `AiGenerationTimings` — returns text plus measured throughput.
- `close()` -> `void` — closes cached native model.
#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationTimings`.
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
#### Exceptions / Errors
- `generate` throws `IOException` per `AiGenerationProvider` contract.
- `generateWithTimings` narrows away `IOException`; chat path throws only unchecked exceptions.
- `Objects.requireNonNull` on `config`, `promptSupport`; null `Timings` degrades to zero-rate `AiGenerationTimings`.
#### Concurrency
- Lazy `model` initialization is unsynchronized; not documented as thread-safe.
